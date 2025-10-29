package com.mhike.app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mhike.app.ui.hike.detail.HikeDetailScreen
import com.mhike.app.ui.hike.form.HikeFormScreen
import com.mhike.app.ui.hike.list.HikeListScreen
import com.mhike.app.ui.hike.list.HikeListViewModel
import com.mhike.app.ui.observation.form.ObservationFormScreen
import com.mhike.app.ui.observation.list.ObservationListScreen
import com.mhike.app.ui.search.SearchScreen
import com.mhike.app.ui.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavGraph() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Destinations.Splash.route) {
        composable(Destinations.Splash.route) {
            SplashScreen(navController = nav)
        }


        composable(Destinations.HikeList.route) {
            val vm: HikeListViewModel = hiltViewModel()
            HikeListScreen(
                hikesFlow = vm.hikes,
                onAddClick = { nav.navigate(Destinations.HikeForm.routeNew()) },
                onEditClick = { hike ->
                    nav.navigate(Destinations.HikeForm.routeEdit(hike.id))
                },
                onDelete = vm::onDelete,
                onResetDatabase = vm::onResetDatabase,
                onOpenObservations = { hike ->
                    nav.navigate(Destinations.ObservationList.route(hike.id, hike.name))
                },
                onOpenSearch = { nav.navigate(Destinations.Search.route) },
                onOpenDetail = { hike ->
                    nav.navigate(Destinations.HikeDetail.route(hike.id))
                }
            )
        }


        composable(
            route = "hike_form?hikeId={hikeId}",
            arguments = listOf(
                navArgument("hikeId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val hikeIdArg = backStack.arguments?.getLong("hikeId") ?: -1L
            val hikeId: Long? = if (hikeIdArg == -1L) null else hikeIdArg
            HikeFormScreen(
                hikeId = hikeId,
                onHikeSaved = {
                    nav.popBackStack(Destinations.HikeList.route, false)
                },
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = "hike_detail/{hikeId}",
            arguments = listOf(
                navArgument("hikeId") { type = NavType.LongType }
            )
        ) {
            HikeDetailScreen(onBack = { nav.popBackStack() })
        }

        composable(
            route = "observation_list/{hikeId}/{hikeName}",
            arguments = listOf(
                navArgument("hikeId") { type = NavType.LongType },
                navArgument("hikeName") { type = NavType.StringType }
            )
        ) { backStack ->
            val hikeId = backStack.arguments!!.getLong("hikeId")
            val hikeName = backStack.arguments!!.getString("hikeName")!!
            ObservationListScreen(
                hikeId = hikeId,
                hikeName = hikeName,
                onAdd = { nav.navigate(Destinations.ObservationForm.routeAdd(hikeId)) },
                onEdit = { obsId ->
                    nav.navigate(Destinations.ObservationForm.routeEdit(hikeId, obsId))
                },
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = "observation_form/{hikeId}?obsId={obsId}",
            arguments = listOf(
                navArgument("hikeId") { type = NavType.LongType },
                navArgument("obsId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val hikeId = backStack.arguments!!.getLong("hikeId")
            val obsIdArg = backStack.arguments!!.getLong("obsId")
            val obsId: Long? = if (obsIdArg == -1L) null else obsIdArg
            ObservationFormScreen(
                hikeId = hikeId,
                obsId = obsId,
                onSaved = { nav.popBackStack() },
                onCancel = { nav.popBackStack() }
            )
        }

        composable(Destinations.Search.route) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onTapSearchResult = { hike ->
                    nav.navigate(Destinations.HikeDetail.route(hike.id))
                }
            )
        }
    }
}