package fr.enssat.babelblock.mentlia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockManifest


class AddTaskBlockRecyclerViewAdapter(
    private val values: Array<TaskBlockManifest>,
    private val onItemClickListener: OnClickListener
) : RecyclerView.Adapter<AddTaskBlockRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_add_taskblock_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.title.text = holder.title.context.getText(item.nameTextResource)
        holder.subtitle.text = holder.subtitle.context.getText(item.descriptionTextResource)
        holder.icon.setImageDrawable(
            ContextCompat.getDrawable(
                holder.icon.context,
                item.iconResource
            )
        )
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(item, holder.title.context)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.title)!!
        val subtitle = view.findViewById<TextView>(R.id.subtitle)!!
        val icon = view.findViewById<ImageView>(R.id.icon)!!
    }

    interface OnClickListener {
        fun onClick(taskBlockManifest: TaskBlockManifest, context: Context)
    }
}