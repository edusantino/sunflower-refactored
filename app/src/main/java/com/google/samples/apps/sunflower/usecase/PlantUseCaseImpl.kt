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

package com.google.samples.apps.sunflower.usecase

import com.santino.db.data.entities.Plant
import com.santino.db.repository.PlantRepository
import kotlinx.coroutines.flow.Flow

class PlantUseCaseImpl(private val plantRepository: PlantRepository) : PlantUseCase {
    override fun getPlants(): Flow<List<Plant>> = plantRepository.getPlants()

    override fun getPlant(plantId: String) = plantRepository.getPlant(plantId)

    override fun getPlantsWithGrowZoneNumber(growZoneNumber: Int) =
        plantRepository.getPlantsWithGrowZoneNumber(growZoneNumber)
}