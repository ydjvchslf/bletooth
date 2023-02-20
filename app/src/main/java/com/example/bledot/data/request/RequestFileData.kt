package com.example.bledot.data.request

import com.google.gson.annotations.SerializedName

data class RequestFileData(
    @SerializedName("files") val files: RemoteFile,
    @SerializedName("form") val form: RemoteForm
)

data class RemoteFile(
    @SerializedName("file") val file : String
)

data class RemoteForm(
    @SerializedName("email") val email: String
)