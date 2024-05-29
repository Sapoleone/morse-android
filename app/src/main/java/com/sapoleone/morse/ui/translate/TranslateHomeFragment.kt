/*This file is part of morseApp.

 *morseApp is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License version 3
 *published by the Free Software Foundation

 *morseApp is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.

 *You should have received a copy of the GNU General Public License
 *along with morseApp.  If not, see <http://www.gnu.org/licenses/>.*/

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