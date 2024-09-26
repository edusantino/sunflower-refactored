/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.compose.plantdetail

import android.text.method.LinkMovementMethod
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.Dimens
import com.google.samples.apps.sunflower.compose.plantdetail.components.PlantImage
import com.google.samples.apps.sunflower.compose.plantdetail.components.fab.PlantFab
import com.google.samples.apps.sunflower.compose.plantdetail.components.toolbar.PlantToolbar
import com.google.samples.apps.sunflower.compose.utils.TextSnackbarContainer
import com.google.samples.apps.sunflower.compose.visible
import com.santino.db.data.entities.Plant
import com.google.samples.apps.sunflower.databinding.ItemPlantDescriptionBinding
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.ui.ViewEvent
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel.PlantingState.PLANTED
import org.koin.androidx.compose.koinViewModel

/**
 * As these callbacks are passed in through multiple Composables, to avoid having to name
 * parameters to not mix them up, they're aggregated in this class.
 */
data class PlantDetailsCallbacks(
    val onAddFabClick: () -> Unit,
    val onRemoveFabClick: () -> Unit,
    val onBackClick: () -> Unit,
    val onShareClick: (String) -> Unit,
    val onGalleryClick: (Plant) -> Unit
)

@Composable
fun PlantDetailsScreen(
    plantDetailsViewModel: PlantDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onShareClick: (String) -> Unit,
    onGalleryClick: (Plant) -> Unit,
) {
    val plant = plantDetailsViewModel.plant.observeAsState().value
    val isPlanted = plantDetailsViewModel.isPlanted.collectAsStateWithLifecycle().value
    plantDetailsViewModel.showSnackbar.observeAsState().value
    val eventUI = plantDetailsViewModel.eventUI.observeAsState().value

    if (plant != null) {
        Surface {
            PlantDetails(
                plant,
                isPlanted,
                PlantDetailsCallbacks(
                    onBackClick = onBackClick,
                    onAddFabClick = {
                        plantDetailsViewModel.addPlantToGarden()
                    },
                    onRemoveFabClick = {
                        plantDetailsViewModel.removePlantFromGarden()
                    },
                    onShareClick = onShareClick,
                    onGalleryClick = onGalleryClick,
                )
            )
        }
    }

    when (eventUI) {
        is ViewEvent.Success -> {
            if (eventUI.data == PLANTED)
                TextSnackbarContainer(
                    snackbarText = stringResource(R.string.added_plant_to_garden),
                    showSnackbar = true,
                    onDismissSnackbar = { plantDetailsViewModel.dismissSnackbar() }
                )
            else
                TextSnackbarContainer(
                    snackbarText = stringResource(R.string.removed_plant_from_garden),
                    showSnackbar = true,
                    onDismissSnackbar = { plantDetailsViewModel.dismissSnackbar() }
                )
        }
        is ViewEvent.Error -> {
            TextSnackbarContainer(
                snackbarText = stringResource(R.string.failed_to_add_plant),
                showSnackbar = true,
                onDismissSnackbar = { plantDetailsViewModel.dismissSnackbar() }
            )
        }
        else -> {}
    }
}

@VisibleForTesting
@Composable
fun PlantDetails(
    plant: Plant,
    isPlanted: Boolean,
    callbacks: PlantDetailsCallbacks,
    modifier: Modifier = Modifier
) {
    // PlantDetails owns the scrollerPosition to simulate CollapsingToolbarLayout's behavior
    val scrollState = rememberScrollState()
    var plantScroller by remember {
        mutableStateOf(PlantDetailsScroller(scrollState, Float.MIN_VALUE))
    }
    val transitionState =
        remember(plantScroller) { plantScroller.toolbarTransitionState }
    val toolbarState = plantScroller.getToolbarState(LocalDensity.current)

    // Transition that fades in/out the header with the image and the Toolbar
    val transition = rememberTransition(transitionState, label = "")
    val toolbarAlpha = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = ""
    ) { toolbarTransitionState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 0f else 1f
    }
    val contentAlpha = transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) }, label = ""
    ) { toolbarTransitionState ->
        if (toolbarTransitionState == ToolbarState.HIDDEN) 1f else 0f
    }

    Box(modifier.fillMaxSize()) {
        PlantDetailsContent(
            scrollState = scrollState,
            toolbarState = toolbarState,
            onNamePosition = { newNamePosition ->
                // Comparing to Float.MIN_VALUE as we are just interested on the original
                // position of name on the screen
                if (plantScroller.namePosition == Float.MIN_VALUE) {
                    plantScroller =
                        plantScroller.copy(namePosition = newNamePosition)
                }
            },
            plant = plant,
            isPlanted = isPlanted,
            imageHeight = with(LocalDensity.current) {
                val candidateHeight =
                    Dimens.PlantDetailAppBarHeight
                // FIXME: Remove this workaround when https://github.com/bumptech/glide/issues/4952
                // is released
                maxOf(candidateHeight, 1.dp)
            },
            onAddFabClick = callbacks.onAddFabClick,
            onRemoveFabClick = callbacks.onRemoveFabClick,
            onGalleryClick = { callbacks.onGalleryClick(plant) },
            contentAlpha = { contentAlpha.value }
        )
        PlantToolbar(
            toolbarState, plant.name, callbacks,
            toolbarAlpha = { toolbarAlpha.value },
            contentAlpha = { contentAlpha.value }
        )
    }
}

@Composable
private fun PlantDetailsContent(
    scrollState: ScrollState,
    toolbarState: ToolbarState,
    plant: Plant,
    isPlanted: Boolean,
    imageHeight: Dp,
    onNamePosition: (Float) -> Unit,
    onAddFabClick: () -> Unit,
    onRemoveFabClick: () -> Unit,
    onGalleryClick: () -> Unit,
    contentAlpha: () -> Float,
) {
    Column(Modifier.verticalScroll(scrollState)) {
        ConstraintLayout {
            val (image, fab, info) = createRefs()
            // Plant Image
            PlantImage(
                imageUrl = plant.imageUrl,
                imageHeight = imageHeight,
                modifier = Modifier
                    .constrainAs(image) { top.linkTo(parent.top) }
                    .alpha(contentAlpha())
            )

            val fabEndMargin = Dimens.PaddingSmall
            // Float Action Button
            PlantFab(
                isPlanted = isPlanted,
                onAddFabClick = onAddFabClick,
                onRemoveFabClick = onRemoveFabClick,
                modifier = Modifier
                    .constrainAs(fab) {
                        centerAround(image.bottom)
                        absoluteRight.linkTo(
                            parent.absoluteRight,
                            margin = fabEndMargin
                        )
                    }
                    .alpha(contentAlpha())
            )

            PlantInformation(
                name = plant.name,
                wateringInterval = plant.wateringInterval,
                description = plant.description,
                onNamePosition = { onNamePosition(it) },
                toolbarState = toolbarState,
                onGalleryClick = onGalleryClick,
                modifier = Modifier.constrainAs(info) {
                    top.linkTo(image.bottom)
                }
            )
        }
    }
}

@Composable
private fun PlantInformation(
    name: String,
    wateringInterval: Int,
    description: String,
    onNamePosition: (Float) -> Unit,
    toolbarState: ToolbarState,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(Dimens.PaddingLarge)) {
        Text(
            text = name,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(
                    start = Dimens.PaddingSmall,
                    end = Dimens.PaddingSmall,
                    bottom = Dimens.PaddingNormal
                )
                .align(Alignment.CenterHorizontally)
                .onGloballyPositioned { onNamePosition(it.positionInWindow().y) }
                .visible { toolbarState == ToolbarState.HIDDEN }
        )
        Box(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = Dimens.PaddingSmall,
                    end = Dimens.PaddingSmall,
                    bottom = Dimens.PaddingNormal
                )
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.watering_needs_prefix),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = Dimens.PaddingSmall)
                        .align(Alignment.CenterHorizontally)
                )

                val wateringIntervalText = pluralStringResource(
                    R.plurals.watering_needs_suffix, wateringInterval, wateringInterval
                )

                Text(
                    text = wateringIntervalText,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_photo_library),
                contentDescription = "Gallery Icon",
                Modifier
                    .clickable { onGalleryClick() }
                    .align(Alignment.CenterEnd)
            )
        }
        PlantDescription(description)
    }
}

@Composable
private fun PlantDescription(description: String) {
    // This remains using AndroidViewBinding because this feature is not in Compose yet
    AndroidViewBinding(ItemPlantDescriptionBinding::inflate) {
        plantDescription.text = HtmlCompat.fromHtml(
            description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        plantDescription.movementMethod = LinkMovementMethod.getInstance()
        plantDescription.linksClickable = true
    }
}

@Preview
@Composable
private fun PlantDetailContentPreview() {
    SunflowerTheme {
        Surface {
            PlantDetails(
                Plant("plantId", "Tomato", "HTML<br>description", 6),
                true,
                PlantDetailsCallbacks({ }, { }, { }, { }, { })
            )
        }
    }
}
