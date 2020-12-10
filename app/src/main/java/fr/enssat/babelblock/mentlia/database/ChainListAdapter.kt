package fr.enssat.babelblock.mentlia.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.R

class ChainListAdapter(
    private val clickCallback: ClickCallback
) : ListAdapter<Chain, ChainListAdapter.ChainViewHolder>(ChainsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChainViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ChainViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chainTextView: TextView = itemView.findViewById(R.id.textView)

        fun bind(item: Chain) {
            var text = item.nom
            if (item.favori == 0) {
                text += " *"
            }

            chainTextView.text = text
            chainTextView.setOnClickListener { clickCallback.onItemClicked(item) }
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

    interface ClickCallback {
        fun onItemClicked(item: Chain)
    }

}
