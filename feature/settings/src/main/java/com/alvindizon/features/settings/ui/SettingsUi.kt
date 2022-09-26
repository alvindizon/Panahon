package com.alvindizon.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.core.units.Temperature
import com.alvindizon.panahon.design.components.CustomSegmentedControl
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.features.settings.R

@Composable
fun SettingsScreen(
    onUpButtonClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.alvindizon.panahon.design.R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onUpButtonClicked() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.back)
                        )
                    }
                },
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                text = stringResource(id = R.string.unit_header)
            )
            MeasurementUnitsCard()
        }
    }
}

@Composable
fun MeasurementUnitsCard() {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.temperature_label)
            )
            CustomSegmentedControl(
                modifier = Modifier.weight(1f),
                items = Temperature.values().map { it.sign },
            )
        }
    }
}

@Preview
@Composable
fun SettingsUiPreview() {
    PanahonTheme {
        SettingsScreen {

        }
    }
}

@Preview
@Composable
fun MeasurementUnitsCardPreview() {
    PanahonTheme {
        MeasurementUnitsCard()
    }
}