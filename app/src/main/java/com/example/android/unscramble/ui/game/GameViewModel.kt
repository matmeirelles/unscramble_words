package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord
        get() = _currentScrambledWord

    private lateinit var currentWord: String

    private val wordsList: MutableList<String> = mutableListOf()

    init {
        Log.d(TAG, "GameViewModel created")
        _currentScrambledWord.value = getNextWord()
    }

    fun onSubmitWord(inputText: String): Boolean {

        Log.d(TAG, "Texto digitado: $inputText | Texto correto: $currentWord")

        return if (inputText.equals(currentWord, true)) {
            _currentWordCount.value = _currentWordCount.value?.inc()
            _score.value = _score.value?.plus(SCORE_INCREASE)

            if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
                _currentScrambledWord.value = getNextWord()
            }

            false
        } else true
    }

    fun onSkipWord() {

        _currentScrambledWord.value = getNextWord()
        _currentWordCount.value = _currentWordCount.value?.inc()
    }

    private fun getNextWord(): String {

        currentWord = allWordsList.random()

        while (wordsList.contains(currentWord)) {
            currentWord = allWordsList.random()
        }

        wordsList.add(currentWord)

        Log.d(TAG, "Texto correto: $currentWord | Palavras já jogadas: $wordsList ")

        val shuffledCurrentWord = currentWord.toCharArray()

        shuffledCurrentWord.shuffle()

        //Embaralha a palavra até que as duas palavras sejam diferentes
        while (shuffledCurrentWord.concatToString().equals(currentWord, false)) {
            shuffledCurrentWord.shuffle()
        }

        return String(shuffledCurrentWord)
    }

    fun restoreValues() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        _currentScrambledWord.value = getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "GameViewModel destroyed")
    }
}