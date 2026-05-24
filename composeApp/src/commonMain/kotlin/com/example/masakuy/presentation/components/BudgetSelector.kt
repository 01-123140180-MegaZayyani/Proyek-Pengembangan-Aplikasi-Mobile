package com.example.masakuy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.theme.OrangeMain

data class BudgetOption(val label: String, val value: Int)

@Composable
fun BudgetSelector(
    options: List<BudgetOption>,
    selectedBudget: Int?,
    onBudgetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            BudgetChip(
                label = option.label,
                isSelected = selectedBudget == option.value,
                onClick = { onBudgetSelected(option.value) }
            )
        }
    }
}

@Composable
fun BudgetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) OrangeMain else Color.Transparent
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) OrangeMain else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = textColor
    )
}