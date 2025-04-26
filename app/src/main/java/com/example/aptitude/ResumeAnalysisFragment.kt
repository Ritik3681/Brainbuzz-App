package com.example.aptitude

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ResumeAnalysisFragment : Fragment() {

    private lateinit var uploadResumeButton: Button
    private lateinit var resumePreviewImage: ImageView
    private lateinit var resumeNameTextView: TextView
    private lateinit var extractedTextTextView: TextView
    private lateinit var resumeAnalysis:Button
    lateinit var progressBar:ProgressBar
    private var selectedFileUri: Uri? = null
    lateinit var backPress:ImageView
    private lateinit var resume_Answer:TextView
    private var interviewQuestions: String? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val fileUri = result.data!!.data
                fileUri?.let {
                    selectedFileUri = it
                    showResumePreview(it)
                   // extractTextFromPdfOrImage(it)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_resume_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadResumeButton = view.findViewById(R.id.uploadResumeButton)
        resumePreviewImage = view.findViewById(R.id.resumePreviewImage)
        resumeNameTextView = view.findViewById(R.id.resumeNameTextView)
        extractedTextTextView = view.findViewById(R.id.extractedTextTextView)
        resumeAnalysis=view.findViewById(R.id.resumeAnalysisButton)
        progressBar=view.findViewById(R.id.progressBar)
        backPress=view.findViewById(R.id.backPress)
        resume_Answer=view.findViewById(R.id.resumeAnswer)

        resume_Answer.setOnClickListener{
            interviewQuestions?.let { questions ->
                val intent = Intent(requireContext(), ResumeAnswerActivity::class.java)
                intent.putExtra("interview_questions", questions)
                startActivity(intent)
            } ?: run {
                Toast.makeText(requireContext(), "No questions available", Toast.LENGTH_SHORT).show()
            }


        }
        backPress.setOnClickListener{
            activity?.onBackPressed()
        }

        // Initialize PDFBox
        PDFBoxResourceLoader.init(requireContext())

        uploadResumeButton.setOnClickListener {
            openFilePicker()
        }
        resumeAnalysis.setOnClickListener{
            selectedFileUri?.let { it1 -> extractTextFromPdfOrImage(it1) }

        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        filePickerLauncher.launch(intent)
    }

    private fun showResumePreview(fileUri: Uri) {
        val bitmap = renderPdfFirstPage(fileUri)
        if (bitmap != null) {
            resumePreviewImage.setImageBitmap(bitmap)
        } else {
            resumePreviewImage.setImageResource(R.drawable.colour)
        }
        resumePreviewImage.visibility = View.VISIBLE
        uploadResumeButton.visibility=View.GONE
        resumeAnalysis.visibility=View.VISIBLE

        // Get file name
        resumeNameTextView.text = getFileName(fileUri)
    }

    private fun renderPdfFirstPage(uri: Uri): Bitmap? {
        return try {
            val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            fileDescriptor?.use {
                val pdfRenderer = PdfRenderer(it)
                val page = pdfRenderer.openPage(0)

                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                page.close()
                pdfRenderer.close()
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return "Unknown File"
    }

    private fun extractTextFromPdfOrImage(pdfUri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                val tempFile = File(requireContext().cacheDir, "temp_resume.pdf")
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val document = PDDocument.load(tempFile)
                val stripper = PDFTextStripper()
                val text = stripper.getText(document).trim()
                document.close()

                if (text.isNotEmpty()) {
                    extractedTextTextView.text = text
                    Log.d("ResumeAnalysis", "Extracted Text: $text")
                } else {
                    Log.w("ResumeAnalysis", "No text found, trying OCR...")
                    val bitmap = convertPdfToImage(pdfUri)
                    if (bitmap != null) {
                        extractTextFromImage(bitmap)
                        Log.d("ResumeAnalysis1111", "Bitmap width: ${bitmap.width}, height: ${bitmap.height}")

                    } else {
                        extractedTextTextView.text = "No text found and OCR failed."
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            extractedTextTextView.text = "Error extracting text: ${e.message}"
        }
    }

    private fun convertPdfToImage(uri: Uri): Bitmap? {
        return try {
            val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
            fileDescriptor?.use {
                val pdfRenderer = PdfRenderer(it)
                val page = pdfRenderer.openPage(0)

                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                page.close()
                pdfRenderer.close()
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractTextFromImage(bitmap: Bitmap) {
        Log.d("ResumeAnalysis1111", "Bitmap width: ${bitmap.width}, height: ${bitmap.height}")

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                Log.d("ResumeAnalysis1111", "Filtered Resume Details: $extractedText")

                val filteredText = filterResumeDetails(extractedText) // Extract only needed details
                Log.d("ResumeAnalysis1111", "Filtered Resume Details: $filteredText")
                analyzeResume(filteredText)

            }
            .addOnFailureListener { e ->
                Log.e("ResumeAnalysis", "OCR Error", e)
            }
    }

    private fun filterResumeDetails(text: String): String {
        Log.d("ResumeAnalysis1111", "Filtered Resume Details: $text")
        val experienceRegex = "(?i)INTERNSHIP(.*?)(?=(PROJECTS|TECHNICAL SKILLS|EDUCATION|$))".toRegex(RegexOption.DOT_MATCHES_ALL)
        val projectsRegex = "(?i)PROJECTS(.*?)(?=(TECHNICAL SKILLS|EDUCATION|$))".toRegex(RegexOption.DOT_MATCHES_ALL)
        val skillsRegex = "(?i)TECHNICAL SKILLS(.*?)(?=(EDUCATION|$))".toRegex(RegexOption.DOT_MATCHES_ALL)
        val educationRegex = "(?i)EDUCATION(.*?)(?=(CERTIFICATIONS|$))".toRegex(RegexOption.DOT_MATCHES_ALL)

        val experience = experienceRegex.find(text)?.value ?: ""
        val projects = projectsRegex.find(text)?.value ?: ""
        val skills = skillsRegex.find(text)?.value ?: ""
        val education = educationRegex.find(text)?.value ?: ""

        return "$experience\n\n$projects\n\n$skills\n\n$education"
    }
    private fun analyzeResume(result: String) {
        progressBar.visibility = View.VISIBLE
        extractedTextTextView.text = "Analyzing..."

        Log.d("analyzeResume", "Starting analysis with extracted result:")
        Log.d("analyzeResume", "Resume Data:\n$result")

        lifecycleScope.launch {
            try {
                val prompt = """
                Based on the following resume details, generate 20 relevant interview questions tailored to the candidate's skills, experience, and job role:

                Resume Details:
                $result

                The questions should focus on the candidate's technical expertise, past projects, problem-solving skills, and role-specific knowledge.
            """.trimIndent()

                Log.d("analyzeResume", "Prompt sent to Gemini:\n$prompt")

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"
                )

                val response = generativeModel.generateContent(prompt)

                Log.d("analyzeResume", "Gemini Response: ${response.text}")

                interviewQuestions = response.text
                resume_Answer.visibility = View.VISIBLE
                extractedTextTextView.text = response.text

            } catch (e: Exception) {
                Log.e("analyzeResume", "Error while analyzing resume", e)
                extractedTextTextView.text = "Analysis failed: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

}
