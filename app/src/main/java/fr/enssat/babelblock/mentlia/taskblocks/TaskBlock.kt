package fr.enssat.babelblock.mentlia.taskblocks

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes

enum class TaskBlockAdditionalParameterType {
    LANGUAGE
}

enum class TaskBlockType {
    IN,
    OUT,
    INOUT
}

data class TaskBlockManifest(
    val id: String,
    val type: TaskBlockType,
    @StringRes val nameTextResource: Int,
    val additionalParameters: Array<TaskBlockAdditionalParameter>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskBlockManifest

        if (id != other.id) return false
        if (type != other.type) return false
        if (nameTextResource != other.nameTextResource) return false
        if (!additionalParameters.contentEquals(other.additionalParameters)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + nameTextResource
        result = 31 * result + additionalParameters.contentHashCode()
        return result
    }
}

data class TaskBlockAdditionalParameter(
    val id: String,
    @StringRes val nameResource: Int,
    val type: TaskBlockAdditionalParameterType,
    val defaultValue: String
)

interface TaskBlock {
    fun getManifest(): TaskBlockManifest
    fun setAdditionalParameter(parameterID: String, value: String)
    fun getPrepareExecutionView(layoutInflater: LayoutInflater, resources: Resources): View
    fun getExecuteView(layoutInflater: LayoutInflater, resources: Resources): View
    suspend fun prepareExecution()
    suspend fun execute(inputString: String?): String?
}