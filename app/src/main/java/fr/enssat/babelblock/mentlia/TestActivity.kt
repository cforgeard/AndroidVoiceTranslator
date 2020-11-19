package fr.enssat.babelblock.mentlia

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import fr.enssat.babelblock.mentlia.databinding.ActivityTestBinding
import timber.log.Timber


class TestActivity : AppCompatActivity(), StartDragListener {

    private lateinit var binding: ActivityTestBinding
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var viewModel: TestViewModel
    private lateinit var viewModelFactory: TestViewModelFactory
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var progressDialog: ProgressDialog

    private val REQUEST_CODE_RECORD_AUDIO_PERMISSION = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this, ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)

        recyclerViewAdapter = RecyclerViewAdapter(this)
        binding.recyclerView.adapter = recyclerViewAdapter
        touchHelper = ItemTouchHelper(ItemMoveCallback(recyclerViewAdapter))
        touchHelper.attachToRecyclerView(binding.recyclerView)
        enableSwipeToDeleteAndUndo();

        viewModelFactory = TestViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)
        viewModel.tasks.observe(this, { recyclerViewAdapter.data = it })
        viewModel.progressMessage.observe(this, { setProgressMessage(it) })

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

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    private fun setProgressMessage(@StringRes stringRes: Int?) {
        Timber.e("progressMessage = %s", if (stringRes == null) "null" else getString(stringRes))
        if (stringRes == null) {
            this.progressDialog.setTitle("")
            this.progressDialog.setCancelable(true)
            this.progressDialog.cancel()
        } else {
            this.progressDialog.setTitle(stringRes)
            this.progressDialog.show()
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

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item = recyclerViewAdapter.data[position]
                viewModel.removeTaskBlock(position)

                val snackbar = Snackbar
                    .make(
                        binding.root,
                        "Item was removed from the list.",
                        Snackbar.LENGTH_LONG
                    )
                snackbar.setAction("UNDO") {
                    viewModel.addTaskBlock(item, position)
                    binding.recyclerView.scrollToPosition(position)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
}