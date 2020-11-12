package fr.enssat.babelblock.mentlia.taskblocks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.TextUtils
import kotlinx.coroutines.delay
import timber.log.Timber


class SpeechRecognizerBlock(appContext: Context) : TaskBlock, RecognitionListener {

    companion object {
        val ARG_SPEECH_RECOGNIZER_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_SPEECH_RECOGNIZER_LANGUAGE",
            TaskBlockAdditionalParameterType.LANGUAGE,
            "fr"
        )
    }

    private var result = ""
    private var error = ""
    private var language = ARG_SPEECH_RECOGNIZER_LANGUAGE.defaultValue

    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(this@SpeechRecognizerBlock)
        }

    override fun id(): String {
        return "SpeechRecognizerBlock"
    }

    override fun requireInputString(): Boolean {
        return false
    }

    override fun willOutputString(): Boolean {
        return true
    }

    override fun additionalParameters(): Array<TaskBlockAdditionalParameter> {
        return arrayOf(ARG_SPEECH_RECOGNIZER_LANGUAGE)
    }

    override fun setAdditionalParameter(parameterID: String, value: String) {
        Timber.e("%s -> %s", parameterID, value)
        if (parameterID == ARG_SPEECH_RECOGNIZER_LANGUAGE.id) {
            language = value
        } else {
            throw TaskBlockException("Unknown parameter : $parameterID")
        }
    }

    override suspend fun execute(inputString: String?): String? {
        result = ""

        val speechRecognizerIntent: Intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            }

        //Start listening
        Timber.i("StartListening...")
        speechRecognizer.startListening(speechRecognizerIntent)

        //Wait for result or error
        while (TextUtils.isEmpty(result) && TextUtils.isEmpty(error)) delay(100)
        if (!TextUtils.isEmpty(error)) throw TaskBlockException("SpeechRecognizerBlock error : $error")

        Timber.i("Block ended, result = %s", result)
        return result
    }

    override fun onError(error: Int) {
        this.error = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "error_audio_error"
            SpeechRecognizer.ERROR_CLIENT -> "error_client"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "error_permission"
            SpeechRecognizer.ERROR_NETWORK -> "error_network"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "error_timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "error_no_match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "error_busy"
            SpeechRecognizer.ERROR_SERVER -> "error_server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "error_timeout"
            else -> "error_unknown"
        }
    }

    override fun onResults(results: Bundle?) {
        val userSaid = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        result = userSaid?.get(0) ?: ""
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}