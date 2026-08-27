# Data Layer Architectural Guidelines

### 🏛️ Clean Architecture Layer Constraints: Data Layer
*   **Layer:** Framework & Infrastructure.
*   **Responsibility:** Network clients, local databases, data models (DTOs), mappers, and repository implementations.
*   **Rules:**
    *   Implements the domain repository interfaces.
    *   Retrofit API definitions and DTO data classes belong here.
    *   Must use explicit extension functions to convert Data DTOs into Domain Models before passing them up. Never leak Retrofit DTOs to the Domain or Presentation layer.
    *   Catch network exceptions here and wrap them in a `kotlin.Result` instance.

---

## 💻 Code Templates & Style Guide

### 1. Retrofit API Client Template
```kotlin
interface ProductApiService {
    @GET("v1/products")
    suspend fun getProducts(): List<ProductDto>
}
```
