package com.sapoleone.morse.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnBinding

class LearnHomeFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val learnViewModel =
            ViewModelProvider(this)[LearnHomeViewModel::class.java]

        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Buttons
            binding.gotoLearn2Choose.setOnClickListener{
                findNavController().navigate(R.id.action_learnHomeFragment_to_learnChooseFragment)
            }
            binding.gotoLearn2Pair.setOnClickListener {
                println("CIAO, pair")
                findNavController().navigate(R.id.action_learnHomeFragment_to_learnPairFragment)
            }
            binding.gotoLearn2Type.setOnClickListener {
                findNavController().navigate(R.id.action_learnHomeFragment_to_learnTypeFragment)
            }
            //BACK button
            binding.backFromLearnHome.setOnClickListener {
                findNavController().navigate(R.id.appHome)
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
