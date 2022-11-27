package com.kriptikz.shadowdrivemobile.data.repositories

data class SolanaAccountInfo(
    val data: List<String>,
    val executable: Boolean,
    val lamports: Long,
    val owner: String,
    val rentEpoch: Long,
)

interface ISolanaRepository {
    suspend fun getAccountInfo(accountAddress: String) : SolanaAccountInfo
    suspend fun findProgramAddress(seeds: List<ByteArray>, programId: ByteArray) : ByteArray
}

class SolanaRepository : ISolanaRepository {
    var connection: String

    constructor(networkUrl: String) {
        connection = networkUrl
    }

    override suspend fun getAccountInfo(accountAddress: String) : SolanaAccountInfo {
        return SolanaAccountInfo(
            data = listOf("firstData", "secondData"),
            executable = false,
            lamports = 100L,
            owner = "owner",
            rentEpoch = 100L,
        )
    }

    override suspend fun findProgramAddress(
        seeds: List<ByteArray>,
        programId: ByteArray
    ): ByteArray {
        return ByteArray(32)
    }
}