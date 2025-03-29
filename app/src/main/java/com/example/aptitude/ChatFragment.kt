package com.example.aptitude

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope

import com.google.ai.client.generativeai.GenerativeModel

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ChatFragment : Fragment() {
    lateinit var backpress:ImageView






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_chat, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userText=view.findViewById<EditText>(R.id.userEditText)
        val btnSumbit=view.findViewById<Button>(R.id.sender)
        val apiResult=view.findViewById<TextView>(R.id.ApiResult)
        val progressBar=view.findViewById<ProgressBar>(R.id.progressBar)
        backpress=view.findViewById(R.id.backpress)
        backpress.setOnClickListener{

        }

//        btnSumbit.setOnClickListener{
//            val result=userText.text.toString()
//            progressBar.visibility = View.VISIBLE
//
//            val generativeMOdel= GenerativeModel(
//                modelName="gemini-pro",
//                apiKey= "AIzaSyD3z4jYXHVFKR2Tcx57-dwcd0UUGwbxn-E"
//            )
//            runBlocking {
//                val response=generativeMOdel.generateContent(result)
//                apiResult.text=response.text
//               progressBar.visibility = View.GONE
//
//            }
//        }
        btnSumbit.setOnClickListener {
            val query = userText.text.toString()
            progressBar.visibility = View.VISIBLE


            lifecycleScope.launch {
                try {

                    val generativeModel = GenerativeModel(
                        modelName = "gemini-2.0-flash",
                        apiKey = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"
                    )

                    // Perform the API call
                    val response = generativeModel.generateContent(query)


                    apiResult.text = response.text
                } catch (e: Exception) {

                    apiResult.text = "Error: ${e.message}"
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }









}