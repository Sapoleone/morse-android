package com.sapoleone.morse.ui.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentTranslateHomeBinding

class TranslateHomeFragment : Fragment() {

    private var _binding: FragmentTranslateHomeBinding? = null
    private val binding get() = _binding!!

    //private lateinit var viewModel: TranslateHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTranslateHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val translateHomeViewModel =
            ViewModelProvider(this)[TranslateHomeViewModel::class.java]

        binding.gotoM2T.setOnClickListener{
            println("Button pressed!")
            findNavController().navigate(R.id.action_translateHomeFragment_to_morse2text)
        }
        binding.gotoT2M.setOnClickListener {
            println("Button pressed!")
            findNavController().navigate(R.id.action_translateHomeFragment_to_text2morse)
        }
        //BACK button
        binding.backFromConv.setOnClickListener {
            findNavController().navigate(R.id.appHome)
        }
        return root
    }
}