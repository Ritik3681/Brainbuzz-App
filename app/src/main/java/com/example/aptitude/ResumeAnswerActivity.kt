package com.example.aptitude

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.LottieAnimationView

class ResumeAnswerActivity : AppCompatActivity() {
    private lateinit var interviewQuestionsTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var backPress: ImageView
    private lateinit var downloadBtn: ImageView
    private val apiKey = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"
    private var fetchedAnswers: String? = null
    private var pdfFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resume_answer)

        interviewQuestionsTextView = findViewById(R.id.interviewQuestionsTextView)
        progressBar = findViewById(R.id.progress_circular)
        backPress = findViewById(R.id.backPress)
        downloadBtn = findViewById(R.id.download_icon)

        val interviewQuestions = intent.getStringExtra("interview_questions") ?: "No questions available"
        generateAnswers(interviewQuestions)

        backPress.setOnClickListener { onBackPressed() }

        downloadBtn.setOnClickListener {
            if (fetchedAnswers != null) {
                if (checkStoragePermission()) {
                    showDownloadConfirmationDialog()

//                    generatePdf(fetchedAnswers!!)
                } else {
                    requestStoragePermission()
                }
            } else {
                Toast.makeText(this, "Please wait, answers are still generating!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateAnswers(questions: String) {
        progressBar.visibility = View.VISIBLE
        interviewQuestionsTextView.text = "Generating answers...\n"
        val questionList = questions.split("\n").filter { it.isNotBlank() }
        val answersList = mutableListOf<String>()

        lifecycleScope.launch {
            for ((index, question) in questionList.withIndex()) {
                val answer = generateAnswerForQuestion(question)
                answersList.add("Q${index + 1}: $question\nAns: $answer\n")
                interviewQuestionsTextView.text = answersList.joinToString("\n\n")
                delay(1500)
            }
            progressBar.visibility = View.GONE
            downloadBtn.visibility=View.VISIBLE
            fetchedAnswers = answersList.joinToString("\n\n")
        }
    }

    private suspend fun generateAnswerForQuestion(question: String): String {
        return try {
            val generativeModel = GenerativeModel(modelName = "gemini-2.0-flash", apiKey = apiKey)
            val response = generativeModel.generateContent("Provide a brief answer for: $question")
            response.text?.replace(Regex("[*#\$]"), "") ?: "No answer available"
        } catch (e: Exception) {
            Log.e("AI Generation", "Error: ${e.message}")
            "Error generating answer: ${e.message}"
        }
    }

    private fun generatePdf(content: String) {
        Log.d("data222", "generatePdf: $content")
        try {
            val fileName = "Interview_Answers.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val document = PDDocument()
            var page = PDPage()
            document.addPage(page)
            var contentStream = PDPageContentStream(document, page)

            contentStream.beginText()
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
            contentStream.newLineAtOffset(50f, 750f)

            val lines = content.split("\n")
            var yOffset = 750f

            for (line in lines) {
                if (yOffset < 100f) {
                    contentStream.endText()
                    contentStream.close()
                    page = PDPage()
                    document.addPage(page)
                    contentStream = PDPageContentStream(document, page)
                    contentStream.beginText()
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
                    contentStream.newLineAtOffset(50f, 750f)
                    yOffset = 750f
                }
                contentStream.showText(line)
                yOffset -= 20f
                contentStream.newLineAtOffset(0f, -20f)
            }

            contentStream.endText()
            contentStream.close()
            document.save(file)
            document.close()

            pdfFilePath = file.absolutePath
            scanFile(file)
            Toast.makeText(this, "PDF Saved in Downloads", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("PDFGeneration", "Error: ${e.message}")
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
        }
    }


    private fun scanFile(file: File) {
        MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null) { path, uri ->
            Log.d("PDFScan", "Scanned: $path -> URI: $uri")
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 101)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, 101)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                    fetchedAnswers?.let { generatePdf(it) }
                } else {
                    Toast.makeText(this, "Permission not granted! Cannot save PDF.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchedAnswers?.let { generatePdf(it) }
            } else {
                Toast.makeText(this, "Permission denied! Cannot save PDF.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Permission granted! You can now download PDFs.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage permission is required to save PDFs.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showDownloadConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_download, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        alertDialog.show()

        val lottieDownload = dialogView.findViewById<LottieAnimationView>(R.id.lottieDownload)
        val btnCancel = dialogView.findViewById<CardView>(R.id.btnCancel)
        val btnDownload = dialogView.findViewById<CardView>(R.id.btnDownload)
        lottieDownload.playAnimation()

        // Lottie Animation starts automatically due to `app:lottie_autoPlay="true"`

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnDownload.setOnClickListener {
            alertDialog.dismiss()
            generatePdf(fetchedAnswers!!)

        }
    }




}
