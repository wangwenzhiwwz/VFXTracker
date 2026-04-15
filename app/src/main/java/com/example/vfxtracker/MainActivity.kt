package com.example.vfxtracker // 请确保这里是您的包名

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            MaterialTheme {
                VFXTrackingScreen()
            }
        }
    }
}

fun calculateSubtleMarkerColor(bg: Color, offset: Float): Color {
    if (bg == Color.Black) {
        val grayValue = 0f + abs(offset)
        return Color(grayValue, grayValue, grayValue)
    }
    if (bg == Color.White) {
        val grayValue = 1f - abs(offset)
        return Color(grayValue, grayValue, grayValue)
    }
    val factor = 1f + offset
    return Color(
        red = (bg.red * factor).coerceIn(0f, 1f),
        green = (bg.green * factor).coerceIn(0f, 1f),
        blue = (bg.blue * factor).coerceIn(0f, 1f),
        alpha = bg.alpha
    )
}

@Composable
fun VFXTrackingScreen() {
    val context = LocalContext.current

    // --- 状态管理 (State) ---
    var showControls by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) } // 🔥 新增：防误触锁定状态

    var edgePadding by remember { mutableFloatStateOf(150f) }
    var markerSize by remember { mutableFloatStateOf(80f) }
    var markerThickness by remember { mutableFloatStateOf(24f) }
    var screenBrightness by remember { mutableFloatStateOf(0.5f) }
    var luminanceOffset by remember { mutableFloatStateOf(-0.2f) }

    val bgOptions = listOf(
        Color(0xFF00FF00), // Chroma Green
        Color(0xFF0000FF), // Chroma Blue
        Color.Black,
        Color.White
    )
    var bgColor by remember { mutableStateOf(bgOptions[0]) }
    val markerColor = calculateSubtleMarkerColor(bgColor, luminanceOffset)

    LaunchedEffect(screenBrightness) {
        val activity = context as? Activity
        activity?.window?.let { window ->
            val params = window.attributes
            params.screenBrightness = screenBrightness
            window.attributes = params
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            // 🔥 更新手势逻辑：锁定后屏蔽双击，仅响应长按解锁
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!isLocked) { // 未锁定时，双击可折叠/展开面板
                            showControls = !showControls
                        }
                    },
                    onLongPress = {
                        if (isLocked) { // 锁定时，长按任意位置强制解锁并呼出面板
                            isLocked = false
                            showControls = true
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val drawCrosshair = { cx: Float, cy: Float ->
                drawLine(
                    color = markerColor,
                    start = Offset(cx - markerSize, cy),
                    end = Offset(cx + markerSize, cy),
                    strokeWidth = markerThickness
                )
                drawLine(
                    color = markerColor,
                    start = Offset(cx, cy - markerSize),
                    end = Offset(cx, cy + markerSize),
                    strokeWidth = markerThickness
                )
            }

            val w = size.width
            val h = size.height

            drawCrosshair(edgePadding, edgePadding)
            drawCrosshair(w - edgePadding, edgePadding)
            drawCrosshair(edgePadding, h - edgePadding)
            drawCrosshair(w - edgePadding, h - edgePadding)
            drawCrosshair(w / 2f, h / 2f)
        }

        // 仅在未锁定且需要显示时才绘制控制面板
        if (!isLocked && showControls) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp))
                    .background(
                        color = Color(0xF21C1C1E),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ColorSelector(options = bgOptions, selected = bgColor) { bgColor = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.08f)))
                    Spacer(modifier = Modifier.height(16.dp))

                    ControlSlider(label = "可见度偏移", valueText = "${(luminanceOffset * 100).toInt()}%", value = luminanceOffset, range = -0.5f..0.5f, onValueChange = { luminanceOffset = it })
                    ControlSlider(label = "屏幕亮度", valueText = "${(screenBrightness * 100).toInt()}%", value = screenBrightness, range = 0.01f..1.0f, onValueChange = { screenBrightness = it })
                    ControlSlider(label = "十字粗细", valueText = "${markerThickness.toInt()}", value = markerThickness, range = 5f..50f, onValueChange = { markerThickness = it })
                    ControlSlider(label = "十字大小", valueText = "${markerSize.toInt()}", value = markerSize, range = 20f..150f, onValueChange = { markerSize = it })

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔥 新增：锁定按钮
                    Button(
                        onClick = { isLocked = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f), // 半透明的高级按钮背景
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "完成设定并锁定屏幕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "锁定后需长按屏幕 2 秒解锁",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSelector(options: List<Color>, selected: Color, onColorSelected: (Color) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "屏幕背景", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(width = if (selected == color) 2.dp else 1.dp, color = if (selected == color) Color.White else Color.White.copy(alpha = 0.2f), shape = CircleShape)
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
fun ControlSlider(label: String, valueText: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, fontWeight = FontWeight.Normal)
            Text(text = valueText, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }
        Slider(
            value = value, onValueChange = onValueChange, valueRange = range, modifier = Modifier.height(32.dp),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.White.copy(alpha = 0.15f))
        )
    }
}