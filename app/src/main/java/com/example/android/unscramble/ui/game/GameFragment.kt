/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

const val TAG = "GameFragment"

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private lateinit var binding: GameFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "GameFragment - onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "GameFragment - onCreateView")

        Log.d(
            "GameFragment", "Word: ${viewModel.currentScrambledWord.value} " +
                    "Score: ${viewModel.score.value} WordCount: ${viewModel.currentWordCount.value}"
        )

        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "GameFragment - onViewCreated")

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        binding.lifecycleOwner = viewLifecycleOwner

        // Cria os listeners dos botões de "Submit" e "Skip"
        binding.submit.setOnClickListener {

            val inputText = binding.textInputEditText.text.toString().trimStart().trimEnd()

            if (!viewModel.onSubmitWord(inputText)) {
                setErrorTextField(false)
            } else {
                setErrorTextField(true)
            }

            if (viewModel.currentWordCount.value!! >= MAX_NO_OF_WORDS) {
                showFinalScoreDialog()
            }
        }

        binding.skip.setOnClickListener {
            viewModel.onSkipWord()

            setErrorTextField(false)

            if (viewModel.currentWordCount.value!! >= MAX_NO_OF_WORDS) {
                showFinalScoreDialog()
            }
        }

        /** Use observer to update the UI - It was taken off to use the data vinculation **/
//        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newWord ->
//            binding.textViewScrambleWord.text = newWord
//        }

//        viewModel.score.observe(viewLifecycleOwner) { newScore ->
//            binding.score.text = getString(R.string.score, newScore)
//        }
//
//        viewModel.currentWordCount.observe(viewLifecycleOwner) { newWordCount ->
//            binding.wordCount.text = getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
//        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "GameFragment - destroyed")
    }

    private fun restartGame() {
        setErrorTextField(false)

        viewModel.restoreValues()
    }

    private fun exitGame() {
        activity?.finish()
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    private fun showFinalScoreDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.try_again)) { _, _ ->
                restartGame()
            }
            .show()
    }
}
