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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.google.samples.apps.sunflower.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsToolbar(
    plantName: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        TopAppBar(
            modifier = modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colorScheme.surface),
            title = {
                Row {
                    IconButton(
                        onBackClick,
                        Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.a11y_back)
                        )
                    }
                    Text(
                        text = plantName,
                        style = MaterialTheme.typography.titleLarge,
                        // As title in TopAppBar has extra inset on the left, need to do this: b/158829169
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    )
                    val shareContentDescription =
                        stringResource(R.string.menu_item_share_plant)
                    IconButton(
                        onShareClick,
                        Modifier
                            .align(Alignment.CenterVertically)
                            // Semantics in parent due to https://issuetracker.google.com/184825850
                            .semantics { contentDescription = shareContentDescription }
                    ) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = null
                        )
                    }
                }
            }
        )
    }
}