package fr.enssat.babelblock.mentlia

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import fr.enssat.babelblock.mentlia.databinding.ActivityTestBinding
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlock
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockAdapter


class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var adapter: TaskBlockAdapter
    private lateinit var viewModel: TestViewModel
    private lateinit var viewModelFactory: TestViewModelFactory
    private lateinit var moveHelper: ItemTouchHelper
    private var loadingDialog: AlertDialog? = null

    private val REQUEST_CODE_RECORD_AUDIO_PERMISSION = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = TestViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)

        adapter = TaskBlockAdapter(
            viewModel.taskBlockChain,
            object : RecyclerViewMoveHelper.StartDragListener {
                override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
                    moveHelper.startDrag(viewHolder)
                }
            })
        binding.recyclerView.adapter = adapter

        moveHelper = RecyclerViewMoveHelper.create(adapter)
        moveHelper.attachToRecyclerView(binding.recyclerView)

        val swipeHandler = object : RecyclerViewSwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showUndoDeleteSnackbar(
                    viewModel.taskBlockChain.get(viewHolder.adapterPosition),
                    viewHolder.adapterPosition
                )
                viewModel.taskBlockChain.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        viewModel.viewState.observe(this, { refresh() })
        binding.runBtn.setOnClickListener {
            viewModel.run()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_task_block -> {
                    openNewBlockDialog()
                    true
                }
                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /*if (requestCode == REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            viewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (viewModel.permissionToRecordAudio) {
            //binding.micBtn.performClick()
        }*/
    }

    private fun refresh() {
        val viewState = viewModel.viewState.value!!
        if (viewState.preparingExecution) {
            this.createLoadingDialogIfNecessary()
            loadingDialog!!.setContentView(
                viewState.taskBlock!!.getPrepareExecutionView(
                    layoutInflater, resources
                )
            )

        } else if (viewState.executing) {
            this.createLoadingDialogIfNecessary()
            loadingDialog!!.setContentView(
                viewState.taskBlock!!.getExecuteView(
                    layoutInflater, resources
                )
            )

        } else {
            if (loadingDialog != null) {
                loadingDialog!!.dismiss()
                loadingDialog = null
            }
        }
    }

    private fun createLoadingDialogIfNecessary() {
        if (loadingDialog == null) {
            loadingDialog = MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .show()
        }
    }

    private fun openNewBlockDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.add_task_block))
            .setItems(viewModel.getAvailableTaskBlocks().toTypedArray()) { _, which ->
                viewModel.createNewTaskBlock(which)
            }
            .show()
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