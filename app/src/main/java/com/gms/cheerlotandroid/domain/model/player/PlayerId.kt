package com.gms.cheerlotandroid.domain.model.player

// 앱 내부에서 선수를 식별하기 위한 값 객체입니다.
@JvmInline
value class PlayerId(val value: String) {
    init {
        require(value.isNotBlank()) { "PlayerId must not be blank." }
    }

    override fun toString(): String = value
}
