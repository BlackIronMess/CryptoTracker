package com.blackironmess.cryptotracker.crypto.domain

import com.blackironmess.cryptotracker.core.domain.util.NetworkError
import com.blackironmess.cryptotracker.core.domain.util.Result

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
}