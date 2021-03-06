package io.vortex.android.reducer

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.vortex.android.VortexSingleStateViewModelImpl
import io.vortex.android.rx.VortexRxRepository
import io.vortex.android.state.VortexLoadingState
import io.vortex.android.state.VortexState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class VortexSingleStateViewModel<State : VortexState> : ViewModel(), VortexSingleStateViewModelImpl<State> {

    private val state: MediatorLiveData<State> by lazy {
        MediatorLiveData<State>()
    }

    private val repository: VortexRxRepository by lazy {
        VortexRxRepository()
    }

    private val loadingState: MutableLiveData<VortexLoadingState> by lazy {
        MutableLiveData<VortexLoadingState>()
    }

    override suspend fun acceptNewState(newState: State) {
        withContext(Dispatchers.IO) {
            getStateHandler().postValue(newState)
        }
    }

    override suspend fun acceptLoadingState(newState: Boolean) {
        withContext(Dispatchers.IO) {
            getLoadingStateHandler().postValue(VortexLoadingState(newState))
        }
    }

    override suspend fun addRxRequest(request: Disposable) {
        withContext(Dispatchers.IO) {
            getRxRepository().addRequest(request)
        }
    }

    override fun getStateHandler(): MutableLiveData<State> {
       return state
    }

    override fun getLoadingStateHandler(): MutableLiveData<VortexLoadingState> {
        return loadingState
    }

    override fun getRxRepository(): VortexRxRepository {
        return repository
    }

    override fun onCleared() {
        getRxRepository().clearRepository()
        super.onCleared()
    }

}
