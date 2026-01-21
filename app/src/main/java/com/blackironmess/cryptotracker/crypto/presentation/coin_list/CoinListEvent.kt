package com.blackironmess.cryptotracker.crypto.presentation.coin_list

import com.blackironmess.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}