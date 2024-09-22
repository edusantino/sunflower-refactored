/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.BaseViewModel
import com.google.samples.apps.sunflower.ui.ViewEvent
import com.google.samples.apps.sunflower.usecase.GardenPlantingUseCase
import com.google.samples.apps.sunflower.usecase.PlantUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * The ViewModel used in [PlantDetailsScreen].
 */
class PlantDetailViewModel (
    private val gardenPlantingUseCase: GardenPlantingUseCase,
    savedStateHandle: SavedStateHandle,
    plantUseCase: PlantUseCase,
) : BaseViewModel() {
    override val eventUI = MutableLiveData<ViewEvent>()

    val plantId: String = savedStateHandle.get<String>(PLANT_ID_SAVED_STATE_KEY)!!

    val isPlanted = gardenPlantingUseCase.isPlanted(plantId)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    val plant = plantUseCase.getPlant(plantId).asLiveData()

    private val _showSnackbar = MutableLiveData(false)
    val showSnackbar: LiveData<Boolean>
        get() = _showSnackbar

    fun addPlantToGarden() {
        launchOnIO {
            val result = gardenPlantingUseCase.createGardenPlanting(plantId)
            result
                .onSuccess {
                    eventUI.postValue(ViewEvent.Success(Unit))
                }
                .onFailure {
                    eventUI.postValue(ViewEvent.Error(it))
                }
        }
    }

    fun removePlantFromGarden() {
        launchOnIO {
            gardenPlantingUseCase.removeGardenPlanting(plantId)
            _showSnackbar.value = true
        }
    }

    fun dismissSnackbar() {
        _showSnackbar.value = false
    }

    companion object {
        private const val PLANT_ID_SAVED_STATE_KEY = "plantId"
    }
}
