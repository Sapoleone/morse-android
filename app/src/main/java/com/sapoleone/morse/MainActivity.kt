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
@file:Suppress("LiftReturnOrAssignment", "UNUSED_PARAMETER", "PrivatePropertyName", "LocalVariableName")

package com.sapoleone.morse

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sapoleone.morse.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@Suppress("MemberVisibilityCanBePrivate")
class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var binding: ActivityMainBinding
    //private lateinit var bindingSetting: FragmentSettingsBinding

    private lateinit var username: String

    private var session_id = "_void_"

    private var score = 0       //TODO: Add db query (Score)

    private var scoreChoose = 0
    private var scorePair = 0
    private var scoreType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session_id = getSession()

    }

    //Var declaration
    private var mTxt = ""

    // Inizio della sessione utilizzando una coroutine per gestire l'asincronia
    fun startSession(username_fun: String, mode: Int): String {
        return runBlocking {  // Modificato per gestire le operazioni asincrone
            if (mode == 0) {
                //Sign IN
                println("startSession()")

                username = username_fun
                val userTag = createUserTag(username)

                if (userTag != "-2") {

                    session_id = "$username#$userTag"

                    println("new_session_id: $session_id")
                    createNewDocument(username, userTag, session_id)

                    updateAllScores()
                    saveSession()
                    println("/startSession()")
                    session_id
                } else {
                    //userTag(username#userTag)>999
                    println("/startSession()")
                    "-2"
                }
            } else {
                //Login
                if(userExist(username_fun)){
                    session_id = username_fun
                    //username = username_fun

                    updateAllScores()
                    saveSession()
                    println("/startSession()")
                    session_id
                }
                else{
                    //User doesn't exist
                    println("/startSession()")
                    "-3"
                }

            }

        }
    }
    private suspend fun userExist(session_id: String): Boolean {
        println("collection: users, document: $session_id")
        val document = db.collection("users").document(session_id).get().await()  // Utilizzo di await per operazioni asincrone
        return document.exists()
    }
    private fun createNewDocument(username: String, userTag:String, session_id:String){
        val data = hashMapOf(
            "scoreChoose" to 0,
            "scorePair" to 0,
            "scoreType" to 0,

            "user" to username,
            "userTag" to userTag,
        )

        db.collection("users").document(session_id)
            .set(data)
    }
    private suspend fun createUserTag(UUID: String): String {  // Modificato per essere una funzione sospesa
        println("  enter createUserTag()")
        val lastUserTag = findLastUserTag(UUID)
        return if (lastUserTag != -2) {

            val strUserTag = formatUserTag(lastUserTag)
            println("  exit createUserTag()")
            strUserTag
        } else {
            "-2"
        }
    }
    private fun formatUserTag(userTag_unformatted: Int): String {
        println("    enter formatUserTag() $userTag_unformatted")
        val userTag_formatted = when {
            userTag_unformatted < 10 -> "00$userTag_unformatted"
            userTag_unformatted < 100 -> "0$userTag_unformatted"
            else -> userTag_unformatted.toString()
        }
        println("    exit  formatUserTag() $userTag_formatted")
        return userTag_formatted
    }
    private suspend fun findLastUserTag(UUID: String): Int {  // Modificato per essere una funzione sospesa
        println("    enter findLastUserTag() $UUID")
        var i = 1
        while (i <= 999) {
            val formatted_i = formatUserTag(i)
            val documentName = "$UUID#$formatted_i"  // Utilizzo dei template di stringa
            println("collection: users, document: $documentName, i: $i")
            val document = db.collection("users").document(documentName).get().await()  // Utilizzo di await per operazioni asincrone
            if (!document.exists()) {
                println("    exit  findLastUserTag() $i")
                return i
            }
            i++
        }
        return -2
    }

    //GET/SET from Fragments
    fun setScore(score: Int, scoreMode:String) {
        val user = session_id
        println("  Saving new score: $score, Scoremode: $scoreMode")
        var data = hashMapOf(
            /*"user" to username,
            "userTag" to userTag,*/
            "scoreChoose" to score,
            "scorePair" to score,
            "scoreType" to score,

        )

        if(scoreMode == "c"){
            // Create a new user with a first and last name
            scoreChoose = score
            data = hashMapOf(
                "scoreChoose" to score,
            )
        }
        if(scoreMode == "p"){
            scorePair = score
            data = hashMapOf(
                "scorePair" to score,
            )
        }
        if(scoreMode == "t"){
            scoreType = score
            data = hashMapOf(
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
            if(scoreChoose == 0){
                scoreChoose = funGetScoreDb("c")
            }
            return scoreChoose
        }
        if(scoreMode == "p"){
            if(scorePair == 0){
                scorePair = funGetScoreDb("p")
            }
            return scorePair
        }
        if(scoreMode == "t"){
            if(scoreType == 0){
                scoreType = funGetScoreDb("t")
            }
            return scoreType
        }
        return -1
    }
    private fun funGetScoreDb(scoreMode: String): Int {
        return runBlocking {
            val score1 = getScoreDb(scoreMode)
            score1
        }
    }
    private suspend fun getScoreDb(scoreMode: String): Int {
        val fieldName:String
        println("  Score: $score, ScoreMode: $scoreMode")
        when (scoreMode) {
            "c" -> {
                fieldName = "scoreChoose"
            }
            "p" -> {
                fieldName = "scorePair"
            }
            "t" -> {
                fieldName = "scoreType"
            }
            else -> {
                fieldName = ""
            }
        }
        return withContext(Dispatchers.IO) {
            val document = db.collection("users").document(session_id).get().await()
            if (document.exists()) {

                val fieldValue = document.getLong(fieldName)
                if(fieldValue == null){
                    0
                }
                else{
                    val intFieldValue = fieldValue.toInt()
                    println("  ScoreGot: $intFieldValue")
                    intFieldValue
                }
            } else {
                0
            }
        }
    }
    fun updateAllScores(){
        lifecycleScope.launch {
            scoreChoose = getScoreDb("c")
            scorePair = getScoreDb("p")
            scoreType = getScoreDb("t")
        }
    }
    fun getSessionId(): String {
        return session_id
    }
    private fun saveSession(){
        println("Session Saved!")
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("session_id", session_id)
        editor.apply()
    }
    fun getSession(): String{
        if (session_id == "_void_") {
            val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            session_id = sharedPreferences.getString("session_id", null).toString()

            if (session_id != "_void_" && session_id != "null" && session_id != "") {
                // Usa sessionId
                println("    Session ID: $session_id")
            }
        }
        return session_id
    }
    fun destroySession(){
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        session_id = "_void_"
        editor.putString("session_id", session_id)
        editor.apply()
    }

    //Morse
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

    //TODO: Move the functions below to MorseFragment
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
}
