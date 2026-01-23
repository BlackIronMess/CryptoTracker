package com.blackironmess.cryptotracker

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blackironmess.cryptotracker.core.presentation.util.ObserveAsEvents
import com.blackironmess.cryptotracker.core.presentation.util.toString
import com.blackironmess.cryptotracker.crypto.presentation.coin_detail.CoinDetailScreen
import com.blackironmess.cryptotracker.crypto.presentation.coin_list.CoinListEvent
import com.blackironmess.cryptotracker.crypto.presentation.coin_list.CoinListScreen
import com.blackironmess.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import com.blackironmess.cryptotracker.ui.theme.CryptoTrackerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = koinViewModel<CoinListViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val context = LocalContext.current

                    ObserveAsEvents(events = viewModel.events) { event ->
                        when(event){
                            is CoinListEvent.Error -> {
                                Toast.makeText(context, event.error.toString(context), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    when{
                        state.selectedCoin != null -> {
                            CoinDetailScreen(
                                state = state,
                                modifier = Modifier
                                    .padding(innerPadding)
                            )
                        } else -> {
                            CoinListScreen(
                                state = state,
                                modifier = Modifier.padding(innerPadding),
                                onAction = viewModel::onAction
                            )
                        }
                    }

                }
            }
        }
    }
}