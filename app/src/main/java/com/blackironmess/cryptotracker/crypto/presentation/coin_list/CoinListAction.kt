package com.blackironmess.cryptotracker.crypto.presentation.coin_list

import com.blackironmess.cryptotracker.crypto.presentation.model.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi): CoinListAction
}