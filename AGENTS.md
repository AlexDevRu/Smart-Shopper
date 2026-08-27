# AI Agent Rules and Architectural Guidelines

You are an expert Android AI Developer Agent. Your task is to generate code, refactor, and build features strictly adhering to the architectural guidelines, tech stack, and conventions defined below. 

Do not deviate from this stack or implement patterns from competing paradigms (e.g., MVVM, custom service locators, XML layouts, or multi-platform libraries).

---

## 🛠️ Core Tech Stack
*   **Language:** Kotlin (100% type-safe, asynchronous-first)
*   **UI Framework:** Jetpack Compose (Declarative UI)
*   **Architecture Pattern:** Clean Architecture + MVI (Model-View-Intent)
*   **Dependency Injection:** Dagger-Hilt
*   **Networking:** Retrofit + OkHttp
*   **Asynchronous Processing:** Kotlin Coroutines & Flow (StateFlow, SharedFlow)

---

## 🏛️ Clean Architecture Layer Constraints

The project is strictly separated into three layers. Dependencies must only flow inward: **Presentation ➔ Domain ➔ Data**.

[ Presentation Layer ] (Compose, MVI ViewModels)│
▼
[ Domain Layer ]  (Use Cases, Domain Models, Repository Interfaces)
▲│[ Data Layer ]    
(Retrofit APIs, Mappers, Repository Implementations)

> [!NOTE]
> For detailed rules per layer, refer to the module-specific `AGENTS.md` files:
> - [Domain Layer Guidelines](file:///D:/AndroidProjects/Secondnumber/domain/AGENTS.md)
> - [Data Layer Guidelines](file:///D:/AndroidProjects/Secondnumber/data/AGENTS.md)
> - [Presentation Layer Guidelines](file:///D:/AndroidProjects/Secondnumber/app/AGENTS.md)

---

## 💉 Dependency Injection (Hilt) Rules

*   Use `@Inject constructor` for injecting dependencies into Use Cases, Repositories, and ViewModels.
*   Put Retrofit client configurations and Repository bindings inside Hilt `NetworkModule` and `RepositoryModule` files.
*   Use `@Binds` for interface-to-implementation bindings to optimize code generation performance.
*   Use `@Provides` only for configuring third-party instances like Retrofit, OkHttp, or Room instances.
*   Scope components accurately: Use `@Singleton` for network/app-wide utilities and `@ViewModelScoped` for repository implementations if they cache state per screen context.

---

## 🚫 Absolute Red Flags (Never Do This)
1.  **Do not** reference `compose` runtime packages or UI elements inside ViewModels or Use Cases.
2.  **Do not** manage UI state using multiple separate `MutableStateFlow` streams. Use exactly one aggregated `UiState` data class or sealed interface per screen.
3.  **Do not** instantiate network clients inside layers manually. Use **Hilt** to provision your single source of truth instances.
4.  **Do not** bypass the Domain layer. ViewModels must never invoke a Data layer Repository directly.
5.  **Do not** hardcode user-facing strings inside Composables. Always use `stringResource`.
6.  **Do not** use `Spacer` for fixed spacing when `Arrangement.spacedBy` is applicable.
