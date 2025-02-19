package ui.component.indicator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.developer.bbee.app.generated.resources.Res
import jp.developer.bbee.app.generated.resources.kodee_angry
import jp.developer.bbee.app.generated.resources.kodee_broken_hearted
import jp.developer.bbee.app.generated.resources.kodee_electrified
import jp.developer.bbee.app.generated.resources.kodee_excited
import jp.developer.bbee.app.generated.resources.kodee_frightened
import jp.developer.bbee.app.generated.resources.kodee_frustrated
import jp.developer.bbee.app.generated.resources.kodee_grumpy
import jp.developer.bbee.app.generated.resources.kodee_lost
import jp.developer.bbee.app.generated.resources.kodee_loving
import jp.developer.bbee.app.generated.resources.kodee_naughty
import jp.developer.bbee.app.generated.resources.kodee_pleased
import jp.developer.bbee.app.generated.resources.kodee_sad
import jp.developer.bbee.app.generated.resources.kodee_shocked
import jp.developer.bbee.app.generated.resources.kodee_surprised
import jp.developer.bbee.app.generated.resources.kodee_welcoming
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.milliseconds

// Constants
private const val BASE_ROTATION_DURATION = 1000
private const val MODULATION_DURATION = 1333
private const val MODULATION_ANGLE = 40f
private const val MODULATION_OFFSET = 0.5f

// Animation
private val ScaleIn = scaleIn(initialScale = 0.1f) +
        fadeIn(tween(durationMillis = 600, easing = FastOutSlowInEasing))
private val ScaleOut = scaleOut(targetScale = 0.1f) +
        fadeOut(tween(durationMillis = 600, easing = FastOutSlowInEasing))

/**
 * A progress indicator that displays a rotating Kodee image.
 *
 * This is a custom implementation of the Material Design progress indicator.
 *
 * @param modifier The modifier to apply to the progress indicator.
 * @param size The size of the progress indicator.
 */
@Composable
fun KodeeProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = KodeeProgressIndicatorDefaults.Size,
) {
    val infiniteTransition = rememberInfiniteTransition()
    var currentKodeeIndex by remember { mutableIntStateOf(getRandomKodeeIndex()) }

    // Randomly change the Kodee image
    LaunchedEffect(Unit) {
        while (currentCoroutineContext().isActive) {
            delay(MODULATION_DURATION.milliseconds)
            currentKodeeIndex = getRandomKodeeIndex(currentKodeeIndex)
        }
    }

    // Continuous rotation animation
    val baseRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = BASE_ROTATION_DURATION,
                easing = LinearEasing,
            ),
        ),
    )

    // Material like modulation
    val modulatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = MODULATION_DURATION,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val modulationAngle = (modulatedProgress - MODULATION_OFFSET) * MODULATION_ANGLE
    val combinedRotation = baseRotation + modulationAngle

    AnimatedContent(
        contentAlignment = Alignment.Center,
        transitionSpec = { ScaleIn togetherWith ScaleOut },
        targetState = KodeeImage.entries[currentKodeeIndex],
        modifier = modifier,
    ) { image ->
        Image(
            painter = painterResource(image.drawableRes),
            contentDescription = image.contentDescription,
            modifier = Modifier
                .size(size)
                .graphicsLayer(
                    rotationZ = combinedRotation,
                ),
        )
    }
}

/** Default values for the [KodeeProgressIndicator]. */
object KodeeProgressIndicatorDefaults {

    /** The default size of the [KodeeProgressIndicator]. */
    val Size: Dp = 64.dp
}

private enum class KodeeImage(
    val drawableRes: DrawableResource,
    val contentDescription: String,
) {

    ANGRY(
        drawableRes = Res.drawable.kodee_angry,
        contentDescription = "Angry Kodee",
    ),

    BROKEN_HEARTED(
        drawableRes = Res.drawable.kodee_broken_hearted,
        contentDescription = "Broken-hearted Kodee",
    ),

    ELECTRIFIED(
        drawableRes = Res.drawable.kodee_electrified,
        contentDescription = "Electrified Kodee",
    ),

    EXCITED(
        drawableRes = Res.drawable.kodee_excited,
        contentDescription = "Excited Kodee",
    ),

    FRIGHTENED(
        drawableRes = Res.drawable.kodee_frightened,
        contentDescription = "Frightened Kodee",
    ),

    FRUSTRATED(
        drawableRes = Res.drawable.kodee_frustrated,
        contentDescription = "Frustrated Kodee",
    ),

    GRUMPY(
        drawableRes = Res.drawable.kodee_grumpy,
        contentDescription = "Grumpy Kodee",
    ),

    LOST(
        drawableRes = Res.drawable.kodee_lost,
        contentDescription = "Lost Kodee",
    ),

    LOVING(
        drawableRes = Res.drawable.kodee_loving,
        contentDescription = "Loving Kodee",
    ),

    NAUGHTY(
        drawableRes = Res.drawable.kodee_naughty,
        contentDescription = "Naughty Kodee",
    ),

    PLEASED(
        drawableRes = Res.drawable.kodee_pleased,
        contentDescription = "Pleased",
    ),

    SAD(
        drawableRes = Res.drawable.kodee_sad,
        contentDescription = "Sad Kodee",
    ),

    SHOCKED(
        drawableRes = Res.drawable.kodee_shocked,
        contentDescription = "Shocked",
    ),

    SURPRISED(
        drawableRes = Res.drawable.kodee_surprised,
        contentDescription = "Surprised Kodee",
    ),

    WELCOMING(
        drawableRes = Res.drawable.kodee_welcoming,
        contentDescription = "Welcoming Kodee",
    ),
}

/**
 * Returns a random index for the [KodeeImage] entries.
 *
 * @param selectedKodeeIndex The index to exclude from the random selection.
 * @return A random index for the [KodeeImage] entries.
 */
private fun getRandomKodeeIndex(selectedKodeeIndex: Int? = null): Int =
    if (selectedKodeeIndex == null) {
        (0..<KodeeImage.entries.size).random()
    } else {
        ((0..<KodeeImage.entries.size) - selectedKodeeIndex).random()
    }

@Preview
@Composable
private fun KodeeProgressIndicatorPreview() {
    MaterialTheme {
        KodeeProgressIndicator()
    }
}
