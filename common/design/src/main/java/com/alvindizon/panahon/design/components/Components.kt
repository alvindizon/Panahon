package com.alvindizon.panahon.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

// ref: https://stackoverflow.com/questions/67023923/materialbuttontogglegroup-in-jetpack-compose
@Composable
fun CustomSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    initialSelectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    // pre-select item based on datastore by recalculating the lambda on initialSelectedIndex change
    var selectedIndex by remember(initialSelectedIndex) { mutableStateOf(initialSelectedIndex) }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                modifier = when (index) {
                    0 -> Modifier.offset(0.dp, 0.dp)
                    else -> Modifier.offset((-1 * index).dp, 0.dp)
                }.zIndex(if (selectedIndex == index) 1f else 0f),
                onClick = {
                    selectedIndex = index
                    onItemClick(index)
                },
                shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                    items.lastIndex -> RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    else -> MaterialTheme.shapes.small
                },
                border = BorderStroke(
                    1.dp, if (selectedIndex == index) {
                        MaterialTheme.colors.primary
                    } else {
                        Color.DarkGray.copy(alpha = 0.75f)
                    }
                ),
                // apply different color depending on selection
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors(
                        backgroundColor = MaterialTheme.colors.primary.copy(
                            alpha = 0.1f
                        ), contentColor = MaterialTheme.colors.primary
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.primary
                    )
                },
            ) {
                Text(
                    text = item,
                    color = if (selectedIndex == index) {
                        MaterialTheme.colors.primary
                    } else {
                        Color.DarkGray.copy(alpha = 0.9f)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }

}
