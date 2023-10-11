package com.example.notessafe.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.notessafe.R
import com.example.notessafe.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : Fragment() {

    lateinit var binding: FragmentSplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({

            if (currentUser != null) {

                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splashScreen_to_homeFragment)
            } else {

                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splashScreen_to_logInScreen)
            }


        }, 3000)

        return binding.root
    }
}