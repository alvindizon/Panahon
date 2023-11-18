package com.alvindizon.panahon.searchlocation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.data.location.model.CurrentLocation
import com.alvindizon.panahon.data.location.model.SearchResult
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.design.utils.toFlagEmoji
import com.alvindizon.panahon.searchlocation.R
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationUiState
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationViewModel


@Composable
fun SearchScreen(
    viewModel: SearchLocationViewModel,
    onUpButtonClicked: () -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit,
    onLocationFound: (CurrentLocation) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }

    state.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message.message)
        }
    }

    showAlertDialog = !state.locationSettingsNotEnabled && !state.locationNotFound

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onUpButtonClicked = onUpButtonClicked,
                onClearClicked = viewModel::clearQuery,
                onSearchQueryChanged = viewModel::onSearchQueryChanged
            )
        }
    ) { paddingValues ->
        SearchScreen(
            modifier = Modifier.padding(paddingValues),
            state = state,
            showAlertDialog = showAlertDialog,
            onSearchResultClicked = {
                viewModel.onSearchResultClick(it)
                onSearchResultClicked(it)
            },
            onFetchLocationClick = {
                // this is necessary so that subsequent clicks would still trigger display of dialog if
                // precise location is still not enabled
                showAlertDialog = !showAlertDialog
                viewModel.onFetchLocationClick()
            },
            onLocationFound = { onLocationFound(it) },
            onDismiss = { showAlertDialog = !showAlertDialog }
        )
    }
}

@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchLocationUiState,
    showAlertDialog: Boolean,
    onSearchResultClicked: (SearchResult) -> Unit,
    onFetchLocationClick: () -> Unit,
    onLocationFound: (CurrentLocation) -> Unit,
    onDismiss: () -> Unit
) {
    if (showAlertDialog) {
        val dialogMessageAndTitle = when {
            state.locationNotFound -> {
                Pair(
                    stringResource(id = com.alvindizon.panahon.design.R.string.location_not_available_msg),
                    stringResource(id = R.string.search_current_location_not_found_title)
                )
            }
            state.locationSettingsNotEnabled -> {
                Pair(
                    stringResource(id = R.string.search_open_settings),
                    stringResource(id = R.string.search_location_not_enabled_title)
                )
            }
            else -> null
        }
        if (dialogMessageAndTitle != null) {
            LocationDialog(
                title = dialogMessageAndTitle.second,
                message = dialogMessageAndTitle.first,
                onDismiss = onDismiss
            )
        }
    }
    when {
        state.isLoading -> LoadingScreen(modifier)
        state.currentLocation != null -> onLocationFound(state.currentLocation)
        else -> SearchResultList(
            searchResults = state.searchResults,
            onSearchResultClicked = onSearchResultClicked,
            onFetchLocationClick = onFetchLocationClick
        )
    }
}

@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onUpButtonClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = { onUpButtonClicked() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = com.alvindizon.panahon.design.R.string.back)
                )
            }
        },
        actions = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        showClearButton = focusState.isFocused
                    }
                    .focusRequester(focusRequester),
                value = searchQuery,
                onValueChange = { onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_bar_msg)
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = showClearButton,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { onClearClicked() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White // have to set Color.White manually for some reason,
                            )
                        }
                    }
                },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )
        }
    )
}

@Composable
fun SearchResultItem(searchResult: SearchResult, onResultClick: (SearchResult) -> Unit) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .clickable { onResultClick(searchResult) }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = searchResult.locationName,
                style = MaterialTheme.typography.h5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = searchResult.stateCountry,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = searchResult.country.toFlagEmoji(),
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SearchResultList(
    modifier: Modifier = Modifier,
    searchResults: List<SearchResult>?,
    onSearchResultClicked: (SearchResult) -> Unit,
    onFetchLocationClick: () -> Unit
) {
    if (searchResults.isNullOrEmpty()) {
        NoSearchResults(modifier = modifier, onFetchLocationClick = onFetchLocationClick)
    } else {
        Column(modifier) {
            LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
                items(searchResults) { searchResult ->
                    SearchResultItem(searchResult) { onSearchResultClicked(it) }
                }
            }
        }
    }

}

@Composable
fun NoSearchResults(
    modifier: Modifier = Modifier,
    onFetchLocationClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val annotatedLinkString: AnnotatedString = buildAnnotatedString {
            val message = stringResource(R.string.search_no_matches_msg)
            val withLink = stringResource(id = R.string.here)
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
        ClickableText(
            text = annotatedLinkString,
            onClick = { onFetchLocationClick() },
            style = MaterialTheme.typography.subtitle2.merge(
                TextStyle(textAlign = TextAlign.Center)
            )
        )
    }
}

@Composable
fun LocationDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = com.alvindizon.panahon.design.R.string.ok))
            }
        }
    )
}

@Preview
@Composable
private fun SearchResultItemPreview() {
    PanahonTheme {
        SearchResultItem(
            SearchResult(
                locationName = "Tokyo",
                state = null,
                country = "JP",
                lat = "0",
                lon = ""
            )
        ) {}
    }
}
