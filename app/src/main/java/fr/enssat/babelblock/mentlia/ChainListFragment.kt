package fr.enssat.babelblock.mentlia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.enssat.babelblock.mentlia.database.Chain
import fr.enssat.babelblock.mentlia.database.ChainListAdapter
import fr.enssat.babelblock.mentlia.database.ChainViewModel
import fr.enssat.babelblock.mentlia.database.ChainViewModelFactory

class ChainListFragment : DialogFragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private var isFavorite = false

    private val chainViewModel: ChainViewModel by viewModels {
        ChainViewModelFactory((requireActivity().application as Application).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chain_list, container, false)

        viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(MainViewModel::class.java)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = ChainListAdapter(
                    object : ChainListAdapter.ClickCallback {
                        override fun onItemClicked(item: Chain) {
                            viewModel.taskBlockChain.fromJSON(item.json, requireContext())
                            dismiss()
                        }
                        override fun deleteItem(item: Chain) {
                            chainViewModel.deleteId(item)
                        }
                    }
                )
                if(isFavorite){
                    chainViewModel.favoriteChains.observe(requireActivity(), { chain ->
                        chain?.let { (adapter as ChainListAdapter).submitList(it) }
                    })
                }else{
                    chainViewModel.allChains.observe(requireActivity(), { chain ->
                        chain?.let { (adapter as ChainListAdapter).submitList(it) }
                    })
                }
            }
        }

        return view
    }

    companion object {
        const val ARG_IS_FAVORITE = "is-favorite"

        fun newInstance(isFavorite: Boolean) =
            ChainListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_FAVORITE, isFavorite)
                }
            }
    }

}