package fr.enssat.babelblock.mentlia.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        private val content: TextView = itemView.findViewById(R.id.content)
        private val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
        private val favoriteImageView: ImageView = itemView.findViewById(R.id.favoriteImageView)

        fun bind(item: Chain) {
            content.text = item.name
            itemView.setOnClickListener { clickCallback.onItemClicked(item) }
            deleteImageView.setOnClickListener { clickCallback.deleteItem(item) }

            if (item.favorite == 0) favoriteImageView.setImageDrawable(null)
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
