package fr.enssat.babelblock.mentlia.taskblocks

class TaskBlockChain(list: List<TaskBlock> = emptyList()) {

    private val list = list.toMutableList()
    val size
        get() = list.size

    private var onChangeListener: (() -> Unit)? = null
    fun setOnChangeListener(callback: () -> Unit) {
        onChangeListener = callback
    }

    fun add(taskBlock: TaskBlock) {
        list.add(taskBlock)
        onChangeListener?.invoke()
    }

    fun add(taskBlock: TaskBlock, position: Int) {
        list.add(position, taskBlock)
        onChangeListener?.invoke()
    }

    fun get(index: Int) = list[index]

    fun move(from: Int, to: Int) {
        val dragged = list.removeAt(from)
        list.add(to, dragged)
        onChangeListener?.invoke()
    }

    fun removeAt(index: Int) {
        list.removeAt(index)
        onChangeListener?.invoke()
    }

    fun indices(): IntRange {
        return list.indices
    }

}