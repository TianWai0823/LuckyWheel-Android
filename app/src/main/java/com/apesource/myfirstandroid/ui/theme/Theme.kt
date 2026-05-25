package com.apesource.myfirstandroid.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 美食主题配色方案 - 禁用动态颜色以保持一致性
private val LightColorScheme = lightColorScheme(
    primary = Amber400,              // 琥珀色主色
    onPrimary = Color.White,
    primaryContainer = Amber100,      // 浅琥珀容器
    onPrimaryContainer = WarmGray800,

    secondary = Orange600,            // 橙色次要色
    onSecondary = Color.White,
    secondaryContainer = Amber50,
    onSecondaryContainer = Orange800,

    tertiary = Coral500,              // 珊瑚色强调
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBD0),
    onTertiaryContainer = Color(0xFF3A0B00),

    error = TomatoRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = WarmGray50,           // 暖白背景
    onBackground = WarmGray800,

    surface = Color.White,            // 白色表面
    onSurface = WarmGray800,
    surfaceVariant = WarmGray100,     // 暖灰变体
    onSurfaceVariant = WarmGray600,

    outline = WarmGray200,            // 边框
    outlineVariant = Color(0xFFCAC4D0),

    inverseSurface = WarmGray800,
    inverseOnSurface = Color(0xFFF5F0EC),
    inversePrimary = Amber300
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAmber,               // 深色琥珀
    onPrimary = Color(0xFF3F2E00),
    primaryContainer = Orange700,
    onPrimaryContainer = Amber100,

    secondary = DarkOrange,            // 深色橙
    onSecondary = Color(0xFF3F2E00),
    secondaryContainer = Color(0xFF5C4000),
    onSecondaryContainer = Color(0xFFFFE082),

    tertiary = Coral500,               // 珊瑚色
    onTertiary = Color(0xFF5F1500),
    tertiaryContainer = Color(0xFF862100),
    onTertiaryContainer = Color(0xFFFFDBD0),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = DarkWarmGray,         // 深暖灰背景
    onBackground = Color(0xFFECECEC),

    surface = DarkSurface,             // 深色表面
    onSurface = Color(0xFFECECEC),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    inverseSurface = Color(0xFFECECEC),
    inverseOnSurface = WarmGray800,
    inversePrimary = Amber400
)

@Composable
fun MyFirstAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 禁用动态颜色，保持美食主题一致性
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
