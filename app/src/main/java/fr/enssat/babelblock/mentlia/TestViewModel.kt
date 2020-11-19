package fr.enssat.babelblock.mentlia

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.enssat.babelblock.mentlia.taskblocks.*
import fr.enssat.babelblock.mentlia.taskblocks.SpeechRecognizerBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockException
import kotlinx.coroutines.launch
import timber.log.Timber

// https://www.varvet.com/blog/voice-to-text-arch-android/
class TestViewModel(application: Application) : AndroidViewModel(application) {

    data class ViewState(val value: String)

    private var viewState: MutableLiveData<ViewState> = MutableLiveData()
    val speechRecognizerBlock = SpeechRecognizerBlock(application.applicationContext)
    val ttsBlock = TextToSpeechBlock(application.applicationContext)
    val translateBlock = TranslatorBlock(application.applicationContext)

    init {
        viewState.value = initViewState()
    }

    var permissionToRecordAudio = checkAudioRecordingPermission(context = application)

    fun getViewState(): LiveData<ViewState> {
        return viewState
    }

    private fun initViewState() =
        ViewState("")

    fun run() {
        val executor = TaskBlockExecutor(3)
        executor.add(speechRecognizerBlock)
        executor.add(translateBlock)
        executor.add(ttsBlock)

        viewModelScope.launch {
            try {
                executor.execute()
            } catch (ex: TaskBlockException) {
                Timber.e(ex)
            }
        }
    }

    private fun checkAudioRecordingPermission(context: Application) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
}