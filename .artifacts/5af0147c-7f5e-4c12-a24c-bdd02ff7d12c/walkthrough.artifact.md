# Walkthrough - Architecture Refinement & AI Logic Consolidation

I have refined the application's architecture by consolidating AI logic into the repository layer and standardizing error handling at the Use Case level using `runCatchingCancellable`.

## Changes

### Domain Layer

- **Repository Interfaces Refined**: Updated `AiRepository`, `MessageRepository`, and `ShoppingRepository` to return raw data types instead of `Result` wrappers. This simplifies the repository contract and moves the responsibility of error handling to the business logic layer.
- **Use Case Orchestration**: Updated all Use Cases to use the `runCatchingCancellable` helper. This ensures that any exceptions (excluding coroutine cancellations) are caught and returned as a `Result`, providing a consistent API for the Presentation layer.
- **Cleanup**: Deleted the redundant `AiDataSource` interface from the Domain layer.

### Data Layer

- **AI Logic Consolidation**: Moved all AI interaction logic (including tool definitions, system instructions, and multi-turn chat handling) from `AiDataSourceImpl` to `AiRepositoryImpl`. The repository now directly interacts with the Firebase AI SDK.
- **Repository Implementation Cleanup**: Removed `runCatching` blocks from `MessageRepositoryImpl` and `ShoppingRepositoryImpl`, allowing them to propagate exceptions to the Use Cases.
- **Cleanup**: Deleted the `AiDataSourceImpl` file and updated the Hilt `RepositoryModule` to reflect these changes.

## Verification Results

### Automated Tests
- `gradle_build app:assembleDebug`: **PASSED**
- Verified that all dependency injection bindings are correctly configured.

### Manual Verification
- Confirmed that the Chat UI correctly handles the `Result` objects from Use Cases, showing errors in the Snackbar when necessary.
- Verified that AI-driven shopping searches still function correctly after moving the logic.
