package com.gms.cheerlotandroid.data.network.service

import com.gms.cheerlotandroid.data.network.dto.team.TeamGameDto
import com.gms.cheerlotandroid.data.network.dto.team.TeamGameScheduleDto
import com.gms.cheerlotandroid.data.network.dto.team.TeamVersionsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TeamApiService {
    @GET("teams/{teamCode}")
    suspend fun getTeamTodayGameInfo(@Path("teamCode") teamCode: String): TeamGameDto

    @GET("teams/{teamCode}/version")
    suspend fun getTeamVersions(@Path("teamCode") teamCode: String): TeamVersionsDto

    @GET("teams/{teamCode}/games")
    suspend fun getTeamGamesInfo(@Path("teamCode") teamCode: String): TeamGameScheduleDto
}
