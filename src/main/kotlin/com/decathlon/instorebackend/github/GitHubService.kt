package com.decathlon.instorebackend.github

import com.decathlon.instorebackend.CurlInterceptor
import com.decathlon.instorebackend.github.model.Repo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

@Service
class GitHubService {
    private val service: GithubInterface

    init {
        val logger = HttpLoggingInterceptor.Logger.DEFAULT
        val interceptor = HttpLoggingInterceptor(logger)
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(CurlInterceptor(logger))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(GithubInterface.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build()
        service = retrofit.create(GithubInterface::class.java)
    }

    fun getListRepo(token: String, apiKey:String): Observable<MutableList<Repo>> {
        return service.listRepos("adrienbusin", token, apiKey)
    }
}