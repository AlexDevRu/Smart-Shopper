# Domain Layer Architectural Guidelines

### 🏛️ Clean Architecture Layer Constraints: Domain Layer
*   **Layer:** Pure Kotlin/Java - No Android Dependencies.
*   **Responsibility:** Contains core business logic, Use Cases, business models, and Repository abstractions.
*   **Rules:** 
    *   Must **NEVER** import `android.*`, Hilt Android annotations, or framework libraries.
    *   Every Use Case must do **one specific thing** and expose a single public `operator fun invoke`.
    *   Repositories must be defined as `interface` contracts here.
