package com.gms.cheerlotandroid.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServerErrorResponseDto(
    val message: String,
    val errorCode: String
)
