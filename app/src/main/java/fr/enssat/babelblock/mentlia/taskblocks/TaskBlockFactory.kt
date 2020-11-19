package fr.enssat.babelblock.mentlia.taskblocks

import android.app.Application
import fr.enssat.babelblock.mentlia.taskblocks.blocks.SpeechRecognizerBlock
import fr.enssat.babelblock.mentlia.taskblocks.blocks.TextToSpeechBlock
import fr.enssat.babelblock.mentlia.taskblocks.blocks.TranslatorBlock

class TaskBlockFactory(private val application: Application) {

    @Suppress("UNCHECKED_CAST")
    fun availableTaskBlock(): Array<TaskBlockManifest> {
        return arrayOf(
            SpeechRecognizerBlock.MANIFEST,
            TranslatorBlock.MANIFEST,
            TextToSpeechBlock.MANIFEST
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : TaskBlock> create(taskBlockID: String): T {
        return when (taskBlockID) {
            SpeechRecognizerBlock.MANIFEST.id -> SpeechRecognizerBlock(application.applicationContext) as T
            TranslatorBlock.MANIFEST.id -> TranslatorBlock(application.applicationContext) as T
            TextToSpeechBlock.MANIFEST.id -> TextToSpeechBlock(application.applicationContext) as T
            else -> throw IllegalArgumentException("Unknown TaskBlock class")
        }
    }
}