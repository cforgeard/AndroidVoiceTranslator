package fr.enssat.babelblock.mentlia

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeToDeleteCallback(private val context: Context) : ItemTouchHelper.Callback() {

    private val background = ColorDrawable()
    private val clearPaint = Paint()
    private val deleteDrawable: Drawable
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int
    private val backgroundColor = Color.parseColor("#b80f0a")

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
        intrinsicWidth = deleteDrawable.intrinsicWidth
        intrinsicHeight = deleteDrawable.intrinsicHeight
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        background.color = backgroundColor
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
}