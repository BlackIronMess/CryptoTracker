package com.blackironmess.cryptotracker.crypto.data.networking

import com.blackironmess.cryptotracker.core.data.networking.constructUrl
import com.blackironmess.cryptotracker.core.data.networking.safeCall
import com.blackironmess.cryptotracker.core.domain.util.NetworkError
import com.blackironmess.cryptotracker.core.domain.util.Result
import com.blackironmess.cryptotracker.core.domain.util.map
import com.blackironmess.cryptotracker.crypto.data.mappers.toCoin
import com.blackironmess.cryptotracker.crypto.data.networking.dto.CoinsResponseDto
import com.blackironmess.cryptotracker.crypto.domain.Coin
import com.blackironmess.cryptotracker.crypto.domain.CoinDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class RemoteCoinDataSource(private val httpClient: HttpClient): CoinDataSource {

    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinsResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

}