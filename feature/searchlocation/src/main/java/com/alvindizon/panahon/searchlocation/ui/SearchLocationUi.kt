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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.design.utils.toFlagEmoji
import com.alvindizon.panahon.searchlocation.R
import com.alvindizon.panahon.searchlocation.model.SearchResult
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationUiState
import com.alvindizon.panahon.searchlocation.viewmodel.SearchLocationViewModel


@Composable
fun SearchScreen(
    viewModel: SearchLocationViewModel,
    onUpButtonClicked: () -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    state.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message.message)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onUpButtonClicked = onUpButtonClicked,
                onClearClicked = viewModel::clearQuery,
                onSearchQueryChanged = viewModel::searchForLocations
            )
        }
    ) { paddingValues ->
        SearchScreen(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onSearchResultClicked = {
                viewModel.saveResultToDb(it)
                onSearchResultClicked(it)
            }
        )
    }
}

@Composable
internal fun SearchScreen(
    modifier: Modifier = Modifier,
    state: SearchLocationUiState,
    onSearchResultClicked: (SearchResult) -> Unit
) {
    when {
        state.isLoading -> LoadingScreen(modifier)
        state.searchResults.isNullOrEmpty() -> NoSearchResults(modifier)
        else -> SearchResultList(
            searchResults = state.searchResults,
            onSearchResultClicked = onSearchResultClicked
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
                                contentDescription = null
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
    searchResults: List<SearchResult>,
    onSearchResultClicked: (SearchResult) -> Unit
) {
    Column(modifier) {
        LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
            items(searchResults) { searchResult ->
                SearchResultItem(searchResult) { onSearchResultClicked(it) }
            }
        }
    }
}

@Composable
fun NoSearchResults(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.search_no_matches_msg))
    }
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
