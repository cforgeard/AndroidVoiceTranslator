package fr.enssat.babelblock.mentlia.taskblocks

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.RecyclerViewMoveHelper

class TaskBlockAdapter(
    private val taskBlockChain: TaskBlockChain,
    private val callback: RecyclerViewMoveHelper.StartDragListener
) :
    RecyclerView.Adapter<TaskBlockAdapter.TaskBlockViewHolder>(),
    RecyclerViewMoveHelper.ItemTouchHelperContract {

    init {
        taskBlockChain.setOnItemInsertedListener { notifyItemInserted(it) }
        taskBlockChain.setOnItemRemovedListener { notifyItemRemoved(it) }
    }

    override fun getItemCount(): Int = taskBlockChain.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskBlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_row, parent, false)
        return TaskBlockViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TaskBlockViewHolder, position: Int) {
        val item = taskBlockChain.get(position)
        holder.taskBlockView.setTaskBlock(item)
        when (item.getManifest().type) {
            TaskBlockType.IN -> holder.startCircle.visibility = GONE
            TaskBlockType.OUT -> holder.endCircle.visibility = GONE
            TaskBlockType.INOUT -> {
                holder.startCircle.visibility = GONE
                holder.endCircle.visibility = GONE
            }
        }

        holder.moveImageView.setOnTouchListener { _, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                callback.requestDrag(holder)
            }
            false
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        taskBlockChain.move(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is TaskBlockViewHolder) {
            viewHolder.cardView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun onRowClear(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is TaskBlockViewHolder) {
            viewHolder.cardView.setBackgroundColor(Color.WHITE)
        }
    }

    class TaskBlockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val moveImageView: ImageView = itemView.findViewById(R.id.moveImageView)
        val taskBlockView: TaskBlockView = itemView.findViewById(R.id.taskBlockView)
        val startCircle: ImageView = itemView.findViewById(R.id.startCircle)
        val endCircle: ImageView = itemView.findViewById(R.id.endCircle)
    }

}