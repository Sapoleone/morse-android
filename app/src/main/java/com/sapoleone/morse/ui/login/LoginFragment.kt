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
import com.sapoleone.morse.databinding.FragmentLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.inputUUID.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.inputUUID.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                println("Clicked!")
                val UUID = binding.inputUUID.text.toString()
                print("    UUID:")
                println(UUID)

                var ch = -1
                var i = 0
                while(i < UUID.length){
                    if(UUID[i] == '#'){
                        ch = 0
                        break
                    }
                    i++
                }

                if(ch == 0){
                    val user_id = (requireActivity() as MainActivity).startSession(UUID, 1)
                    if (user_id != "-3"){
                        print("    user_id:")
                        println(user_id)

                        printLoginConfirmed(user_id)

                    } else{
                        binding.welcomeUser.isVisible = true
                        binding.welcomeUser.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
                        binding.welcomeUser.text = "You MUST sign in!"
                    }
                } else {
                    val user_id = (requireActivity() as MainActivity).startSession(UUID, 1)
                    if (user_id != "-3"){
                        print("    user_id:")
                        println(user_id)
                        printLoginConfirmed(user_id)

                    } else {
                        binding.welcomeUser.isVisible = true
                        binding.welcomeUser.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_wrong_background)
                        binding.welcomeUser.text = "username#number"
                    }
                }


                true
            } else {
                false
            }
        }
        binding.backFromSettings.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_appHome)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun printLoginConfirmed(user_id:String){
        runBlocking {
            binding.welcomeUser.isVisible = true
            binding.welcomeUser.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_correct_background)
            binding.welcomeUser.text = "Welcome, $user_id \n You can go back!"
            delay(1000)
            findNavController().navigate(R.id.action_loginFragment_to_appHome)
        }
    }

}

