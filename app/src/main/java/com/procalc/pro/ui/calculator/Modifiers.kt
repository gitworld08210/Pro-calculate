package com.procalc.pro.ui.calculator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

/** Ripple-free press handling so the custom scale animation is the only feedback. */
fun Modifier.simplePress(
    interaction: MutableInteractionSource,
    onClick: () -> Unit,
): Modifier = clickable(
    interactionSource = interaction,
    indication = null,
    onClick = onClick,
)

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.combinedPress(
    interaction: MutableInteractionSource,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier = combinedClickable(
    interactionSource = interaction,
    indication = null,
    onClick = onClick,
    onLongClick = onLongClick,
)
