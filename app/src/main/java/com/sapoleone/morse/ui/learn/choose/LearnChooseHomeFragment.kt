package com.sapoleone.morse.ui.learn.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnChooseHomeBinding

class LearnChooseHomeFragment : Fragment() {

    private var _binding: FragmentLearnChooseHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnChooseHomeBinding.inflate(inflater, container, false)

        binding.gotoStdChoose.setOnClickListener {
            findNavController().navigate(R.id.action_learnChooseHomeFragment_to_learnChooseFragment)
        }
        binding.gotoInfiniChoose.setOnClickListener {
            findNavController().navigate(R.id.action_learnChooseHomeFragment_to_infiniChooseFragment)
        }
        binding.BackFromChooseHome.setOnClickListener {
            findNavController().navigate(R.id.action_learnChooseHomeFragment_to_learnHomeFragment)
        }
        return binding.root
    }
}