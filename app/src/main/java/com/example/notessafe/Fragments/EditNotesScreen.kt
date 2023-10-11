package com.example.notessafe.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.notessafe.R
import com.example.notessafe.databinding.FragmentEditNotesScreenBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditNotesScreen : Fragment() {

    lateinit var binding: FragmentEditNotesScreenBinding
    private var selectedSpinnerValue: String? = null
    lateinit var db: FirebaseFirestore
    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditNotesScreenBinding.inflate(layoutInflater, container, false)


        db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid.toString()

        MobileAds.initialize(requireContext()) {}
        loadIntertialAds()

        showBanner()

        val nTitle = arguments?.getString("nTitle").toString()
        val nNotes = arguments?.getString("nNotes").toString()
        val nFilter = arguments?.getString("nFilter").toString()
        val checker = arguments?.getString("checker").toString()

        val options: List<String> = listOf("Personal", "Study", "Work", "Others")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = adapter

        // Set the default selection (e.g., select the second option)
        val defaultSelectionIndex = 0
        binding.filterSpinner.setSelection(defaultSelectionIndex)

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSpinnerValue = parent?.getItemAtPosition(position).toString()
                // Do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }

        if (checker == "true") {


            if (nTitle != null || nNotes != null) {


                binding.edtTitle.setText(nTitle)
                binding.edtNotes.setText(nNotes)

                if (nFilter == "Personal") {

                    val defaultSelectionIndex = 0
                    binding.filterSpinner.setSelection(defaultSelectionIndex)

                } else if (nFilter == "Study") {

                    val defaultSelectionIndex = 1
                    binding.filterSpinner.setSelection(defaultSelectionIndex)

                } else if (nFilter == "Work") {

                    val defaultSelectionIndex = 2
                    binding.filterSpinner.setSelection(defaultSelectionIndex)

                } else if (nFilter == "Others") {

                    val defaultSelectionIndex = 3
                    binding.filterSpinner.setSelection(defaultSelectionIndex)

                }
            }

        }

        binding.saveBtn.setOnClickListener {

            val title = binding.edtTitle.text.toString()
            val filter = selectedSpinnerValue.toString()
            val notes = binding.edtNotes.text.toString()
            val userId = uid

            if (title.isEmpty() || notes.isEmpty()) {
                Toast.makeText(requireContext(), "Title & Note are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Create a mutable map to hold the data
                val data = mutableMapOf(
                    "title" to title,
                    "filter" to filter,
                    "notes" to notes
                )

                // Use the document reference to update or create the note
                val noteRef =
                    db.collection("user").document(userId).collection("notes").document(title)

                noteRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            // The document already exists, update it
                            noteRef.update(data as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Data is updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    NavHostFragment.findNavController(this)
                                        .navigate(R.id.action_editNotesScreen_to_homeFragment)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to update data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            // The document does not exist, create it
                            noteRef.set(data)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Data is saved successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showIntertialAds()
                                    NavHostFragment.findNavController(this)
                                        .navigate(R.id.action_editNotesScreen_to_homeFragment)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to save data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
            }
        }


        binding.backBtn.setOnClickListener {

            NavHostFragment.findNavController(this)
                .navigate(R.id.action_editNotesScreen_to_homeFragment)

        }

        binding.deleteBtn.setOnClickListener {

            val title = binding.edtTitle.text.toString()
            val userId = uid


            val dialog = BottomSheetDialog(requireContext())

            val view = layoutInflater.inflate(R.layout.fragment_delete_bottom_menu, null)

            val deleteBtn = view.findViewById<Button>(R.id.deleteButton)
            val cancleBtn = view.findViewById<Button>(R.id.cancelButton)

            deleteBtn.setOnClickListener {

                db.collection("user").document(userId).collection("notes").document(title).delete()
                    .addOnSuccessListener {

                        Toast.makeText(requireContext(), "Data Deleted Successfully!", Toast.LENGTH_SHORT).show()
                        NavHostFragment.findNavController(this)
                            .navigate(R.id.action_editNotesScreen_to_homeFragment)

                    }
                    .addOnFailureListener {

                        Toast.makeText(requireContext(), "Deletion Cancled", Toast.LENGTH_SHORT).show()

                    }

                dialog.dismiss()

            }

            cancleBtn.setOnClickListener {

                dialog.dismiss()

            }


            dialog.setCancelable(false)


            dialog.setContentView(view)


            dialog.show()
        }

        return binding.root
    }


    private fun loadIntertialAds() {

        var adRequestI = AdRequest.Builder().build()

        InterstitialAd.load(
            requireActivity(),
            "ca-app-pub-4678475583179673/6293925199",
            adRequestI,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    // Show the ad after a delay (for example, 2 seconds)
                    Handler(Looper.getMainLooper()).postDelayed({
                        showIntertialAds()
                    }, 3000)
                }
            })
    }

    private fun showIntertialAds() {


        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        } else {
        }
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
            }

            override fun onAdImpression() {
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
    }

    private fun showBanner() {
        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

}