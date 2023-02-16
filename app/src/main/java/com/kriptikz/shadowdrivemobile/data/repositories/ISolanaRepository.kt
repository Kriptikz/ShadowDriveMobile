package com.kriptikz.shadowdrivemobile.data.repositories

import com.portto.solana.web3.Connection
import com.portto.solana.web3.PublicKey
import com.portto.solana.web3.util.Cluster
import com.solana.Solana
import com.solana.actions.findSPLTokenDestinationAddressOfExistingAccount
import com.solana.api.getSplTokenAccountInfo
import com.solana.api.getVersion
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.rxsolana.actions.getTokenWallets
import com.solana.rxsolana.api.getBalance
import org.bitcoinj.core.Base58
import org.bitcoinj.core.Utils
import org.bouncycastle.util.encoders.Base64
import org.near.borshj.BorshBuffer


class UserInfoAccount {
    public var accountCounter: UInt? = null;
    private var delCounter: UInt? = null;
    private var agreedToTos: Boolean? = null;
    private var lifetimeBadCsam: Boolean? = null;

    constructor(data: List<String>) {
        if (data[1] == "base64") {
            val decodedData = Base64.decode(data[0])

            val buffer = BorshBuffer.wrap(decodedData)

            buffer.read(8) // Anchor Discriminator

            this.accountCounter = buffer.readU32().toUInt()
            this.delCounter = buffer.readU32().toUInt()
            this.agreedToTos = buffer.readBoolean()
            this.lifetimeBadCsam = buffer.readBoolean()
        } else {
           throw(Error("Decoding of data type ${data[1]} is not implemented"))
        }
    }

    companion object {
        val ProgramId = "2e1wdyNhUvE76y6yUCvah2KaviavMJYKoRun8acMRBZZ"
    }
}

interface ISolanaRepository {
    suspend fun getUserInfoAccountCounter(userPublicKey: String) : UInt
    suspend fun getAllStorageAccounts(userPublicKey: String): ArrayList<String>
    suspend fun getSolBalance(userPublicKey: String): Double
    suspend fun getShdwBalance(userPublicKey: String): Double
    fun base58Encode(byteArray: ByteArray): String
}

class SolanaRepository : ISolanaRepository {
    var connection: Connection

    val solana: Solana

    val SHDW_TOKEN_MINT = "SHDWyBxihqiCj6YekG2GUr7wqKLeLAMK1gHZck9pL6y"

    constructor() {
        connection = Connection(Cluster.MAINNET_BETA)
        val endPoint = RPCEndpoint.mainnetBetaSolana
        val network = HttpNetworkingRouter(endPoint)

        solana = Solana(network)
    }

    override suspend fun getUserInfoAccountCounter(userPublicKey: String): UInt {
        val userInfoAccountAddress = PublicKey.findProgramAddress(listOf("user-info".toByteArray(Charsets.UTF_8), PublicKey(userPublicKey).toByteArray()), PublicKey(UserInfoAccount.ProgramId)).address


        val userAccountInfo = connection.getAccountInfo(userInfoAccountAddress.toBase58())

        val userAccount = UserInfoAccount(userAccountInfo!!.data)

        return userAccount.accountCounter!!
    }

    override suspend fun getAllStorageAccounts(userPublicKey: String): ArrayList<String> {
        val currentAccountSeed = getUserInfoAccountCounter(userPublicKey)


        val storageAccounts = java.util.ArrayList<String>()

        for (i in 0u until currentAccountSeed) {

            val accountSeedByteArray = ByteArray(4)
            Utils.uint32ToByteArrayLE(i.toLong(), accountSeedByteArray, 0)
            val pda = PublicKey.findProgramAddress(
                listOf(
                    "storage-account".toByteArray(Charsets.UTF_8),
                    PublicKey(userPublicKey).toByteArray(),
                    accountSeedByteArray
                ),
                PublicKey(UserInfoAccount.ProgramId)
            ).address
            storageAccounts.add(pda.toBase58())
        }

        return storageAccounts
    }

    override suspend fun getSolBalance(userPublicKey: String): Double {
        return solana.api.getBalance(com.solana.core.PublicKey(userPublicKey)).blockingGet() / 1_000_000_000.0
    }

    override suspend fun getShdwBalance(userPublicKey: String): Double {
//        val accounts = connection.getTokenAccountsByOwner(PublicKey(userPublicKey).toBase58(),
//            Connection.Mint(SHDW_TOKEN_MINT.toBase58()),
//            Commitment.CONFIRMED
//        )

//        var totalShdwInAccounts = 0.0
//        accounts.value.forEach {
//            totalShdwInAccounts += it.account.data.parsed.info.tokenAmount.uiAmount
//        }
//        return totalShdwInAccounts
//        val tokenAccount = (solana.api.getTokenAccountsByOwner(com.solana.core.PublicKey(userPublicKey), com.solana.core.PublicKey(SHDW_TOKEN_MINT))).blockingGet()

//        val account = solana.api.token
//        println("TOKEN ACCOUNT: ${tokenAccount}")
//        val balance = (solana.api.getTokenAccountBalance(com.solana.core.PublicKey(tokenAccount.pubkey))).blockingGet().uiAmount

//        val tokenAccounts = solana.action.getTokenWallets(com.solana.core.PublicKey(userPublicKey)).blockingGet()
        val version = solana.api.getVersion()

            println("VERSION ${version}")
//
//        println("TOKENACCOUNTS: ${tokenAccounts}")
//
//        tokenAccounts.forEach { wallet ->
//            println("WALLET ${wallet.token.name}")
//        }

//        val address = solana.action.findSPLTokenDestinationAddressOfExistingAccount(com.solana.core.PublicKey(SHDW_TOKEN_MINT), com.solana.core.PublicKey(userPublicKey)).getOrThrows()

//        println("LOGC: ADDRESS FOR ATA: ${address.first.toBase58()}")


        val tokenAccountAddress = com.solana.core.PublicKey.associatedTokenAddress(
            com.solana.core.PublicKey(userPublicKey),
            com.solana.core.PublicKey(SHDW_TOKEN_MINT)
        ).address

        val tokenAccountInfo = solana.api.getSplTokenAccountInfo(tokenAccountAddress)


        var balance = 0.0

        balance = if (tokenAccountInfo.isSuccess) {
            tokenAccountInfo.getOrThrow().parsed.info.tokenAmount?.uiAmount!!
        } else {
            0.0
        }

        return balance
    }

    override fun base58Encode(byteArray: ByteArray): String {
        return Base58.encode(byteArray)
    }
}