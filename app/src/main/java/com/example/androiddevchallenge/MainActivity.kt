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

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.AppTheme
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    var target: Calendar? by rememberSaveable { mutableStateOf(null) }

    fun addTime(value: Int, unit: TimeUnit) {
        val valueInSeconds = unit.toSeconds(value.toLong()).toInt()

        val newCalendar = (target?.let { Calendar.getInstance().apply { time = it.time } } ?: Calendar.getInstance()).apply {
            add(Calendar.SECOND, valueInSeconds)
        }
        target = newCalendar
    }

    fun stopClock() {
        target = null
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TickingClock(
                target = target?.time,
                textStyle = MaterialTheme.typography.h1.copy(
                    color = MaterialTheme.colors.onSurface,
                )
            )
            Spacer(Modifier.height(16.dp))

            ButtonGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                onButtonClick = ::addTime,
                onStopClick = ::stopClock
            )
        }
    }
}

@Preview
@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    onButtonClick: (Int, TimeUnit) -> Unit = { _, _ -> },
    onStopClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
            CircleButton(text = "+1H", onClick = { onButtonClick.invoke(1, TimeUnit.HOURS) }, modifier = Modifier.height(IntrinsicSize.Max))
            CircleButton(text = "+1M", onClick = { onButtonClick.invoke(1, TimeUnit.MINUTES) }, modifier = Modifier.height(IntrinsicSize.Max))
            CircleButton(text = "+10S", onClick = { onButtonClick.invoke(10, TimeUnit.SECONDS) }, modifier = Modifier.height(IntrinsicSize.Max))
        }
        Spacer(Modifier.height(16.dp))
        CircleButton(text = "STOP", onClick = onStopClick, modifier = Modifier.size(128.dp))
    }
}

@Composable
fun CircleButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
        border = BorderStroke(2.dp, MaterialTheme.colors.onBackground)
    ) {
        Box(
            Modifier
                .aspectRatio(1f)
                .clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Preview(group = "Button")
@Composable
fun CircleButtonPreview() {
    AppTheme {
        CircleButton("Circle Button", onClick = {})
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    AppTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    AppTheme(darkTheme = true) {
        MyApp()
    }
}
