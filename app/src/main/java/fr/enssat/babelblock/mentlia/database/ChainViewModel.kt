package fr.enssat.babelblock.mentlia.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ChainViewModel (private val repository: ChainRepository) : ViewModel() {

    // Using LiveData and caching what allChain returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allChains: LiveData<List<Chain>> = repository.allChain.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(chain: Chain) = viewModelScope.launch {
        repository.insert(chain)
    }
    /**
     * Launching a new coroutine to delete the data in a non-blocking way
     */
    fun deleteId(chain: Chain) = viewModelScope.launch {
        repository.deleteId(chain)
    }
}

class ChainViewModelFactory(private val repository: ChainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}