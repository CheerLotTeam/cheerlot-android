package com.gms.cheerlotandroid.presentation.teammembers

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo

data class TeamMembersRow(
    val id: String,
    val playerName: String,
    val backNumber: Int,
    val song: CheerSongInfo?
) {
    val hasSong: Boolean get() = song != null
    val titleText: String? get() = song?.title?.takeIf { it != "기본 응원가" }
}
