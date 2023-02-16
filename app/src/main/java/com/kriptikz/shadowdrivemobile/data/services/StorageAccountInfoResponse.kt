package com.kriptikz.shadowdrivemobile.data.services

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class StorageAccountInfoResponse(
    @SerialName(value = "reserved_bytes")
    var reservedBytes: Int,
    @SerialName(value = "current_usage")
    var currentUsage: Int,
    var immutable: Boolean,
    @SerialName(value = "to_be_deleted")
    var toBeDeleted: Boolean,
    @SerialName(value = "delete_request_epoch")
    var deleteRequestEpoch: Int,
    var owner1: String,
    @SerialName(value = "account_counter_seed")
    var accountCounterSeed: Int,
    @SerialName(value = "creation_time")
    var creationTime: Long,
    @SerialName(value = "creation_epoch")
    var creationEpoch: Int,
    @SerialName(value = "last_fee_epoch")
    var lastFeeEpoch: Int,
    var identifier: String,
    var version: String,
)
