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

package com.sapoleone.morse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.MainActivity
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var sessionId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.backFromSettings.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_appHome)
        }
        sessionId = (requireActivity() as MainActivity).getSessionId()
        function()

        return binding.root
    }
    private fun function(){
        if(sessionId != "_void_" && sessionId != "null" && sessionId != "") {

            binding.logOutBt.setOnClickListener {
                (requireActivity() as MainActivity).destroySession()
                findNavController().navigate(R.id.action_accountFragment_to_appHome)
            }

            //Disable the others
            binding.loginBt.setBackgroundColor(requireContext().getColor(R.color.colorGray42))
            binding.signInBt.setBackgroundColor(requireContext().getColor(R.color.colorGray42))
        } else {
            binding.loginBt.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
            }
            binding.signInBt.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_signInFragment)
            }

            binding.logOutBt.setBackgroundColor(requireContext().getColor(R.color.colorGray42))
        }
    }
}