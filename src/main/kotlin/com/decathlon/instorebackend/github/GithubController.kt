package com.decathlon.instorebackend.github

import com.decathlon.instorebackend.github.model.Repo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import rx.Observable

@RestController
class GithubController {

    @Autowired
    lateinit var githubService: GitHubService

    @GetMapping("/repos")
    fun getRepos(@RequestHeader("Authorization") token: String, @RequestHeader("x-api-key") apiKey:String): Observable<MutableList<Repo>> {
        return githubService.getListRepo(token, apiKey)
    }
}