/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.androiddevchallenge

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val tickerLayoutResizeAnimationSpec = tween<IntSize>(
    delayMillis = 0,
    durationMillis = 330,
    easing = LinearEasing
)

private val textAnimationSpec = tween<Float>(
    delayMillis = 100,
    durationMillis = 660,
    easing = FastOutSlowInEasing
)

@Composable
fun Ticker(
    modifier: Modifier = Modifier,
    text: String,
    shouldAnimate: Boolean,
    prefix: String? = null,
    suffix: String? = null,
    textStyle: TextStyle = TextStyle.Default,
) {
    val rowModifier = if (shouldAnimate) {
        modifier.animateContentSize(tickerLayoutResizeAnimationSpec)
    } else {
        modifier
    }

    val textItemHeight = (textStyle.fontSize.value * 1.5).dp // Simple assumption but makes calculations much simpler
    Row(rowModifier) {
        prefix?.let {
            TickerText(it, textStyle, textItemHeight)
        }
        text.toCharArray().map {
            CharColumn(it, textStyle, textItemHeight, shouldAnimate)
        }
        suffix?.let {
            TickerText(it, textStyle, textItemHeight)
        }
    }
}

@Composable
internal fun CharColumn(selectedChar: Char, textStyle: TextStyle, textItemHeight: Dp, shouldAnimate: Boolean) {
    val textList = when {
        selectedChar.isDigit() -> "9876543210 ".toCharArray()
        else -> charArrayOf(selectedChar, ' ')
    }

    val selectedCharIndex = textList.reversed().indexOf(selectedChar)

    val textItemHeightPx = with(LocalDensity.current) { textItemHeight.toPx() }.roundToInt() // Rounds off fractional pixels

    val scrollState = rememberScrollState(0)

    LaunchedEffect(selectedChar) {
        val totalColumnHeight = textItemHeightPx * textList.size

        // This can occur if the textList changes (ie from symbol to digit or vice-versa)
        if (scrollState.value >= totalColumnHeight) scrollState.scrollTo(0)

        val selectedCharPosition = textItemHeightPx * selectedCharIndex
        Log.d("TICKER", "$selectedChar - $selectedCharPosition - ${scrollState.value}")

        if (shouldAnimate) {
            scrollState.animateScrollTo(
                value = selectedCharPosition,
                animationSpec = textAnimationSpec
            )
        } else {
            scrollState.scrollTo(value = selectedCharPosition)
        }
    }

    Column(
        modifier = Modifier
            .height(textItemHeight)
            .verticalScroll(state = scrollState, reverseScrolling = true, enabled = false),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            textList.map {
                TickerText(
                    text = it.toString(),
                    textStyle = textStyle,
                    textItemHeight = textItemHeight
                )
            }
        }
    )
}

@Composable
private fun TickerText(text: String, textStyle: TextStyle, textItemHeight: Dp) {
    Text(
        text = text,
        style = textStyle,
        modifier = Modifier.height(textItemHeight)
    )
}
