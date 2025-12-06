package com.secureqr.data.remote

import com.google.gson.annotations.SerializedName

data class GsbResponse(
    @SerializedName("matches") val matches: List<ThreatMatch>? = null
)

data class ThreatMatch(
    @SerializedName("threatType") val threatType: String,
    @SerializedName("platformType") val platformType: String,
    @SerializedName("threatEntryType") val threatEntryType: String,
    @SerializedName("threat") val threat: ThreatEntry,
    @SerializedName("cacheDuration") val cacheDuration: String?
)