package fr.enssat.babelblock.mentlia.taskblocks

import timber.log.Timber
import java.lang.RuntimeException

class TaskBlockExecutor(initialCapacity: Int) : ArrayList<TaskBlock>(initialCapacity) {

    suspend fun execute() {
        if (size == 0) return

        if (get(0).getManifest().type !== TaskBlockType.OUT) {
            throw RuntimeException("First TaskBlock cannot require input string")
        }

        for (i in 1 until size) {
            val currentTask = get(i)
            val taskBeforeCurrentTask = get(i - 1)

            if (currentTask.getManifest().type != TaskBlockType.OUT && taskBeforeCurrentTask.getManifest().type == TaskBlockType.IN) {
                throw RuntimeException(
                    """
                    Invalid task combination : Task #$i (${currentTask.getManifest().id}) requires an input string but previous task ${taskBeforeCurrentTask.getManifest().id} don't output one
                    """.trimIndent()
                )
            }
        }

        Timber.e("Task #0 (${get(0).getManifest().id}) BEFORE <- null")
        var previousTaskOuput: String? = get(0).execute(null)
        Timber.e("Task #0 (${get(0).getManifest().id}) AFTER -> $previousTaskOuput")
        for (i in 1 until size) {
            Timber.e("Task #$i (${get(i).getManifest().id}) BEFORE <- $previousTaskOuput")
            previousTaskOuput = get(i).execute(previousTaskOuput)
            Timber.e("Task #$i (${get(i).getManifest().id}) AFTER -> $previousTaskOuput")
        }
    }
}