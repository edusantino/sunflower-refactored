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

package com.google.samples.apps.sunflower.compose.plantdetail.components.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.google.samples.apps.sunflower.compose.plantdetail.PlantDetailsCallbacks
import com.google.samples.apps.sunflower.compose.plantdetail.ToolbarState
import com.google.samples.apps.sunflower.compose.plantdetail.isShown

@Composable
fun PlantToolbar(
    toolbarState: ToolbarState,
    plantName: String,
    callbacks: PlantDetailsCallbacks,
    toolbarAlpha: () -> Float,
    contentAlpha: () -> Float
) {
    val onShareClick = {
        callbacks.onShareClick(plantName)
    }
    if (toolbarState.isShown) {
        PlantDetailsToolbar(
            plantName = plantName,
            onBackClick = callbacks.onBackClick,
            onShareClick = onShareClick,
            modifier = Modifier.alpha(toolbarAlpha())
        )
    } else {
        PlantHeaderActions(
            onBackClick = callbacks.onBackClick,
            onShareClick = onShareClick,
            modifier = Modifier.alpha(contentAlpha())
        )
    }
}