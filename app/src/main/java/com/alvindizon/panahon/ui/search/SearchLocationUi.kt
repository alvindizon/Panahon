package com.alvindizon.panahon.ui.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alvindizon.panahon.core.utils.rememberFlowWithLifecycle
import com.alvindizon.panahon.design.components.LoadingScreen
import com.alvindizon.panahon.design.theme.PanahonTheme
import com.alvindizon.panahon.viewmodel.SearchLocationUiState
import com.alvindizon.panahon.viewmodel.SearchLocationViewModel


data class SearchResult(
    val locationName: String,
    val state: String?,
    val country: String,
    val lat: String,
    val lon: String
) {
    val stateCountry
        get() = if (state != null) "$state, $country" else country
}

@Composable
fun SearchScreen(
    viewModel: SearchLocationViewModel,
    onUpButtonClicked: () -> Unit
) {

    val searchQuery by viewModel.searchQuery.collectAsState()

    val context = LocalContext.current

    val state = rememberFlowWithLifecycle(viewModel.searchLocationUiState).collectAsState(
        initial = SearchLocationUiState.Empty
    ).value

    Scaffold(
        topBar = {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onUpButtonClicked = { onUpButtonClicked() },
                onClearClicked = { viewModel.clearQuery() },
                onSearchQueryChanged = { viewModel.searchForLocations(it) }
            )
        }
    ) {
        when (state) {
            SearchLocationUiState.Empty -> NoSearchResults()
            is SearchLocationUiState.Searching -> LoadingScreen()
            is SearchLocationUiState.Error -> {
                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            is SearchLocationUiState.Success -> {
                SearchResultList(
                    searchResults = state.searchResults,
                    onSearchResultClicked = {
                        viewModel.saveResultToDb(it)
                        Toast.makeText(
                            context,
                            "Location saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
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
                placeholder = { Text("Search for locations") },
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
            Text(
                text = searchResult.stateCountry,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SearchResultList(
    searchResults: List<SearchResult>,
    onSearchResultClicked: (SearchResult) -> Unit
) {
    Column {
        LazyColumn(contentPadding = PaddingValues(top = 8.dp)) {
            items(searchResults) { searchResult ->
                SearchResultItem(searchResult) { onSearchResultClicked(it) }
            }
        }
    }
}

@Composable
fun NoSearchResults() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No matches found")
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
                country = "Japan",
                lat = "0",
                lon = ""
            )
        ) {}
    }
}
