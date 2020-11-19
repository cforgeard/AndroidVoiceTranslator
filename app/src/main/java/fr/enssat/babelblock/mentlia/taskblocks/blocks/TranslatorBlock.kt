package fr.enssat.babelblock.mentlia.taskblocks.blocks

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.taskblocks.*
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
            continuation.resumeWithException(task.exception ?: RuntimeException("Unknown task exception"))
        }
    }
}

class TranslatorBlock(appContext: Context) : TaskBlock {

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
            R.string.translator_block_prepare_execution,
            R.string.translator_block_execute,
            arrayOf(ARG_TRANSLATE_SOURCE_LANGUAGE, ARG_TRANSLATE_TARGET_LANGUAGE)
        )

    }

    private var translator: Translator? = null
    private var sourceLanguage = ARG_TRANSLATE_SOURCE_LANGUAGE.defaultValue
    private var targetLanguage = ARG_TRANSLATE_TARGET_LANGUAGE.defaultValue

    override fun getManifest(): TaskBlockManifest {
        return MANIFEST
    }

    override fun setAdditionalParameter(parameterID: String, value: String) {
        Timber.e("%s -> %s", parameterID, value)
        if (parameterID == ARG_TRANSLATE_SOURCE_LANGUAGE.id) {
            sourceLanguage = value
        } else if (parameterID == ARG_TRANSLATE_TARGET_LANGUAGE.id) {
            targetLanguage = value
        } else {
            throw TaskBlockException("Unknown parameter : $parameterID")
        }
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
            throw TaskBlockException("TranslatorBlock.prepareExecution failed", throwable)
        }
    }

    override suspend fun execute(inputString: String?): String? {
        try {
            Timber.i("translate start...")
            val translateTask = translator!!.translate(inputString!!)
            val translatedText = translateTask.await()!!
            Timber.i("translate OK")

            return translatedText
        } catch (throwable: Throwable) {
            throw TaskBlockException("TranslatorBlock.execute failed", throwable)
        } finally {
            translator?.close()
        }
    }
}