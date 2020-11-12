package fr.enssat.babelblock.mentlia.taskblocks

import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import timber.log.Timber
import java.lang.RuntimeException

class TaskBlockExecutor(initialCapacity: Int) : ArrayList<TaskBlock>(initialCapacity) {

    suspend fun execute() {
        if (size == 0) return

        if (get(0).requireInputString()) {
            throw RuntimeException("First TaskBlock cannot require input string")
        }

        for (i in 1..size - 1) {
            val currentTask = get(i)
            val taskBeforeCurrentTask = get(i - 1)

            if (currentTask.requireInputString() && !taskBeforeCurrentTask.willOutputString()) {
                throw RuntimeException(
                    """
                    Invalid task combination : Task #$i (${currentTask.id()}) requires an input string but previous task ${taskBeforeCurrentTask.id()} don't output one
                    """.trimIndent()
                )
            }
        }

        Timber.e("Task #0 (${get(0).id()}) BEFORE <- null")
        var previousTaskOuput: String? = get(0).execute(null)
        Timber.e("Task #0 (${get(0).id()}) AFTER -> $previousTaskOuput")
        for (i in 1..size - 1) {
            Timber.e("Task #$i (${get(i).id()}) BEFORE <- $previousTaskOuput")
            previousTaskOuput = get(i).execute(previousTaskOuput)
            Timber.e("Task #$i (${get(i).id()}) AFTER -> $previousTaskOuput")
        }
    }
}