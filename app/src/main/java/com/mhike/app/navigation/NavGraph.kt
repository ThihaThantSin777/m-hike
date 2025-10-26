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
import com.mhike.app.ui.hike.form.HikeReviewScreen
import com.mhike.app.ui.hike.list.HikeListScreen
import com.mhike.app.ui.hike.list.HikeListViewModel
import com.mhike.app.ui.observation.form.ObservationFormScreen
import com.mhike.app.ui.observation.list.ObservationListScreen
import com.mhike.app.ui.search.SearchScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Destinations.HikeList.route) {

        // List
        composable(Destinations.HikeList.route) {
            val vm: HikeListViewModel = hiltViewModel()
            HikeListScreen(
                hikesFlow = vm.hikes,
                onAddClick = { nav.navigate(Destinations.HikeForm.route) },
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

        // Form
        composable(Destinations.HikeForm.route) {
            HikeFormScreen(
                onReview = { draftId -> nav.navigate(Destinations.HikeReview.route(draftId)) },
                onBack = { nav.popBackStack() }
            )
        }

        // Review
        composable(
            route = Destinations.HikeReview("{draftId}").route,
            arguments = listOf(navArgument("draftId") { type = NavType.StringType })
        ) { backStackEntry ->
            val draftId = backStackEntry.arguments?.getString("draftId")!!
            HikeReviewScreen(
                draftId = draftId,
                onConfirmSaved = { nav.popBackStack(Destinations.HikeList.route, false) },
                onEdit = { nav.popBackStack() }
            )
        }

        // NEW: Detail
        composable(
            route = Destinations.HikeDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments!!.getLong("id")
            HikeDetailScreen(
                hikeId = id,
                onBack = { nav.popBackStack() },
                onOpenObservations = { hid, name ->
                    nav.navigate(Destinations.ObservationList.route(hid, name))
                },
                onAddObservation = { hid ->
                    nav.navigate(Destinations.ObservationForm.routeAdd(hid))
                }
            )
        }

        // Observations list
        composable(
            route = Destinations.ObservationList.route,
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
                onEdit = { obsId -> nav.navigate(Destinations.ObservationForm.routeEdit(hikeId, obsId)) },
                onBack = { nav.popBackStack() }
            )
        }

        // Observation form
        composable(
            route = Destinations.ObservationForm.route,
            arguments = listOf(
                navArgument("hikeId") { type = NavType.LongType },
                navArgument("obsId") { type = NavType.LongType; defaultValue = -1L }
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

        // Search
        composable(Destinations.Search.route) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpenObservations = { hike ->
                    nav.navigate(Destinations.ObservationList.route(hike.id, hike.name))
                }
            )
        }
    }
}
