package com.sapoleone.morse.ui.learn.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnPairBinding

class LearnPairFragment : Fragment() {

    private lateinit var binding: FragmentLearnPairBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnPairBinding.inflate(inflater, container, false)

        binding.backFromPair.setOnClickListener {
            findNavController().navigate(R.id.action_learnPairFragment_to_learnHomeFragment)
        }

        return binding.root
    }

}