package com.mhike.app.navigation

sealed class Destinations(val route: String) {

    data object Splash : Destinations("splash")
    data object HikeList : Destinations("hike_list")

    data object HikeForm : Destinations("hike_form?hikeId={hikeId}") {
        fun routeNew() = "hike_form"
        fun routeEdit(hikeId: Long) = "hike_form?hikeId=$hikeId"
    }

    data object HikeDetail : Destinations("hike_detail/{hikeId}") {
        fun route(id: Long) = "hike_detail/$id"
    }

    data object ObservationList : Destinations("observation_list/{hikeId}/{hikeName}") {
        fun route(hikeId: Long, hikeName: String) =
            "observation_list/$hikeId/${hikeName.replace('/', ' ')}"
    }

    data object ObservationForm : Destinations("observation_form/{hikeId}?obsId={obsId}") {
        fun routeAdd(hikeId: Long) = "observation_form/$hikeId"
        fun routeEdit(hikeId: Long, obsId: Long) = "observation_form/$hikeId?obsId=$obsId"
    }

    // Search
    data object Search : Destinations("search")
}