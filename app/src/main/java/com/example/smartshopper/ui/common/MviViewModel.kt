package com.example.smartshopper.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI architecture.
 *
 * @param State Immutable UI State representing exactly what the screen displays.
 * @param Intent User Actions / Intents originating from the UI.
 * @param Effect One-time Side Effects (Navigation, Toasts, Dialogs).
 */
abstract class MviViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Handles the user [Intent] sent from the UI.
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * Sends a user [Intent] to be handled by the ViewModel.
     */
    fun sendIntent(intent: Intent) {
        handleIntent(intent)
    }

    /**
     * Updates the current UI State.
     */
    protected fun updateState(reducer: (State) -> State) {
        _uiState.update(reducer)
    }

    /**
     * Sends a one-time side effect to the UI.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
