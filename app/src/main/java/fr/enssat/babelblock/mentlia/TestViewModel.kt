package fr.enssat.babelblock.mentlia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

// https://www.varvet.com/blog/voice-to-text-arch-android/
class TestViewModel(private val app: Application) : AndroidViewModel(app) {

    val tasks = MutableLiveData<ArrayList<TaskBlock>>(ArrayList())
    val progressMessage = MutableLiveData<Int?>(null)

    private var taskBlockFactory = TaskBlockFactory(app)

    fun getAvailableTaskBlocks(): List<String> {
        return taskBlockFactory.availableTaskBlock().map {
            app.getString(it.nameTextResource)
        }
    }

    fun createNewTaskBlock(index: Int) {
        addTaskBlock(
            taskBlockFactory.create(
                taskBlockFactory.availableTaskBlock()[index].id
            )
        )
    }

    fun addTaskBlock(taskBlock: TaskBlock) {
        val list = tasks.value!!
        list.add(taskBlock)
        tasks.postValue(list)
    }

    fun addTaskBlock(taskBlock: TaskBlock, index: Int) {
        val list = tasks.value!!
        list.add(index, taskBlock)
        tasks.postValue(list)
    }

    fun removeTaskBlock(index: Int) {
        val list = tasks.value!!
        list.removeAt(index)
        tasks.postValue(list)
    }

    fun run() {
        val tasks = tasks.value!!
        if (tasks.size == 0) return

        var previousTaskOuput: String? = null
        viewModelScope.launch {
            for (i in tasks.indices) {
                val task = tasks[i]
                progressMessage.postValue(task.getManifest().prepareTextExecuteResource)
                Timber.e("Task #$i (${task.getManifest().id}) prepareExecution...")
                task.prepareExecution()
                Timber.e("Task #$i (${task.getManifest().id}) prepareExecution OK")
            }

            for (i in tasks.indices) {
                val task = tasks[i]
                progressMessage.postValue(task.getManifest().executeTextResource)
                Timber.e("Task #$i (${task.getManifest().id}) BEFORE <- $previousTaskOuput")
                previousTaskOuput = task.execute(previousTaskOuput)
                Timber.e("Task #$i (${task.getManifest().id}) AFTER -> $previousTaskOuput")
            }

            progressMessage.postValue(null)
        }
    }
}