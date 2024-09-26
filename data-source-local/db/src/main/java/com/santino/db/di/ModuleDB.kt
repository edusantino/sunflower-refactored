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

package com.santino.db.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.santino.db.AppDatabase
import com.santino.db.repository.GardenPlantingRepository
import com.santino.db.repository.GardenPlantingRepositoryImpl
import com.santino.db.repository.PlantRepository
import com.santino.db.repository.PlantRepositoryImpl
import com.santino.db.workers.SeedDatabaseWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

object ModuleDB : KoinComponent {
    val koinModule = module {
        single {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                .build()
                            WorkManager.getInstance(androidContext()).enqueue(request)
                        }
                    }
                )
                .build()
        }
        single { get<AppDatabase>().gardenPlantingDao() }
        single { get<AppDatabase>().plantDao() }

        factory<GardenPlantingRepository> { GardenPlantingRepositoryImpl(gardenPlantingDao = get()) }
        factory<PlantRepository> { PlantRepositoryImpl(plantDao = get()) }
    }

    private const val DATABASE_NAME = "sunflower-db"
    const val PLANT_DATA_FILENAME = "plants.json"
    const val KEY_FILENAME = "PLANT_DATA_FILENAME"
}