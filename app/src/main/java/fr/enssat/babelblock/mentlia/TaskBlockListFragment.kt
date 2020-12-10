package fr.enssat.babelblock.mentlia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fr.enssat.babelblock.mentlia.databinding.FragmentTaskblockListBinding
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockAdapter

class TaskBlockListFragment : Fragment() {

    private lateinit var binding: FragmentTaskblockListBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var moveHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_taskblock_list, container, false)
        binding = FragmentTaskblockListBinding.bind(view)

        viewModelFactory = MainViewModelFactory(activity!!.application)
        viewModel = ViewModelProvider(
            activity!!,
            viewModelFactory
        ).get(MainViewModel::class.java)

        val adapter = TaskBlockAdapter(
            viewModel.taskBlockChain,
            object : RecyclerViewMoveHelper.StartDragListener {
                override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
                    moveHelper.startDrag(viewHolder)
                }
            },
            object : TaskBlockAdapter.DeleteCallback {
                override fun deleteItem(item: TaskBlock) {
                    val position = viewModel.taskBlockChain.indexOf(item)
                    showUndoDeleteSnackbar(item, position)
                    viewModel.taskBlockChain.removeAt(position)
                }
            })
        binding.recyclerView.adapter = adapter
        moveHelper = RecyclerViewMoveHelper.create(adapter)
        moveHelper.attachToRecyclerView(binding.recyclerView)

        return view
    }

    private fun showUndoDeleteSnackbar(item: TaskBlock, position: Int) {
        val snackbar = Snackbar
            .make(binding.root, R.string.item_was_removed, Snackbar.LENGTH_LONG)

        snackbar.setAction(R.string.undo) {
            viewModel.taskBlockChain.add(item, position)
            binding.recyclerView.scrollToPosition(position)
        }
        snackbar.show()
    }

}