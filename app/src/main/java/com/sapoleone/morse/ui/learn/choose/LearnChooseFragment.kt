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

@file:Suppress("SameParameterValue", "PrivatePropertyName")

package com.sapoleone.morse.ui.learn.choose

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.MainActivity
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnChooseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Suppress("SameParameterValue", "SameParameterValue")
class LearnChooseFragment : Fragment() {
    //val db = Firebase.firestore
    private lateinit var binding: FragmentLearnChooseBinding

    private val ciphTxt = arrayOf("A",  "B",    "C",    "D",   "E", "F",    "G",   "H",    "I",  "J",    "K",   "L",    "M",  "N",  "O",   "P",    "Q",    "R",   "S",   "T", "U",   "V",    "W",   "X",    "Y",    "Z",    "CH")
    private val ciphMor = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", "----")

    private var alphabetLength = 0

    private var score = 0
    private var highScore = 0

    private var isFirstIteration = true

    private lateinit var session_id:String

    private var canBe = 0 //0: Both;  1: OnlyMorse;  2: OnlyText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnChooseBinding.inflate(inflater, container, false)

        binding.backFromChoose.setOnClickListener {
            findNavController().navigate(R.id.action_learnChooseFragment_to_learnChooseHomeFragment)
        }
        binding.sendSettingsChoose.setOnClickListener {
            changeCanBe()
        }
        /*binding.menu!!.setOnClickListener {
            (activity as MainActivity).menu()
        }*/

        alphabetLength = ciphMor.size
        println(ciphMor[25])
        gameMain()
        return binding.root
    }

    private fun updateScore() {
        println("start upScore")
        session_id = (requireActivity() as MainActivity).getSessionId()
        if(session_id != "_void_"){
            highScore = (requireActivity() as MainActivity).getScore("hc")
            println("  hs: $highScore")
            println("exit  upScore")
        } else {
            println("exit  upScore (session_id: _void_)")
        }
    }
    private fun gameMain(){
        val isPromptMorse : Boolean = when (canBe) {
            0 ->    {   isPromptMorse() }

            1 ->    {   true            }

            else -> {   false           }
        }
        if(isFirstIteration){
            updateScore()
        }
        //Generate the question(Prompt)
        val prompt : String = if (isPromptMorse){
            selectMorsePrompt()
        } 
        else{
            selectTextPrompt()
        }

        val index      : Int    = findPromptId (prompt, isPromptMorse)
        val antiPrompt : String = findAntiPrompt(index, isPromptMorse)
        //Generate the wrong answers
        val array = if (isPromptMorse){
            selectTextArray(antiPrompt)
        } 
        else{
            selectMorseArray(antiPrompt)
        }

        array[3] = antiPrompt
        printArray(array, prompt)

        if(isFirstIteration){
            isFirstIteration = false
        }
    }

    private fun findAntiPrompt(index: Int, isPromptMorse: Boolean): String {
        return if(isPromptMorse){
            ciphTxt[index]
        } else{
            ciphMor[index]
        }
    }

    private fun printWrong(array: Array<String>, prompt: String, rand: Int, waitTime: Long, btnCode: Int){
        print("Wrong, you selected: ")
        println(array[(btnCode + rand)%4])
        score = 0 //TODO: Stop the Game here!

        binding.isCorrectChoose.isVisible = true
        binding.isCorrectChoose.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
        binding.isCorrectChoose.text = buildString {
            //append(getString(R.string.wrong))
            append("  ")
            append(prompt)
            append("   ")
            append(array[3])
        }
        printScore()
        //aaa

        CoroutineScope(Dispatchers.Main).launch {
            delay(waitTime)
            binding.isCorrectChoose.isVisible = false
        }
    }

    private fun printCorrect(array: Array<String>, rand: Int, waitTime: Long, btnCode: Int){
        print("Correct, you selected: ")
        println(array[(btnCode + rand)%4])
        score += 100

        binding.isCorrectChoose.isVisible = true
        binding.isCorrectChoose.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_correct_background)
        binding.isCorrectChoose.text = getString(R.string.correct)
        printScore()

        CoroutineScope(Dispatchers.Main).launch {
            delay(waitTime)
            binding.isCorrectChoose.isVisible = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun printArray(array: Array<String>, prompt : String) {
        val rand = Random.nextInt(0, 10)
        val waitTime : Long = 1000
        val waitWrongTime : Long = 10000

        printScore()

        binding.choosePrompt.text = prompt

        binding.btnAns1.text = array[(0 + rand)%4]
        binding.btnAns2.text = array[(1 + rand)%4]
        binding.btnAns3.text = array[(2 + rand)%4]
        binding.btnAns4.text = array[(3 + rand)%4]

        binding.btnAns1.setOnClickListener {
            if((0 + rand) % 4 == 3){
                printCorrect(array, rand, waitTime, 0)
            }
            else{
                printWrong(array, prompt, rand, waitWrongTime, 0)
            }
            gameMain()
        }
        binding.btnAns2.setOnClickListener {
            if((1 + rand) % 4 == 3){
                printCorrect(array, rand, waitTime, 1)
            }
            else{
                printWrong(array, prompt, rand, waitWrongTime, 1)
            }
            gameMain()
        }
        binding.btnAns3.setOnClickListener {
            if((2 + rand) % 4 == 3){
                printCorrect(array, rand, waitTime, 2)
            }
            else{
                printWrong(array, prompt, rand, waitWrongTime, 2)
            }
            gameMain()
        }
        binding.btnAns4.setOnClickListener {
            if((3 + rand) % 4 == 3){
                printCorrect(array, rand, waitTime, 3)
            }
            else{
                printWrong(array, prompt, rand, waitWrongTime, 3)
            }
            gameMain()
        }
    }

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
        return -1
    }

    private fun selectTextPrompt(): String {
        val rand = Random.nextInt(0, 26)
        val prompt = ciphTxt[rand]
        print("Prompt selected: Text , ")
        print(rand)
        print(", ")
        println(prompt)
        return prompt
    }

    private fun selectMorsePrompt(): String {
        val rand = Random.nextInt(0, 26)
        val prompt = ciphMor[rand]
        print("Prompt selected: Morse, ")
        print(rand)
        print(", ")
        println(prompt)
        return prompt
    }

    private fun selectTextArray(antiPrompt: String): Array<String> {
        val array = arrayOf("", "", "", "")

        var i = 0
        while (i < 3){
            val rand = Random.nextInt(0, 26)
            val text = ciphTxt[rand]

            print("Prompt selected: Text , ")
            print(rand)
            print(", ")
            println(text)

            if(text in array){
                continue
            }
            if (text == antiPrompt){
                continue
            }

            array[i] = text
            i++
        }

        return array
    }

    private fun selectMorseArray(antiPrompt: String): Array<String> {
        val array = arrayOf("", "", "", "")

        var i = 0
        while (i < 3){
            val rand = Random.nextInt(0, 26)
            val text = ciphMor[rand]

            print("Prompt selected: Morse, ")
            print(rand)
            print(", ")
            println(text)

            if(text in array){
                continue
            }
            if (text == antiPrompt){
                continue
            }

            array[i] = text
            i++
        }

        return array
    }

    private fun isPromptMorse(): Boolean {

        return Random.nextBoolean()

    }

    private fun changeCanBe() {
        println("fun: changeCanBe")
        val tempCanBe = canBe
        if (binding.checkBoxMorse.isChecked && !(binding.checkBoxText.isChecked)){
            canBe = 1
            Toast.makeText(requireContext(), "Only Morse",          Toast.LENGTH_LONG).show()
        }
        if (!(binding.checkBoxMorse.isChecked) && binding.checkBoxText.isChecked){
            canBe = 2
            Toast.makeText(requireContext(), "Only Text",           Toast.LENGTH_LONG).show()
        }
        if (!(binding.checkBoxMorse.isChecked) && !(binding.checkBoxText.isChecked)){
            canBe = 2
            Toast.makeText(requireContext(), "Warn, Set only TEXT", Toast.LENGTH_LONG).show()
            binding.checkBoxText.isChecked = true
        }
        if(binding.checkBoxMorse.isChecked && binding.checkBoxText.isChecked){
            canBe = 0
            Toast.makeText(requireContext(), "Both",                Toast.LENGTH_LONG).show()
        }

        if (tempCanBe != canBe){
            gameMain()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun printScore(){
        Log.d("format_hs","Enter printScore")
        if (score >= highScore && score != 0){
            Log.d("format_hs","  Formatted! (HS: $highScore, S: $score)")
            binding.scoreboardChoose.isVisible = false
            binding.highScoreChoose!!.text = "New HighScore: $score"
            binding.highScoreChoose!!.setTextColor(Color.parseColor("#00FF00"))

        }
        else{
            Log.d("format_hs","  Formatted! (HS: $highScore, S: $score)")
            binding.highScoreChoose!!.text = "HighScore: $highScore"
            binding.scoreboardChoose.text = "Score: $score"
            binding.scoreboardChoose.isVisible = true
        }
        Log.d("format_hs","Exit  printScore")
    }

    override fun onDestroyView() {
        if(session_id != "_void_" && session_id != "null" && session_id != "" && score != 0 && score > highScore) {
            println("Set new score: $score")

            (activity as MainActivity).setScore(score, "hc")
        }
        super.onDestroyView()
    }
}