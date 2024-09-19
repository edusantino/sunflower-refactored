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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.google.samples.apps.sunflower.MainCoroutineRule
import com.google.samples.apps.sunflower.utilities.testPlant
import com.santino.db.AppDatabase
import com.santino.db.repository.GardenPlantingRepositoryImpl
import com.santino.db.repository.PlantRepositoryImpl
import javax.inject.Inject

class PlantDetailViewModelImplTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var viewModel: PlantDetailViewModel
    //private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val coroutineRule = MainCoroutineRule()

    /*@get:Rule
    val rule: RuleChain = RuleChain
            .outerRule(hiltRule)
            .around(instantTaskExecutorRule)
      */      //.around(coroutineRule)

    @Inject
    lateinit var plantRepositoryImpl: PlantRepositoryImpl

    @Inject
    lateinit var gardenPlantingRepositoryImpl: GardenPlantingRepositoryImpl

    /*@Before
    fun setUp() {
        hiltRule.inject()
*/
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        //appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        val savedStateHandle: SavedStateHandle = SavedStateHandle().apply {
            set("plantId", testPlant.plantId)
        }
        //viewModel = PlantDetailViewModelImpl(savedStateHandle, plantRepositoryImpl, gardenPlantingRepositoryImpl)
    }

    /*@After
    fun tearDown() {
        appDatabase.close()
    }*/

    /*@Suppress("BlockingMethodInNonBlockingContext")
    @Test
    @Throws(InterruptedException::class)
    fun testDefaultValues() = coroutineRule.runBlockingTest {
        assertFalse(viewModel.isPlanted.first())
    }*/