package com.sapoleone.morse.ui.translate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TranslateHomeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Translate Home Fragment"
    }
    val text: LiveData<String> = _text
}