@file:Suppress("SameParameterValue")

package com.sapoleone.morse.ui.learn.words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentLearnWordsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class LearnWordsFragment : Fragment() {
	private var numberBack = -2
	private var _binding: FragmentLearnWordsBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentLearnWordsBinding.inflate(inflater, container, false)

		binding.backFromLearnWords.setOnClickListener{
			findNavController().navigate(R.id.action_learnHomeFragment_to_learnWordsFragment)
		}

		game()
		return binding.root
	}
	private fun game() {
		val textOutput = binding.outputLearnWord

		val parolaCasuale = getString()
		val parolaMorse: String = encode(parolaCasuale)
		print("Traduci dal morse: ")
		textOutput.text = parolaMorse

		binding.learnWordsInput.setOnClickListener{
			val risposta = (binding.learnWordsInput.text).toString()
			if (normalize(risposta) == normalize(parolaCasuale)) {
				println("Congratulazioni!")
				printCorrect(2000)
			} else {
				println("La parola corretta era: $parolaCasuale")
				printWrong(2000, parolaCasuale)
			}
			binding.learnWordsInput.setText("")
			game()
		}

	}

	private fun getString(): String {
		val paroleScout = arrayOf(
			"Avventura", "Esplorazione", "Bussola", "Tenda", "Scoutismo",
			"Caccia", "Uguaglianza", "Natura", "Bivacco", "Sentiero",
			"Branco", "Campo", "Fuoco", "Promessa", "Legge", "Squadriglia",
			"Uniforme", "Nodo", "Orientamento", "Simbolo", "Servizio",
			"Condivisione", "Rispettare", "Coraggio", "Nord", "Missione",
			"Grande Gioco", "Gara di cucina", "Il capo supremo Tony",
			"Brevetto", "Specialita"
		)
		var number: Int
		do {
			number = Random.nextInt(0, paroleScout.size)
		} while (number == numberBack)
		numberBack = number
		return paroleScout[number]
	}

	private fun encode(txt: String): String {

		//Init
		var out = ""
		val ciph1 = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
		val ciph2 = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
		val ciph3 = arrayOf(".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--..")

		//Direct Translation
		for (i in txt.indices) {
			for (j in ciph3.indices) {
				if (txt[i] == ciph1[j] || txt[i] == ciph2[j]) {
					out += ciph3[j]
					out += "/"
					//println(ciph1[j]);
				}
				if (txt[i] == ' ') {
					out += "/"
					break
				}
			}
		}
		out += "/"
		return out
	}
	private fun normalize(str: String): String {
		var tmp = ""
		for (i in str.indices) {
			if (str[i] != ' ') {
				tmp += str[i]
			}
		}
		return tmp.lowercase()
	}

	private fun printWrong(waitTime: Long, parolaCasuale: String){

		binding.learnWordsResponse.isVisible = true
		binding.learnWordsResponse.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
		binding.learnWordsResponse.text = parolaCasuale

		CoroutineScope(Dispatchers.Main).launch {
			delay(waitTime)
			binding.learnWordsResponse.isVisible = false
		}
	}

	private fun printCorrect(waitTime: Long){

		binding.learnWordsResponse.isVisible = true
		binding.learnWordsResponse.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_correct_background)
		binding.learnWordsResponse.text = getString(R.string.correct)

		CoroutineScope(Dispatchers.Main).launch {
			delay(waitTime)
			binding.learnWordsResponse.isVisible = false
		}
	}
}