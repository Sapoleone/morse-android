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
@file:Suppress("SameParameterValue", "SameParameterValue", "SameParameterValue")

package com.sapoleone.morse.ui.learn.pair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnPairBinding
import kotlin.random.Random

@Suppress("SameParameterValue", "SameParameterValue")
class LearnPairFragment : Fragment() {

    private var _binding: FragmentLearnPairBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val learnPairViewModel = ViewModelProvider(this)[LearnPairViewModel::class.java]

        _binding = FragmentLearnPairBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backFromPair.setOnClickListener {
            findNavController().navigate(R.id.action_learnPairFragment_to_learnHomeFragment)
        }

        val bM1 = binding.btnM1
        val bM2 = binding.btnM2
        val bM3 = binding.btnM3
        val bM4 = binding.btnM4
        val bM5 = binding.btnM5

        val bT1 = binding.btnT1
        val bT2 = binding.btnT2
        val bT3 = binding.btnT3
        val bT4 = binding.btnT4
        val bT5 = binding.btnT5


        gameMain()
        arrMorse = initMorse()
        arrText = initTextFromMorse()
    }

    private val ciphTxt = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    private val ciphMor = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
    private var arrMorse = Array(4) { "" }
    private var arrText = Array(4) { "" }


    private fun findPromptId(prompt: String, isPromptMorse: Boolean): Int {

        for (i in 0..25){
            if(isPromptMorse){
                if(ciphMor[i] == prompt){
                    return i
                }
            }
            else{
                if(ciphTxt[i] == prompt){
                    return i
                }
            }
        }
        return 0
    }

    private fun initTextFromMorse(): Array<String> {
        val tempArrText = Array(4) { "" }
        var i = 0
        while (i < 4){
            val id = findPromptId(arrMorse[i], true)
            tempArrText[i] = ciphTxt[id]
            i++
        }
        return tempArrText
    }

    private fun initMorse(): Array<String> {
        val tempMorseArr = arrayOf("", "", "", "", "")
        for (i in 0..4){
            val rand = Random.nextInt(0, 25)
            if(isValInArr(tempMorseArr, ciphMor[rand])){
                tempMorseArr[i] = ciphMor[rand]
            }
        }
        tempMorseArr.shuffle()
        return tempMorseArr
    }

    /*private fun fillArr(valor: String) : Array<String> {
       var arr[]: Array<String>
       for (i in 0..4){
           arr[i] = valor
       }
        return arr
    }*/

    private fun isValInArr(arr: Array<String>, value: String): Boolean {
        for (i in arr){
            if(i == value){
                return true
            }
        }
        return false
    }

    private fun gameMain() {
        println("Now the fun. is full")
    }

}
