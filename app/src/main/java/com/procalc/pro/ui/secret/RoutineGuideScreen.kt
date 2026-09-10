package com.procalc.pro.ui.secret

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.procalc.pro.ui.clickableNoRipple
import com.procalc.pro.ui.theme.LocalCalcPalette

private data class Routine(
    val title: String,
    val premise: String,
    val setup: List<String>,
    val performance: List<String>,
)

private val routines = listOf(
    Routine(
        title = "Serial number prediction",
        premise = "You seal a prediction. A borrowed banknote's serial number is put through " +
            "random arithmetic — and the total matches what you wrote before anyone touched the note.",
        setup = listOf(
            "Write a number on a card, seal it in an envelope, and give it to someone to hold.",
            "Set the effect to Number and enter the same number you wrote.",
            "Set When it fires to First =, and leave Return to honest on.",
            "Arm it with your secret gesture just before you hand the phone over.",
        ),
        performance = listOf(
            "Borrow a banknote. Ask them to read the serial digits aloud and type them in.",
            "Have them add their house number, multiply by the day of the month, subtract their age — " +
                "let them choose freely. Every keystroke is genuinely calculated.",
            "Tell them to press = and read the total out loud.",
            "Have the envelope opened by a third person. The numbers match.",
        ),
    ),
    Routine(
        title = "The exact moment",
        premise = "A prediction written days ago names the precise date and minute at which the " +
            "spectator would stop a runaway calculation.",
        setup = listOf(
            "Choose Date + time, then pick the formats you want — 31.12.2026 and 14.05 read most naturally.",
            "Write the same date and time on a card in advance. Nobody sees it until the end.",
            "Set When it fires to First =.",
        ),
        performance = listOf(
            "Let them build a long, messy sum: birth years, ages, phone digits, anything.",
            "Stress that you never touched the phone and could not know where they would stop.",
            "They press =. The display shows today's date and the current minute.",
            "Now your prediction is read out. It matches to the minute.",
        ),
    ),
    Routine(
        title = "Honest first, impossible last",
        premise = "The spectator tests the calculator several times and it is perfectly normal. " +
            "Only the final total is impossible.",
        setup = listOf(
            "Set your number or date as usual.",
            "Set When it fires to Nth = and choose how many honest answers you want first — 3 is comfortable.",
            "Arm before you hand the phone over.",
        ),
        performance = listOf(
            "Invite them to check the calculator. Let them verify two or three sums themselves.",
            "Hand it back for the real experiment. The next = is the one that matters.",
            "Because they proved it honest with their own hands, the finish is far stronger.",
        ),
    ),
    Routine(
        title = "Stealing the number on the fly",
        premise = "You never set anything in advance — you learn their number mid-routine and " +
            "force it back at them moments later.",
        setup = listOf(
            "Leave Long-press = to store a number switched on.",
            "The effect can stay Off until you need it.",
        ),
        performance = listOf(
            "When you learn the number you want — a card value, a chosen year, a peeked PIN — " +
                "type it on the calculator as though idly fiddling.",
            "Hold = for a moment. Three short buzzes confirm it is stored and armed, " +
                "and the screen clears itself so there is no trace.",
            "Hand the phone over and let them work towards their own total.",
        ),
    ),
    Routine(
        title = "Several spectators, one total",
        premise = "Three people work independently on the same phone and all arrive at the same number.",
        setup = listOf(
            "Set your number, then set When it fires to Every =.",
            "Turn Return to honest off for this one, so every press keeps the force alive.",
        ),
        performance = listOf(
            "Each person types their own private calculation and presses =.",
            "Clear the screen between people so nothing carries over visually.",
            "Everyone announces their total — and it is identical.",
            "Switch Return to honest back on when you are finished.",
        ),
    ),
)

private val craftNotes = listOf(
    "Arm as late as you can. An armed app that sits in your pocket for an hour is an app that " +
        "fires at the wrong moment.",
    "Leave Return to honest switched on unless a routine truly needs otherwise. If they grab " +
        "the phone afterwards, everything they try is real.",
    "Long-press AC at any moment to go instantly honest and clear the screen. Learn where that key is by feel.",
    "Match the shape of the answer to the sum. A four-digit total after multiplying big numbers " +
        "invites questions — pick forced numbers that could plausibly result from the maths they did.",
    "For serial numbers and PINs, turn on Show digits exactly as typed so no thousands " +
        "separators appear where an audience would not expect them.",
    "Never repeat the same effect for the same audience. The second time, they watch the phone " +
        "instead of the moment.",
    "Rehearse the secret gesture until it is invisible. The vibration tells you it worked, " +
        "so you never need to glance at the screen to check.",
)

@Composable
fun RoutineGuideScreen(onClose: () -> Unit) {
    val palette = LocalCalcPalette.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to palette.backdropCore,
                    0.6f to palette.backdropEdge,
                    1f to palette.backdropEdge,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.displayPrimary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickableNoRipple(onClose)
                        .padding(10.dp),
                )
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Routines",
                        color = palette.displayPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "Five ways to use what you have built",
                        color = palette.displaySecondary,
                        fontSize = 12.sp,
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                items(routines) { routine ->
                    SectionCard(title = routine.title, subtitle = routine.premise) {
                        StepBlock(heading = "Before you perform", steps = routine.setup)
                        Spacer(Modifier.height(14.dp))
                        StepBlock(heading = "In performance", steps = routine.performance)
                    }
                }

                item {
                    SectionCard(
                        title = "Craft notes",
                        subtitle = "The difference between a trick that works and one that fools people.",
                    ) {
                        craftNotes.forEach { note ->
                            BulletLine(text = note)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepBlock(heading: String, steps: List<String>) {
    val palette = LocalCalcPalette.current
    Text(
        text = heading.uppercase(),
        color = palette.accent.copy(alpha = 0.8f),
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(8.dp))
    steps.forEachIndexed { index, step ->
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "${index + 1}",
                color = palette.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(22.dp),
            )
            Text(
                text = step,
                color = palette.displayPrimary.copy(alpha = 0.86f),
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
        }
    }
}

@Composable
private fun BulletLine(text: String) {
    val palette = LocalCalcPalette.current
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp, end = 12.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(palette.accent.copy(alpha = 0.75f))
        )
        Text(
            text = text,
            color = palette.displayPrimary.copy(alpha = 0.86f),
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}
