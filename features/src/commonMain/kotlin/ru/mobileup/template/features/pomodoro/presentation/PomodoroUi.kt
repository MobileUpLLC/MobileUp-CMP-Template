package ru.mobileup.template.features.pomodoro.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.template.core.theme.AppTheme
import ru.mobileup.template.core.theme.custom.CustomTheme
import ru.mobileup.template.core.widget.button.AppButton
import ru.mobileup.template.core.widget.button.ButtonType
import ru.mobileup.template.core.widget.toolbar.AppToolbar
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pomodoro_cycle_label
import ru.mobileup.template.features.generated.resources.pomodoro_pause
import ru.mobileup.template.features.generated.resources.pomodoro_phase_long_break
import ru.mobileup.template.features.generated.resources.pomodoro_phase_short_break
import ru.mobileup.template.features.generated.resources.pomodoro_phase_work
import ru.mobileup.template.features.generated.resources.pomodoro_reset
import ru.mobileup.template.features.generated.resources.pomodoro_skip
import ru.mobileup.template.features.generated.resources.pomodoro_start
import ru.mobileup.template.features.generated.resources.pomodoro_title
import ru.mobileup.template.features.pomodoro.domain.PomodoroPhase
import ru.mobileup.template.features.pomodoro.domain.PomodoroState
import kotlin.math.ceil

@Composable
fun PomodoroUi(
    component: PomodoroComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val palette = phasePalette(state.phase)

    val animatedBackground by animateColorAsState(
        targetValue = palette.background,
        animationSpec = tween(durationMillis = 600)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppToolbar(
                title = stringResource(Res.string.pomodoro_title),
                showBackButton = true
            )
        },
        containerColor = animatedBackground
    ) { innerPadding ->
        PomodoroContent(
            state = state,
            totalCycles = component.totalCyclesBeforeLongBreak,
            palette = palette,
            innerPadding = innerPadding,
            onStartPauseClick = component::onStartPauseClick,
            onResetClick = component::onResetClick,
            onSkipClick = component::onSkipClick
        )
    }
}

@Composable
private fun PomodoroContent(
    state: PomodoroState,
    totalCycles: Int,
    palette: PhasePalette,
    innerPadding: PaddingValues,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        PhaseHeader(phase = state.phase, palette = palette)

        TimerCircle(
            state = state,
            palette = palette,
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .aspectRatio(1f)
        )

        CycleIndicator(
            completedInRound = state.completedWorkCycles % totalCycles,
            total = totalCycles,
            currentInRound = state.phase == PomodoroPhase.Work,
            palette = palette
        )

        Spacer(Modifier.height(8.dp))

        Controls(
            isRunning = state.isRunning,
            onStartPauseClick = onStartPauseClick,
            onResetClick = onResetClick,
            onSkipClick = onSkipClick
        )
    }
}

@Composable
private fun PhaseHeader(phase: PomodoroPhase, palette: PhasePalette) {
    val label = stringResource(
        when (phase) {
            PomodoroPhase.Work -> Res.string.pomodoro_phase_work
            PomodoroPhase.ShortBreak -> Res.string.pomodoro_phase_short_break
            PomodoroPhase.LongBreak -> Res.string.pomodoro_phase_long_break
        }
    )
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(palette.chip)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = palette.onPrimary,
            style = CustomTheme.typography.button.bold
        )
    }
}

@Composable
private fun TimerCircle(
    state: PomodoroState,
    palette: PhasePalette,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )
    val trackColor by animateColorAsState(
        targetValue = palette.track,
        animationSpec = tween(durationMillis = 600)
    )
    val strokeWidthDp = 18.dp
    val strokeWidthPx = with(LocalDensity.current) { strokeWidthDp.toPx() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
            val topLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = FULL_SWEEP,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        palette.primary,
                        palette.accent,
                        palette.primary
                    ),
                    center = Offset(size.width / 2f, size.height / 2f)
                ),
                startAngle = START_ANGLE,
                sweepAngle = FULL_SWEEP * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
        TimerCenter(state = state, palette = palette)
    }
}

@Composable
private fun TimerCenter(state: PomodoroState, palette: PhasePalette) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatMmSs(state.remaining.inWholeMilliseconds),
            color = palette.onSurface,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (state.isRunning) "" else "Paused",
            color = palette.onSurface.copy(alpha = 0.6f),
            style = CustomTheme.typography.body.regular
        )
    }
}

@Composable
private fun CycleIndicator(
    completedInRound: Int,
    total: Int,
    currentInRound: Boolean,
    palette: PhasePalette
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(total) { index ->
                val isFilled = index < completedInRound
                val isCurrent = index == completedInRound && currentInRound
                val dotColor by animateColorAsState(
                    targetValue = when {
                        isFilled -> palette.primary
                        isCurrent -> palette.accent
                        else -> palette.track
                    },
                    animationSpec = tween(durationMillis = 400)
                )
                val dotSize = if (isCurrent) 14.dp else 10.dp
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                Res.string.pomodoro_cycle_label,
                completedInRound.coerceAtMost(total),
                total
            ),
            color = palette.onSurface.copy(alpha = 0.7f),
            style = CustomTheme.typography.caption.regular
        )
    }
}

@Composable
private fun Controls(
    isRunning: Boolean,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppButton(
            text = stringResource(Res.string.pomodoro_reset),
            buttonType = ButtonType.Secondary,
            onClick = onResetClick,
            modifier = Modifier.weight(1f)
        )
        AppButton(
            text = stringResource(if (isRunning) Res.string.pomodoro_pause else Res.string.pomodoro_start),
            buttonType = ButtonType.Primary,
            onClick = onStartPauseClick,
            modifier = Modifier.weight(1.4f)
        )
        AppButton(
            text = stringResource(Res.string.pomodoro_skip),
            buttonType = ButtonType.Secondary,
            onClick = onSkipClick,
            modifier = Modifier.weight(1f)
        )
    }
}

private data class PhasePalette(
    val background: Color,
    val primary: Color,
    val accent: Color,
    val chip: Color,
    val track: Color,
    val onPrimary: Color,
    val onSurface: Color
)

@Composable
private fun phasePalette(phase: PomodoroPhase): PhasePalette {
    val onSurface = CustomTheme.colors.text.primary
    val track = lerp(CustomTheme.colors.background.surface, onSurface, 0.08f)
    return when (phase) {
        PomodoroPhase.Work -> PhasePalette(
            background = Color(0xFFFDECEA),
            primary = Color(0xFFE25C5C),
            accent = Color(0xFFFF8A65),
            chip = Color(0xFFE25C5C),
            track = track,
            onPrimary = Color.White,
            onSurface = onSurface
        )
        PomodoroPhase.ShortBreak -> PhasePalette(
            background = Color(0xFFE7F5EE),
            primary = Color(0xFF3FA776),
            accent = Color(0xFF7BC9A2),
            chip = Color(0xFF3FA776),
            track = track,
            onPrimary = Color.White,
            onSurface = onSurface
        )
        PomodoroPhase.LongBreak -> PhasePalette(
            background = Color(0xFFE6EEFA),
            primary = Color(0xFF3D6BB6),
            accent = Color(0xFF7BA5E0),
            chip = Color(0xFF3D6BB6),
            track = track,
            onPrimary = Color.White,
            onSurface = onSurface
        )
    }
}

private fun formatMmSs(remainingMs: Long): String {
    val totalSeconds = ceil(remainingMs.coerceAtLeast(0L) / MS_IN_SECOND.toDouble()).toLong()
    val minutes = totalSeconds / SECONDS_IN_MINUTE
    val seconds = totalSeconds % SECONDS_IN_MINUTE
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

private const val MS_IN_SECOND = 1000L
private const val SECONDS_IN_MINUTE = 60L
private const val FULL_SWEEP = 360f
private const val START_ANGLE = -90f

@Preview
@Composable
private fun PomodoroUiWorkRunningPreview() {
    AppTheme {
        PomodoroUi(FakePomodoroComponent(FakePomodoroComponent.WORK_RUNNING))
    }
}

@Preview
@Composable
private fun PomodoroUiShortBreakPreview() {
    AppTheme {
        PomodoroUi(FakePomodoroComponent(FakePomodoroComponent.SHORT_BREAK_PAUSED))
    }
}

@Preview
@Composable
private fun PomodoroUiLongBreakPreview() {
    AppTheme {
        PomodoroUi(FakePomodoroComponent(FakePomodoroComponent.LONG_BREAK_RUNNING))
    }
}
