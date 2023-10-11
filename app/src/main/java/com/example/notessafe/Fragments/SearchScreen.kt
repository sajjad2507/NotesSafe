package com.example.notessafe.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notessafe.Adapters.NotesAdapter
import com.example.notessafe.DataModle.NotesDataModel
import com.example.notessafe.databinding.FragmentSearchScreenBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchScreen : Fragment() {

    lateinit var binding: FragmentSearchScreenBinding
    lateinit var db: FirebaseFirestore
    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchScreenBinding.inflate(layoutInflater, container, false)

        MobileAds.initialize(requireContext()) {}

        showBanner()

        showNotes("")

        // Add a TextWatcher to the customSearchView
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called as the text changes
                val query = s.toString()
                showNotes(query)
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text changes
            }
        })

        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            // Handle the search icon click event here
            showNotes(query)
        }

        return binding.root
    }

    private fun showNotes(filter: String) {


        db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val uId = user!!.uid

        db.collection("user").document(uId).collection("notes")
            .addSnapshotListener { value, error ->

                val allNotes = arrayListOf<NotesDataModel>()
                var filteredList = arrayListOf<NotesDataModel>()
                val data = value?.toObjects(NotesDataModel::class.java)
                allNotes.addAll(data!!)


                // Add notes to filter list
                if (filter.isBlank()) {
                    // If the filter is blank, show all notes
                    filteredList = allNotes
                } else {
                    for (note in allNotes) {
                        val titleContainsQuery = note.title.contains(filter, ignoreCase = true)
                        val notesContainsQuery = note.notes.contains(filter, ignoreCase = true)

                        if (titleContainsQuery || notesContainsQuery) {
                            filteredList.add(note)
                        }
                    }

                }

                binding.searchRcv.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                binding.searchRcv.adapter =
                    NotesAdapter(
                        requireContext(),
                        filteredList, requireParentFragment()
                    )


            }

    }


    private fun showBanner() {
        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

}