package com.gms.cheerlotandroid.domain.model.team

// 앱 내부에서 팀을 식별하기 위한 값 객체입니다.
@JvmInline
value class TeamId(val value: String) {
    init {
        require(value.isNotBlank()) { "TeamId must not be blank." }
    }

    override fun toString(): String = value
}
