package fr.enssat.babelblock.mentlia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockChain
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockFactory
import kotlinx.coroutines.launch
import timber.log.Timber

// https://www.varvet.com/blog/voice-to-text-arch-android/
class TestViewModel(private val app: Application) : AndroidViewModel(app) {

    data class ViewState(
        val preparingExecution: Boolean,
        val executing: Boolean,
        val taskBlock: TaskBlock?
    )

    val taskBlockChain = TaskBlockChain()
    val viewState = MutableLiveData(
        ViewState(
            preparingExecution = false,
            executing = false,
            taskBlock = null
        )
    )

    private var taskBlockFactory = TaskBlockFactory(app)

    fun getAvailableTaskBlocks(): List<String> {
        return taskBlockFactory.availableTaskBlock().map {
            app.getString(it.nameTextResource)
        }
    }

    fun createNewTaskBlock(index: Int) {
        taskBlockChain.add(
            taskBlockFactory.create(
                taskBlockFactory.availableTaskBlock()[index].id
            )
        )
    }

    fun run() {
        val tasks = taskBlockChain
        if (tasks.size == 0) return

        var previousTaskOutput: String? = null
        viewModelScope.launch {
            for (i in taskBlockChain.indices()) {
                val task = tasks.get(i)
                viewState.postValue(
                    ViewState(
                        preparingExecution = true,
                        executing = false,
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
                    taskBlock = null
                )
            )
        }
    }
}