package com.example.aptitude

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Register : Fragment() {
    lateinit var backpress: Button
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var confirmPassword: EditText
    private lateinit var auth: FirebaseAuth
    lateinit var register: Button
    var RegisterListener: OnRegisterClickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = view.findViewById(R.id.EnterEmail)
        password = view.findViewById(R.id.EnterPassword)
        confirmPassword = view.findViewById(R.id.ConfirmPassword)
        register = view.findViewById(R.id.RegisterButton)
        backpress = view.findViewById(R.id.backpessLogin)

        backpress.setOnClickListener {
            activity?.onBackPressed()
        }

        register.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()
        val confirmPasswordText = confirmPassword.text.toString().trim()
        if (TextUtils.isEmpty(emailText)) {
            email.error = "Email is required."
            return
        }
        if (!isValidEmail(emailText)) {
            email.error = "Enter a valid email address."
            return
        }

        if (TextUtils.isEmpty(passwordText)) {
            password.error = "Password is required."
            return
        }

        if (passwordText != confirmPasswordText) {
            confirmPassword.error = "Passwords do not match."
            return
        }

        // Create a new user
        auth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(activity, "Registration Success.", Toast.LENGTH_SHORT).show()
                    sendVerificationEmail()
                    navigateToLoginFragment()
                } else {
                    Toast.makeText(activity, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLoginFragment() {
        
            val homeFragment = Login()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragment, homeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        
    }


    private fun sendVerificationEmail() {
        val user: FirebaseUser? = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Verification email sent. Please verify your email.", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    navigateToLoginFragment()
                } else {
                    Toast.makeText(activity, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRegisterClickedListener) {
            RegisterListener = context
        } else {
            throw RuntimeException("$context must implement OnRegisterClickedListener")
        }
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    interface OnRegisterClickedListener {
        fun OnRegisterclicked()
    }
}
