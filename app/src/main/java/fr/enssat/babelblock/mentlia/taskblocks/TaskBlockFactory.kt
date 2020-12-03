package fr.enssat.babelblock.mentlia.taskblocks

import android.content.Context
import fr.enssat.babelblock.mentlia.taskblocks.blocks.SpeechRecognizerBlock
import fr.enssat.babelblock.mentlia.taskblocks.blocks.TextToSpeechBlock
import fr.enssat.babelblock.mentlia.taskblocks.blocks.TranslatorBlock

class TaskBlockFactory {

    companion object {
        fun availableTaskBlock(): Array<TaskBlockManifest> {
            return arrayOf(
                SpeechRecognizerBlock.MANIFEST,
                TranslatorBlock.MANIFEST,
                TextToSpeechBlock.MANIFEST
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : TaskBlock> create(taskBlockID: String, appContext: Context): T {
            return when (taskBlockID) {
                SpeechRecognizerBlock.MANIFEST.id -> SpeechRecognizerBlock(appContext) as T
                TranslatorBlock.MANIFEST.id -> TranslatorBlock(appContext) as T
                TextToSpeechBlock.MANIFEST.id -> TextToSpeechBlock(appContext) as T
                else -> throw IllegalArgumentException("Unknown TaskBlock class")
            }
        }
    }
}