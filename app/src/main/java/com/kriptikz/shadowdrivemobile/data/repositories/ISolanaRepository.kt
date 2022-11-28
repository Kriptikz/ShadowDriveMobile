package com.kriptikz.shadowdrivemobile.data.repositories

import com.portto.solana.web3.Connection
import com.portto.solana.web3.PublicKey
import com.portto.solana.web3.util.Cluster
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
    fun base58Encode(byteArray: ByteArray): String
}

class SolanaRepository : ISolanaRepository {
    var connection: Connection

    constructor() {
        connection = Connection(Cluster.MAINNET_BETA)
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

    override fun base58Encode(byteArray: ByteArray): String {
        return Base58.encode(byteArray)
    }
}