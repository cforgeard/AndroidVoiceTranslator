package fr.enssat.babelblock.mentlia

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.Translation.getClient
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import timber.log.Timber

const val KEY_SOURCE_LANGUAGE = "KEY_SOURCE_LANGUAGE"
const val KEY_TARGET_LANGUAGE = "KEY_TARGET_LANGUAGE"
const val KEY_TEXT = "KEY_TEXT"

class TranslateWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        var translator: Translator? = null
        return try {
            val sourceLanguage = inputData.getString(KEY_SOURCE_LANGUAGE)!!
            val targetLanguage = inputData.getString(KEY_TARGET_LANGUAGE)!!
            val textToTranslate = inputData.getString(KEY_TEXT)!!

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            translator = getClient(options)
            Timber.w("downloadModelIfNeeded")
            Tasks.await(translator.downloadModelIfNeeded())

            Timber.w("translate")
            val translateTask = translator.translate(textToTranslate)
            val translatedText = Tasks.await(translateTask)!!

            Timber.w("SUCCESS! $translatedText")
            val outputData = workDataOf(KEY_TEXT to translatedText)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            Result.failure()
        } finally {
            translator?.close()
        }
    }

}