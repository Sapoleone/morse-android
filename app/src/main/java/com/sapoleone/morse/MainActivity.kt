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
@file:Suppress("LiftReturnOrAssignment", "UNUSED_PARAMETER", "PrivatePropertyName", "LocalVariableName", "KotlinConstantConditions")

package com.sapoleone.morse

//import androidx.appcompat.app.AlertDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sapoleone.morse.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.File
import java.io.IOException

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
@Suppress("MemberVisibilityCanBePrivate")
class MainActivity : AppCompatActivity(){

    private val db = Firebase.firestore
    private val debugAppInstall = true

    private lateinit var binding: ActivityMainBinding

    private lateinit var username: String

    private var session_id = "_void_"

    private var score = 0

    private var scoreChoose = 0
    private var scorePair = 0
    private var scoreType = 0
    private var scoreHighChoose = 0

    //TODO: CHANGE VERSION
    private val currentVersion    = 2 //Version: x.0.0
    private val currentDecimal    = 0 //Version: 0.x.0
    private val currentSubdecimal = 2 //Version: 0.0.x
    private val betaId            = 0 //If betaId is 0 is a release else is a beta

    private var currentVersionFull = buildString {
        append(currentVersion)
        append(".")
        append(currentDecimal)
        append(".")
        append(currentSubdecimal)
    }
    private var currentVersionVeryFull =""
    private lateinit var latestVersionFull: String
    private var latestVersion = -5
    private var latestDecimal = -5
    private var latestSubdecimal = -5

    private lateinit var downloadUrl: String
    //private val REQUEST_WRITE_PERMISSION = 100
    //private val REQUEST_INSTALL_PERMISSION = 101
    companion object {
        private const val REQUEST_INSTALL_PACKAGES_CODE = 100
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("perm", "isGranted: $isGranted")
        if (isGranted){
            Log.i("perm", "Permission granted")
            permissonsGranted()
        }else{
            Log.e("perm", "Permission denied!")
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("version", "CURRENT VERSION: $currentVersionFull")
        Log.i("version","CURRENT FULL VERSION: $currentVersionVeryFull")

        checkForUpdate()
        session_id = getSession()
        currentVersionVeryFull = getVersionVeryFull()

        scoreChoose = funGetScoreDb("c")
        scorePair = funGetScoreDb("p")
        scoreType = funGetScoreDb("t")
        scoreHighChoose = funGetScoreDb("hc")



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
                if (userExist(username_fun)) {
                    session_id = username_fun
                    //username = username_fun

                    updateAllScores()
                    saveSession()
                    println("/startSession()")
                    session_id
                } else {
                    //User doesn't exist
                    println("/startSession()")
                    "-3"
                }

            }

        }
    }

    private suspend fun userExist(session_id: String): Boolean {
        println("collection: users, document: $session_id")
        val document = db.collection("users").document(session_id).get()
            .await()  // Utilizzo di await per operazioni asincrone
        return document.exists()
    }

    private fun createNewDocument(username: String, userTag: String, session_id: String) {
        val data = hashMapOf(
            "scoreChoose" to 0,
            "scorePair" to 0,
            "scoreType" to 0,
            "scoreHighChoose" to 0,

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
            val document = db.collection("users").document(documentName).get()
                .await()  // Utilizzo di await per operazioni asincrone
            if (!document.exists()) {
                println("    exit  findLastUserTag() $i")
                return i
            }
            i++
        }
        return -2
    }

    //GET/SET from Fragments
    fun setScore(score: Int, scoreMode: String) {
        val user = session_id
        //println("  Saving new score: $score, Scoremode: $scoreMode")

        var data = hashMapOf(
            /*"user" to username,
            "userTag" to userTag,*/
            "scoreChoose" to scoreChoose,
            "scorePair" to scorePair,
            "scoreType" to scoreType,
            "scoreHighChoose" to scoreHighChoose,
        )


        if (scoreMode == "c") {
            // Create a new user with a first and last name
            scoreChoose = score
            data = hashMapOf(
                "scoreChoose" to score,
                "scorePair" to scorePair,
                "scoreType" to scoreType,
                "scoreHighChoose" to scoreHighChoose,
            )
        }
        if (scoreMode == "hc") {

            scoreHighChoose = score
            data = hashMapOf(
                "scoreChoose" to scoreChoose,
                "scorePair" to scorePair,
                "scoreType" to scoreType,
                "scoreHighChoose" to score,
            )
        }
        if (scoreMode == "p") {
            scorePair = score
            data = hashMapOf(
                "scoreChoose" to scoreChoose,
                "scorePair" to score,
                "scoreType" to scoreType,
                "scoreHighChoose" to scoreHighChoose,
            )
        }
        if (scoreMode == "t") {
            scoreType = score
            data = hashMapOf(
                "scoreChoose" to scoreChoose,
                "scorePair" to scorePair,
                "scoreType" to score,
                "scoreHighChoose" to scoreHighChoose,
            )
        }

        Log.i(
            "db",
            "Saved New Value (scoreMode:$scoreMode, value: $score, session_id: $session_id)"
        )
        Log.d("db", "c: $scoreChoose")
        Log.d("db", "p: $scorePair")
        Log.d("db", "t: $scoreType")
        Log.d("db", "hc: $scoreHighChoose")

        // Add a new document with a generated ID
        db.collection("users").document(user)
            .set(data)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
        this.score = score
    }

    fun getScore(scoreMode: String): Int {
        Log.i("query_global_var", "Asked for: scoremode $scoreMode")

        if (scoreMode == "c") {
            Log.i("query_global_var", "Scoremode '$scoreMode' is $scoreChoose")
            if (scoreChoose == 0) {
                Log.i("query_global_var", "Scoremode '$scoreMode' is 0, querying db...")
                scoreChoose = funGetScoreDb("c")
            }
            return scoreChoose
        }

        if (scoreMode == "p") {
            Log.i("query_global_var", "Scoremode '$scoreMode' is $scorePair")
            if (scorePair == 0) {
                Log.i("query_global_var", "Scoremode '$scoreMode' is 0, querying db...")
                scorePair = funGetScoreDb("p")
            }
            return scorePair
        }

        if (scoreMode == "t") {
            Log.i("query_global_var", "Scoremode '$scoreMode' is $scoreType")
            if (scoreType == 0) {
                Log.i("query_global_var", "Scoremode '$scoreMode' is 0, querying db...")
                scoreType = funGetScoreDb("t")
            }
            return scoreType
        }

        if (scoreMode == "hc") {//High Choose score
            Log.i("query_global_var", "Scoremode '$scoreMode' is $scoreHighChoose")
            if (scoreHighChoose == 0) {
                Log.i("query_global_var", "Scoremode '$scoreMode' is 0, querying db...")
                scoreHighChoose = funGetScoreDb("hc")
            }
            return scoreHighChoose
        }
        return -1
    }

    private fun funGetScoreDb(scoreMode: String): Int {
        return runBlocking {
            val score1 = getScoreDb(scoreMode)
            Log.i("db", "Get info from db (scoremode: $scoreMode, score: $score1)")
            score1
        }
    }

    private suspend fun getScoreDb(scoreMode: String): Int {
        val fieldName: String
        println("  Score: $score, ScoreMode: $scoreMode")
        when (scoreMode) {
            "c" -> {
                fieldName = "scoreChoose"
            }

            "hc" -> {
                fieldName = "scoreHighChoose"
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
                if (fieldValue == null) {
                    0
                } else {
                    val intFieldValue = fieldValue.toInt()
                    println("  ScoreGot: $intFieldValue")
                    intFieldValue
                }
            } else {
                0
            }
        }
    }

    fun updateAllScores() {
        lifecycleScope.launch {
            scoreChoose = getScoreDb("c")
            scorePair = getScoreDb("p")
            scoreType = getScoreDb("t")
        }
    }

    fun getSessionId(): String {
        return session_id
    }

    fun getVersionVeryFull():String{
        if (betaId == 0){
            currentVersionVeryFull = "$currentVersionFull-release"
        } else if (betaId < 10) {
            currentVersionVeryFull = "$currentVersionFull-beta0$betaId"
        } else {
            currentVersionVeryFull = "$currentVersionFull-beta$betaId"
        }
        return currentVersionVeryFull
    }

    private fun saveSession() {
        println("Session Saved!")
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("session_id", session_id)
        editor.apply()
    }

    fun getSession(): String {
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

    fun destroySession() {
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        session_id = "_void_"
        editor.putString("session_id", session_id)
        editor.apply()
    }

    //Morse
    @Suppress("NAME_SHADOWING")
    fun decode(mTxt:String) {
        //Init
        var i = 0
        var out = ""
        val ciph1 = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
        val ciph2 = arrayOf(".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..")
        //Cycle through the whole PHRASE
        while (i < mTxt.length) {
            var let = ""
            //Cycle through the LETTER
            while (mTxt[i] != '/') {
                let += mTxt[i]
                i++
                if (i >= mTxt.length) {
                    break
                }
            }
            println("LET: $let")
            i++
            //Conversion from MORSE to LETTERS
            var found = false
            var j = 0
            while (j < ciph2.size) {
                if (let == ciph2[j]) {
                    out += ciph1[j]
                    found = true
                    break
                }
                j++
            }

            //Check if the WORD is ended
            if (i < mTxt.length && mTxt[i] == '/') {
                out += " "
            }

            if (!found) {
                // Handle Morse code not found
                // CHAR not found / Morse WRONG
                out += "#"
            }
        }

        println(out)
        val previewTextView = findViewById<TextView>(R.id.preview)
        previewTextView.text = out
        val newSizeInSp = 36
        previewTextView.textSize = newSizeInSp.toFloat()

        if (mTxt == "" && out == "") {
            val previewTextView = findViewById<TextView>(R.id.preview)
            previewTextView.text = getString(R.string.use_the_buttons_below_to_write)

            val newSizeInSp = 20
            previewTextView.textSize = newSizeInSp.toFloat()
        }
        this.mTxt = ""
    }

    fun encode(ignoredView: View?) {
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
        while (i < txt.length) {

            var j = 0                       //@FOR initialization
            while (j < ciph3.size) {         //@FOR condition
                if (txt[i] == ciph1[j] || txt[i] == ciph2[j]) {
                    out += ciph3[j]
                    out += "/"
                    println(ciph1[j])
                }
                j++                         //@FOR increment
                if (txt[i] == ' ') {
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

     fun printAndChange(text:String) {
        val previewTextView = findViewById<TextView>(R.id.preview)
        previewTextView.text = text

        val newSizeInSp = 36
        previewTextView.textSize = newSizeInSp.toFloat()
    }

    //Version control
    private fun checkForUpdate() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.brusegan.it/samuele_dir/apk/morseApp/lastest-version.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Connection error", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateCheck", "Connection error", e)
                }
            }

            @Suppress("KotlinConstantConditions")
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (body != null) {
                            try {
                                val jsonObject = Gson().fromJson(body, JsonObject::class.java)
                                latestVersionFull = jsonObject.get("versionFull").asString
                                downloadUrl = jsonObject.get("url").asString
                                latestVersion = jsonObject.get("version").asInt
                                latestDecimal = jsonObject.get("decimal").asInt
                                latestSubdecimal = jsonObject.get("subversion").asInt
                                if(debugAppInstall){
                                    downloadRequest(downloadUrl)
                                }
                                else if (currentVersion <= latestVersion) {
                                    if (currentVersion < latestVersion) {
                                        downloadRequest(downloadUrl)
                                    }
                                    if (currentVersion == latestVersion) {
                                        if (currentDecimal <= latestDecimal) {
                                            if (currentDecimal < latestDecimal) {
                                                downloadRequest(downloadUrl)
                                            }
                                            if (currentDecimal == latestDecimal) {

                                                if (latestSubdecimal != -2 && latestSubdecimal != -3) {

                                                    if (currentSubdecimal <= latestSubdecimal) {

                                                        if (currentSubdecimal < latestSubdecimal) {
                                                            downloadRequest(downloadUrl)
                                                        }
                                                    }
                                                } else {
                                                    latestSubdecimal = 0
                                                    if (currentSubdecimal <= latestSubdecimal) {
                                                        if (currentSubdecimal < latestSubdecimal) {
                                                            downloadRequest(downloadUrl)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error parsing update information",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("UpdateCheck", "Error parsing update information", e)
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Empty response body",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("UpdateCheck", "Empty response body")
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Server returned an error",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("UpdateCheck", "Server returned an error: ${response.code}")
                        }
                    }
                }
            }
        })
    }

    private fun downloadRequest(url: String) {
        runOnUiThread {
            AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.new_version))
                .setMessage(
                    getString(
                        R.string.update_string,
                        latestVersionFull,
                        currentVersionFull
                    )
                )
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    downloadAndInstallApk(url)
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
    }

    //@Suppress("DEPRECATION", "NAME_SHADOWING")
    @SuppressLint("InlinedApi")
    private fun downloadAndInstallApk(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Downloading morseApp update")
        request.setDescription("Downloading new version of the app")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "morseApp-latest.apk"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)
        Log.d("down", "Download enqueued with ID: $downloadId")

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val uri = downloadManager.getUriForDownloadedFile(downloadId)
                if (uri == null) {
                    Log.e("down", "Failed to retrieve URI for downloaded file.")
                    return
                }
                Log.d("down", "URI: $uri")

                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "morseApp-latest.apk"
                )
                val fileUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.sapoleone.morse.fileprovider",
                    file
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("down", "Android version >= O")
                    if (!packageManager.canRequestPackageInstalls()) {
                        Log.d("down", "Starting Requesting permission mejo")
                        launchRequestPermission()
                        installApk(fileUri)
                    } else {
                        installApk(fileUri)
                    }
                } else {
                    Log.d("down", "Android version < O")
                    installApk(fileUri)
                }
                unregisterReceiver(this)
            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)

        Log.d("down", "BroadcastReceiver registered")
    }

    fun installApk(apkUri: Uri) {
        Log.d("down", "Installing started")
        val packageInstaller = packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        contentResolver.openInputStream(apkUri)?.use { input ->
            session.openWrite("my_app_install", 0, -1).use { output ->
                input.copyTo(output)
                session.fsync(output)
            }
        }

        session.commit(createIntentSender(sessionId))
        session.close()
    }

    private fun createIntentSender(sessionId: Int): IntentSender {
        Log.d("down", "Created IntentSender")
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        return pendingIntent.intentSender
    }
    class InstallResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            when (status) {
                PackageInstaller.STATUS_SUCCESS -> {
                    Log.i("down", "Installation succesfull")
                }
                PackageInstaller.STATUS_FAILURE -> {
                    val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                    Log.e("down", "Installation failed: $message")
                }
                // Gestisci altri casi se necessario
            }
        }
    }

    fun launchRequestPermission(){
        val permission = Manifest.permission.REQUEST_INSTALL_PACKAGES
        //val permission = Manifest.permission.CAMERA
        Log.d("perm", "Launching request for $permission permission")
        permissionLauncher.launch(permission)
    }
    fun launchRequestPermissionMejo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse(String.format("package:%s", packageName)))
                startActivityForResult(intent, REQUEST_INSTALL_PACKAGES_CODE)
            }
        }

    }
    fun permissonsGranted(){
        Log.i("perm", "Permission Granted")
    }

    /*
    fun checkPermissions(permission: String, requestCode: Int){
        Log.d("perm", "Checking the $permission permission")
        if(ContextCompat.checkSelfPermission(this,permission)==PackageManager.PERMISSION_DENIED){
            Log.d("perm", "Requesting the $permission permission")
            //ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            ActivityResultContracts.RequestPermission()

        } else {
            Log.d("perm", "Permission $permission was already granted")
            Toast.makeText(this, "Permission Already granted", Toast.LENGTH_SHORT).show()   //TODO: Revove this Toast
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_INSTALL_PACKAGES_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()       //TODO: Revove this Toast
                Log.i("perm", "Install packages permission granted!")
            } else {
                Toast.makeText(this, "Cannot install the app", Toast.LENGTH_LONG).show()    //TODO: Revove this Toast
                Log.e("perm", "Install packages permission denied")
            }
        }
    }*/

}

