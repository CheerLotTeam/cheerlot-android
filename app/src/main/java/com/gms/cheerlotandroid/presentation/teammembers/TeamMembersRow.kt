package com.gms.cheerlotandroid.presentation.teammembers

import com.gms.cheerlotandroid.domain.model.cheersong.CheerSongInfo
import com.gms.cheerlotandroid.domain.model.player.PlayerId
import com.gms.cheerlotandroid.domain.model.player.PlayerInfo

data class TeamMembersRow(
    val id: String,
    val playerId: PlayerId,
    val playerName: String,
    val backNumber: Int,
    val song: CheerSongInfo?
) {
    val hasSong: Boolean get() = song != null
    val titleText: String? get() = song?.title?.takeIf { it != "기본 응원가" }
}

// 응원가 있는 선수 먼저, 그다음 이름순. 선수당 응원가가 여러 개면 곡 개수만큼 row로 펼칩니다
// (iOS TeamMembersViewModel과 동일). 전체선수/검색 화면이 동일한 로스터 데이터를 같은 규칙으로
// row화해야 해서 공용 함수로 뺐습니다.
internal fun List<PlayerInfo>.toTeamMembersRows(): List<TeamMembersRow> {
    return sortedWith(compareByDescending<PlayerInfo> { it.cheerSongs.isNotEmpty() }.thenBy { it.name })
        .flatMap { player ->
            if (player.cheerSongs.isEmpty()) {
                listOf(
                    TeamMembersRow(
                        id = "${player.id.value}-empty",
                        playerId = player.id,
                        playerName = player.name,
                        backNumber = player.backNumber,
                        song = null
                    )
                )
            } else {
                player.cheerSongs.map { song ->
                    TeamMembersRow(
                        id = "${player.id.value}-${song.id}",
                        playerId = player.id,
                        playerName = player.name,
                        backNumber = player.backNumber,
                        song = song
                    )
                }
            }
        }
}
