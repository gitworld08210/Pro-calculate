# Pro Calculator

A genuine Android calculator with a hidden performer layer.

To an audience it is an ordinary, elegant calculator — real arithmetic, real
answers, no clue that anything else exists. To you it is an instrument: you can
decide in advance what the display will show when `=` is pressed, whether that is
a number you sealed in an envelope this morning or the exact minute you are
standing in right now.

Built with Kotlin and Jetpack Compose. Installs as **Calculator**.

---

## Install

Download **`dist/ProCalculator-1.5.0.apk`** from this repository onto your phone
and open it. Android will ask you to allow installs from this source — that is
normal for an app that does not come from the Play Store.

Requires Android 8.0 or newer.

---

## The secret controls

Nothing on screen hints at any of this. Learn these four gestures and you never
need to look at the phone while performing.

| Gesture | What happens |
| --- | --- |
| **Long-press the top-left of the display** | Opens the code prompt, then your private settings. Default code is `1111` — change it. |
| **Double-tap the top-right of the display** | Arms or disarms the routine. One vibration means armed, two means honest. |
| **Long-press `=`** | Stores whatever number is on screen as the forced answer, arms it, and clears the screen. Three vibrations confirm. |
| **Long-press `AC`** | Panic. Instantly honest, screen cleared. |

The arming gesture is configurable if you would rather use the top-left corner or
a long press on the number line.

While a routine is armed, a single four-pixel dot sits in the top-left of the
display. You can find it; nobody across a table can. It can be switched off.

---

## What you can force

- **A number** you choose in advance — any digits, with an option to show them
  exactly as typed so serial numbers and PINs have no thousands separators.
- **Today's date**, in eight formats from `31.12.2026` to `20261231`.
- **The current time**, to the minute or the second.
- **Date and time together**, with your choice of separator.
- **A specific moment** you pick from a calendar — useful when the prediction was
  written days earlier.

### When it fires

- **First `=`** — the very next press.
- **Nth `=`** — let them verify two or three honest sums with their own hands
  first, then fire. This is the strongest option and worth the extra beat.
- **Every `=`** — for handing one phone around a group and having everyone arrive
  at the same total.

**Return to honest after the reveal** is on by default. The moment the force
fires, the calculator goes back to being genuinely honest, so if the phone gets
taken from you and prodded, everything it does is real.

Anything unresolvable — an empty forced number, a preset date you never chose —
falls back to honest arithmetic rather than showing something broken in front of
an audience.

---

## Routines

The app carries its own guide: open the settings and tap the book icon. It
includes five worked routines with setup and performance steps —

1. **Serial number prediction** — a borrowed banknote, random arithmetic, and a
   sealed envelope that matches.
2. **The exact moment** — a prediction written days ago naming the precise minute
   they stopped a runaway sum.
3. **Honest first, impossible last** — they test the calculator themselves before
   the one that matters.
4. **Stealing the number on the fly** — no advance setup; you learn their number
   mid-routine and force it back at them.
5. **Several spectators, one total** — three people, three private sums, one
   identical answer.

Plus craft notes on when to arm, how to choose a plausible forced number, and why
you should never perform the same effect twice for the same people.

---

## Build it yourself

Requires JDK 17 and the Android SDK (platform 35, build-tools 35.0.0).

```bash
export ANDROID_HOME=/path/to/android-sdk
export JAVA_HOME=/path/to/jdk-17

./gradlew assembleRelease      # app/build/outputs/apk/release/app-release.apk
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # 23 tests over the arithmetic and the force logic
```

The release build is signed with the standard debug key so it can be side-loaded
directly. Swap in a real keystore in `app/build.gradle.kts` before you ever
publish it.

---

## How it is put together

```
app/src/main/java/com/procalc/pro/
├── engine/            Pure arithmetic. No Android, no UI, fully unit-tested.
│   ├── Evaluator.kt         BigDecimal evaluation with real operator precedence
│   ├── NumberFormatter.kt   Grouping, digit limits, scientific notation
│   └── CalculatorEngine.kt  Immutable state + one pure function per key
├── magic/             The performer layer.
│   ├── MagicModels.kt       Modes, triggers, formats, settings
│   ├── ForceResolver.kt     Turns settings into the string on the display
│   └── MagicSettingsStore.kt  Persistence under innocuous keys
├── vm/                CalculatorViewModel — decides honest vs forced
├── ui/                Compose UI, three palettes, hidden gestures
└── util/Haptics.kt    Distinct vibration patterns you can read by feel
```

The arithmetic layer has no idea the performer layer exists. The force is applied
at exactly one place — the moment `=` is evaluated — which is what keeps the
calculator trustworthy the rest of the time.

---

## A note on use

This is a prop for performance. It is for entertaining people who know they are
watching a magician, and for nothing else. Do not use it where someone is relying
on the answer being true.
