package fr.enssat.babelblock.mentlia.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_chain, parent, false)
        return ChainViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chainTextView: TextView = itemView.findViewById(R.id.content)
        private val chainButtonSupp: ImageButton = itemView.findViewById(R.id.supp_btn)
        private val chainFavorite: TextView = itemView.findViewById(R.id.item_number)

        fun bind(item: Chain) {
            var text = item.name
            if (item.favorite == 0) {
                chainFavorite.text = "*"
            }

            chainTextView.text = text
            chainTextView.setOnClickListener { clickCallback.onItemClicked(item) }
            chainButtonSupp.setOnClickListener { clickCallback.deleteItem(item) }
        }
    }

    class ChainsComparator : DiffUtil.ItemCallback<Chain>() {
        override fun areItemsTheSame(oldItem: Chain, newItem: Chain): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Chain, newItem: Chain): Boolean {
            return (oldItem.name == newItem.name && oldItem.json == oldItem.json)
        }
    }

    interface ClickCallback {
        fun onItemClicked(item: Chain)
        fun deleteItem(item: Chain)
    }

}
