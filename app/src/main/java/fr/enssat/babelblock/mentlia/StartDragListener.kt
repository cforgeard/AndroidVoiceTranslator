package fr.enssat.babelblock.mentlia

import androidx.recyclerview.widget.RecyclerView

interface StartDragListener {
    fun requestDrag(viewHolder: RecyclerView.ViewHolder)
}