package com.example.bledot.data

import com.google.gson.annotations.SerializedName

data class RemoteFileData(
    @SerializedName("files") val files: RemoteFile,
    @SerializedName("form") val form: RemoteForm
)

data class RemoteFile(
    @SerializedName("file") val file : String
)

data class RemoteForm(
    @SerializedName("email") val email: String
)