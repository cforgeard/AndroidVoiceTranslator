package fr.enssat.babelblock.mentlia.taskblocks.blocks

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.TextUtils
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.taskblocks.*
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*

class TextToSpeechBlock(appContext: Context) : TaskBlock, UtteranceProgressListener() {

    companion object {
        val ARG_TTS_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TTS_LANGUAGE",
            R.string.text_to_speech_block_language_arg_name,
            TaskBlockAdditionalParameterType.LANGUAGE,
            "en"
        )

        val MANIFEST = TaskBlockManifest(
            "TextToSpeechBlock",
            TaskBlockType.IN,
            R.string.text_to_speech_block_name,
            R.string.text_to_speech_prepare_execution,
            R.string.text_to_speech_execute,
            arrayOf(ARG_TTS_LANGUAGE)
        )
    }

    private var utteranceID = ""
    private var error = ""
    private var ttsReady = false
    private var ttsDone = false
    private var language = ARG_TTS_LANGUAGE.defaultValue

    private val textToSpeech: TextToSpeech = TextToSpeech(appContext) {
        if (it == TextToSpeech.ERROR) {
            error = "error_init"
            Timber.e("Failed to initialize TTS")
        } else {
            onTTSReady()
        }
    }

    private fun onTTSReady() {
        textToSpeech.setOnUtteranceProgressListener(this)
        ttsReady = true
        Timber.i("TTS ready")
    }

    override fun getManifest(): TaskBlockManifest {
        return MANIFEST
    }

    override fun setAdditionalParameter(parameterID: String, value: String) {
        Timber.e("%s -> %s", parameterID, value)
        if (parameterID == ARG_TTS_LANGUAGE.id) {
            language = value
        } else {
            throw TaskBlockException("Unknown parameter : $parameterID")
        }
    }

    override suspend fun prepareExecution() {
        //Wait for speechRecognizer initialization
        while (!ttsReady) delay(100)
        if (!TextUtils.isEmpty(error)) throw TaskBlockException("TextToSpeechBlock error : $error")
        Timber.i("Init OK")
    }

    override suspend fun execute(inputString: String?): String? {
        ttsDone = false
        utteranceID = UUID.randomUUID().toString()
        textToSpeech.language = Locale.forLanguageTag(language)

        //Speak and wait for speak done or error
        if (textToSpeech.speak(
                inputString!!,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceID
            ) == TextToSpeech.ERROR
        ) {
            throw TaskBlockException("TextToSpeechBlock error : error_queue")
        }

        while (!ttsDone) delay(100)
        if (!TextUtils.isEmpty(error)) throw TaskBlockException("TextToSpeechBlock error : $error")
        Timber.i("Speak OK")

        return null
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        super.onError(utteranceId, errorCode)
        if (utteranceId.equals(utteranceID)) {
            this.error = when (errorCode) {
                TextToSpeech.ERROR_INVALID_REQUEST -> "error_invalid_request"
                TextToSpeech.ERROR_NETWORK -> "error_network"
                TextToSpeech.ERROR_NETWORK_TIMEOUT -> "error_network_timeout"
                TextToSpeech.ERROR_NOT_INSTALLED_YET -> "error_not_installed_yet"
                TextToSpeech.ERROR_OUTPUT -> "error_output"
                TextToSpeech.ERROR_SERVICE -> "error_service"
                TextToSpeech.ERROR_SYNTHESIS -> "error_synthesis"
                else -> "error_unknown"
            }
        }
    }

    override fun onDone(utteranceId: String?) {
        if (utteranceId.equals(utteranceID)) {
            this.ttsDone = true
        }
    }

    override fun onStart(utteranceId: String?) {}
    override fun onError(utteranceId: String?) {}
}