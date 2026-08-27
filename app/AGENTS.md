# Presentation Layer & UI Architectural Guidelines

### 🏛️ Clean Architecture Layer Constraints: Presentation Layer
*   **Responsibility:** Composable screens, UI state definitions, and ViewModels.
*   **Rules:**
    *   Follows a strict **Unidirectional Data Flow (UDF)** using the MVI pattern.
    *   No business logic allowed in Composables or ViewModels. ViewModels delegate to Use Cases.

---

## 🔄 MVI (Model-View-Intent) Strict Contract

Each feature must declare its MVI contract inside a single file or a structured package using explicit Kotlin `sealed interface` structures.

### Contract Blueprint
```kotlin
// Example: ProductContract.kt

// 1. Immutable UI State representing exactly what the screen displays
data class ProductUiState(
    val products: List<Product>
)

// 2. User Actions / Intents originating from the UI
sealed interface ProductUiIntent {
    data object LoadProducts : ProductUiIntent
    data class OnProductClicked(val productId: String) : ProductUiIntent
    data object Refresh : ProductUiIntent
}

// 3. One-time Side Effects (Navigation, Toasts, Dialogs)
sealed interface ProductUiEffect {
    data class NavigateToDetails(val productId: String) : ProductUiEffect
    data class ShowToast(val message: String) : ProductUiEffect
}
```

### ViewModel Rules
*   Must extend `MviViewModel` from base package.
*   Must be annotated with `@HiltViewModel`.

---

## 🎨 Jetpack Compose Rules

When generating or refactoring UI components, strictly follow these structural layout rules:

*   **Feature-Based Package Structure (Mandatory):**
    *   Each screen MUST reside in its own dedicated package.
    *   The package name must reflect the feature/screen (e.g., `auth_confirmation`, `login`, `product_details`).

    **Required structure:**
    ```
       ui/
        └── <feature_name>/
            ├── <Feature>Screen.kt
            ├── <Feature>ViewModel.kt
            └── components/
                ├── <ReusableSubComposable>.kt
                ├── <AnotherComponent>.kt
    ```

    *   **Screen-level composables:**
        *   The main screen (`XScreen` and `XContent`) MUST remain in the root of the feature package.

    *   **Sub-composables:**
        *   All reusable or extracted UI parts MUST be placed inside the `components/` sub-package.
        *   Each component should be in its own file if it has non-trivial logic or UI.

    *   **Do NOT:**
        *   Do not place multiple screens in the same package.
        *   Do not mix components from different screens in a shared package unless they are truly global reusable UI components.
        *   Do not keep large private composables inside the screen file if they can be extracted into `components/`.

    *   **Exception:**
        *   Very small, tightly coupled composables (1–10 lines) MAY remain private inside the screen file.
*   **Global Reusable Components (Cross-Feature):**
    *   UI components reused across multiple features (including their UI models and any feature-specific UI logic) MUST be placed in a `ui/base/common` package.
    *   If the component is complex, create a dedicated sub-package inside `ui/base/common` (e.g., `ui/base/common/button`, `ui/base/common/dialog`, `ui/base/common/listitem`).
    *   These components must remain independent from any specific feature package.
    *   They may include their own UI state models and internal logic, but MUST NOT depend on feature-specific ViewModels or contracts.
    *   Use this rule only when the component is truly shared across multiple features — avoid premature abstraction.
*   **Mandatory Previews:** Every screen file must include a valid `@Preview` function to visualize the UI inside Android Studio without launching an emulator.
*   **Dual-Function Architecture (`XScreen` & `XContent`):** To make the code testable and rendering-friendly for Previews, split your screen structure into two exact composables:
    1.  **`XScreen` (Stateful Wrapper):** Handles Hilt ViewModel injection, collects `UiState`, intercepts `UiEffect` streams, and passes stateless data/lambdas down.
    2.  **`XContent` (Stateless Presenter - PRIVATE):** Takes the raw UI state and event callbacks as direct parameters. This function contains zero business logic or ViewModel references, allowing it to render successfully in the Preview container.
*   **Strict UI-Layer Models (No Domain Models):** UI components (especially collections like lists, grids, or complex items) must never consume domain or data layer models directly. Always create dedicated, lightweight UI state models (e.g., `UserItemUiState`) optimized for rendering, and map domain models to these UI models before exposing them to the Compose layer.
*   **Private Previews:** All `@Preview` composable functions must explicitly use the `private` modifier to keep the compilation package scope clean.

---

## 💻 Code Templates & Style Guide

### 1. ViewModel Pattern
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : MviViewModel<LoginUiState, LoginIntent, LoginEffect>(LoginUiState()) {

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> {
                val isError = intent.email.isNotEmpty() && !intent.email.isEmailValid()
                updateState { it.copy(email = intent.email, isEmailError = isError) }
            }
            is LoginIntent.LoginClicked -> {
                performLogin()
            }
        }
    }

    private fun performLogin() {
        val email = uiState.value.email
        if (!email.isEmailValid()) {
            updateState { it.copy(isEmailError = true) }
            return
        }
        
        updateState { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            loginUseCase(email)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.ShowError(error.message.orEmpty()))
                }
        }
    }
}
```

### 2. Jetpack Compose Screen Template
```kotlin
@Composable
fun AuthConfirmationScreen(
    viewModel: AuthConfirmationViewModel,
    onBackClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthConfirmationEffect.NavigateToHome -> onNavigateToHome()
                is AuthConfirmationEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    AuthConfirmationScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onCodeChanged = { viewModel.sendIntent(AuthConfirmationIntent.CodeChanged(it)) },
        onConfirmClick = { viewModel.sendIntent(AuthConfirmationIntent.ConfirmClicked) }
    )
}

@Composable
private fun AuthConfirmationScreenContent(
    state: AuthConfirmationUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auth_confirm_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            OutlinedTextField(
                value = state.code,
                onValueChange = onCodeChanged,
                label = { Text(stringResource(R.string.auth_confirm_code_label)) },
                isError = state.isError,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onConfirmClick,
                enabled = state.code.length == 4 && !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.auth_confirm_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthConfirmationScreenPreview() {
    AuthConfirmationScreenContent(
        state = AuthConfirmationUiState(code = "12", isLoading = false),
        onBackClick = {},
        onCodeChanged = {},
        onConfirmClick = {}
    )
}
```
