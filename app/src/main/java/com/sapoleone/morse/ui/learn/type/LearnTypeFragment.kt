package com.sapoleone.morse.ui.learn.type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnTypeBinding
import kotlin.random.Random

class LearnTypeFragment : Fragment() {

    private lateinit var binding: FragmentLearnTypeBinding
    private lateinit var viewModel: LearnTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLearnTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[LearnTypeViewModel::class.java]
        typeGameMain()

        binding.backFromType.setOnClickListener {
            findNavController().navigate(R.id.action_learnTypeFragment_to_learnHomeFragment)
        }

    }

    private fun selectPrompt(): String {
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")

        val rand = Random.nextInt(0, 50)
        var prompt = ""
        print("Rand: ")
        println(rand)
        if (rand <= 25){
            prompt = ciph1[rand]
            print("Prompt selected: Text , ciph1, ")
            print(rand)
            print(", ")
        }
        else{
            prompt = ciph2[rand - 25]
            print("Prompt selected: Morse, ciph2, ")
            print(rand-25)
            print(", ")
        }
        println(prompt)
        return prompt
    }

    private fun printPrompt(prompt : String){
        //val promptOutput = binding.typePrompt
        //promptOutput.text = prompt
        binding.typePrompt.text = prompt
        println(prompt)
    }

    private fun verifyInput(/*input: TextInputEditText,*/ prompt: String, index : Int, isMorse : Boolean) : Boolean {
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")

        val isCorrect : Boolean
        print("Index: ")
        println(index)

        isCorrect = if (!isMorse){
            if (ciph2[index] == prompt){
                true
            } else{
                false
            }
        } else{
            if (ciph1[index] == prompt){
                true
            } else{
                false
            }
        }

        return isCorrect
    }

    private fun waitForInput(prompt : String) : TextInputEditText {
        val input = binding.typeInput
        binding.typeInput.setOnClickListener {
            val index  = findPromptIndex(prompt)
            val isMorse = findPromptIsMorse(prompt)
            val result = verifyInput(/*input,*/ prompt, index, isMorse)
            if(result){
                println("Correct")
            }
            else{
                println("Wrong")
            }
            typeGameMain()
        }
        return input
    }

    private fun findPromptIsMorse(prompt: String): Boolean {
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")

        var i = 0
        while(i <= 50){
            if (i <= 25){
                if(prompt == ciph1[i]){
                    return true
                }
            }
            else{
                if(prompt == ciph2[i-25]){
                    return false

                }
            }
            i++
        }
        return false
    }

    private fun findPromptIndex(prompt: String): Int {
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
        val iTemp = -1

        var i = 0
        while(i <= 50){
            if (i <= 25){
                if(prompt == ciph1[i]){
                    return i
                }
            }
            else{
                if(prompt == ciph2[i-25]){
                    return i-25

                }
            }
            i++
        }
        return iTemp
    }


    private fun typeGameMain(){
        //while (true){
            val prompt = selectPrompt()
            printPrompt(prompt)
            waitForInput(prompt)
        //}
    }
}