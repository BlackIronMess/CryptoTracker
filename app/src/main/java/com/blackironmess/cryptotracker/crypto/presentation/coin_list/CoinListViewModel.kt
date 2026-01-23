package com.blackironmess.cryptotracker.crypto.presentation.coin_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackironmess.cryptotracker.core.domain.util.onError
import com.blackironmess.cryptotracker.core.domain.util.onSuccess
import com.blackironmess.cryptotracker.crypto.domain.CoinDataSource
import com.blackironmess.cryptotracker.crypto.presentation.model.CoinUi
import com.blackironmess.cryptotracker.crypto.presentation.model.toCoinUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAction(action: CoinListAction){
        when(action){
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectCoin(coinUi: CoinUi){
        _state.update { state -> state.copy(selectedCoin = coinUi) }

        viewModelScope.launch(Dispatchers.IO) {
            coinDataSource.getCoinHistory(coinId = coinUi.id, start = ZonedDateTime.now().minusDays(5L), end = ZonedDateTime.now() )
                .onSuccess { history ->
                    println(history)
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }

    }

    private fun loadCoins() = viewModelScope.launch (Dispatchers.IO){
        _state.update { state -> state.copy(isLoading = true) }

        coinDataSource
            .getCoins()
            .onSuccess { coins ->
                _state.update { state -> state.copy(
                    coins = coins.map { it.toCoinUi() },
                    isLoading = false
                ) }
            }
            .onError { error ->
                _state.update { state -> state.copy(isLoading = false) }
                _events.send(CoinListEvent.Error(error))
            }

    }

}