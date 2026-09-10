package com.procalc.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.procalc.pro.ui.calculator.CalculatorScreen
import com.procalc.pro.ui.secret.PinGate
import com.procalc.pro.ui.secret.RoutineGuideScreen
import com.procalc.pro.ui.secret.SecretSettingsScreen
import com.procalc.pro.ui.theme.ProCalculatorTheme
import com.procalc.pro.util.Haptics
import com.procalc.pro.vm.ArmFeedback
import com.procalc.pro.vm.CalculatorViewModel
import com.procalc.pro.vm.Key

private enum class Screen { CALCULATOR, SETTINGS, GUIDE }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { ProCalculatorApp() }
    }
}

@Composable
private fun ProCalculatorApp() {
    val viewModel: CalculatorViewModel = viewModel()
    val settings by viewModel.settings.collectAsState()

    val context = LocalContext.current
    val haptics = remember { Haptics(context) }

    var screen by remember { mutableStateOf(Screen.CALCULATOR) }
    var askForPin by remember { mutableStateOf(false) }

    fun react(feedback: ArmFeedback) {
        when (feedback) {
            ArmFeedback.ARMED -> haptics.armed(settings.haptics)
            ArmFeedback.DISARMED -> haptics.disarmed(settings.haptics)
            ArmFeedback.STORED -> haptics.stored(settings.haptics)
            ArmFeedback.IGNORED -> Unit
        }
    }

    ProCalculatorTheme(palette = settings.palette) {
        when (screen) {
            Screen.CALCULATOR -> CalculatorScreen(
                state = viewModel.calc,
                settings = settings,
                onKey = { key ->
                    if (key !is Key.Equals) haptics.tick(settings.haptics)
                    viewModel.onKey(key)
                },
                onQuickSet = { react(viewModel.quickSetForce()) },
                onArmToggle = { react(viewModel.toggleArmed()) },
                onPanic = {
                    viewModel.panic()
                    haptics.disarmed(settings.haptics)
                },
                onOpenSecret = { askForPin = true },
            )

            Screen.SETTINGS -> {
                BackHandler { screen = Screen.CALCULATOR }
                SecretSettingsScreen(
                    settings = settings,
                    onUpdate = { transform -> viewModel.updateSettings(transform) },
                    onSetArmed = { armed ->
                        viewModel.setArmed(armed)
                        react(if (armed) ArmFeedback.ARMED else ArmFeedback.DISARMED)
                    },
                    onPanic = {
                        viewModel.panic()
                        haptics.disarmed(settings.haptics)
                    },
                    onOpenGuide = { screen = Screen.GUIDE },
                    onClose = { screen = Screen.CALCULATOR },
                )
            }

            Screen.GUIDE -> {
                BackHandler { screen = Screen.SETTINGS }
                RoutineGuideScreen(onClose = { screen = Screen.SETTINGS })
            }
        }

        if (askForPin) {
            PinGate(
                onVerify = viewModel::verifyPin,
                onSuccess = {
                    askForPin = false
                    screen = Screen.SETTINGS
                },
                onDismiss = { askForPin = false },
            )
        }
    }
}
