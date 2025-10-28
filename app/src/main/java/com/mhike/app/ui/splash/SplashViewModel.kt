package com.mhike.app.ui.splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhike.app.domain.usecase.SeedHikesIfEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val seedHikesIfEmpty: SeedHikesIfEmpty
) : ViewModel() {

    init {
        viewModelScope.launch {
            runCatching { seedHikesIfEmpty() }
        }
    }
}
