package com.group02.mobile.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink

object DigitalInkManager {
    private const val TAG = "DigitalInkManager"
    private var recognizer: DigitalInkRecognizer? = null
    private var model: DigitalInkRecognitionModel? = null

    fun setup(languageCode: String = "ja") {
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag(languageCode)
        if (modelIdentifier == null) {
            Log.e(TAG, "No model for language: $languageCode")
            return
        }

        model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        val remoteModelManager = RemoteModelManager.getInstance()

        remoteModelManager.download(model!!, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                Log.i(TAG, "Model downloaded")
                recognizer = DigitalInkRecognition.getClient(
                    DigitalInkRecognizerOptions.builder(model!!).build()
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error downloading model", e)
            }
    }

    fun recognize(ink: Ink, onResult: (String, Float) -> Unit) {
        val currentRecognizer = recognizer
        if (currentRecognizer == null) {
            Log.e(TAG, "Recognizer not initialized")
            return
        }

        currentRecognizer.recognize(ink)
            .addOnSuccessListener { result ->
                if (result.candidates.isNotEmpty()) {
                    val candidate = result.candidates[0]
                    onResult(candidate.text, candidate.score ?: 0f)
                } else {
                    onResult("", 0f)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error recognizing ink", e)
                onResult("", 0f)
            }
    }
}
