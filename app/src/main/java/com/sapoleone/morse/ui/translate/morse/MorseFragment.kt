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
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentMorseBinding


class MorseFragment : Fragment() {

    private var _binding: FragmentMorseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val morseViewModel =
            ViewModelProvider(this)[MorseViewModel::class.java]

        _binding = FragmentMorseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.backFromMorse2Text.setOnClickListener {
            println("Btn 2")
            findNavController().navigate(R.id.action_morse2text_to_translateHomeFragment)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*//Var declaration
    var mTxt = ""


    fun decode(view: View?){
        //Init
        var i = 0
        var out = ""
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
        //Cycle through the whole PHRASE
        while (i<mTxt.length){
            var let = ""

            //Cycle through the LETTER
            while(mTxt[i] == '/'){
                let += mTxt[i]
                i++
            }
            i++

            //Check if the WORD is ended
            if(mTxt[i] == '/'){
                let += " "
            }

            //Conversion from MORSE to LETTERS
            //NOTE: How to use a for?; Using a WHILE instead
            var j = 0               //@FOR initialization
            while (j < 26){         //@FOR condition
                if(let == ciph2[j]){
                    out += ciph1[j]
                    break
                }
                j++                 //@FOR increment
            }
        }

        println(out)
    }

    fun dashClick(view: View?) {
        //println("DASH")
        mTxt += "-"
        println(mTxt)
    }
    fun dotClick(view: View?) {
        //println("DOT")
        mTxt += "."
        println(mTxt)
    }
    fun slashClick(view: View?) {
        //println("SLASH")
        mTxt += "/"
        println(mTxt)
    }
    fun cancClick(view: View?) {
        //println("CANC")
        mTxt = mTxt.dropLast(1)
        println(mTxt)
    }*/
}


