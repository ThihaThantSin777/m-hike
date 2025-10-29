package com.mhike.app.data.local.seed

import com.mhike.app.data.local.entity.HikeEntity
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate

object HikeSeed {

    fun sample(): List<HikeEntity> = listOf(
        HikeEntity(
            name = "Banff Lakeside Trail",
            location = "Banff, Canada",
            date = LocalDate.parse("2025-01-18"),
            parking = true,
            lengthKm = 9.8,
            difficulty = "Moderate",
            description = "A stunning lakeside path with panoramic mountain views in Banff National Park.",
            terrain = "Rocky, uneven paths",
            expectedWeather = "Cold with light snow"
        ),
        HikeEntity(
            name = "Mount Fuji Yoshida Trail",
            location = "Yamanashi, Japan",
            date = LocalDate.parse("2025-02-10"),
            parking = true,
            lengthKm = 11.3,
            difficulty = "Hard",
            description = "Iconic route up Mount Fuji offering breathtaking sunrise views.",
            terrain = "Volcanic rock and steep slopes",
            expectedWeather = "Cool and windy"
        ),
        HikeEntity(
            name = "Torres del Paine W Circuit",
            location = "Patagonia, Chile",
            date = LocalDate.parse("2025-03-05"),
            parking = false,
            lengthKm = 76.0,
            difficulty = "Hard",
            description = "Famous multi-day trek through glaciers, lakes, and granite towers.",
            terrain = "Mixed terrain: forest, rock, glacier",
            expectedWeather = "Windy and cold"
        ),
        HikeEntity(
            name = "Table Mountain Platteklip Gorge",
            location = "Cape Town, South Africa",
            date = LocalDate.parse("2025-04-14"),
            parking = true,
            lengthKm = 5.4,
            difficulty = "Moderate",
            description = "Steep stone steps leading to one of the most iconic summits in the world.",
            terrain = "Stone stairs and rocky sections",
            expectedWeather = "Warm and sunny"
        ),
        HikeEntity(
            name = "Lake District Helvellyn Trail",
            location = "Cumbria, England",
            date = LocalDate.parse("2025-05-28"),
            parking = true,
            lengthKm = 7.6,
            difficulty = "Moderate",
            description = "Classic ridge walk offering dramatic views of Striding Edge and Red Tarn.",
            terrain = "Rocky ridge and grassy slopes",
            expectedWeather = "Cloudy with occasional rain"
        ),
        HikeEntity(
            name = "Tongariro Alpine Crossing",
            location = "North Island, New Zealand",
            date = LocalDate.parse("2025-06-15"),
            parking = false,
            lengthKm = 19.4,
            difficulty = "Hard",
            description = "World-famous volcanic trail through emerald lakes and lunar landscapes.",
            terrain = "Volcanic rock, steep ascents",
            expectedWeather = "Cool and unpredictable"
        ),
        HikeEntity(
            name = "Grand Canyon Bright Angel Trail",
            location = "Arizona, USA",
            date = LocalDate.parse("2025-07-07"),
            parking = true,
            lengthKm = 15.3,
            difficulty = "Hard",
            description = "Steep descent into the Grand Canyon with stunning red-rock scenery.",
            terrain = "Dry desert path with switchbacks",
            expectedWeather = "Hot and dry"
        ),
        HikeEntity(
            name = "Cinque Terre Coastal Walk",
            location = "Liguria, Italy",
            date = LocalDate.parse("2025-08-09"),
            parking = false,
            lengthKm = 12.0,
            difficulty = "Easy",
            description = "Picturesque seaside path linking colorful villages along the Italian coast.",
            terrain = "Stone paths and stairways",
            expectedWeather = "Sunny and breezy"
        ),
        HikeEntity(
            name = "Everest View Trail",
            location = "Khumbu, Nepal",
            date = LocalDate.parse("2025-09-20"),
            parking = false,
            lengthKm = 8.9,
            difficulty = "Moderate",
            description = "Scenic trek to view Mount Everest from Tengboche Monastery.",
            terrain = "Mountain trail and suspension bridges",
            expectedWeather = "Cold mornings, mild afternoons"
        ),
        HikeEntity(
            name = "Blue Mountains Wentworth Falls",
            location = "New South Wales, Australia",
            date = LocalDate.parse("2025-10-18"),
            parking = true,
            lengthKm = 6.7,
            difficulty = "Easy",
            description = "A serene hike with rainforest steps, waterfalls, and valley lookouts.",
            terrain = "Forest path with stairs",
            expectedWeather = "Mild and partly cloudy"
        )
    )
}
