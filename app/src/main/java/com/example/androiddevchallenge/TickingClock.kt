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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun TickingClock(
    target: Date?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
) {
    val coroutineScope = rememberCoroutineScope()
    var job: Job? = null

    var currentDateTime by remember { mutableStateOf(Calendar.getInstance().time) }
    var reachedTarget by remember { mutableStateOf(false) }

    suspend fun reloadDateTime() {
        val newDateTime = Calendar.getInstance().time
        currentDateTime = newDateTime
        reachedTarget = (currentDateTime.time / 1000) >= (target?.time?.div(1000) ?: 0)

        if (!reachedTarget) {
            delay(250L)
            reloadDateTime()
        }
    }

    DisposableEffect(target) {
        currentDateTime = Calendar.getInstance().time

        if (target != null) {
            job = coroutineScope.launch(Dispatchers.Default) {
                reloadDateTime()
            }
        } else {
            job?.cancel()
        }

        onDispose {
            job?.cancel()
        }
    }

    val text = when {
        target == null -> "READY"
        reachedTarget -> "TIME UP"
        else -> getCountingText(target, currentDateTime)
    }
    val shouldAnimate = text != "TIME UP" && text != "READY"

    Ticker(
        text = text,
        shouldAnimate = shouldAnimate,
        textStyle = textStyle,
        modifier = modifier
    )
}

private fun getCountingText(target: Date?, currentDateTime: Date): String {
    if (target == null) return "TIME UP"

    val difference = (target.time - currentDateTime.time).coerceAtLeast(0L)
    val diffSeconds = difference / 1000
    if (diffSeconds == 0L) return "TIME UP"

    val hours = TimeUnit.SECONDS.toHours(diffSeconds)
    val minutes = TimeUnit.SECONDS.toMinutes(diffSeconds) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = diffSeconds.rem(60)

    return buildString {
        if (hours > 0L) append("${String.format("%02d", hours)}:")
        if (minutes > 0L || hours > 0L) append("${String.format("%02d", minutes)}:")
        if (seconds > 0L || minutes > 0L || hours > 0L) append(String.format("%02d", seconds))
    }
}
