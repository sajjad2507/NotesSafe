package com.example.notessafe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.notessafe.R
import com.example.notessafe.databinding.FragmentRegisterScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterScreen : Fragment() {

    lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterScreenBinding.inflate(layoutInflater, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.registerBtn.setOnClickListener {

            val eMail = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            auth.createUserWithEmailAndPassword(eMail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                        goToLogIn()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.registerLogIn.setOnClickListener {

            NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerScreen_to_logInScreen)

        }

        return binding.root
    }

    private fun goToLogIn() {
        NavHostFragment.findNavController(this).navigate(R.id.action_registerScreen_to_logInScreen)
    }

}