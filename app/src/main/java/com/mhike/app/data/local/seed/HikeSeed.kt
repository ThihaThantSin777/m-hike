package com.mhike.app.data.local.seed

import com.mhike.app.data.local.entity.HikeEntity
import kotlinx.datetime.toLocalDate

object HikeSeed {

    fun sample(): List<HikeEntity> = listOf(
        HikeEntity(
            name = "Doi Inthanon Summit Trail",
            location = "Chiang Mai, Thailand",
            date = "2025-01-12".toLocalDate(),
            parking = true,
            lengthKm = 8.4,
            difficulty = "Moderate",
            description = "A beautiful trail to Thailand’s highest peak with cool weather year-round.",
            terrain = "Mountain trail, paved sections",
            expectedWeather = "Cool and breezy"
        ),
        HikeEntity(
            name = "Khao Yai Waterfall Loop",
            location = "Nakhon Ratchasima, Thailand",
            date = "2025-02-03".toLocalDate(),
            parking = true,
            lengthKm = 6.1,
            difficulty = "Easy",
            description = "Gentle walk through forest paths with scenic waterfall views.",
            terrain = "Forest and rocky paths",
            expectedWeather = "Humid and warm"
        ),
        HikeEntity(
            name = "Phu Kradueng Plateau Trek",
            location = "Loei, Thailand",
            date = "2025-03-21".toLocalDate(),
            parking = false,
            lengthKm = 13.7,
            difficulty = "Hard",
            description = "Long ascent with rewarding views at the summit. Overnight camping recommended.",
            terrain = "Steep incline, dirt trail",
            expectedWeather = "Mild daytime, cold nights"
        ),
        HikeEntity(
            name = "Erawan Falls Trail",
            location = "Kanchanaburi, Thailand",
            date = "2025-04-18".toLocalDate(),
            parking = true,
            lengthKm = 7.0,
            difficulty = "Moderate",
            description = "Seven-tiered waterfall hike, great for swimming in turquoise pools.",
            terrain = "Forest and rocky sections",
            expectedWeather = "Sunny with occasional drizzle"
        ),
        HikeEntity(
            name = "Doi Suthep Nature Path",
            location = "Chiang Mai, Thailand",
            date = "2025-05-09".toLocalDate(),
            parking = true,
            lengthKm = 4.2,
            difficulty = "Easy",
            description = "A short forest walk with a temple at the end. Ideal for morning hikes.",
            terrain = "Forest trail, some stairs",
            expectedWeather = "Cool morning breeze"
        ),
        HikeEntity(
            name = "Khao Sok Rainforest Loop",
            location = "Surat Thani, Thailand",
            date = "2025-06-22".toLocalDate(),
            parking = false,
            lengthKm = 9.3,
            difficulty = "Moderate",
            description = "Dense rainforest trail with diverse wildlife and lush greenery.",
            terrain = "Muddy, slippery paths",
            expectedWeather = "Rainy and humid"
        )
    )
}
