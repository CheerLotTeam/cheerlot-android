package com.gms.cheerlotandroid.data.network

import com.gms.cheerlotandroid.data.network.dto.ServerErrorResponseDto
import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

sealed interface NetworkError {
    // 4xx/5xx 응답. serverMessage는 서버가 준 에러 메시지(파싱 성공 시)이거나 raw 문자열.
    data class Http(val code: Int, val serverMessage: String?) : NetworkError

    // 인터넷 연결 자체가 안 되는 경우 (IOException).
    data class Connection(val cause: Throwable) : NetworkError

    // 응답은 왔는데 JSON 파싱이 실패한 경우.
    data class Decoding(val cause: Throwable) : NetworkError

    val userMessage: String
        get() = when (this) {
            is Http -> serverMessage ?: defaultMessageFor(code)
            is Connection -> "네트워크 연결을 확인해주세요."
            is Decoding -> "일시적인 오류가 발생했습니다."
        }

    companion object {
        // 서버 메시지가 없을 때 상태코드 대역별로 보여줄 기본 문구.
        private fun defaultMessageFor(code: Int): String {
            return when (code) {
                in 400..499 -> "요청을 처리할 수 없습니다."
                in 500..599 -> "서버에 일시적인 문제가 발생했습니다."
                else -> "알 수 없는 오류가 발생했습니다. (상태코드: $code)"
            }
        }
    }
}

// NetworkError(데이터 클래스)는 그냥 던질 수 없어서(Throwable이 아님) Exception으로 감싸는 래퍼입니다.
class NetworkException(val networkError: NetworkError) : Exception()

// Retrofit/OkHttp/직렬화 예외를 NetworkException(NetworkError)로 변환해 도메인 계층에 노출합니다.
suspend fun <T> safeApiCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        val rawErrorBody = e.response()?.errorBody()?.string()
        // 서버 에러 바디를 ServerErrorResponseDto로 파싱 시도 후 message만 사용.
        // 그 형식이 아니거나 파싱 실패하면 raw 문자열로 폴백합니다.
        val serverMessage = rawErrorBody?.let { body ->
            runCatching { networkJson.decodeFromString<ServerErrorResponseDto>(body).message }
                .getOrNull()
        } ?: rawErrorBody
        throw NetworkException(NetworkError.Http(code = e.code(), serverMessage = serverMessage))
    } catch (e: SerializationException) {
        throw NetworkException(NetworkError.Decoding(e))
    } catch (e: IOException) {
        throw NetworkException(NetworkError.Connection(e))
    }
}
