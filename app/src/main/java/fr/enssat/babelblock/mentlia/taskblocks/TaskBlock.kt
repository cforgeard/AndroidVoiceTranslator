package fr.enssat.babelblock.mentlia.taskblocks

enum class TaskBlockAdditionalParameterType {
    LANGUAGE
}

data class TaskBlockAdditionalParameter(
    val id: String,
    val type: TaskBlockAdditionalParameterType,
    val defaultValue: String
)

interface TaskBlock {
    fun id(): String
    fun requireInputString(): Boolean
    fun willOutputString(): Boolean
    fun additionalParameters(): Array<TaskBlockAdditionalParameter>
    fun setAdditionalParameter(parameterID: String, value: String)
    suspend fun execute(inputString: String?): String?
}