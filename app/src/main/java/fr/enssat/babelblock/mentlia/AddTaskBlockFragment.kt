package fr.enssat.babelblock.mentlia

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import fr.enssat.babelblock.mentlia.databinding.FragmentAddTaskblockBinding
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockFactory
import fr.enssat.babelblock.mentlia.taskblocks.TaskBlockManifest


class AddTaskBlockFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTaskblockBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private var isDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isDialog = it.getBoolean(ARG_IS_DIALOG, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_taskblock, container, false)
        binding = FragmentAddTaskblockBinding.bind(view)

        viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MainViewModel::class.java)

        binding.recyclerView.adapter =
            AddTaskBlockRecyclerViewAdapter(
                TaskBlockFactory.availableTaskBlock(),
                object : AddTaskBlockRecyclerViewAdapter.OnClickListener {
                    override fun onClick(taskBlockManifest: TaskBlockManifest, context: Context) {
                        viewModel.taskBlockChain.add(
                            TaskBlockFactory.create(
                                taskBlockManifest.id,
                                context
                            )
                        )

                        if (isDialog) {
                            dismiss()
                        }
                    }
                }
            )

        if (isDialog) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16F,
                resources.displayMetrics
            ).toInt()

            params.setMargins(px, 0, px, px)
            binding.recyclerView.layoutParams = params
        } else {
            binding.title.visibility = GONE
        }

        return view
    }

    companion object {
        const val ARG_IS_DIALOG = "is-dialog"
        fun newInstance(isDialog: Boolean) =
            AddTaskBlockFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_DIALOG, isDialog)
                }
            }

    }
}