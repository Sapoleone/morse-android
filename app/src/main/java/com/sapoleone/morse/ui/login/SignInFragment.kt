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

package com.sapoleone.morse.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.MainActivity
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentSignInBinding

@Suppress("LocalVariableName")
class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        println("Wells, gg")
        binding.inputUsername.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.inputUsername.setOnEditorActionListener { _, actionId, _ ->
            println("#PRESSED ENTER")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                println("#PRESSED ENTER2")
                val username = binding.inputUsername.text.toString()
                var i = 0
                while(i < username.length){
                    if(username[i] == '#'){
                        i = -1
                        break
                    }
                    i++
                }
                //Non è presente un '#'
                if (i != -1){
                    println(" preCALL")
                    val user_id = (requireActivity() as MainActivity).startSession(username, 0)

                    println("postCALL")
                    if(user_id != "-2"){
                        binding.successfulSignIn.isVisible = true
                        binding.successfulSignIn.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_correct_background)
                        binding.successfulSignIn.text = buildString {
                            append("Welcome, ")
                            append(user_id)
                            append("\n")
                            append("You can go back!")
                        }
                    }
                    else{
                        binding.successfulSignIn.isVisible = true
                        binding.successfulSignIn.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
                        binding.successfulSignIn.text = "FATAL ERROR: username over 999"
                    }

                }
                else{
                    binding.successfulSignIn.isVisible = true
                    binding.successfulSignIn.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
                    binding.successfulSignIn.text = "You cannot use the '#' char!"
                }
                true
            } else {
                false
            }

        }
        binding.backFromSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_appHome)
        }

        return binding.root
    }
}