package com.example.aptitude

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aptitude.databinding.ResultRecyclerviewLayoutBinding

class ResultAdapter(private val questionlist:List<QuestionModel>,
                    private val userAnswers: List<String>?):RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ResultRecyclerviewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return questionlist.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val question = questionlist[position]
        val userAnswer = if (position < userAnswers!!.size) {
            Log.d("werwer", "onBindViewHolder:$userAnswers ")


            userAnswers[position]
        } else {
            "No Answer"
        }
        holder.bind(question, userAnswer)
    }


    class ResultViewHolder( private val binding: ResultRecyclerviewLayoutBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionModel, userAnswer: String) {
            binding.questionTextView.text = question.question
            binding.userAnswerTextView.text = "Your Answer: $userAnswer"
            binding.correctAnswerTextView.text = "Correct Answer: ${question.Correct}"

            if (userAnswer == question.Correct) {
                binding.userAnswerTextView.setTextColor(binding.root.context.getColor(R.color.nwdf))
            } else {
                binding.userAnswerTextView.setTextColor(binding.root.context.getColor(R.color.Red))
            }
        }


    }
}