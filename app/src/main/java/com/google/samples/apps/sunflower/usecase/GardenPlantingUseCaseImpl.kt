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

import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.santino.db.repository.GardenPlantingRepository
import kotlinx.coroutines.flow.Flow

class GardenPlantingUseCaseImpl(
    private val plantingRepository: GardenPlantingRepository
) : GardenPlantingUseCase {
    override suspend fun createGardenPlanting(plantId: String): Result<Unit> {
        return try {
            plantingRepository.createGardenPlanting(plantId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeGardenPlanting(plantId: String): Result<Unit> {
        return try {
            plantingRepository.removeGardenPlanting(plantId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override fun isPlanted(plantId: String) = plantingRepository.isPlanted(plantId)

    override fun getPlantedGardens() = plantingRepository.getPlantedGardens()
}