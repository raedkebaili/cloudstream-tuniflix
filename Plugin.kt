package com.cloudstream.tuniflix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class Tuniflix : MainAPI() {
    override var mainUrl = "https://tuniflix.site"
    override var name = "Tuniflix"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = listOf(
        MainPageData(
            "Derniers ajouts", "$mainUrl/", "div.film"
        )
    )

    override suspend fun getMainPage(page: Int, request : MainPageRequest): HomePageResponse {
        val doc = app.get(request.data).document
        val home = doc.select("div.film").map {
            val title = it.selectFirst("h3")?.text() ?: "Sans titre"
            val href = it.selectFirst("a")?.attr("href") ?: return@map null
            val posterUrl = it.selectFirst("img")?.attr("src")
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = posterUrl
            }
        }.filterNotNull()
        return newHomePageResponse(request.name, home)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: return ErrorLoadingException("Titre manquant")
        val poster = doc.selectFirst("img")?.attr("src")
        val description = doc.selectFirst(".description")?.text()
        val videoUrl = doc.select("iframe").attr("src")
        return newMovieLoadResponse(title, url, videoUrl) {
            this.posterUrl = poster
            this.plot = description
        }
    }
}