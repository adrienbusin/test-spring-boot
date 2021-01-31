package com.decathlon.instorebackend.github.model

import com.google.gson.annotations.SerializedName

class Repo(val id: Int = 0, val name: String? = null, @SerializedName("full_name") val fullName: String? = null, val html_url: String? = null)