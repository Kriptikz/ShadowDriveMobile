package com.kriptikz.shadowdrivemobile.ui.screens.home_screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kriptikz.shadowdrivemobile.ShadowDriveMobileApplication
import com.kriptikz.shadowdrivemobile.data.database.AppRoomDatabase
import com.kriptikz.shadowdrivemobile.data.entities.StorageAccount
import com.kriptikz.shadowdrivemobile.data.entities.Wallet
import com.kriptikz.shadowdrivemobile.data.repositories.ISolanaRepository
import com.kriptikz.shadowdrivemobile.data.repositories.ShadowDriveRepository
import com.kriptikz.shadowdrivemobile.data.repositories.StorageAccountRepository
import com.kriptikz.shadowdrivemobile.data.repositories.WalletRepository
import com.solana.mobilewalletadapter.clientlib.protocol.JsonRpc20Client
import com.solana.mobilewalletadapter.clientlib.protocol.MobileWalletAdapterClient
import com.solana.mobilewalletadapter.clientlib.scenario.LocalAssociationIntentCreator
import com.solana.mobilewalletadapter.clientlib.scenario.LocalAssociationScenario
import com.solana.mobilewalletadapter.clientlib.scenario.Scenario
import com.solana.mobilewalletadapter.common.ProtocolContract
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

data class HomeUiState(
    var usedStorage: Double,
    var totalStorage: Double,
    var drives: List<StorageAccount>,
    var recentItems: List<RecentItem>,
    var wallet: Wallet?,
    var sol: Double,
    var shdw: Double,
)

class HomeScreenViewModel(
    application: ShadowDriveMobileApplication,
    private val solanaRepository: ISolanaRepository,
    private val shadowDriveRepository: ShadowDriveRepository
) : ViewModel() {
    var homeUiState: HomeUiState by mutableStateOf(
        HomeUiState(
            usedStorage = 0.0,
            totalStorage = 0.0,
            drives = listOf(),
            recentItems = listOf(),
            wallet = null,
            sol = 0.0,
            shdw = 0.0
        )
    )
        private set

    var drives: List<StorageAccount> by mutableStateOf(
        emptyList()
    )

    var wallet: Wallet? by mutableStateOf(
        null
    )

    private val mobileWalletAdapterClientSem =
        Semaphore(1) // allow only a single MWA connection at a time

    private val walletRepository: WalletRepository
    private val storageAccountRepository: StorageAccountRepository


    init {
        val appDb = AppRoomDatabase.getInstance(application)
        val walletDao = appDb.walletDao()
        walletRepository = WalletRepository(walletDao)

        val storageAccountDao = appDb.storageAccountDao()
        storageAccountRepository = StorageAccountRepository(storageAccountDao)

        viewModelScope.launch(Dispatchers.Main) {
            println("LOGC: WHAT")
            homeUiState = asyncLoadHomeUiState()

            println("LOGC: MAIN THREAD INIT")
            if (homeUiState.wallet != null) {
                println("LOGC: FOUND A WALLET")
                homeUiState = asyncLoadNetData(homeUiState.wallet!!)
            } else {
                println("LOGC: DIDN'T FIND A WALLET")
            }
        }
    }

    private suspend fun asyncLoadWallets(): List<Wallet> =
        viewModelScope.async(Dispatchers.IO) {
            return@async walletRepository.getAllWallets()
        }.await()

    private suspend fun asyncLoadNetData(wallet: Wallet): HomeUiState =
        viewModelScope.async(Dispatchers.IO) {
            try {
                val solBalance = solanaRepository.getSolBalance(wallet.publicKey)
                val shdwBalance = solanaRepository.getShdwBalance(wallet.publicKey)

                homeUiState = homeUiState.copy(
                    sol = solBalance,
                    shdw = shdwBalance,
                    wallet = wallet
                )

                println("LOGC: Balance: ${solBalance}")

                val accountPubKeys = solanaRepository.getAllStorageAccounts(wallet.publicKey)

                var usedStorage = 0.0
                var totalStorage = 0.0

                val drives = homeUiState.drives.toMutableList()

                accountPubKeys.forEach { accountPubKey ->
                    val storageAccount = shadowDriveRepository.getStorageAccountInfo(accountPubKey)

                    println("LOGC: SA: ${storageAccount}")

                    drives.add(storageAccount)
                    usedStorage += storageAccount.currentUsage
                    totalStorage += storageAccount.reservedBytes

                    val loadedStorageAccount = storageAccountRepository.findStorageAccountByAddress(storageAccount.address)

                    if (loadedStorageAccount.isEmpty()) {
                        storageAccountRepository.insertStorageAccount(storageAccount)
                    }

                    homeUiState = homeUiState.copy(
                        drives = drives
                    )
                }

                println("LOGC: HomeUiState: ${homeUiState}")
                return@async HomeUiState(
                    usedStorage = usedStorage,
                    totalStorage = totalStorage,
                    drives = drives,
                    recentItems = listOf(),
                    wallet = wallet,
                    sol = solBalance,
                    shdw = shdwBalance
                )
            } catch (error: NullPointerException) {
                println("Exception getting storage accounts")
                return@async HomeUiState(
                    usedStorage = 0.0,
                    totalStorage = 0.0,
                    drives = listOf(),
                    recentItems = listOf(),
                    wallet = null,
                    sol = 0.0,
                    shdw = 0.0
                )
            }
        }.await()


    private suspend fun asyncLoadHomeUiState(): HomeUiState =
        viewModelScope.async(Dispatchers.IO) {
            val wallets = walletRepository.getAllWallets()
            val loadedDrives = storageAccountRepository.getAllStorageAccounts()

            var totalStorage = 0.0
            var usedStorage = 0.0

            loadedDrives.forEach { storageAccount ->
                totalStorage += storageAccount.reservedBytes
                usedStorage += storageAccount.currentUsage
            }

            return@async HomeUiState(
                usedStorage = usedStorage,
                totalStorage = totalStorage,
                drives = loadedDrives,
                recentItems = listOf(),
                wallet = if (wallets.isNotEmpty()) wallets[0] else null,
                sol = 0.0,
                shdw = 0.0
            )
        }.await()

    fun disconnect() {
        println("LOGC: Disconnecting")

        homeUiState.wallet?.let {
            println("LOGC: DELETING WALLET ${it.id} ${it.publicKey}")
            walletRepository.deleteWallet(it.publicKey)
            println("LOGC: DELETING ALL STORAGE ACCOUNTS")
            storageAccountRepository.deleteAllStorageAccounts()
        }

        homeUiState = getDefaultState(null, emptyList())
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

    private suspend fun getDrives(publicKey: String) {
        try {
            val accountPubKeys = solanaRepository.getAllStorageAccounts(publicKey)

            var usedStorage = 0.0
            var totalStorage = 0.0

            accountPubKeys.forEach { accountPubKey ->
                val storageAccount = shadowDriveRepository.getStorageAccountInfo(accountPubKey)

                println("LOGC: SA: ${storageAccount}")
                val drives = homeUiState.drives.toMutableList()

                drives.add(storageAccount)
                usedStorage += storageAccount.currentUsage
                totalStorage += storageAccount.reservedBytes

                val loadedStorageAccount = storageAccountRepository.findStorageAccountByAddress(storageAccount.address)

                if (loadedStorageAccount.isEmpty()) {
                    storageAccountRepository.insertStorageAccount(storageAccount)
                }

                homeUiState.usedStorage = usedStorage
                homeUiState.totalStorage = totalStorage
                homeUiState.drives = drives
            }

            println("LOGC: HomeUiState: ${homeUiState}")
        } catch (error: NullPointerException) {
            println("Exception getting storage accounts")
        }
    }

    private fun getDefaultState(wallet: Wallet?, drives: List<StorageAccount>): HomeUiState {
        return HomeUiState(
            usedStorage = 0.0,
            totalStorage = 0.0,
            drives = drives,
            recentItems = emptyList(),
            wallet = null,
            sol = 0.0,
            shdw = 0.0
        )
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

            viewModelScope.launch(Dispatchers.Main) {
                val loadedWallets = asyncLoadWallets()

                val loadedWallet = if (loadedWallets.isNotEmpty()) {
                    println("LOGC: found a wallet in search result")
                    loadedWallets[0]
                } else {
                    println("LOGC: inserting a wallet")
                    walletRepository.insertWallet(
                        Wallet(
                            publicKey = pk,
                            authToken = result.authToken
                        )
                    )
                    Wallet(publicKey = pk, authToken = result.authToken)
                }

                homeUiState = asyncLoadNetData(loadedWallet)
//                getDrives(pk)

                Log.d(TAG, "LOGC: Authorized: ${homeUiState.wallet?.publicKey}")
            }
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
                            localAssociation.start()
                                .get(LOCAL_ASSOCIATION_START_TIMEOUT_MS, TimeUnit.MILLISECONDS)
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
                    localAssociation.close()
                        .get(LOCAL_ASSOCIATION_CLOSE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                }
            }
        }
    }


    interface StartActivityForResultSender {
        fun startActivityForResult(
            intent: Intent,
            onActivityCompleteCallback: () -> Unit
        ) // throws ActivityNotFoundException
    }

    companion object {
        private val TAG = HomeScreenViewModel::class.simpleName
        private const val LOCAL_ASSOCIATION_START_TIMEOUT_MS =
            60000L // LocalAssociationScenario.start() has a shorter timeout; this is just a backup safety measure
        private const val LOCAL_ASSOCIATION_CLOSE_TIMEOUT_MS = 5000L
        private const val LOCAL_ASSOCIATION_CANCEL_AFTER_WALLET_CLOSED_TIMEOUT_MS = 5000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShadowDriveMobileApplication)
                val solanaRepository = application.container.solanaRepository
                val shadowDriveRepository = application.container.shadowDriveRepository
                HomeScreenViewModel(
                    application = application,
                    solanaRepository = solanaRepository,
                    shadowDriveRepository = shadowDriveRepository
                )
            }
        }
    }
}
