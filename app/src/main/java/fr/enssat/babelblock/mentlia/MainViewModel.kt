package fr.enssat.babelblock.mentlia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockChain
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockException
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockType
import kotlinx.coroutines.launch
import timber.log.Timber

// https://www.varvet.com/blog/voice-to-text-arch-android/
class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    data class ViewState(
        val preparingExecution: Boolean,
        val executing: Boolean,
        val error: TaskBlockException?,
        val taskBlock: TaskBlock?
    )

    val taskBlockChain = TaskBlockChain()
    val viewState = MutableLiveData(
        ViewState(
            preparingExecution = false,
            executing = false,
            error = null,
            taskBlock = null
        )
    )

    fun canBeRun(): Boolean {
        if (taskBlockChain.size == 0) return true
        if (taskBlockChain.get(0).getManifest().type != TaskBlockType.OUT) return false

        for (i in 1 until taskBlockChain.size) {
            val item = taskBlockChain.get(i)
            val previousItem = taskBlockChain.get(i - 1)

            if (item.getManifest().type != TaskBlockType.OUT &&
                previousItem.getManifest().type == TaskBlockType.IN
            ) {
                return false
            }
        }
        return true
    }

    fun run() {
        val tasks = taskBlockChain
        if (tasks.size == 0) return

        var previousTaskOutput: String? = null
        viewModelScope.launch {
            try {
                for (i in taskBlockChain.indices()) {
                    val task = tasks.get(i)
                    viewState.postValue(
                        ViewState(
                            preparingExecution = true,
                            executing = false,
                            error = null,
                            taskBlock = task
                        )
                    )

                    Timber.e("Task #$i (${task.getManifest().id}) prepareExecution...")
                    task.prepareExecution()
                    Timber.e("Task #$i (${task.getManifest().id}) prepareExecution OK")
                }

                for (i in taskBlockChain.indices()) {
                    val task = tasks.get(i)
                    viewState.postValue(
                        ViewState(
                            preparingExecution = false,
                            executing = true,
                            error = null,
                            taskBlock = task
                        )
                    )

                    Timber.e("Task #$i (${task.getManifest().id}) BEFORE <- $previousTaskOutput")
                    previousTaskOutput = task.execute(previousTaskOutput)
                    Timber.e("Task #$i (${task.getManifest().id}) AFTER -> $previousTaskOutput")
                }

                viewState.postValue(
                    ViewState(
                        preparingExecution = false,
                        executing = false,
                        error = null,
                        taskBlock = null
                    )
                )
            } catch (exception: TaskBlockException) {
                viewState.postValue(
                    ViewState(
                        preparingExecution = false,
                        executing = false,
                        error = exception,
                        taskBlock = null
                    )
                )
            }
        }
    }
}