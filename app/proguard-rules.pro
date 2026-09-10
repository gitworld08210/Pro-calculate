# Keep Compose runtime metadata used for recomposition tooling.
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
