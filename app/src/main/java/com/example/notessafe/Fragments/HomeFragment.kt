package com.example.notessafe.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notessafe.Adapters.NotesAdapter
import com.example.notessafe.DataModle.NotesDataModel
import com.example.notessafe.R
import com.example.notessafe.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    private var backPressedTime: Long = 0
    private val doubleBackToExitPressedMessage = "Press back again to exit"
    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        MobileAds.initialize(requireContext()) {}
        auth = FirebaseAuth.getInstance()

        showBanner()

        filteredData("All")

        binding.logout.setOnClickListener {

            auth.signOut()
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_splashScreen)

        }

        binding.addNotes.setOnClickListener {

            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_editNotesScreen)

        }

        binding.filterAll.setOnClickListener {

            binding.filterAll.setTextColor(0xff9864f1.toInt())
            binding.filterStudy.setTextColor(0xffffffff.toInt())
            binding.filterWork.setTextColor(0xffffffff.toInt())
            binding.filterPersonal.setTextColor(0xffffffff.toInt())
            filteredData("All")

        }

        binding.filterPersonal.setOnClickListener {

            binding.filterAll.setTextColor(0xffffffff.toInt()) // Black
            binding.filterStudy.setTextColor(0xffffffff.toInt()) // Red
            binding.filterWork.setTextColor(0xffffffff.toInt()) // Green
            binding.filterPersonal.setTextColor(0xff9864f1.toInt()) // Blue
            filteredData("Personal")

        }

            binding.filterStudy.setOnClickListener {

                binding.filterAll.setTextColor(0xffffffff.toInt())
                binding.filterStudy.setTextColor(0xff9864f1.toInt())
                binding.filterWork.setTextColor(0xffffffff.toInt())
                binding.filterPersonal.setTextColor(0xffffffff.toInt())
                filteredData("Study")

            }

        binding.filterWork.setOnClickListener {

            binding.filterAll.setTextColor(0xffffffff.toInt())
            binding.filterStudy.setTextColor(0xffffffff.toInt())
            binding.filterWork.setTextColor(0xff9864f1.toInt())
            binding.filterPersonal.setTextColor(0xffffffff.toInt())
            filteredData("Work")

        }

        binding.homeSearch.setOnClickListener {

            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_searchScreen)

        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        requireActivity().finish()
                        return
                    } else {
                        Toast.makeText(
                            requireContext(),
                            doubleBackToExitPressedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            })

        return binding.root
    }

    private fun filteredData(filter: String) {

        db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val uId = user!!.uid

        db.collection("user").document(uId).collection("notes").addSnapshotListener { value, error ->

            val allNotes = arrayListOf<NotesDataModel>()
            var filteredList = arrayListOf<NotesDataModel>()
            val data = value?.toObjects(NotesDataModel::class.java)
            allNotes.addAll(data!!)
            
            if (filter == "All") {
                
                filteredList = allNotes
                
            } else if (filter == "Personal") {
                
                for (i in allNotes) {
                    
                    if (i.filter == "Personal") {
                        
                        filteredList.add(i)
                        
                    }
                    
                }
                
            } else if (filter == "Work") {

                for (i in allNotes) {

                    if (i.filter == "Work") {

                        filteredList.add(i)

                    }

                }

            } else if (filter == "Study") {

                for (i in allNotes) {

                    if (i.filter == "Study") {

                        filteredList.add(i)

                    }

                }

            } else {

                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                
            }

            binding.noteRcv.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.noteRcv.adapter =
                NotesAdapter(requireContext(),
                    filteredList, requireParentFragment())

        }

    }

    private fun showBanner() {
        mAdView = binding.adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

}