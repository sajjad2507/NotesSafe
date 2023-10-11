package com.example.notessafe.Fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import com.example.notessafe.R
import com.example.notessafe.databinding.FragmentLogInScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogInScreen : Fragment() {

    lateinit var binding: FragmentLogInScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLogInScreenBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.demo.setOnClickListener {

            val videoIdOrUrl = "oa1a49wb_go"
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$videoIdOrUrl")
            )

            // Ensure the YouTube app is used if installed, otherwise, use a web browser.
            intent.setPackage("com.google.android.youtube")

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the exception. For example, open the URL in a web browser.
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoIdOrUrl"))
                startActivity(webIntent)
            }

        }

        binding.loginButton.setOnClickListener {

            val sEmail = binding.editTextEmail.text.toString().trim()
            val sPass = binding.editTextPassword.text.toString().trim()

            auth.signInWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {

                        goToHome()

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        }

        binding.googleBtn.setOnClickListener {

            signInGoogle()

        }

        binding.logInRegister.setOnClickListener {


            NavHostFragment.findNavController(this)
                .navigate(R.id.action_logInScreen_to_registerScreen)

        }

        return binding.root
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            } else {
                Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                goToHome()
            } else {
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {

            goToHome()

        }
    }

    private fun goToHome() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_logInScreen_to_homeFragment)
    }

}