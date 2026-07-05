package com.gms.cheerlotandroid.presentation.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object MainTabIcons {
    val Lineup: ImageVector = ImageVector.Builder(
        name = "Lineup",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(8f, 6f)
            horizontalLineTo(21f)
            moveTo(8f, 12f)
            horizontalLineTo(21f)
            moveTo(8f, 18f)
            horizontalLineTo(21f)
            moveTo(3f, 6f)
            horizontalLineTo(3.01f)
            moveTo(3f, 12f)
            horizontalLineTo(3.01f)
            moveTo(3f, 18f)
            horizontalLineTo(3.01f)
        }
    }.build()

    val TeamMembers: ImageVector = ImageVector.Builder(
        name = "TeamMembers",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(16f, 21f)
            verticalLineTo(19f)
            curveTo(16f, 17.9f, 15.1f, 17f, 14f, 17f)
            horizontalLineTo(6f)
            curveTo(4.9f, 17f, 4f, 17.9f, 4f, 19f)
            verticalLineTo(21f)
            moveTo(10f, 13f)
            curveTo(12.2f, 13f, 14f, 11.2f, 14f, 9f)
            curveTo(14f, 6.8f, 12.2f, 5f, 10f, 5f)
            curveTo(7.8f, 5f, 6f, 6.8f, 6f, 9f)
            curveTo(6f, 11.2f, 7.8f, 13f, 10f, 13f)
            moveTo(22f, 21f)
            verticalLineTo(19f)
            curveTo(22f, 18.1f, 21.4f, 17.3f, 20.5f, 17.1f)
            moveTo(17f, 5.1f)
            curveTo(18.7f, 5.5f, 20f, 7.1f, 20f, 9f)
            curveTo(20f, 10.9f, 18.7f, 12.5f, 17f, 12.9f)
        }
    }.build()

    val Search: ImageVector = ImageVector.Builder(
        name = "Search",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(11f, 19f)
            curveTo(15.4f, 19f, 19f, 15.4f, 19f, 11f)
            curveTo(19f, 6.6f, 15.4f, 3f, 11f, 3f)
            curveTo(6.6f, 3f, 3f, 6.6f, 3f, 11f)
            curveTo(3f, 15.4f, 6.6f, 19f, 11f, 19f)
            moveTo(21f, 21f)
            lineTo(16.65f, 16.65f)
        }
    }.build()
}
