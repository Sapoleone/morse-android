package com.sapoleone.morse

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sapoleone.morse.databinding.ActivityMainBinding
import com.sapoleone.morse.databinding.FragmentSettingsBinding

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingSetting: FragmentSettingsBinding

    private lateinit var username: String
    private var score = 0       //TODO: Add db query (Score)
    private var scoreChoose = 0 //TODO: Add db query (ScoreChoose)
    private var scorePair = 0   //TODO: Add db query (ScorePair)
    private var scoreType = 0   //TODO: Add db query (ScoreType)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
            R.id.appHome, R.id.morse2text, R.id.text2morse
            )
        )
        setUsername("test")
    }

    //Var declaration
    private var mTxt = ""

    fun setUsername(username:String){
        print("    Username updated! User:")
        println(username)
        this.username = username
    }
    @Suppress("NAME_SHADOWING")
    fun decode(ignoredView: View?){
        //Init
        var i = 0
        var out = ""
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
        //Cycle through the whole PHRASE
        while (i < mTxt.length){
            var let = ""

            //Cycle through the LETTER
            while(mTxt[i] != '/'){
                let += mTxt[i]
                i++
                if (i >= mTxt.length) { break }
            }
            println("LET: $let")

            i++

            //Conversion from MORSE to LETTERS
            //NOTE: How to use a for?; Using a WHILE instead
            var found = false
            var j = 0                       //@FOR initialization
            while (j < ciph2.size){         //@FOR condition
                if(let == ciph2[j]){
                    out += ciph1[j]
                    found = true
                    break
                }
                j++                         //@FOR increment
            }

            //Check if the WORD is ended
            if(i < mTxt.length && mTxt[i] == '/'){
                out += " "
            }

            /*if (!found) {
                // Handle Morse code not found
                // CHAR not found / Morse WRONG
                out += "#"
            }*/
        }

        println(out)
        val previewTextView = findViewById<TextView>(R.id.preview)
        previewTextView.text = out
        val newSizeInSp = 36
        previewTextView.textSize = newSizeInSp.toFloat()

        if(mTxt == "" && out == ""){
            val previewTextView = findViewById<TextView>(R.id.preview)
            previewTextView.text = getString(R.string.use_the_buttons_below_to_write)


            val newSizeInSp = 20
            previewTextView.textSize = newSizeInSp.toFloat()
        }
        mTxt = ""
        out = ""
    }
    fun encode(ignoredView: View?){
        //Input
        val editText = findViewById<EditText>(R.id.typeInput)
        val txt = editText.text.toString()

        //Init
        var i = 0
        var out = ""
        val ciph1 = arrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
        val ciph2 = arrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        val ciph3 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")

        //Direct Translation
        while(i < txt.length){

            var j = 0                       //@FOR initialization
            while (j < ciph3.size){         //@FOR condition
                if(txt[i] == ciph1[j] || txt[i] == ciph2[j]){
                    out += ciph3[j]
                    out += "/"
                    println(ciph1[j])
                }
                j++                         //@FOR increment
                if (txt[i] == ' '){
                    out += "/"
                    break
                }
            }
            i++
        }
        out += "/"
        val outputTextView = findViewById<TextView>(R.id.output)
        outputTextView.text = out

        val newSizeInSp = 20
        outputTextView.textSize = newSizeInSp.toFloat()
    }
    private fun printAndChange() {
        val previewTextView = findViewById<TextView>(R.id.preview)
        previewTextView.text = mTxt

        val newSizeInSp = 36
        previewTextView.textSize = newSizeInSp.toFloat()
    }
    fun dashClick(ignoredView: View?) {
        //println("DASH")
        mTxt += "-"
        println(mTxt)
        printAndChange()

    }
    fun dotClick(ignoredView: View?) {
        //println("DOT")
        mTxt += "."
        println(mTxt)
        printAndChange()
    }
    fun slashClick(ignoredView: View?) {
        //println("SLASH")
        mTxt += "/"
        println(mTxt)
        printAndChange()
    }
    fun cancClick(ignoredView: View?) {
        //println("CANC")
        mTxt = mTxt.dropLast(1)
        printAndChange()
    }
    fun setScore(score: Int, scoreMode:String) {
        //TODO: Add Username input
        //val user = "Sapoleone"
        val user = username
        print("User")
        println(username)
        var data = hashMapOf(
            "user" to user,
            "score" to score,
        )

        if(scoreMode == "c"){
            // Create a new user with a first and last name
            data = hashMapOf(
                "user" to user,
                "scoreChoose" to score,
            )
        }
        if(scoreMode == "p"){
            data = hashMapOf(
                "user" to user,
                "scorePair" to score,
            )
        }
        if(scoreMode == "t"){
            data = hashMapOf(
                "user" to user,
                "scoreType" to score,
            )
        }

        // Add a new document with a generated ID
        db.collection("users").document(user)
            .set(data)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
        this.score = score
    }
    fun getScore(scoreMode:String): Int {
        if(scoreMode == "c"){
            return scoreChoose
        }
        if(scoreMode == "p"){
            return scorePair
        }
        if(scoreMode == "t"){
            return scoreType
        }
        return -1
    }
    fun sendInput(ignoredView: View?) {
        //val user = bindingSetting.inputUser.text.toString()
        val inputUser = findViewById<EditText>(R.id.inputUser)
        val user = inputUser.text.toString()

        /*bindingSetting.inputUser.setOnClickListener {
            println("Username sent!")
            println(user)
            setUsername(user)
        }*/

        println("    Username sent!")
        println(user)
        //setUsername(user)

        print("    Username updated! User:")
        println(user)
        username = user
    }
}
