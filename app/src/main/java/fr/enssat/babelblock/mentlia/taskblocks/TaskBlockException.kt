package fr.enssat.babelblock.mentlia.taskblocks

@Suppress("CanBeParameter")
class TaskBlockException @JvmOverloads constructor(
    val taskBlock: TaskBlock,
    val detail: String,
    val userErrorMessage: String,
    cause: Throwable? = null,
) : RuntimeException("${taskBlock.getManifest().id} error $detail", cause)