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

package com.google.samples.apps.sunflower.compose.plantdetail.components.fab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.google.samples.apps.sunflower.R

@Composable
fun PlantFab(
    isPlanted: Boolean,
    onAddFabClick: () -> Unit,
    onRemoveFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val addPlantContentDescription = stringResource(R.string.add_plant)
    FloatingActionButton(
        onClick = if (isPlanted) onRemoveFabClick else onAddFabClick,
        shape = MaterialTheme.shapes.small,
        // Semantics in parent due to https://issuetracker.google.com/184825850
        modifier = modifier.semantics {
            contentDescription = addPlantContentDescription
        }
    ) {
        Icon(
            if (isPlanted) Icons.Default.Clear else Icons.Default.Add,
            contentDescription = null
        )
    }
}