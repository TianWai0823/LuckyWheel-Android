package com.apesource.myfirstandroid

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import java.util.UUID

data class WheelOption(
    val id: Int,
    val text: String,
    val color: Color,
    val weight: Int = 1
)

data class WheelConfig(
    val id: String,
    val name: String,
    val options: List<WheelOption>,
    val createdAt: Long = System.currentTimeMillis()
)

val wheelColors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFF4ECDC4),
    Color(0xFFFFE66D),
    Color(0xFF95E1D3),
    Color(0xFFF38181),
    Color(0xFFAA96DA),
    Color(0xFFFCBAD3),
    Color(0xFFA8D8EA),
    Color(0xFFFF9F43),
    Color(0xFF6C5CE7)
)

data class ColorCategory(
    val name: String,
    val colors: List<Color>
)

val presetColors = listOf(
    ColorCategory(
        "温暖色系",
        listOf(
            Color(0xFFFF6B6B), Color(0xFFFF8A8A), Color(0xFFF38181),
            Color(0xFFFA9A5D), Color(0xFFFF9F43), Color(0xFFFB8C00),
            Color(0xFFFFD166), Color(0xFFFFE66D), Color(0xFFF9CA24)
        )
    ),
    ColorCategory(
        "清爽色系",
        listOf(
            Color(0xFF00B894), Color(0xFF4ECDC4), Color(0xFF95E1D3),
            Color(0xFF74B9FF), Color(0xFFA8D8EA), Color(0xFF74B9FF),
            Color(0xFF6C5CE7), Color(0xFFA29BFE), Color(0xFFAA96DA)
        )
    ),
    ColorCategory(
        "柔和色系",
        listOf(
            Color(0xFFFCBAD3), Color(0xFFFDCB6E), Color(0xFF81ECEC),
            Color(0xFFDDA0DD), Color(0xFFFFF5E6), Color(0xFFFFB6C1),
            Color(0xFFE0FFFF), Color(0xFFE6E6FA), Color(0xFFFFFACD)
        )
    ),
    ColorCategory(
        "深色系",
        listOf(
            Color(0xFF2D3436), Color(0xFF636E72), Color(0xFFB2BEC3),
            Color(0xFF1E272E), Color(0xFF30336B), Color(0xFF4A47A3),
            Color(0xFF535C68), Color(0xFF2C3E50), Color(0xFF34495E)
        )
    ),
    ColorCategory(
        "美食色系",
        listOf(
            Color(0xFFD35400), Color(0xFFE67E22), Color(0xFFF39C12),
            Color(0xFF27AE60), Color(0xFF16A085), Color(0xFFC0392B),
            Color(0xFF8E44AD), Color(0xFF2980B9), Color(0xFF795548)
        )
    )
)

val quickColors = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
    Color(0xFF95E1D3), Color(0xFFF38181), Color(0xFFAA96DA),
    Color(0xFFFCBAD3), Color(0xFFA8D8EA), Color(0xFFFF9F43),
    Color(0xFF6C5CE7), Color(0xFF27AE60), Color(0xFFE67E22),
    Color(0xFF795548), Color(0xFF2980B9), Color(0xFF9B59B6)
)

private const val PREFS_NAME = "FortuneWheelPrefs"
private const val KEY_OPTIONS = "wheel_options"

fun saveOptions(context: Context, options: List<WheelOption>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = options.joinToString(separator = "|||") {
        "${it.id}|${it.text}|${it.color.toArgb()}|${it.weight}"
    }
    prefs.edit().putString(KEY_OPTIONS, json).apply()
}

fun loadOptions(context: Context): List<WheelOption> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(KEY_OPTIONS, null) ?: return getDefaultOptions()
    return try {
        json.split("|||").mapNotNull { str ->
            val parts = str.split("|")
            if (parts.size == 4) {
                WheelOption(
                    id = parts[0].toInt(),
                    text = parts[1],
                    color = Color(parts[2].toInt()),
                    weight = parts[3].toInt()
                )
            } else null
        }
    } catch (e: Exception) {
        getDefaultOptions()
    }
}

fun getDefaultOptions(): List<WheelOption> = listOf(
    WheelOption(1, "火锅", wheelColors[0], 1),
    WheelOption(2, "烧烤", wheelColors[1], 1),
    WheelOption(3, "日料", wheelColors[2], 1),
    WheelOption(4, "西餐", wheelColors[3], 1),
    WheelOption(5, "中餐", wheelColors[4], 1)
)

fun getPresetConfigs(): List<WheelConfig> = listOf(
    WheelConfig(
        id = "preset-mixue",
        name = "🥤 蜜雪冰城",
        options = listOf(
            WheelOption(1, "珍珠奶茶", wheelColors[0], 1),
            WheelOption(2, "冰鲜柠檬水", wheelColors[1], 1),
            WheelOption(3, "蜜桃四季春", wheelColors[2], 1),
            WheelOption(4, "草莓摇摇奶昔", wheelColors[3], 1),
            WheelOption(5, "茉莉奶绿", wheelColors[4], 1),
            WheelOption(6, "棒打鲜橙", wheelColors[5], 1),
            WheelOption(7, "芝士奶盖四季春", wheelColors[6], 1),
            WheelOption(8, "三拼霸霸奶茶", wheelColors[7], 1),
            WheelOption(9, "奥利奥圣代", wheelColors[8], 1),
            WheelOption(10, "新鲜冰淇淋", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-luckin-coffee",
        name = "☕ 幸运咖",
        options = listOf(
            WheelOption(1, "椰椰拿铁", wheelColors[0], 1),
            WheelOption(2, "招牌冰拿铁", wheelColors[1], 1),
            WheelOption(3, "杨梅吐气香柠茶", wheelColors[2], 1),
            WheelOption(4, "芭乐冰拿铁", wheelColors[3], 1),
            WheelOption(5, "抹茶幸运冰", wheelColors[4], 1),
            WheelOption(6, "多莓冰萃咖", wheelColors[5], 1),
            WheelOption(7, "摇摇冰椰美式", wheelColors[6], 1),
            WheelOption(8, "岭南荔枝冰茶", wheelColors[7], 1),
            WheelOption(9, "芭乐抹茶椰", wheelColors[8], 1),
            WheelOption(10, "杨梅吐气幸运冰", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-ruixing",
        name = "☕ 瑞幸咖啡",
        options = listOf(
            WheelOption(1, "生椰拿铁", wheelColors[0], 1),
            WheelOption(2, "椰云拿铁", wheelColors[1], 1),
            WheelOption(3, "厚乳拿铁", wheelColors[2], 1),
            WheelOption(4, "丝绒拿铁", wheelColors[3], 1),
            WheelOption(5, "碧螺知春拿铁", wheelColors[4], 1),
            WheelOption(6, "橙C美式", wheelColors[5], 1),
            WheelOption(7, "柠C美式", wheelColors[6], 1),
            WheelOption(8, "葡萄冰萃美式", wheelColors[7], 1),
            WheelOption(9, "茉莉花香拿铁", wheelColors[8], 1),
            WheelOption(10, "马斯卡彭生酪拿铁", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-cudi",
        name = "☕ 库迪咖啡",
        options = listOf(
            WheelOption(1, "生椰拿铁", wheelColors[0], 1),
            WheelOption(2, "潘帕斯蓝生酪茉莉拿铁", wheelColors[1], 1),
            WheelOption(3, "摩卡库可冰", wheelColors[2], 1),
            WheelOption(4, "星辰厚乳拿铁", wheelColors[3], 1),
            WheelOption(5, "粉椰三重奏", wheelColors[4], 1),
            WheelOption(6, "柚见气泡冰萃", wheelColors[5], 1),
            WheelOption(7, "开心果芝芝拿铁", wheelColors[6], 1),
            WheelOption(8, "蜜意绵云库可冰", wheelColors[7], 1),
            WheelOption(9, "橙C美式", wheelColors[8], 1),
            WheelOption(10, "纯萃燕麦拿铁", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-chapubu",
        name = "🍵 茶瀑布",
        options = listOf(
            WheelOption(1, "荔枝冰奶", wheelColors[0], 1),
            WheelOption(2, "青苹果茉莉冰奶", wheelColors[1], 1),
            WheelOption(3, "洛神雪", wheelColors[2], 1),
            WheelOption(4, "爆红洛神柠檬茶", wheelColors[3], 1),
            WheelOption(5, "橙C洛神", wheelColors[4], 1),
            WheelOption(6, "葡萄气泡水", wheelColors[5], 1),
            WheelOption(7, "芭乐冰奶", wheelColors[6], 1),
            WheelOption(8, "草莓冰奶", wheelColors[7], 1),
            WheelOption(9, "冰蓝海盐冰奶", wheelColors[8], 1),
            WheelOption(10, "芝士万里山海", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-chabaidao",
        name = "🧋 茶百道",
        options = listOf(
            WheelOption(1, "杨枝甘露", wheelColors[0], 1),
            WheelOption(2, "青提茉莉", wheelColors[1], 1),
            WheelOption(3, "豆乳玉麒麟", wheelColors[2], 1),
            WheelOption(4, "荔枝冰奶", wheelColors[3], 1),
            WheelOption(5, "西瓜波波", wheelColors[4], 1),
            WheelOption(6, "奥利奥半熟芝士", wheelColors[5], 1),
            WheelOption(7, "铁观音奶冻", wheelColors[6], 1),
            WheelOption(8, "草莓奶冻", wheelColors[7], 1),
            WheelOption(9, "黄金椰椰乌龙", wheelColors[8], 1),
            WheelOption(10, "乌漆嘛黑", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-yihetang",
        name = "🍵 益禾堂",
        options = listOf(
            WheelOption(1, "益禾烤奶", wheelColors[0], 1),
            WheelOption(2, "薄荷奶绿", wheelColors[1], 1),
            WheelOption(3, "禾风奶绿", wheelColors[2], 1),
            WheelOption(4, "益杯烧仙草", wheelColors[3], 1),
            WheelOption(5, "葡萄啵啵", wheelColors[4], 1),
            WheelOption(6, "宇治抹茶", wheelColors[5], 1),
            WheelOption(7, "鲜柠撞奶", wheelColors[6], 1),
            WheelOption(8, "杨枝甘露", wheelColors[7], 1),
            WheelOption(9, "柠檬小麦青汁", wheelColors[8], 1),
            WheelOption(10, "冻柠蜜", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    ),
    WheelConfig(
        id = "preset-bawang",
        name = "🍵 霸王茶姬",
        options = listOf(
            WheelOption(1, "伯牙绝弦", wheelColors[0], 1),
            WheelOption(2, "花田乌龙", wheelColors[1], 1),
            WheelOption(3, "桂馥兰香", wheelColors[2], 1),
            WheelOption(4, "浮生梦媞", wheelColors[3], 1),
            WheelOption(5, "青青糯山", wheelColors[4], 1),
            WheelOption(6, "寻香山茶", wheelColors[5], 1),
            WheelOption(7, "春日桃桃", wheelColors[6], 1),
            WheelOption(8, "夏梦玫珑", wheelColors[7], 1),
            WheelOption(9, "万里木兰", wheelColors[8], 1),
            WheelOption(10, "白雾红尘", wheelColors[9], 1)
        ),
        createdAt = System.currentTimeMillis()
    )
)

private const val PREFS_PRESETS_INITIALIZED = "presets_initialized"

fun checkAndInitPresets(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    if (!prefs.getBoolean(PREFS_PRESETS_INITIALIZED, false)) {
        val existingConfigs = loadConfigs(context)
        val presets = getPresetConfigs()
        val newConfigs = (presets + existingConfigs).distinctBy { it.id }
        saveConfigs(context, newConfigs)
        prefs.edit().putBoolean(PREFS_PRESETS_INITIALIZED, true).apply()
    }
}

private const val KEY_CONFIGS = "wheel_configs"
private const val CONFIG_SEPARATOR = "###CONFIG###"
private const val OPTION_SEPARATOR = "|||"
private const val FIELD_SEPARATOR = "|"

fun saveConfig(context: Context, config: WheelConfig) {
    val configs = loadConfigs(context).toMutableList()
    val existingIndex = configs.indexOfFirst { it.id == config.id }
    if (existingIndex >= 0) {
        configs[existingIndex] = config
    } else {
        configs.add(config)
    }
    saveConfigs(context, configs)
}

private fun saveConfigs(context: Context, configs: List<WheelConfig>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = configs.joinToString(separator = CONFIG_SEPARATOR) { config ->
        val optionsJson = config.options.joinToString(OPTION_SEPARATOR) { opt ->
            "${opt.id}|${opt.text}|${opt.color.toArgb()}|${opt.weight}"
        }
        "${config.id}|${config.name}|${config.createdAt}|$optionsJson"
    }
    prefs.edit().putString(KEY_CONFIGS, json).apply()
}

fun loadConfigs(context: Context): List<WheelConfig> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(KEY_CONFIGS, null) ?: return emptyList()
    return try {
        json.split(CONFIG_SEPARATOR).mapNotNull { configStr ->
            val parts = configStr.split(FIELD_SEPARATOR, limit = 4)
            if (parts.size >= 4) {
                val configId = parts[0]
                val configName = parts[1]
                val createdAt = parts[2].toLongOrNull() ?: System.currentTimeMillis()
                val optionsStr = parts[3]
                val options = optionsStr.split(OPTION_SEPARATOR).mapNotNull { optStr ->
                    val optParts = optStr.split(FIELD_SEPARATOR)
                    if (optParts.size == 4) {
                        WheelOption(
                            id = optParts[0].toInt(),
                            text = optParts[1],
                            color = Color(optParts[2].toInt()),
                            weight = optParts[3].toInt()
                        )
                    } else null
                }
                WheelConfig(
                    id = configId,
                    name = configName,
                    options = options,
                    createdAt = createdAt
                )
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun deleteConfig(context: Context, configId: String) {
    val configs = loadConfigs(context).filter { it.id != configId }
    saveConfigs(context, configs)
}

fun renameConfig(context: Context, configId: String, newName: String) {
    val configs = loadConfigs(context).map {
        if (it.id == configId) it.copy(name = newName) else it
    }
    saveConfigs(context, configs)
}

data class SpinConfig(
    val startAngle: Float,
    val targetAngle: Float,
    val spinDuration: Long,
    val selectedText: String
)

@Composable
fun FortuneWheelApp() {
    var showSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var options by remember { mutableStateOf(loadOptions(context)) }
    var isSpinning by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var hasSpun by remember { mutableStateOf(false) }
    var currentConfigName by remember { mutableStateOf<String?>(null) }
    var currentConfigId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        checkAndInitPresets(context)
    }

    val onResultReset = {
        resultText = null
        hasSpun = false
    }

    val onConfigLoaded: (String, String) -> Unit = { configName, configId ->
        currentConfigName = configName
        currentConfigId = configId
    }

    val onConfigReset = {
        currentConfigName = null
        currentConfigId = null
    }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var spinConfig by remember { mutableStateOf<SpinConfig?>(null) }

    LaunchedEffect(options) {
        saveOptions(context, options)
    }

    LaunchedEffect(spinConfig) {
        val config = spinConfig ?: return@LaunchedEffect
        val startTime = System.currentTimeMillis()
        val endTime = startTime + config.spinDuration

        while (System.currentTimeMillis() < endTime) {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = elapsed.toFloat() / config.spinDuration
            val decelerate = 1f - (1f - progress) * (1f - progress)
            rotationAngle = config.startAngle + (config.targetAngle - config.startAngle) * decelerate
            delay(16)
        }
        rotationAngle = config.targetAngle
        isSpinning = false
        resultText = config.selectedText
        hasSpun = true
        spinConfig = null
    }

    if (showSettings) {
        SettingsScreen(
            onBack = { showSettings = false },
            options = options,
            onOptionsChange = { options = it },
            isSpinning = isSpinning,
            onConfigLoaded = onConfigLoaded,
            onConfigReset = onConfigReset,
            currentConfigId = currentConfigId,
            currentConfigName = currentConfigName
        )
    } else {
        MainWheelScreen(
            onNavigateToSettings = { showSettings = true },
            options = options,
            onOptionsChange = { options = it },
            isSpinning = isSpinning,
            onIsSpinningChange = { isSpinning = it },
            resultText = resultText,
            hasSpun = hasSpun,
            onResultReset = onResultReset,
            configName = currentConfigName,
            rotationAngle = rotationAngle,
            onSpinConfigChange = { spinConfig = it }
        )
    }
}

@Composable
fun MainWheelScreen(
    onNavigateToSettings: () -> Unit,
    options: List<WheelOption>,
    onOptionsChange: (List<WheelOption>) -> Unit,
    isSpinning: Boolean,
    onIsSpinningChange: (Boolean) -> Unit,
    resultText: String?,
    hasSpun: Boolean,
    onResultReset: () -> Unit,
    configName: String?,
    rotationAngle: Float,
    onSpinConfigChange: (SpinConfig?) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val screenHeight = maxHeight
        val wheelSize = if (screenHeight * 0.55f > 320.dp) 320.dp else screenHeight * 0.55f

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = configName ?: "🍜 美食大转盘",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onNavigateToSettings,
                    enabled = !isSpinning
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = if (isSpinning) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Canvas(modifier = Modifier.size(50.dp)) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2

                            val shadowPath = Path().apply {
                                moveTo(centerX + 2f, centerY + 2f)
                                lineTo(centerX - 16.dp.toPx() + 2f, centerY - 30.dp.toPx() + 2f)
                                lineTo(centerX + 16.dp.toPx() + 2f, centerY - 30.dp.toPx() + 2f)
                                close()
                            }
                            drawPath(
                                path = shadowPath,
                                color = Color.Black.copy(alpha = 0.2f)
                            )

                            val pointerPath = Path().apply {
                                moveTo(centerX, centerY + 8.dp.toPx())
                                lineTo(centerX - 16.dp.toPx(), centerY - 30.dp.toPx())
                                lineTo(centerX + 16.dp.toPx(), centerY - 30.dp.toPx())
                                lineTo(centerX, centerY + 8.dp.toPx())
                                close()
                            }

                            drawPath(
                                path = pointerPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500)
                                    ),
                                    startY = centerY - 30.dp.toPx(),
                                    endY = centerY + 8.dp.toPx()
                                )
                            )

                            val highlightPath = Path().apply {
                                moveTo(centerX, centerY - 25.dp.toPx())
                                lineTo(centerX + 6.dp.toPx(), centerY - 8.dp.toPx())
                                lineTo(centerX - 2.dp.toPx(), centerY - 8.dp.toPx())
                                lineTo(centerX, centerY - 25.dp.toPx())
                                close()
                            }
                            drawPath(
                                path = highlightPath,
                                color = Color.White.copy(alpha = 0.4f)
                            )

                            drawPath(
                                path = pointerPath,
                                color = Color(0xFFCC8400),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(wheelSize)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FortuneWheel(
                            options = options,
                            rotation = rotationAngle,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isSpinning && options.size >= 2) {
                                onResultReset()
                                onIsSpinningChange(true)

                                val totalWeight = options.sumOf { it.weight }
                                val randomValue = Random.nextInt(totalWeight)
                                var cumulative = 0
                                var selectedIndex = 0
                                for ((i, option) in options.withIndex()) {
                                    cumulative += option.weight
                                    if (randomValue < cumulative) {
                                        selectedIndex = i
                                        break
                                    }
                                }

                                val spinDuration = 3000L + Random.nextLong(1000)
                                val startAngle = rotationAngle
                                val baseRotations = 5

                                val totalWeightFloat = totalWeight.toFloat()
                                var targetAngleForIndex = 0f
                                for (i in 0 until selectedIndex) {
                                    targetAngleForIndex += (options[i].weight / totalWeightFloat) * 360f
                                }
                                targetAngleForIndex += (options[selectedIndex].weight / totalWeightFloat / 2) * 360f

                                val targetSectorAngle = (360f - targetAngleForIndex) % 360f
                                val targetAngle = startAngle + (baseRotations * 360f) + (targetSectorAngle - (startAngle % 360f)) + 360f

                                onSpinConfigChange(
                                    SpinConfig(
                                        startAngle = startAngle,
                                        targetAngle = targetAngle,
                                        spinDuration = spinDuration,
                                        selectedText = options[selectedIndex].text
                                    )
                                )
                            }
                        },
                        enabled = !isSpinning && options.size >= 2,
                        modifier = Modifier
                            .width(180.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (options.size >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp,
                            disabledElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = if (isSpinning) "🎡 旋转中..." else "🎯 开始选择",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (hasSpun) "🎉 最终选择" else "⬆️ 点击开始选择",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText ?: "等待抽奖",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    options: List<WheelOption>,
    onOptionsChange: (List<WheelOption>) -> Unit,
    isSpinning: Boolean,
    onConfigLoaded: (String, String) -> Unit,
    onConfigReset: () -> Unit,
    currentConfigId: String?,
    currentConfigName: String?
) {
    val context = LocalContext.current

    BackHandler {
        onBack()
    }
    var inputText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf<WheelOption?>(null) }
    var showSaveConfirm by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showConfigsDialog by remember { mutableStateOf(false) }
    var configs by remember { mutableStateOf(loadConfigs(context)) }
    var configToRename by remember { mutableStateOf<WheelConfig?>(null) }
    var configToDelete by remember { mutableStateOf<WheelConfig?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var isUpdatingConfig by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "设置",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = if (isSpinning) CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            ) else CardDefaults.cardColors()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "转盘内容列表",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isSpinning) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🎡 转盘旋转中...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("添加新内容") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val newId = (options.maxOfOrNull { it.id } ?: 0) + 1
                                val colorIndex = options.size % wheelColors.size
                                onOptionsChange(
                                    options + WheelOption(
                                        id = newId,
                                        text = inputText.trim(),
                                        color = wheelColors[colorIndex],
                                        weight = 1
                                    )
                                )
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "添加",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(options, key = { it.id }) { option ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(option.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = option.text,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = "权重: ${option.weight}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { showEditDialog = option },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "编辑",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onOptionsChange(options.filter { it.id != option.id }) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "删除",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (options.size < 2) {
                    Text(
                        text = "至少需要2个选项",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { showClearConfirm = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("清空")
            }

            OutlinedButton(
                onClick = { showResetConfirm = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("重置")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (currentConfigId != null) {
                        val updatedConfig = WheelConfig(
                            id = currentConfigId,
                            name = currentConfigName ?: "自定义配置",
                            options = options
                        )
                        saveConfig(context, updatedConfig)
                        configs = loadConfigs(context)
                        showSaveConfirm = true
                        isUpdatingConfig = true
                    } else {
                        showSaveDialog = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = options.size >= 2,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Done, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (currentConfigId != null) "更新配置" else "保存配置")
            }

            OutlinedButton(
                onClick = {
                    configs = loadConfigs(context)
                    showConfigsDialog = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("已存配置 (${configs.size})")
            }
        }
    }

    if (showSaveConfirm) {
        AlertDialog(
            onDismissRequest = { showSaveConfirm = false },
            title = { Text("保存成功") },
            text = { Text("转盘内容已保存") },
            confirmButton = {
                TextButton(onClick = { showSaveConfirm = false }) {
                    Text("确定")
                }
            }
        )
    }

    if (showSaveDialog) {
        var configName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("保存配置") },
            text = {
                Column {
                    Text("为当前配置输入一个名称", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = configName,
                        onValueChange = { configName = it },
                        label = { Text("配置名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (configName.isNotBlank()) {
                            val newConfig = WheelConfig(
                                id = UUID.randomUUID().toString(),
                                name = configName.trim(),
                                options = options
                            )
                            saveConfig(context, newConfig)
                            configs = loadConfigs(context)
                            showSaveDialog = false
                            showSaveConfirm = true
                        }
                    },
                    enabled = configName.isNotBlank()
                ) {
                    Text("保存", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showConfigsDialog) {
        AlertDialog(
            onDismissRequest = { showConfigsDialog = false },
            title = { Text("已保存的配置") },
            text = {
                if (configs.isEmpty()) {
                    Text("暂无保存的配置", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(configs) { config ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionsChange(config.options)
                                        onConfigLoaded(config.name, config.id)
                                        showConfigsDialog = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = config.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${config.options.size} 个选项",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            configToRename = config
                                            showConfigsDialog = false
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "重命名",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            configToDelete = config
                                            showConfigsDialog = false
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "删除",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showConfigsDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }

    configToRename?.let { config ->
        var newName by remember { mutableStateOf(config.name) }
        AlertDialog(
            onDismissRequest = { configToRename = null },
            title = { Text("重命名配置") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("配置名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            renameConfig(context, config.id, newName.trim())
                            configs = loadConfigs(context)
                            configToRename = null
                        }
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("保存", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { configToRename = null }) {
                    Text("取消")
                }
            }
        )
    }

    configToDelete?.let { config ->
        AlertDialog(
            onDismissRequest = { configToDelete = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除配置「${config.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfig(context, config.id)
                        configs = loadConfigs(context)
                        configToDelete = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { configToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("确认清空") },
            text = { Text("确定要清空所有选项吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onOptionsChange(emptyList())
                        onConfigReset()
                        showClearConfirm = false
                    }
                ) {
                    Text("清空", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("确认重置") },
            text = { Text("确定要重置为默认选项吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onOptionsChange(getDefaultOptions())
                        onConfigReset()
                        showResetConfirm = false
                    }
                ) {
                    Text("重置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }

    showEditDialog?.let { option ->
        var editText by remember { mutableStateOf(option.text) }
        var editWeight by remember { mutableStateOf(option.weight.toString()) }
        var selectedColor by remember { mutableStateOf(option.color) }
        var showColorPicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("编辑选项") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        label = { Text("选项名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editWeight,
                        onValueChange = { editWeight = it },
                        label = { Text("权重 (1-10)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "颜色:", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(selectedColor, CircleShape)
                                .clickable { showColorPicker = true }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "点击选择颜色", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val weight = editWeight.toIntOrNull() ?: 1
                        val finalWeight = weight.coerceIn(1, 10)
                        val updatedOptions = options.map {
                            if (it.id == option.id) {
                                it.copy(text = editText.trim(), weight = finalWeight, color = selectedColor)
                            } else it
                        }
                        onOptionsChange(updatedOptions)
                        showEditDialog = null
                    },
                    enabled = editText.isNotBlank()
                ) {
                    Text("保存", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("取消")
                }
            }
        )

        if (showColorPicker) {
            AlertDialog(
                onDismissRequest = { showColorPicker = false },
                title = { Text("选择颜色") },
                text = {
                    Column {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(presetColors) { category ->
                                Column {
                                    Text(
                                        text = category.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        category.colors.forEach { color ->
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(color, CircleShape)
                                                    .border(
                                                        width = 2.dp,
                                                        color = if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                        shape = CircleShape
                                                    )
                                                    .clickable {
                                                        selectedColor = color
                                                        showColorPicker = false
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showColorPicker = false }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Composable
fun FortuneWheel(
    options: List<WheelOption>,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = modifier.rotate(rotation)) {
        val canvasSize = size.minDimension
        val center = Offset(canvasSize / 2, canvasSize / 2)
        val radius = canvasSize / 2 - 8.dp.toPx()

        val totalWeight = options.sumOf { it.weight }
        var currentAngle = -90f

        val textScale = when {
            options.size <= 4 -> 1.2f
            options.size <= 6 -> 1.0f
            options.size <= 8 -> 0.9f
            options.size <= 10 -> 0.8f
            else -> 0.7f
        }

        val baseTextSize = 22.sp.toPx() * textScale
        val labelRadiusRatio = when {
            options.size <= 6 -> 0.65f
            else -> 0.6f
        }

        options.forEach { option ->
            val angle = (option.weight.toFloat() / totalWeight.toFloat()) * 360f

            val startAngle = currentAngle
            val sweepAngle = angle

            val path = Path().apply {
                moveTo(center.x, center.y)
                arcTo(
                    rect = Rect(
                        left = center.x - radius,
                        top = center.y - radius,
                        right = center.x + radius,
                        bottom = center.y + radius
                    ),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweepAngle,
                    forceMoveTo = false
                )
                close()
            }

            drawPath(
                path = path,
                color = option.color.copy(alpha = 0.95f)
            )

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.2f),
                style = Stroke(width = 2.dp.toPx())
            )

            drawLine(
                color = Color.White.copy(alpha = 0.4f),
                start = center,
                end = Offset(
                    center.x + radius * cos(Math.toRadians(startAngle.toDouble())).toFloat(),
                    center.y + radius * sin(Math.toRadians(startAngle.toDouble())).toFloat()
                ),
                strokeWidth = 2.dp.toPx()
            )

            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = radius * labelRadiusRatio
            val labelX = center.x + cos(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius
            val labelY = center.y + sin(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius

            val paint = Paint().apply {
                color = Color.White.toArgb()
                textSize = baseTextSize
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setShadowLayer(3f, 1f, 1f, android.graphics.Color.BLACK)
            }

            val maxTextLength = when {
                options.size <= 6 -> 5
                options.size <= 10 -> 4
                else -> 3
            }
            val displayText = if (option.text.length > maxTextLength) {
                option.text.substring(0, maxTextLength) + "..."
            } else {
                option.text
            }

            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(labelAngle + 180f, labelX, labelY)
            drawContext.canvas.nativeCanvas.drawText(
                displayText,
                labelX,
                labelY + paint.textSize / 3,
                paint
            )
            drawContext.canvas.nativeCanvas.restore()

            currentAngle += sweepAngle
        }

        val centerRadius = radius * 0.28f
        drawCircle(
            color = Color.White,
            center = center,
            radius = centerRadius
        )

        val innerRadius = radius * 0.2f
        drawCircle(
            color = primaryColor,
            center = center,
            radius = innerRadius
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            center = Offset(center.x - innerRadius * 0.3f, center.y - innerRadius * 0.3f),
            radius = innerRadius * 0.35f
        )
    }
}
