package fr.enssat.babelblock.mentlia.taskblocks

import android.content.Context
import androidx.work.*
import fr.enssat.babelblock.mentlia.KEY_SOURCE_LANGUAGE
import fr.enssat.babelblock.mentlia.KEY_TARGET_LANGUAGE
import fr.enssat.babelblock.mentlia.KEY_TEXT
import fr.enssat.babelblock.mentlia.TranslateWorker
import kotlinx.coroutines.delay
import timber.log.Timber


class TranslatorBlock(appContext: Context) : TaskBlock {

    companion object {
        val ARG_TRANSLATE_SOURCE_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TRANSLATE_SOURCE_LANGUAGE",
            TaskBlockAdditionalParameterType.LANGUAGE,
            "fr"
        )

        val ARG_TRANSLATE_TARGET_LANGUAGE = TaskBlockAdditionalParameter(
            "ARG_TRANSLATE_TARGET_LANGUAGE",
            TaskBlockAdditionalParameterType.LANGUAGE,
            "en"
        )

        private const val TRANSLATE_WORK_NAME = "TRANSLATE_WORK_NAME"
        private const val TRANSLATE_WORKER_TAG = "TRANSLATE_WORKER_TAG"
    }

    private val workManager = WorkManager.getInstance(appContext)
    private var sourceLanguage = ARG_TRANSLATE_SOURCE_LANGUAGE.defaultValue
    private var targetLanguage = ARG_TRANSLATE_TARGET_LANGUAGE.defaultValue

    override fun id(): String {
        return "TranslatorBlock"
    }

    override fun requireInputString(): Boolean {
        return true
    }

    override fun willOutputString(): Boolean {
        return true
    }

    override fun additionalParameters(): Array<TaskBlockAdditionalParameter> {
        return arrayOf(ARG_TRANSLATE_SOURCE_LANGUAGE, ARG_TRANSLATE_TARGET_LANGUAGE)
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

    override suspend fun execute(inputString: String?): String? {
        val data = workDataOf(
            KEY_SOURCE_LANGUAGE to sourceLanguage,
            KEY_TARGET_LANGUAGE to targetLanguage,
            KEY_TEXT to inputString!!
        )

        Timber.i("Enqueue workTask...")
        workManager.beginUniqueWork(
            TRANSLATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<TranslateWorker>().addTag(TRANSLATE_WORKER_TAG)
                .setInputData(data).build()
        ).enqueue()

        Timber.i("Work Task launched, waiting for end...")
        var workResult = workManager.getWorkInfosByTag(TRANSLATE_WORKER_TAG).await()!![0]
        while (workResult.state in arrayOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED)) {
            delay(100)
            workResult = workManager.getWorkInfosByTag(TRANSLATE_WORKER_TAG).await()!![0]
        }

        if (workResult.state == WorkInfo.State.SUCCEEDED) {
            val result = workResult.outputData.getString(KEY_TEXT)!!
            Timber.i("Translate success, result = %s", result)
            return result
        } else {
            throw TaskBlockException("TranslatorBlock error : error_generic")
        }
    }
}