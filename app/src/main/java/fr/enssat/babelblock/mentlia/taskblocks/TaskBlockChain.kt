package fr.enssat.babelblock.mentlia.taskblocks

import android.content.Context
import org.json.JSONArray

class TaskBlockChain(list: List<TaskBlock> = emptyList()) {

    private val list = list.toMutableList()
    val size
        get() = list.size

    private var onItemInsertedListener: ((position: Int) -> Unit)? = null
    private var onItemMovedListener: ((oldPosition: Int, newPosition: Int) -> Unit)? = null
    private var onItemRemovedListener: ((position: Int) -> Unit)? = null

    fun setOnItemInsertedListener(callback: (position: Int) -> Unit) {
        onItemInsertedListener = callback
    }

    fun setOnItemRemovedListener(callback: (position: Int) -> Unit) {
        onItemRemovedListener = callback
    }

    fun add(taskBlock: TaskBlock) {
        list.add(taskBlock)
        onItemInsertedListener?.invoke(list.size - 1)
    }

    fun add(taskBlock: TaskBlock, position: Int) {
        list.add(position, taskBlock)
        onItemInsertedListener?.invoke(position)
    }

    fun get(index: Int) = list[index]

    fun move(from: Int, to: Int) {
        val dragged = list.removeAt(from)
        list.add(to, dragged)
        onItemMovedListener?.invoke(from, to)
    }

    fun removeAt(index: Int) {
        list.removeAt(index)
        onItemRemovedListener?.invoke(index)
    }

    fun removeAll() {
        for (i in indices()) {
            removeAt(0)
        }
    }

    fun indexOf(taskBlock: TaskBlock): Int {
        return list.indexOf(taskBlock)
    }

    fun indices(): IntRange {
        return list.indices
    }

    fun toJSON(): JSONArray {
        val jsonArray = JSONArray()
        for (item in list) {
            val jsonObject = item.toJSON()
            jsonObject.put("__id", item.getManifest().id)
            jsonArray.put(jsonObject)
        }
        return jsonArray
    }

    fun fromJSON(jsonArray: JSONArray, appContext: Context) {
        removeAll()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val taskBlock: TaskBlock =
                TaskBlockFactory.create(jsonObject.getString("__id"), appContext)
            taskBlock.fromJSON(jsonObject)
            add(taskBlock)
        }
    }

}