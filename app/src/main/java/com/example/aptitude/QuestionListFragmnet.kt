package com.example.aptitude

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.example.aptitude.databinding.DialogBoxLayoutBinding
import com.example.aptitude.databinding.FragmentQuestionListFragmnetBinding
import com.google.android.material.textview.MaterialTextView

class QuestionListFragmnet : Fragment(), View.OnClickListener {
    private val userAnswers = mutableListOf<String>()
    lateinit var backpress:ImageView

    companion object {
        var questionModelist: List<QuestionModel> = listOf()
        var time: String = ""
    }

    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0
    private lateinit var binding: FragmentQuestionListFragmnetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionListFragmnetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.option1.setOnClickListener(this)
        binding.option2.setOnClickListener(this)
        binding.option3.setOnClickListener(this)
        binding.option4.setOnClickListener(this)
        binding.nextbtn.setOnClickListener(this)
        binding.prevbtn.setOnClickListener(this)

        loadQuestion()
        startTimer()
        backpress=view.findViewById(R.id.back)
        backpress.setOnClickListener{
            activity?.onBackPressed()
        }
    }

    private fun startTimer() {
        val timeInMinutes = if (time.isNotEmpty()) time else "10"

        val totalTimeInMillis = try {
            timeInMinutes.toInt() * 60 * 1000L
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            1 * 60 * 1000L
        }

        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerindicator.text = String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {

            }
        }.start()
    }

    private fun loadQuestion() {
        selectedAnswer = ""

        if (questionModelist.isEmpty()) {
            return
        }

        if (currentQuestionIndex == questionModelist.size) {
            endOFQuestionList()
            return
        }
        resetOptionBackgrounds()

        val question = questionModelist[currentQuestionIndex]
        binding.questionindecator.text = "Question ${currentQuestionIndex + 1}/${questionModelist.size}"
        binding.qizeProgressBar.progress =
            (currentQuestionIndex.toFloat() / questionModelist.size.toFloat() * 100).toInt()
        binding.questionTextView.text = question.question
        binding.option1.text = question.option[0]
        binding.option2.text = question.option[1]
        binding.option3.text = question.option[2]
        binding.option4.text = question.option[3]


//        if (userAnswers.size > currentQuestionIndex) {
//            selectedAnswer = userAnswers[currentQuestionIndex]
//            highlightPreviousSelectedOption(selectedAnswer)
//        }

    }



    override fun onClick(view: View?) {
        val clickedButton = view as TextView

        when (clickedButton.id) {
            R.id.option1, R.id.option2, R.id.option3, R.id.option4 -> {
                highlightSelectedOption(clickedButton)
                selectedAnswer = clickedButton.text.toString()
               // userAnswers.add(selectedAnswer) // Add the selected answer to the list

            }
            R.id.nextbtn -> {
                if (currentQuestionIndex < userAnswers.size) {
                    userAnswers[currentQuestionIndex] = selectedAnswer
                } else {
                    if (currentQuestionIndex == userAnswers.size) {
                        userAnswers.add(selectedAnswer)
                    } else {
                        userAnswers.add("")
                    }
                }

                if (currentQuestionIndex < questionModelist.size && selectedAnswer == questionModelist[currentQuestionIndex].Correct) {
                    score++
                }

                currentQuestionIndex++
                loadQuestion()
                highlightPreviousSelectedOption()
            }

            R.id.prevbtn -> {
                if (currentQuestionIndex > 0) {
                    if (currentQuestionIndex < userAnswers.size) {
                        userAnswers[currentQuestionIndex] = selectedAnswer
                    } else {
                        userAnswers.add(selectedAnswer)
                    }

                    currentQuestionIndex--
                    loadQuestion()

                    selectedAnswer = if (currentQuestionIndex < userAnswers.size) {
                        userAnswers[currentQuestionIndex]
                    } else {
                        ""
                    }


                    highlightPreviousSelectedOption()
                }
            }
        }
    }


//    override fun onClick(view: View?) {
//        val clickedButton = view as Button
//
//        when (clickedButton.id) {
//            R.id.option1, R.id.option2, R.id.option3, R.id.option4 -> {
//                highlightSelectedOption(clickedButton)
//                selectedAnswer = clickedButton.text.toString()
//            }
//
//            R.id.nextbtn -> {
//                if (selectedAnswer == questionModelist[currentQuestionIndex].Correct) {
//                    score++
//                    Log.i("score", score.toString())
//                }
//
//                currentQuestionIndex++
//                loadQuestion()
//            }
//        }
//    }

    private fun endOFQuestionList() {
        val totalquestion = questionModelist.size
        val percentage = ((score.toFloat() / totalquestion.toFloat()) * 100).toInt()


        val dialogbinding = DialogBoxLayoutBinding.inflate(layoutInflater)
        dialogbinding.apply {
            scoreProgressBar.progress = percentage
            scorePercentage.text = "$percentage%"
            pass.text = if (percentage > 60) {
                pass.setTextColor(Color.BLACK)
                "Congrats! You Passed"
            } else {
                pass.setTextColor(Color.RED)
                "Oops! You Failed"
            }
            scoretext.text = "$score out of $totalquestion are correct"

        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogbinding.root)
            .setCancelable(false)
            .create()
        dialogbinding. finish.setOnClickListener {
           alertDialog?.dismiss()
           // activity?.onBackPressed()
            val resultFragment = ResultFragment()
            val bundle = Bundle()
            bundle.putStringArrayList("userAnswers", ArrayList(userAnswers)) // Pass the user's answers
            resultFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, resultFragment)
                .commit()

            // Close the activity
        }

         // Correctly create the dialog using .create()

        alertDialog.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun highlightSelectedOption(selectedButton: TextView) {
        resetOptionBackgrounds()


//        selectedButton.setBackgroundColor(R.color.nwdf)
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.nwdf))
    }

    private fun resetOptionBackgrounds() {
        // Reset all option backgrounds to default
//        binding.const1.setBackgroundResource(R.drawable.colour)
//        binding.const2.setBackgroundResource(R.drawable.colour)
//        binding.const3.setBackgroundResource(R.drawable.colour)
//        binding.const4.setBackgroundResource(R.drawable.colour)

        val defaultColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.option1.setTextColor(defaultColor)
        binding.option2.setTextColor(defaultColor)
        binding.option3.setTextColor(defaultColor)
        binding.option4.setTextColor(defaultColor)
    }


    private fun highlightPreviousSelectedOption() {
        resetOptionBackgrounds()

        when (selectedAnswer) {
            binding.option1.text.toString() -> binding.option1.setTextColor(ContextCompat.getColor(requireContext(), R.color.nwdf))
            binding.option2.text.toString() -> binding.option2.setTextColor(ContextCompat.getColor(requireContext(), R.color.nwdf))
            binding.option3.text.toString() -> binding.option3.setTextColor(ContextCompat.getColor(requireContext(), R.color.nwdf))
            binding.option4.text.toString() -> binding.option4.setTextColor(ContextCompat.getColor(requireContext(), R.color.nwdf))
        }
    }

}
