package com.example.aptitude

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Login : Fragment() {
    var RegisterListener: OnRegisterClickedListener? = null
    var QuizCategoriesListener: OnQuizeCategoriesClickedlistener? = null
    lateinit var register: Button
    lateinit var quizcategoriebtn: Button
    private lateinit var auth: FirebaseAuth
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginbtn: Button
    lateinit var forgotPasswordButton:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailEditText = view.findViewById(R.id.email)
        passwordEditText = view.findViewById(R.id.pasword)
        loginbtn = view.findViewById(R.id.loginbtn)
        forgotPasswordButton=view.findViewById(R.id.forgotPassword)

        register = view.findViewById(R.id.register)
        register.setOnClickListener {
            RegisterListener?.OnRegisterClicked()
        }

        quizcategoriebtn = view.findViewById(R.id.loginbtn)
        quizcategoriebtn.setOnClickListener {
            QuizCategoriesListener?.OnQuizCategoriesClicked()


        }


//        auth = FirebaseAuth.getInstance()
//        if (savedInstanceState == null) {
//            if (auth.currentUser != null) {
//                // User is signed in, navigate to Home Fragment
//                navigateToMainFragment()
//            } else {
//                // No user is signed in, navigate to Login Fragment
//                navigateToLoginFragment()
//            }
//        }

        loginbtn.setOnClickListener {
            loginUser()
        }

        forgotPasswordButton.setOnClickListener{
            navigateToForgotPasswordFragment()
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RegisterListener = context as OnRegisterClickedListener
        QuizCategoriesListener = context as OnQuizeCategoriesClickedlistener
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required."
            return
        } else {
//            if(email == this && password == this){
//                sendToNexFragment()
//            }else{
//                keepLoginFragment()
//            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user: FirebaseUser? = auth.currentUser
                        Toast.makeText(activity, "Authentication Success.", Toast.LENGTH_SHORT).show()
                        navigateToMainFragment()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            activity,
                            "Authentication Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required."
            return
        }

        // Authenticate user

    }


    private fun navigateToMainFragment() {
        val loginFragment = QuizCategoriesFragment() // Create an instance of the LoginFragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, loginFragment) // Replace the current fragment
        // Optional: add to back stack so the user can navigate back
        transaction.commit()
    }

    private fun navigateToLoginFragment() {
        val loginFragment = Login() // Create an instance of the LoginFragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, loginFragment) // Replace the current fragment
        transaction.addToBackStack(null) // Optional: add to back stack so the user can navigate back
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, navigate to the main fragment or activity
            navigateToMainFragment()
        }
    }

    private fun navigateToForgotPasswordFragment() { // New
        val forgotPasswordFragment = ForgotPasswordFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, forgotPasswordFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    interface OnRegisterClickedListener {
        fun OnRegisterClicked()
    }

    interface OnQuizeCategoriesClickedlistener {
        fun OnQuizCategoriesClicked()
    }


}