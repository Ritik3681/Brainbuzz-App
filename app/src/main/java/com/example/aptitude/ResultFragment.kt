package com.example.aptitude

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aptitude.databinding.FragmentResultBinding


class ResultFragment : Fragment() {
    lateinit var homeBackpress:Button

    private lateinit var binding: FragmentResultBinding
    private lateinit var resultAdapter: ResultAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val questionList = QuestionListFragmnet.questionModelist
        val userAnswers = getArguments()?.getStringArrayList("userAnswers")


        resultAdapter = ResultAdapter(questionList, userAnswers)
        binding.resultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resultRecyclerView.adapter = resultAdapter

        homeBackpress=view.findViewById(R.id.backpress)
        homeBackpress.setOnClickListener  {
            navigateToMainFragment()

        }
    }

    private fun navigateToMainFragment() {
        val loginFragment = QuizCategoriesFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, loginFragment)
        transaction.commit()
    }



}