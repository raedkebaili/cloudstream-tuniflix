package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class TuniflixPlugin : MainAPI() {
    override var mainUrl = "https://tuniflix.site"
    override var name = "Tuniflix"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun loadMainPage(page: Int): HomePageResponse {
        val items = app.get(mainUrl).document.select("div.card").mapNotNull {
            val title = it.selectFirst("h2")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            newMovieSearchResponse(title, href) {
                this.posterUrl = it.selectFirst("img")?.attr("src")
            }
        }
        return newHomePageResponse(items)
    }
}
