package fr.enssat.babelblock.mentlia.taskblocks

import java.lang.RuntimeException

class TaskBlockException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)