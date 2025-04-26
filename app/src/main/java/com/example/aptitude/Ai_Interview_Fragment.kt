package com.example.aptitude

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.util.*

class Ai_Interview_Fragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var questionTextView: TextView
    private lateinit var answerTextView: TextView
    private lateinit var analysisTextView: TextView
    private lateinit var speakButton: Button
    private lateinit var nextButton: Button
    private lateinit var generateButton: Button
    private lateinit var query: EditText
    private lateinit var result1: TextView
    private lateinit var progressBar:ProgressBar
    private lateinit var hr:CardView
    private lateinit var technical_Questions:CardView
    private lateinit var resume:CardView
    private lateinit var user_topic:CardView


    private val SPEECH_REQUEST_CODE = 1

    private var questions = mutableListOf(
        "Tell me about yourself.",
        "What are your strengths?",
        "Why do you want this job?",
        "Describe a challenge you've overcome.",
        "Where do you see yourself in five years?"
    )

    private var currentQuestionIndex = 0
    private val userAnswers = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai__interview_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionTextView = view.findViewById(R.id.questionTextView)
        answerTextView = view.findViewById(R.id.answerTextView)
        analysisTextView = view.findViewById(R.id.analysisTextView)
        speakButton = view.findViewById(R.id.speakButton)
        nextButton = view.findViewById(R.id.nextButton)
        generateButton = view.findViewById(R.id.generateButton)
        query = view.findViewById(R.id.topicEditText)
        result1 = view.findViewById(R.id.result)
        progressBar=view.findViewById(R.id.progressBar)
         hr=view.findViewById(R.id.advanced)
         technical_Questions=view.findViewById(R.id.tech_Interview)
         resume=view.findViewById(R.id.resume)
         user_topic=view.findViewById(R.id.User_query)
        val robot_Image=view.findViewById<CardView>(R.id.robotImage)
        val searchBar=view.findViewById<LinearLayout>(R.id.searchBar)
        val robot_Image1=view.findViewById<CardView>(R.id.robot_image)
        val backprss=view.findViewById<ImageView>(R.id.backPress)
        //val query=view.findViewById<Button>(R.id.generateButton)

        backprss.setOnClickListener {
            activity?.onBackPressed()
        }

        tts = TextToSpeech(requireContext(), this)

        askQuestion()

        speakButton.setOnClickListener {

            startVoiceRecognition()
        }

        nextButton.setOnClickListener {

            moveToNextQuestion()
        }
        hr.setOnClickListener{
            robot_Image1.visibility=View.GONE
            robot_Image.visibility=View.VISIBLE
            hr.visibility=View.GONE
            technical_Questions.visibility=View.GONE
            resume.visibility=View.GONE
            user_topic.visibility=View.GONE


            analyzeResume(" final HR round questions for college placements")


        }
        technical_Questions.setOnClickListener{
            robot_Image.visibility=View.VISIBLE
            robot_Image1.visibility=View.GONE

            hr.visibility=View.GONE
            technical_Questions.visibility=View.GONE
            resume.visibility=View.GONE
            user_topic.visibility=View.GONE
            analyzeResume("Asked the questions from oops, Operating system , computer network , Sql , Database Management system ")


        }
        resume.setOnClickListener{
            robot_Image.visibility=View.VISIBLE
            robot_Image1.visibility=View.GONE

            hr.visibility=View.GONE
            technical_Questions.visibility=View.GONE
            resume.visibility=View.GONE
            user_topic.visibility=View.GONE

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, ResumeAnalysisFragment())
                .addToBackStack(null)
                .commit()
        }
        user_topic.setOnClickListener{
            searchBar.visibility=View.VISIBLE
            robot_Image.visibility=View.VISIBLE
            robot_Image1.visibility=View.GONE
            hr.visibility=View.GONE
            technical_Questions.visibility=View.GONE
            resume.visibility=View.GONE
            user_topic.visibility=View.GONE
        }

        generateButton.setOnClickListener {
            val topic = query.text.toString().trim()
            searchBar.visibility=View.GONE
            user_topic.visibility=View.GONE
            if (topic.isNotEmpty()) {
                analyzeResume(topic)
            }
        }
    }



    private fun askQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionTextView.text = question
            answerTextView.text = ""
            analysisTextView.text = ""
            tts.speak(question, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun moveToNextQuestion() {
        val answer = answerTextView.text.toString()
        userAnswers.add(answer)

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            askQuestion()
        } else {

            showFinalAIReport()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Log.e("Speech", "Speech recognition not supported", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0) ?: ""
            answerTextView.text = spokenText
        }
    }

    private fun showFinalAIReport() {
        questionTextView.text = "Generating AI Feedback..."
        speakButton.isEnabled = false
        nextButton.isEnabled = false
        speakButton.visibility=View.GONE
        nextButton.visibility=View.GONE

        progressBar.visibility=View.VISIBLE

        lifecycleScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash", // or your available model
                    apiKey = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"
                )

                val reportInput = buildString {
                    append("I recently participated in an interview and was asked the following questions. Please provide constructive feedback:\n\n")
                    for (i in questions.indices) {
                        val answer = userAnswers.getOrNull(i)
                        if (answer.isNullOrBlank()) {
                            append("${i + 1}. Q: ${questions[i]}\nStatus: Skipped or unanswered\n\n")
                        } else {
                            append("${i + 1}. Q: ${questions[i]}\nStatus: Answered\n\n")
                        }
                    }
                    append(
                        """
    I recently participated in an interview and was asked several questions. Below are the answers I gave, but please DO NOT repeat the questions or answers back in your response.

    Instead, just provide:
    - A structured summary of my strengths and weaknesses.
    - Which areas were strong or weak.
    - Tips to improve the weak areas.
    - Actionable suggestions for improvement.
    
    Please do NOT include the actual questions or answers in your response.
    Just provide the feedback in clean, plain text format.
    
     Do not include any special characters like *, #, ${'$'}, etc.
                    Just provide the plain questions.
    """.trimIndent()
                    )

                }


                val response = generativeModel.generateContent(reportInput)

                val responseText = response.text ?: "Could not generate feedback."
                analysisTextView.text = responseText
                tts.speak("Here is your AI feedback report.", TextToSpeech.QUEUE_FLUSH, null, null)
                answerTextView.visibility = View.GONE
                speakButton.visibility = View.GONE
                nextButton.visibility = View.GONE
                analysisTextView.visibility = View.VISIBLE
                analysisTextView.text = responseText
                hr.visibility=View.VISIBLE
                technical_Questions.visibility=View.VISIBLE
                user_topic.visibility=View.VISIBLE
                progressBar.visibility=View.GONE


            } catch (e: Exception) {
                progressBar.visibility=View.GONE
                analysisTextView.text = "Failed to generate AI feedback: ${e.message}"
                Log.e("FinalAIReport", "Error: ${e.message}", e)
            }
        }
    }

    private fun analyzeResume(topic: String) {
        progressBar.visibility=View.VISIBLE
        lifecycleScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"
                )

                val prompt = """
                    Provide 5 interview questions related to this topic: "$topic"
                    Format:
                    1. Question one
                    2. Question two
                    3. Question three
                    4. Question four
                    5. Question five
                    Do not include any special characters like *, #, $, etc.
                    Just provide the plain questions.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""

                result1.text = responseText

                // Extract questions
                val extractedQuestions = responseText
                    .split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { it.replace(Regex("^\\d+\\.\\s*"), "") }

                if (extractedQuestions.isNotEmpty()) {
                    questions.clear()
                    questions.addAll(extractedQuestions)
                    currentQuestionIndex = 0
                    userAnswers.clear()

                    questionTextView.visibility = View.VISIBLE
                    answerTextView.visibility = View.VISIBLE
                    speakButton.visibility = View.VISIBLE
                    nextButton.visibility = View.VISIBLE
                    progressBar.visibility=View.GONE

                    askQuestion()
                }

            } catch (e: Exception) {
                Log.e("ResumeAnalysis", "Error: ${e.message}", e)

            }
        }
    }

    override fun onDestroyView() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroyView()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }
}
