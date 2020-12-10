package fr.enssat.babelblock.mentlia

import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import fr.enssat.babelblock.mentlia.database.Chain
import fr.enssat.babelblock.mentlia.database.ChainViewModel
import fr.enssat.babelblock.mentlia.database.ChainViewModelFactory
import fr.enssat.babelblock.mentlia.databinding.NewChainBinding


class NewChainFragment : DialogFragment() {

    private lateinit var binding: NewChainBinding
    private lateinit var viewModel: MainViewModel
    private val chainViewModel: ChainViewModel by viewModels {
        ChainViewModelFactory((requireActivity().application as Application).repository)
    }
    private var isDialog = false


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.new_chain, container, false)
        view.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //Perform Code
                //TODO
                return@setOnKeyListener true
            }
            false
        }
        binding = NewChainBinding.bind(view)
        binding.databaseSave.setOnClickListener {
            if (!TextUtils.isEmpty(binding.nom.text)) {
                val nom = binding.nom.text.toString()
                val favori = binding.favori.isChecked
                val chain = Chain(nom,booleanToInt(favori), viewModel.taskBlockChain.toJSON())
                chainViewModel.insert(chain)
            }
            if (isDialog) {
                dismiss()
            }
        }
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
            binding.newChainFragment.layoutParams = params
        }
        return view
    }

    fun booleanToInt(b: Boolean): Int {
        return if (b) 1 else 0
    }

    companion object {
        const val ARG_IS_DIALOG = "is-dialog"

        fun newInstance(isDialog: Boolean, viewModel: MainViewModel) =
            NewChainFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_DIALOG, isDialog)
                }
                this.viewModel = viewModel
            }
    }

}
