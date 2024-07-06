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
@file:Suppress("SameParameterValue", "KotlinConstantConditions", "LocalVariableName")

package com.sapoleone.morse.ui.learn.type

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnTypeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Suppress("SameParameterValue")
class LearnTypeFragment : Fragment() {

    private lateinit var binding: FragmentLearnTypeBinding
    private val ciphTxt = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    private val ciphMor = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
    private var score:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLearnTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeGameMain()

        binding.backFromType.setOnClickListener {
            findNavController().navigate(R.id.action_learnTypeFragment_to_learnHomeFragment)
        }

    }



    private fun printWrong(waitTime: Long){
        score -= 50

        binding.isCorrectType.isVisible = true
        binding.isCorrectType.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
        binding.isCorrectType.text = getString(R.string.wrong)

        CoroutineScope(Dispatchers.Main).launch {
            delay(waitTime)
            binding.isCorrectType.isVisible = false
        }
    }

    private fun printCorrect(waitTime: Long){
        score += 100

        binding.isCorrectType.isVisible = true
        binding.isCorrectType.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_correct_background)
        binding.isCorrectType.text = getString(R.string.correct)

        CoroutineScope(Dispatchers.Main).launch {
            delay(waitTime)
            binding.isCorrectType.isVisible = false
        }
    }
    private fun selectPrompt(): String {
        val rand = Random.nextInt(0, 50)
        var prompt = ""
        print("Rand: ")
        println(rand)
        if (rand <= 25){
            prompt = ciphTxt[rand]
            print("Prompt selected: Text , ciphTxt, ")
            print(rand)
            print(", ")
        }
        else{
            prompt = ciphMor[rand - 25]
            print("Prompt selected: Morse, ciphMor, ")
            print(rand-25)
            print(", ")
        }
        println(prompt)
        return prompt
    }

    @SuppressLint("SetTextI18n")
    private fun printPrompt(prompt : String){
        //val promptOutput = binding.typePrompt
        //promptOutput.text = prompt
        binding.typePrompt.text = prompt
        binding.scoreboardType!!.text = "Score: $score"
        println(prompt)
    }

    private fun verifyInput(prompt: String, inputIndex : Int, isMorse : Boolean) : Boolean {
        var isCorrect = false

        val debug_string = buildString {
            append("Prompt: ")
            append(prompt)
            append("inputIndex: ")
            append(inputIndex)
            append("isMorse: ")
            append(isMorse)
        }

        println(debug_string)

        if (!isMorse && prompt == ciphMor[inputIndex]){
            isCorrect = true
        }

        if ( isMorse && prompt == ciphTxt[inputIndex]){
            isCorrect = true
        }

        return isCorrect
    }

    private fun waitForInput(prompt : String) : String {
        val input:String = binding.typeInput.toString()

        binding.typeInput.setOnClickListener {
            val index  = findPromptIndex(prompt)
            val isMorse = findPromptIsMorse(prompt)
            val inputIndex  = findPromptIndex(input)
            if(inputIndex != -1){
                val result = verifyInput(/*input,*/ prompt, inputIndex, isMorse)
                if(result){
                    println("Correct")
                    printCorrect(1000)
                }
                else{
                    println("Wrong")
                    printWrong(1000)
                }
            } else {
               /* println("Cos'hai scritto?")
               printWrong(1000)*/
            }
            typeGameMain()
        }
        return input
    }

    private fun findPromptIsMorse(prompt: String): Boolean {
        var i = 0
        while(i <= 50){
            if (i <= 25){
                if(prompt == ciphTxt[i]){
                    return true
                }
            }
            else{
                if(prompt == ciphMor[i-25]){
                    return false

                }
            }
            i++
        }
        return false
    }

    private fun findPromptIndex(prompt: String): Int {
        var halt = 0
        var i = 0
        while(i < ciphMor.size && halt == 0){

            val cTxtI = ciphTxt[i]
            val cMorI = ciphMor[i]
            Log.w("tag", "CICLO 1   prompt:$prompt, ciphTxt:$cTxtI, ciphTxt:$cMorI, i:$i, halt:$halt")

            if(prompt == ciphTxt[i]){
                Log.w("tag", "$prompt == $cTxtI")
                halt = 1
                break
            } else {
                Log.w("tag", "$prompt != $cTxtI")
            }
            i++
        }
        i = 0
        while(i < ciphTxt.size && halt == 0){
            val cTxtI = ciphTxt[i]
            val cMorI = ciphMor[i]
            Log.w("tag", "CICLO 2   prompt:$prompt, ciphTxt:$cTxtI, ciphTxt:$cMorI, i:$i, halt:$halt")

            if(prompt == ciphMor[i]){
                Log.w("tag", "$prompt == $cMorI")
                halt = 1
                break
            } else{
                Log.w("tag", "$prompt != $cMorI")
            }
            i++
        }
        return if(halt == 0){
            Log.w("tag", "returning -1")
            -1
        } else{
            Log.w("tag", "returning $i")
            i
        }

    }

    private fun typeGameMain(){
        //while (true){
            val prompt = selectPrompt()
            printPrompt(prompt)
            waitForInput(prompt)
        //}
    }
}