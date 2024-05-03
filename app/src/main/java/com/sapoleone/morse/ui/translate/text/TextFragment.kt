package com.sapoleone.morse.ui.translate.text

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentTextBinding

class TextFragment : Fragment() {

    private var _binding: FragmentTextBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //val textViewModel = ViewModelProvider(this)[TextViewModel::class.java]

        _binding = FragmentTextBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backFromText2Morse.setOnClickListener {
            findNavController().navigate(R.id.action_text2morse_to_translateHomeFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}