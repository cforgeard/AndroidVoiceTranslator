package fr.enssat.babelblock.mentlia

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveCallback(private val adapter: ItemTouchHelperContract) : ItemTouchHelper.Callback() {

    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(myViewHolder: RecyclerViewAdapter.ViewHolder)
        fun onRowClear(myViewHolder: RecyclerViewAdapter.ViewHolder)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition);
        return true;
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is RecyclerViewAdapter.ViewHolder) {
                val myViewHolder: RecyclerViewAdapter.ViewHolder = viewHolder
                adapter.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is RecyclerViewAdapter.ViewHolder) {
            val myViewHolder: RecyclerViewAdapter.ViewHolder = viewHolder
            adapter.onRowClear(myViewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}