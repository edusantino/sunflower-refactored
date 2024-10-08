/*
 * Copyright 2024 Google LLC
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

package com.google.samples.apps.sunflower.di

import com.google.samples.apps.sunflower.usecase.AddToGardenUseCase
import com.google.samples.apps.sunflower.usecase.AddToGardenUseCaseImpl
import com.google.samples.apps.sunflower.usecase.GetPlantedGardensUseCase
import com.google.samples.apps.sunflower.usecase.GetPlantedGardensUseCaseImpl
import com.google.samples.apps.sunflower.usecase.PlantUseCase
import com.google.samples.apps.sunflower.usecase.PlantUseCaseImpl
import com.google.samples.apps.sunflower.usecase.RemoveFromGardenUseCase
import com.google.samples.apps.sunflower.usecase.RemoveFromGardenUseCaseImpl
import com.google.samples.apps.sunflower.usecase.SearchResultUseCase
import com.google.samples.apps.sunflower.usecase.SearchResultUseCaseImpl
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

object ModuleApp : KoinComponent {
    val koinModule = module {
        viewModel { GardenPlantingListViewModel(gardenPlantingRepository = get()) }
        viewModel { PlantListViewModel(plantRepository = get(), savedStateHandle = get()) }
        viewModel {
            PlantDetailViewModel(
                addToGardenUseCase = get(),
                removeFromGardenUseCase = get(),
                plantUseCase = get(),
                savedStateHandle = get()
            )
        }

        factory<GetPlantedGardensUseCase> { GetPlantedGardensUseCaseImpl(repository = get()) }
        factory<RemoveFromGardenUseCase> { RemoveFromGardenUseCaseImpl(plantingRepository = get()) }
        factory<AddToGardenUseCase> { AddToGardenUseCaseImpl(plantingRepository = get()) }
        factory<PlantUseCase> { PlantUseCaseImpl(plantRepository = get()) }
        factory<SearchResultUseCase> { SearchResultUseCaseImpl(repository = get()) }
        single { com.santino.api.UnsplashService.create() }
    }
}