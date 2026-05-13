package ru.mobileup.template.core.theme.values

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.mobileup.template.core.theme.custom.BodyTypography
import ru.mobileup.template.core.theme.custom.ButtonTypography
import ru.mobileup.template.core.theme.custom.CaptionTypography
import ru.mobileup.template.core.theme.custom.CustomTypography
import ru.mobileup.template.core.theme.custom.TitleTypography

val AppTypography = CustomTypography(
    title = TitleTypography(
        regular = TextStyle(
            fontSize = 24.sp
        )
    ),
    body = BodyTypography(
        regular = TextStyle(
            fontSize = 16.sp
        )
    ),
    caption = CaptionTypography(
        regular = TextStyle(
            fontSize = 12.sp
        )
    ),
    button = ButtonTypography(
        regular = TextStyle(
            fontSize = 16.sp
        ),
        bold = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    )
)