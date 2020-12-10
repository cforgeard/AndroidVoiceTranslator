package fr.enssat.babelblock.mentlia

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import fr.enssat.babelblock.mentlia.database.*
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockAdapter

/**
 * A fragment representing a list of Items.
 */
class FavoriFragment : DialogFragment() {
    private var instance: FavoriFragment? = null

    init {
        instance = this
    }

    private val chainViewModel: ChainViewModel by viewModels {
        ChainViewModelFactory((requireActivity().application as Application).repository)
    }
    private var columnCount = 1
    private var isDialog = false
    private var recyclerView: RecyclerView? = null
    private var adapter: ChainListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            isDialog = it.getBoolean(ARG_IS_DIALOG, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favori_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView = findViewById<RecyclerView>(R.id.recyclerviewtest)
                this.adapter = ChainListAdapter(object : TaskBlockAdapter.DeleteCallback {
                    override fun deleteItem(item: Chain) {
                        val position = chainViewModel.allChains.indexOf(item)
                    }
                })
                chainViewModel.allChains.observe(requireActivity(), Observer { chain ->
                    // Update the cached copy of the words in the adapter.
                    chain?.let { (this.adapter as ChainListAdapter).submitList(it) }
                })

            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView?.adapter = this.adapter

        recyclerView?.setOnClickListener{

            if (isDialog) {
                dismiss()
            }
        }

    }

    companion object {
        const val ARG_IS_DIALOG = "is-dialog"
        const val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int,isDialog: Boolean) =
            FavoriFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putBoolean(ARG_IS_DIALOG, isDialog)
                }
            }
    }
}