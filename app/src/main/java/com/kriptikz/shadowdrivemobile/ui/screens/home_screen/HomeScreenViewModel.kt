package com.kriptikz.shadowdrivemobile.ui.screens.home_screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kriptikz.shadowdrivemobile.ShadowDriveMobileApplication
import com.kriptikz.shadowdrivemobile.data.repositories.ISolanaRepository
import com.solana.mobilewalletadapter.clientlib.protocol.JsonRpc20Client
import com.solana.mobilewalletadapter.clientlib.protocol.MobileWalletAdapterClient
import com.solana.mobilewalletadapter.clientlib.scenario.LocalAssociationIntentCreator
import com.solana.mobilewalletadapter.clientlib.scenario.LocalAssociationScenario
import com.solana.mobilewalletadapter.clientlib.scenario.Scenario
import com.solana.mobilewalletadapter.common.ProtocolContract
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

data class HomeUiState(
    val usedStorage: Double,
    val totalStorage: Double,
    val drives: List<String>,
    val recentItems: List<RecentItem>,
    val publicKey: String,
    val authorized: Boolean
)

class HomeScreenViewModel(
    private val solanaRepository: ISolanaRepository
) : ViewModel() {
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState(
            usedStorage = 0.0,
            totalStorage = 0.0,
            drives = listOf(),
            recentItems = listOf(),
            publicKey = "",
            authorized = false
        ))
        private set

    private val mobileWalletAdapterClientSem = Semaphore(1) // allow only a single MWA connection at a time


    init {
        getDefaultState()
    }

    suspend fun authorize(sender: StartActivityForResultSender) {
        val result = localAssociateAndExecute(sender) { client ->
            doAuthorize(client)
        }

        result?.let {
            if (it) {
                println("LOGC: Authorized")
            } else {
                println("LOGC: FAILED")
            }
        }
    }

    fun getDefaultState() {
        viewModelScope.launch {

            val drives = solanaRepository.getAllStorageAccounts("6HE1JdihWH71nT18CPUCghahVLRqAGYkmBJWZAPE3ggU")


            homeUiState = HomeUiState(
                usedStorage = 0.0,
                totalStorage = 0.0,
                drives = drives,
                recentItems = listOf(),
                publicKey = "",
                authorized = false
            )
        }
    }

    // NOTE: blocks and waits for completion of remote method call
    private fun doAuthorize(client: MobileWalletAdapterClient): Boolean {
        var authorized = false
        try {
            val result = client.authorize(
                Uri.parse("https://solana.com"),
                Uri.parse("favicon.ico"),
                "Solana",
                ProtocolContract.CLUSTER_MAINNET_BETA
            ).get()
            val pk = solanaRepository.base58Encode(result.publicKey)

            this.homeUiState = this.homeUiState.copy(
                usedStorage = 0.3,
                totalStorage = 0.8,
                publicKey = pk,
                authorized = true)

            Log.d(TAG, "Authorized: $homeUiState")
            authorized = true
        } catch (e: ExecutionException) {
            when (val cause = e.cause) {
                is IOException -> Log.e(TAG, "IO error while sending authorize", cause)
                is TimeoutException ->
                    Log.e(TAG, "Timed out while waiting for authorize result", cause)
                is JsonRpc20Client.JsonRpc20RemoteException ->
                    when (cause.code) {
                        ProtocolContract.ERROR_AUTHORIZATION_FAILED ->
                            Log.e(TAG, "Not authorized", cause)
                        ProtocolContract.ERROR_CLUSTER_NOT_SUPPORTED ->
                            Log.e(TAG, "Cluster not supported", cause)
                        else ->
                            Log.e(TAG, "Remote exception for authorize", cause)
                    }
                is MobileWalletAdapterClient.InsecureWalletEndpointUriException ->
                    Log.e(TAG, "authorize result contained a non-HTTPS wallet base URI", e)
                is JsonRpc20Client.JsonRpc20Exception ->
                    Log.e(TAG, "JSON-RPC client exception for authorize", cause)
                else -> throw e
            }
        } catch (e: CancellationException) {
            Log.e(TAG, "authorize request was cancelled", e)
        } catch (e: InterruptedException) {
            Log.e(TAG, "authorize request was interrupted", e)
        }

        return authorized
    }


    private suspend fun <T> localAssociateAndExecute(
        sender: StartActivityForResultSender,
        uriPrefix: Uri? = null,
        action: suspend (MobileWalletAdapterClient) -> T?
    ): T? = coroutineScope {
        return@coroutineScope mobileWalletAdapterClientSem.withPermit {
            val localAssociation = LocalAssociationScenario(Scenario.DEFAULT_CLIENT_TIMEOUT_MS)

            val associationIntent = LocalAssociationIntentCreator.createAssociationIntent(
                uriPrefix,
                localAssociation.port,
                localAssociation.session
            )
            try {
                sender.startActivityForResult(associationIntent) {
                    viewModelScope.launch {
                        // Ensure this coroutine will wrap up in a timely fashion when the launched
                        // activity completes
                        delay(LOCAL_ASSOCIATION_CANCEL_AFTER_WALLET_CLOSED_TIMEOUT_MS)
                        this@coroutineScope.cancel()
                    }
                }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Failed to start intent=$associationIntent", e)
                return@withPermit null
            }

            return@withPermit withContext(Dispatchers.IO) {
                try {
                    val mobileWalletAdapterClient = try {
                        runInterruptible {
                            localAssociation.start().get(LOCAL_ASSOCIATION_START_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                        }
                    } catch (e: InterruptedException) {
                        Log.w(TAG, "Interrupted while waiting for local association to be ready")
                        return@withContext null
                    } catch (e: TimeoutException) {
                        Log.e(TAG, "Timed out waiting for local association to be ready")
                        return@withContext null
                    } catch (e: ExecutionException) {
                        Log.e(TAG, "Failed establishing local association with wallet", e.cause)
                        return@withContext null
                    } catch (e: CancellationException) {
                        Log.e(TAG, "Local association was cancelled before connected", e)
                        return@withContext null
                    }

                    // NOTE: this is a blocking method call, appropriate in the Dispatchers.IO context
                    action(mobileWalletAdapterClient)
                } finally {
                    @Suppress("BlockingMethodInNonBlockingContext") // running in Dispatchers.IO; blocking is appropriate
                    localAssociation.close().get(LOCAL_ASSOCIATION_CLOSE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                }
            }
        }
    }


    interface StartActivityForResultSender {
        fun startActivityForResult(intent: Intent, onActivityCompleteCallback: () -> Unit) // throws ActivityNotFoundException
    }

    companion object {
        private val TAG = HomeScreenViewModel::class.simpleName
        private const val LOCAL_ASSOCIATION_START_TIMEOUT_MS = 60000L // LocalAssociationScenario.start() has a shorter timeout; this is just a backup safety measure
        private const val LOCAL_ASSOCIATION_CLOSE_TIMEOUT_MS = 5000L
        private const val LOCAL_ASSOCIATION_CANCEL_AFTER_WALLET_CLOSED_TIMEOUT_MS = 5000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShadowDriveMobileApplication)
                val solanaRepository = application.container.solanaRepository
                HomeScreenViewModel(solanaRepository = solanaRepository)
            }
        }
    }
}
