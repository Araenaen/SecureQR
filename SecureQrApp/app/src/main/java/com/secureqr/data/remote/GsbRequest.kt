package com.secureqr.data.remote

import com.google.gson.annotations.SerializedName

data class GsbRequest(
    @SerializedName("client") val client: ClientInfo,
    @SerializedName("threatInfo") val threatInfo: ThreatInfo
)

data class ClientInfo(
    @SerializedName("clientId") val clientId: String = "com.secureqr",
    @SerializedName("clientVersion") val clientVersion: String = "1.0.0"
)

data class ThreatInfo(
    @SerializedName("threatTypes") val threatTypes: List<String>,
    @SerializedName("platformTypes") val platformTypes: List<String>,
    @SerializedName("threatEntryTypes") val threatEntryTypes: List<String>,
    @SerializedName("threatEntries") val threatEntries: List<ThreatEntry>
)

data class ThreatEntry(
    @SerializedName("url") val url: String
)