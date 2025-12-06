package com.secureqr.data.remote

import com.secureqr.data.remote.GsbRequest
import com.secureqr.data.remote.GsbResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface GsbApiService {
    @POST("v4/threatMatches:find")
    suspend fun checkUrl(
        @Body request: GsbRequest
    ): Response<GsbResponse>
}