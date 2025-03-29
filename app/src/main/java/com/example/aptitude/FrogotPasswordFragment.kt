package com.example.aptitude

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var resetPasswordButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var backpress:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frogot_password, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton)
        auth = FirebaseAuth.getInstance()

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                emailEditText.error = "Email is required"
                return@setOnClickListener
            }

            // Send password reset email
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Password reset email sent. After resetting, please return to the app and log in with your new password.", Toast.LENGTH_LONG).show()
                        navigateToLoginFragment()
                    } else {
                        Toast.makeText(
                            activity,
                            "Failed to send reset email: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backpress=view.findViewById(R.id.backLoginFragment)
        backpress.setOnClickListener{
            activity?.onBackPressed()
        }
    }

    private fun navigateToLoginFragment() {
        val loginFragment = Login() // Replace with your Login fragment
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, loginFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
