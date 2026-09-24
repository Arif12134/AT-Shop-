package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Hero2DAnimationBanner(
    onExploreClick: () -> Unit,
    onShopNowClick: () -> Unit
) {
    // Continuous infinite transitions for 2D visual animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_2d_anim")

    // Rotation for luxury holographic tech rings
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    // Counter rotation for inner ring
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    // Pulse scale for glowing aura
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Shimmer wave horizontal offset
    val shimmerWave by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_wave"
    )

    // Floating vertical bob for product accents
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(22.dp))
            .testTag("hero_banner_section"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            // 2D ANIMATED CANVAS BACKGROUND
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Deep luxury space gradient background
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A),
                            Color(0xFF050B18)
                        ),
                        center = Offset(canvasWidth * 0.75f, canvasHeight * 0.35f),
                        radius = canvasWidth * 0.8f
                    )
                )

                // Shimmering Cyber Ambient Diagonal Light Rays
                val sweepX = canvasWidth * shimmerWave
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.28f * pulseScale),
                            AmberAccent.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(sweepX, canvasHeight * 0.3f),
                        radius = canvasWidth * 0.6f
                    ),
                    radius = canvasWidth * 0.6f,
                    center = Offset(sweepX, canvasHeight * 0.3f)
                )

                // 2D Luxury Animated Orbital Rings (Upper Right Corner)
                val ringCenter = Offset(canvasWidth * 0.82f, canvasHeight * 0.36f)

                // Ambient halo
                drawCircle(
                    color = PrimaryBlue.copy(alpha = 0.25f * pulseScale),
                    radius = 95f * pulseScale,
                    center = ringCenter
                )

                drawCircle(
                    color = AmberAccent.copy(alpha = 0.15f * pulseScale),
                    radius = 65f * pulseScale,
                    center = ringCenter
                )

                // Outer Rotating Ring with Dashes
                rotate(ringRotation, pivot = ringCenter) {
                    drawCircle(
                        color = PrimaryBlueLight,
                        radius = 80f,
                        center = ringCenter,
                        style = Stroke(
                            width = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
                        )
                    )
                    // Satellite Dot 1
                    drawCircle(
                        color = AmberAccent,
                        radius = 5f,
                        center = Offset(ringCenter.x + 80f, ringCenter.y)
                    )
                }

                // Inner Counter-Rotating Ring
                rotate(counterRotation, pivot = ringCenter) {
                    drawCircle(
                        color = AmberAccent,
                        radius = 52f,
                        center = ringCenter,
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                        )
                    )
                    // Satellite Dot 2
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(ringCenter.x, ringCenter.y - 52f)
                    )
                }

                // Center Core Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, PrimaryBlue, Color.Transparent),
                        center = ringCenter,
                        radius = 30f
                    ),
                    radius = 24f,
                    center = ringCenter
                )

                // Floating 2D Star/Particle Constellation
                val particles = listOf(
                    Triple(0.12f, 0.18f, 3f),
                    Triple(0.28f, 0.12f, 4f),
                    Triple(0.45f, 0.25f, 2.5f),
                    Triple(0.65f, 0.15f, 3.5f),
                    Triple(0.88f, 0.12f, 4f),
                    Triple(0.18f, 0.42f, 3f),
                    Triple(0.92f, 0.65f, 3f),
                    Triple(0.55f, 0.48f, 2f)
                )

                particles.forEachIndexed { idx, (px, py, r) ->
                    val wavePhase = (ringRotation + idx * 45f) * (Math.PI / 180f).toFloat()
                    val pAlpha = 0.3f + 0.5f * (0.5f + 0.5f * sin(wavePhase))
                    val pOffset = Offset(
                        canvasWidth * px,
                        canvasHeight * py + sin(wavePhase) * 6f
                    )
                    drawCircle(
                        color = if (idx % 2 == 0) AmberAccent.copy(alpha = pAlpha) else PrimaryBlueLight.copy(alpha = pAlpha),
                        radius = r,
                        center = pOffset
                    )
                }

                // Cyber Grid Lines at the bottom edge
                for (i in 0..6) {
                    val y = canvasHeight * 0.78f + i * 14f
                    val alpha = 0.04f + (i * 0.02f)
                    drawLine(
                        color = PrimaryBlueLight.copy(alpha = alpha),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.2f
                    )
                }
            }

            // FLOATING LUXURY BADGE GRAPHICS (2D Animated Layer)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 22.dp, end = 20.dp)
                    .offset(y = floatingOffset.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEXT-GEN LUXURY",
                            color = AmberAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // FOREGROUND LUXURY CONTENT & TYPOGRAPHY
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Badges & Innovation Header
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "APEX OF INNOVATION",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = AmberAccent.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "EXPERIENCE NEXT-GEN LUXURY",
                                color = AmberAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "NEW SEASON FLAGSHIPS • EXPLORE OUR EXCLUSIVE COLLECTION",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Middle / Main Title & Description
                Column {
                    Text(
                        text = "Upgrade Your World with AT Shop",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Discover exclusive flagship technology, designer fashion, and premium essentials delivered straight to your door.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        ),
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 3
                    )
                }

                // Bottom Call to Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Explore Catalog → Button
                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_explore_button")
                        ) {
                            Text(
                                text = "Explore Catalog →",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }

                        // SHOP THE COLLECTION NOW Button
                        OutlinedButton(
                            onClick = onShopNowClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AmberAccent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("hero_shop_now_button")
                        ) {
                            Text(
                                text = "SHOP NOW",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = AmberAccent,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
