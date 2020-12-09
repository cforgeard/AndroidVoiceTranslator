package fr.enssat.babelblock.mentlia.taskblocks.blocks

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringDef
import com.google.android.gms.tasks.Task
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.taskblocks.*
import org.json.JSONObject
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//https://stackoverflow.com/questions/50473637/how-to-transform-an-android-task-to-a-kotlin-deferred
suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            @Suppress("UNCHECKED_CAST")
            continuation.resume(task.result as T)
        } else {
            continuation.resumeWithException(
                task.exception ?: RuntimeException("Unknown task exception")
            )
        }
    }
}

class TranslatorBlock(private val appContext: Context) : TaskBlock {

    companion object {
        val ARG_TRANSLATE_SOURCE_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TRANSLATE_SOURCE_LANGUAGE",
            R.string.translator_block_source_language_arg_name,
            TaskBlockAdditionalParameterType.LANGUAGE,
            "fr"
        )

        val ARG_TRANSLATE_TARGET_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TRANSLATE_TARGET_LANGUAGE",
            R.string.translator_block_target_language_arg_name,
            TaskBlockAdditionalParameterType.LANGUAGE,
            "en"
        )

        val MANIFEST = TaskBlockManifest(
            "TranslatorBlock",
            TaskBlockType.INOUT,
            R.string.translator_block_name,
            R.string.translator_block_description,
            R.drawable.ic_baseline_g_translate_24,
            arrayOf(ARG_TRANSLATE_SOURCE_LANGUAGE, ARG_TRANSLATE_TARGET_LANGUAGE)
        )

        const val ERROR_DOWNLOAD_MODEL_FAILED = "error_download_model_failed"
        const val ERROR_TRANSLATE_FAILED = "error_translate_failed"
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        ERROR_DOWNLOAD_MODEL_FAILED,
        ERROR_TRANSLATE_FAILED
    )
    annotation class Error

    private var translator: Translator? = null
    private var currentString: String? = null
    private var sourceLanguage = ARG_TRANSLATE_SOURCE_LANGUAGE.defaultValue
    private var targetLanguage = ARG_TRANSLATE_TARGET_LANGUAGE.defaultValue

    override fun getManifest(): TaskBlockManifest {
        return MANIFEST
    }

    override fun setAdditionalParameter(parameterID: String, value: String) {
        Timber.e("%s -> %s", parameterID, value)
        when (parameterID) {
            ARG_TRANSLATE_SOURCE_LANGUAGE.id -> {
                sourceLanguage = value
            }
            ARG_TRANSLATE_TARGET_LANGUAGE.id -> {
                targetLanguage = value
            }
            else -> {
                throw IllegalArgumentException("Unknown parameter : $parameterID")
            }
        }
    }

    override fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(ARG_TRANSLATE_SOURCE_LANGUAGE.id, sourceLanguage)
        jsonObject.put(ARG_TRANSLATE_TARGET_LANGUAGE.id, targetLanguage)
        return jsonObject
    }

    override fun fromJSON(jsonObject: JSONObject) {
        sourceLanguage = jsonObject.optString(
            ARG_TRANSLATE_SOURCE_LANGUAGE.id,
            ARG_TRANSLATE_SOURCE_LANGUAGE.defaultValue
        )
        targetLanguage = jsonObject.optString(
            ARG_TRANSLATE_TARGET_LANGUAGE.id,
            ARG_TRANSLATE_TARGET_LANGUAGE.defaultValue
        )
    }

    @SuppressLint("InflateParams")
    override fun getPrepareExecutionView(
        layoutInflater: LayoutInflater,
        resources: Resources
    ): View {
        val view = layoutInflater.inflate(R.layout.generic_loading_dialog, null)
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.translator_block_prepare_execution)
        view.findViewById<TextView>(R.id.subtitle).text = "$sourceLanguage -> $targetLanguage"
        return view
    }

    @SuppressLint("InflateParams")
    override fun getExecuteView(layoutInflater: LayoutInflater, resources: Resources): View {
        val view = layoutInflater.inflate(R.layout.generic_loading_dialog, null)
        view.findViewById<TextView>(R.id.title).text =
            resources.getString(R.string.translator_block_execute)
        view.findViewById<TextView>(R.id.subtitle).text = currentString
        return view
    }

    override suspend fun prepareExecution() {
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            translator = Translation.getClient(options)
            Timber.i("downloadModelIfNeeded start...")
            translator!!.downloadModelIfNeeded().await()
            Timber.i("downloadModelIfNeeded OK")
        } catch (throwable: Throwable) {
            throw TaskBlockException(
                this,
                ERROR_DOWNLOAD_MODEL_FAILED,
                appContext.getString(R.string.translate_model_error)
            )
        }
    }

    override suspend fun execute(inputString: String?): String {
        try {
            currentString = inputString
            Timber.i("translate start...")
            val translateTask = translator!!.translate(inputString!!)
            val translatedText = translateTask.await()!!
            Timber.i("translate OK")

            return translatedText
        } catch (throwable: Throwable) {
            throw TaskBlockException(
                this,
                ERROR_TRANSLATE_FAILED,
                appContext.getString(R.string.unknown_error)
            )
        } finally {
            currentString = null
            translator?.close()
        }
    }
}