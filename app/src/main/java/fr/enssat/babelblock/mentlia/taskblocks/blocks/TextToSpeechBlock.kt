package fr.enssat.babelblock.mentlia.taskblocks.blocks

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import java.util.*

class TextToSpeechBlock(private val appContext: Context) : TaskBlock, UtteranceProgressListener() {

    companion object {
        val ARG_TTS_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TTS_LANGUAGE",
            R.string.text_to_speech_block_language_arg_name,
            TaskBlockAdditionalParameterType.LANGUAGE,
            "en"
        )

        val MANIFEST = TaskBlockManifest(
            "TextToSpeechBlock",
            TaskBlockType.INOUT,
            R.string.text_to_speech_block_name,
            R.string.text_to_speech_block_description,
            R.drawable.ic_baseline_speaker_24,
            arrayOf(ARG_TTS_LANGUAGE)
        )

        const val ERROR_INIT = "error_queue"
        const val ERROR_INVALID_REQUEST = "error_invalid_request"
        const val ERROR_NETWORK = "error_network"
        const val ERROR_NETWORK_TIMEOUT = "error_network_timeout"
        const val ERROR_NOT_INSTALLED_YET = "error_not_installed_yet"
        const val ERROR_OUTPUT = "error_output"
        const val ERROR_SERVICE = "error_service"
        const val ERROR_SYNTHESIS = "error_synthesis"
        const val ERROR_QUEUE = "error_queue"
        const val ERROR_UNKNOWN = "error_unknown"
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        ERROR_INIT,
        ERROR_INVALID_REQUEST,
        ERROR_NETWORK,
        ERROR_NETWORK_TIMEOUT,
        ERROR_NOT_INSTALLED_YET,
        ERROR_OUTPUT,
        ERROR_SERVICE,
        ERROR_SYNTHESIS,
        ERROR_QUEUE,
        ERROR_UNKNOWN
    )
    annotation class Error

    private var utteranceID = ""

    @Error
    private var error: String? = null
    private var ttsReady = false
    private var ttsDone = false
    private var currentString: String? = null
    private var language = ARG_TTS_LANGUAGE.defaultValue

    private val textToSpeech: TextToSpeech = TextToSpeech(appContext) {
        if (it == TextToSpeech.ERROR) {
            error = ERROR_INIT
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
            throw IllegalArgumentException("Unknown parameter : $parameterID")
        }
    }

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(ARG_TTS_LANGUAGE.id, language)
        return jsonObject
    }

    override fun fromJSON(jsonObject: JSONObject) {
        language = jsonObject.optString(
            ARG_TTS_LANGUAGE.id,
            ARG_TTS_LANGUAGE.defaultValue
        )
    }

    @SuppressLint("InflateParams")
    override fun getPrepareExecutionView(
        layoutInflater: LayoutInflater,
        resources: Resources
    ): View {
        val view = layoutInflater.inflate(R.layout.generic_loading_dialog, null)
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.text_to_speech_prepare_execution)
        view.findViewById<TextView>(R.id.subtitle).text = ""
        return view
    }

    @SuppressLint("InflateParams")
    override fun getExecuteView(layoutInflater: LayoutInflater, resources: Resources): View {
        val view = layoutInflater.inflate(R.layout.generic_dialog, null)
        view.findViewById<ImageView>(R.id.imageView).setImageDrawable(
            ContextCompat.getDrawable(view.context, R.drawable.ic_baseline_speaker_24)
        )
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.text_to_speech_execute)
        view.findViewById<TextView>(R.id.subtitle).text = currentString
        return view
    }

    override suspend fun prepareExecution() {
        //Wait for speechRecognizer initialization
        while (!ttsReady) delay(100)
        if (error != null) throw TaskBlockException(this, error!!, generateUserErrorMessage())
        Timber.i("Init OK")
    }

    override suspend fun execute(inputString: String?): String? {
        ttsDone = false
        currentString = inputString
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
            throw TaskBlockException(this, ERROR_QUEUE, generateUserErrorMessage())
        }

        while (!ttsDone) delay(100)
        if (error != null) throw TaskBlockException(this, error!!, generateUserErrorMessage())
        Timber.i("Speak OK")

        currentString = null
        return inputString
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        super.onError(utteranceId, errorCode)
        if (utteranceId.equals(utteranceID)) {
            this.error = when (errorCode) {
                TextToSpeech.ERROR_INVALID_REQUEST -> ERROR_INVALID_REQUEST
                TextToSpeech.ERROR_NETWORK -> ERROR_NETWORK
                TextToSpeech.ERROR_NETWORK_TIMEOUT -> ERROR_NETWORK_TIMEOUT
                TextToSpeech.ERROR_NOT_INSTALLED_YET -> ERROR_NOT_INSTALLED_YET
                TextToSpeech.ERROR_OUTPUT -> ERROR_OUTPUT
                TextToSpeech.ERROR_SERVICE -> ERROR_SERVICE
                TextToSpeech.ERROR_SYNTHESIS -> ERROR_SYNTHESIS
                else -> ERROR_UNKNOWN
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

    private fun generateUserErrorMessage(): String {
        when (error) {
            ERROR_NETWORK, ERROR_NETWORK_TIMEOUT -> appContext.getString(R.string.network_error)
            else -> appContext.getString(R.string.unknown_error)
        }
        return ""
    }

}