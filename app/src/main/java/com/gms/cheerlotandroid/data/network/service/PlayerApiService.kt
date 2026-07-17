package com.gms.cheerlotandroid.data.network.service

import com.gms.cheerlotandroid.data.network.dto.player.AllPlayersDto
import com.gms.cheerlotandroid.data.network.dto.player.LineupDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlayerApiService {
    @GET("players/team/{teamCode}")
    suspend fun getLineup(
        @Path("teamCode") teamCode: String,
        @Query("role") role: String = "starter"
    ): LineupDto

    @GET("players/team/{teamCode}")
    suspend fun getAllPlayers(@Path("teamCode") teamCode: String): AllPlayersDto
}
