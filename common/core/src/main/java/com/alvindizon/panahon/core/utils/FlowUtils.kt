package com.alvindizon.panahon.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

// https://github.com/chrisbanes/tivi/blob/8d15aab2a14a37354cf2231f816c481cf6326f73/common-ui-compose/src/main/java/app/tivi/common/compose/FlowWithLifecycle.kt
@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}


/**
 * Returns a [StateFlow] that access data associated with the given key.
 *
 * @param scope The scope used to synchronize the [StateFlow] and [SavedStateHandle]
 * @param key The identifier for the value
 * @param initialValue If no value exists with the given [key], a new one is created
 *  with the given [initialValue].
 *
 * @see SavedStateHandle.getLiveData
 */
fun <T> SavedStateHandle.getStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> {
    val liveData = getLiveData(key, initialValue)
    val stateFlow = MutableStateFlow(initialValue)

    // Synchronize the LiveData with the StateFlow
    val observer = Observer<T> { value ->
        if (stateFlow.value != value) {
            stateFlow.value = value
        }
    }
    liveData.observeForever(observer)

    stateFlow.onCompletion {
        // Stop observing the LiveData if the StateFlow completes
        withContext(Dispatchers.Main.immediate) {
            liveData.removeObserver(observer)
        }
    }.onEach { value ->
        // Synchronize the StateFlow with the LiveData
        withContext(Dispatchers.Main.immediate) {
            if (liveData.value != value) {
                liveData.value = value
            }
        }
    }.launchIn(scope)

    return stateFlow
}
