package com.decathlon.instorebackend.github

import com.decathlon.instorebackend.github.model.Repo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import rx.Observable

interface GithubInterface {

    @GET("/users/{user}/repos")
    fun listRepos(@Path("user") user: String?,
                  @Header("Authorization") token: String,
                  @Header("x-api-key") apiKey: String
    ): Observable<MutableList<Repo>>

    companion object {
        const val ENDPOINT = "https://api.github.com"
    }
}