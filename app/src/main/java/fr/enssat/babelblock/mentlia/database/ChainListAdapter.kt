package fr.enssat.babelblock.mentlia.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.R

class ChainListAdapter : ListAdapter<Chain, ChainListAdapter.ChainViewHolder>(ChainsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChainViewHolder {
        return ChainViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ChainViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.nom)
    }

    class ChainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ChainItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            ChainItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): ChainViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview, parent, false)
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
