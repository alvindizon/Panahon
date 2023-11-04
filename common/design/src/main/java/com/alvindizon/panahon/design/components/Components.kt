package com.alvindizon.panahon.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.alvindizon.panahon.design.R
import com.alvindizon.panahon.design.theme.PanahonTheme

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DataUnavailableScreen(modifier: Modifier = Modifier, messageResId: Int? = null) {
    Text(
        text = stringResource(id = messageResId ?: R.string.generic_try_again_msg),
        modifier = modifier.wrapContentSize(),
        textAlign = TextAlign.Center
    )
}

// ref: https://stackoverflow.com/questions/67023923/materialbuttontogglegroup-in-jetpack-compose
@Composable
fun CustomSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    initialSelectedIndex: Int,
    onItemClick: (String) -> Unit
) {
    // pre-select item based on datastore by recalculating the lambda on initialSelectedIndex change
    var selectedIndex by remember(initialSelectedIndex) { mutableStateOf(initialSelectedIndex) }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                contentPadding = PaddingValues(0.dp),
                modifier = when (index) {
                    0 -> Modifier.offset(0.dp, 0.dp)
                    else -> Modifier.offset((-1 * index).dp, 0.dp)
                }.zIndex(if (selectedIndex == index) 1f else 0f),
                onClick = {
                    selectedIndex = index
                    onItemClick(item)
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

@Composable
fun NoDataScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DataUnavailableScreen(messageResId = R.string.generic_error_msg)
    }
}


@Composable
fun LocationRationaleScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    showSnackbar: Boolean,
    showLocationUnavailableMsg: Boolean,
    setShowSnackbar: (Boolean) -> Unit,
    onEnableLocationButtonClick: () -> Unit,
    onSnackbarButtonClick: () -> Unit,
    onSearchLinkClick: () -> Unit,
) {
    val snackbarMsg = stringResource(R.string.home_snackbar_msg)
    val snackbarBtnMsg = stringResource(R.string.home_snackbar_btn_txt)
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        val message = stringResource(R.string.home_location_rationale_msg2)
        val withLink = stringResource(id = R.string.search)
        val startIndex = message.indexOf(withLink)
        val endIndex = startIndex + withLink.length
        append(message)
        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colors.secondary
            ),
            start = startIndex,
            end = endIndex
        )
        addStringAnnotation("", "", startIndex, endIndex)
    }
    if (showSnackbar) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                message = snackbarMsg,
                actionLabel = snackbarBtnMsg
            )
            when (result) {
                SnackbarResult.Dismissed -> setShowSnackbar(false)
                SnackbarResult.ActionPerformed -> {
                    setShowSnackbar(false)
                    onSnackbarButtonClick()
                }
            }
        }
    }
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val message = if (showLocationUnavailableMsg) {
            stringResource(id = com.alvindizon.panahon.design.R.string.location_not_available_msg)
        } else {
            stringResource(id = R.string.home_location_rationale_msg)
        }
        Text(
            textAlign = TextAlign.Center,
            text = message,
            style = MaterialTheme.typography.subtitle2,
        )
        Spacer(modifier = Modifier.height(8.dp))
        ClickableText(
            text = annotatedLinkString,
            onClick = { onSearchLinkClick() },
            style = MaterialTheme.typography.subtitle2.merge(
                TextStyle(textAlign = TextAlign.Center)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onEnableLocationButtonClick() }) {
            Text(
                text = stringResource(id = R.string.home_share_location_btn),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

@Preview
@Composable
private fun PermissionNotGrantedScreenPreview() {
    val scaffoldState = rememberScaffoldState()
    val (showSnackbar, setShowSnackbar) = remember { mutableStateOf(false) }
    PanahonTheme {
        LocationRationaleScreen(
            scaffoldState = scaffoldState,
            showSnackbar = showSnackbar,
            setShowSnackbar = setShowSnackbar,
            showLocationUnavailableMsg = false,
            onEnableLocationButtonClick = {},
            onSnackbarButtonClick = {},
            onSearchLinkClick = {}
        )
    }
}

@Composable
fun ErrorAlertDialog(errorMessage: String, onOkClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onOkClick,
        confirmButton = {
            TextButton(onClick = onOkClick)
            { Text(text = "OK") }
        },
        title = { Text(text = "Error") },
        text = { Text(text = errorMessage) }
    )
}