package fr.enssat.babelblock.mentlia.taskblocks.blocks

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringDef
import androidx.core.content.ContextCompat
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.taskblocks.*
import kotlinx.coroutines.delay
import org.json.JSONObject
import timber.log.Timber


class SpeechRecognizerBlock(private val appContext: Context) : TaskBlock, RecognitionListener {

    companion object {
        val ARG_SPEECH_RECOGNIZER_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_SPEECH_RECOGNIZER_LANGUAGE",
            R.string.speech_recognizer_block_language_arg_name,
            TaskBlockAdditionalParameterType.LANGUAGE,
            "fr"
        )

        val MANIFEST = TaskBlockManifest(
            "SpeechRecognizerBlock",
            TaskBlockType.OUT,
            R.string.speech_recognizer_block_name,
            R.string.speech_recognizer_block_description,
            R.drawable.ic_baseline_mic_24,
            arrayOf(ARG_SPEECH_RECOGNIZER_LANGUAGE)
        )

        const val ERROR_AUDIO = "error_audio_error"
        const val ERROR_CLIENT = "error_client"
        const val ERROR_INSUFFICIENT_PERMISSIONS = "error_permission"
        const val ERROR_NETWORK = "error_network"
        const val ERROR_NETWORK_TIMEOUT = "error_timeout"
        const val ERROR_NO_MATCH = "error_no_match"
        const val ERROR_RECOGNIZER_BUSY = "error_busy"
        const val ERROR_SERVER = "error_server"
        const val ERROR_SPEECH_TIMEOUT = "error_timeout"
        const val ERROR_UNKNOWN = "error_unknown"
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        ERROR_AUDIO,
        ERROR_CLIENT,
        ERROR_INSUFFICIENT_PERMISSIONS,
        ERROR_NETWORK,
        ERROR_NETWORK_TIMEOUT,
        ERROR_NO_MATCH,
        ERROR_RECOGNIZER_BUSY,
        ERROR_SERVER,
        ERROR_SPEECH_TIMEOUT,
        ERROR_UNKNOWN
    )
    annotation class Error

    private var result = ""

    @Error
    private var error: String? = null
    private var language = ARG_SPEECH_RECOGNIZER_LANGUAGE.defaultValue

    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(appContext).apply {
            setRecognitionListener(this@SpeechRecognizerBlock)
        }

    override fun getManifest(): TaskBlockManifest {
        return MANIFEST
    }

    override fun getAdditionalParameter(parameterID: String): String {
        if (parameterID == ARG_SPEECH_RECOGNIZER_LANGUAGE.id) {
            return language
        } else {
            throw IllegalArgumentException("Unknown parameter : $parameterID")
        }
    }

    override fun setAdditionalParameter(parameterID: String, value: String) {
        Timber.e("%s -> %s", parameterID, value)
        if (parameterID == ARG_SPEECH_RECOGNIZER_LANGUAGE.id) {
            language = value
        } else {
            throw IllegalArgumentException("Unknown parameter : $parameterID")
        }
    }

    @SuppressLint("InflateParams")
    override fun getPrepareExecutionView(
        layoutInflater: LayoutInflater,
        resources: Resources
    ): View {
        val view = layoutInflater.inflate(R.layout.generic_loading_dialog, null)
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.speech_recognizer_block_prepare_execution)
        view.findViewById<TextView>(R.id.subtitle).text = ""
        return view
    }

    @SuppressLint("InflateParams")
    override fun getExecuteView(layoutInflater: LayoutInflater, resources: Resources): View {
        val view = layoutInflater.inflate(R.layout.generic_dialog, null)
        view.findViewById<ImageView>(R.id.imageView).setImageDrawable(
            ContextCompat.getDrawable(view.context, R.drawable.ic_baseline_mic_24)
        )
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.speech_recognizer_block_execute)
        view.findViewById<TextView>(R.id.subtitle).text = ""
        return view
    }

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(ARG_SPEECH_RECOGNIZER_LANGUAGE.id, language)
        return jsonObject
    }

    override fun fromJSON(jsonObject: JSONObject) {
        language = jsonObject.optString(
            ARG_SPEECH_RECOGNIZER_LANGUAGE.id,
            ARG_SPEECH_RECOGNIZER_LANGUAGE.defaultValue
        )
    }

    override suspend fun prepareExecution() {
        error = null
        //nothing to do
    }

    override suspend fun execute(inputString: String?): String {
        result = ""
        error = null

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
        while (TextUtils.isEmpty(result) && error == null) delay(100)
        if (error != null) throw TaskBlockException(this, error!!, generateUserErrorMessage())

        Timber.i("Block ended, result = %s", result)
        return result
    }

    override fun onError(error: Int) {
        this.error = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> ERROR_AUDIO
            SpeechRecognizer.ERROR_CLIENT -> ERROR_CLIENT
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> ERROR_INSUFFICIENT_PERMISSIONS
            SpeechRecognizer.ERROR_NETWORK -> ERROR_NETWORK
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> ERROR_NETWORK_TIMEOUT
            SpeechRecognizer.ERROR_NO_MATCH -> ERROR_NO_MATCH
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> ERROR_RECOGNIZER_BUSY
            SpeechRecognizer.ERROR_SERVER -> ERROR_SERVER
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> ERROR_SPEECH_TIMEOUT
            else -> ERROR_SPEECH_TIMEOUT
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

    private fun generateUserErrorMessage(): String {
        when (error) {
            ERROR_NO_MATCH -> appContext.getString(R.string.no_match_speech_recognizer_error)
            ERROR_NETWORK, ERROR_NETWORK_TIMEOUT -> appContext.getString(R.string.network_error)
            else -> appContext.getString(R.string.unknown_error)
        }
        return ""
    }
}