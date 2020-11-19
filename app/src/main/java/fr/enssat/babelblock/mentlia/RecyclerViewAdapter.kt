package fr.enssat.babelblock.mentlia

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockView
import timber.log.Timber
import java.util.*

class RecyclerViewAdapter(
    private val callback: StartDragListener
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val taskBlockView: TaskBlockView = itemView.findViewById(R.id.taskBlockView)
    }

    var data = listOf<TaskBlock>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskBlockView.setTaskBlock(data[position])
        holder.imageView.setOnTouchListener { _, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                callback.requestDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: ViewHolder) {
        myViewHolder.cardView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onRowClear(myViewHolder: ViewHolder) {
        myViewHolder.cardView.setBackgroundColor(Color.WHITE)
    }


}