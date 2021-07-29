package com.app.damapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.damapp.ui.theme.Blue500
import com.app.damapp.ui.theme.Blue700

class DamUI(var viewModel: ViewModel) {

    private var status: MutableState<String> = mutableStateOf("ND")
    private var waterLevel: MutableState<Float> = mutableStateOf(0f)
    private var damOpening: MutableState<UInt> = mutableStateOf(servoValue)
    private var manualMode: MutableState<Boolean> = mutableStateOf(false)

    private var bluetoothStatus: MutableState<String> = mutableStateOf("ND")

    @Composable
    fun init() {

        MaterialTheme {
            val typography = MaterialTheme.typography
            val scrollState = rememberScrollState();

            Scaffold(
                content = {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .verticalScroll(state = scrollState, enabled = true),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Title
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Dam Application",
                                style = typography.h4,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dam Mode
                        if (manualMode.value) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Modalità manuale",
                                    style = typography.h5,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue500,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(120.dp))

                        // Dam info
                        Card(
                            modifier = Modifier
                                .clickable { },
                            shape = RoundedCornerShape(20),
                            elevation = 10.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Row(
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Stato diga: ",
                                        style = typography.body1
                                    )
                                    Text(
                                        text = status.value.toString(),
                                        style = typography.body1,
                                    )
                                }

                                if (status.value.toString() == "Alarm" || status.value.toString() == "Pre-alarm") {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        Text(
                                            "Distanza dal sonar: ",
                                            style = typography.body1
                                        )

                                        if (status.value.toString() == "normal") {
                                            Text(
                                                text = " > 100cm",
                                                style = typography.body1,
                                            )
                                        } else {
                                            Text(
                                                text = waterLevel.value.toString() + "cm",
                                                style = typography.body1,
                                            )
                                        }
                                    }
                                }

                                if (status.value.toString() == "Alarm") {
                                    Spacer(modifier = Modifier.height(10.dp))


                                    Row(
                                        horizontalArrangement = Arrangement.Center
                                    ) {

                                        Text(
                                            "Livello apertura diga: ",
                                            style = typography.body1
                                        )
                                        Text(
                                            text = damOpening.value.toString() + "%",
                                            style = typography.body1
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        Card(
                            modifier = Modifier
                                .clickable { },
                            shape = RoundedCornerShape(20),
                            elevation = 10.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Bluetooth status: ",
                                        style = typography.body1,
                                    )
                                    Text(
                                        text = bluetoothStatus.value.toString(),
                                        style = typography.body1,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    connectToBTServer()
                                },
                                modifier = Modifier
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Blue700
                                ),
                                shape = RoundedCornerShape(25),
                            ) {
                                Text(
                                    "Connetti BT",
                                    style = typography.body1,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(50.dp))

                        // Dam Opening Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Button(
                                onClick = {
                                    if (manualMode.value) {
                                        openDam()
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Blue700
                                ),
                                shape = RoundedCornerShape(25)
                            ) {
                                Text(
                                    "Apri",
                                    style = typography.body1,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Button(
                                onClick = {
                                    if (manualMode.value) {
                                        closeDam()
                                    }
                                },
                                modifier = Modifier
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Blue700
                                ),
                                shape = RoundedCornerShape(25),
                            ) {
                                Text(
                                    "Chiudi",
                                    style = typography.body1,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dam Manual Mode Button
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(
                                onClick = {
                                    activateManualMode()
                                },
                                border = BorderStroke(1.dp, Blue500),
                                shape = RoundedCornerShape(25)
                            ) {
                                Text(
                                    "Modalità Manuale",
                                    color = Blue500
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    /**
     * Toggles Manual mode if the Dam is in Alarm mode
     */
    fun activateManualMode() {
        if (status.value == "Alarm") manualMode.value = !manualMode.value
    }

    /**
     * Update the values of UI elements
     */
    fun updateUI() {
        status.value = viewModel.getDamState()
        waterLevel.value = viewModel.getWaterLevel()
        bluetoothStatus.value = viewModel.bluetoothStatus!!
        when (servoValue) {
            0u -> damOpening.value = 0u
            30u -> damOpening.value = 20u
            60u -> damOpening.value = 40u
            90u -> damOpening.value = 60u
            in 120u..150u  -> damOpening.value = 80u
            180u -> damOpening.value = 100u

        }
    }

//    fun convert(number: Int, original: IntRange, target: IntRange): Int {
//        val ratio = number.toFloat() / (original.endInclusive - original.start)
//        return (ratio * (target.endInclusive - target.start)).toInt()
//    }
}