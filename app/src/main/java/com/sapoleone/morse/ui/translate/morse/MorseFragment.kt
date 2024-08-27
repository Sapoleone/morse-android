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

package com.sapoleone.morse.ui.translate.morse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.MainActivity
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentMorseBinding


class MorseFragment : Fragment() {

    private var _binding: FragmentMorseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var mTxt = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val morseViewModel =
            ViewModelProvider(this)[MorseViewModel::class.java]

        _binding = FragmentMorseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backFromMorse2Text.setOnClickListener {
            println("Btn 2")
            findNavController().navigate(R.id.action_morse2text_to_translateHomeFragment)
        }

        binding.enterBt!!.setOnClickListener {
            (requireActivity() as MainActivity).decode(mTxt)
            mTxt = "";
        }

        binding.dashBt.setOnClickListener {
            //println("DASH")
            mTxt += "-"
            println(mTxt)
            (requireActivity() as MainActivity).printAndChange(mTxt)

        }

        binding.dotBt.setOnClickListener {
            //println("DOT")
            mTxt += "."
            println(mTxt)
            (requireActivity() as MainActivity).printAndChange(mTxt)
        }

        binding.slashBt.setOnClickListener {
            //println("SLASH")
            mTxt += "/"
            println(mTxt)
            (requireActivity() as MainActivity).printAndChange(mTxt)
        }

        binding.delBt.setOnClickListener {
            //println("CANC")
            mTxt = mTxt.dropLast(1)
            (requireActivity() as MainActivity).printAndChange(mTxt)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


