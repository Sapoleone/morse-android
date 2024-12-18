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

package com.sapoleone.morse.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sapoleone.morse.MainActivity
import com.sapoleone.morse.R
import com.sapoleone.morse.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


@Suppress("LocalVariableName")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var currentVersionVeryFull = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        /* val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        binding.gotoConv.setOnClickListener {
            findNavController().navigate(R.id.action_appHome_to_translateHomeFragment)
        }

        binding.gotoLearn.setOnClickListener {
            findNavController().navigate(R.id.action_appHome_to_learnHomeFragment)
        }
        binding.btnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_appHome_to_accountFragment)
        }
        binding.btnFeedback.setOnClickListener{
            val uri = Uri.parse("https://github.com/Sapoleone/morse-android/issues/new")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        /*binding.permBtn.setOnClickListener {
            (requireActivity() as MainActivity).launchRequestPermission()
        }*/

        val session_id = (requireActivity() as MainActivity).getSession()
        updateUser(session_id)
        updateVersion()


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    fun updateUser(session_id:String){
        if(session_id != "_void_" && session_id != "null" && session_id != ""){
            println("Updated displayed user!, HOME")
            binding.currentUser!!.text = "User: $session_id"
        } else {
            binding.currentUser!!.text = getString(R.string.you_re_not_logged_in)
        }
    }
    private fun updateVersion(){
        runBlocking{
            currentVersionVeryFull = (requireActivity() as MainActivity).getVersionVeryFull()
            val temp = "v$currentVersionVeryFull"
            println("temp: $temp")
            binding.version.text = temp
            delay(1000)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}