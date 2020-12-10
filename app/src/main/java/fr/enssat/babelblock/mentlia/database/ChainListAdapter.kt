package fr.enssat.babelblock.mentlia.database

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.R
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockAdapter

class ChainListAdapter(
    private val deleteCallback: TaskBlockAdapter.DeleteCallback
) : ListAdapter<Chain, ChainListAdapter.ChainViewHolder>(ChainsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChainViewHolder {
        return ChainViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ChainViewHolder, position: Int) {
        val current = getItem(position)
        var text = current.nom
        if (current.favori == 0) {
            text = text + " *"
        }
        holder.bind(text)
        deleteCallback.deleteItem(current)
        holder.moveImageView.setOnTouchListener { _, event ->
            if (event.action ==
                MotionEvent.ACTION_DOWN
            ) {
                startDragListener.requestDrag(holder)
            }
            false
        }
    }

    class ChainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ChainItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            if(text == ""){
                ChainItemView.text = "Pas d'élément dans base de données"
            }else{
                ChainItemView.text = text
            }
        }

        companion object {
            fun create(parent: ViewGroup): ChainViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return ChainViewHolder(view)
            }
        }
    }

    class ChainsComparator : DiffUtil.ItemCallback<Chain>() {
        override fun areItemsTheSame(oldItem: Chain, newItem: Chain): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Chain, newItem: Chain): Boolean {
            return (oldItem.nom == newItem.nom && oldItem.json == oldItem.json)
        }
    }
}
