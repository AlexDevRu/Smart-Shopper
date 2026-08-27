# Implementation Plan - Retrieve language and country from system settings

Integrate system-level `language` and `country` into the product search flow by implementing a `SystemSettingsProvider` and updating the `ProductApiService` and DataSources.

## User Review Required

> [!IMPORTANT]
> **System Locale Integration**: Instead of a local database, I will use `java.util.Locale` to retrieve the device's current system language and country code.
>
> **API Query Parameters**: The `hl` (language) and `gl` (country) parameters will be automatically appended to the OpenWebNinja shopping search request based on the device's active locale.

## Proposed Changes

### [domain] Layer

#### [NEW] [SystemSettingsProvider.kt](file:///D:/AndroidProjects/SmartShopper/domain/src/main/java/com/example/domain/repository/SystemSettingsProvider.kt)
- Define interface to retrieve device-specific information.
- Methods:
    - `fun getLanguageCode(): String` (e.g., "en")
    - `fun getCountryCode(): String` (e.g., "us")

#### [MODIFY] [OpenWebNinjaDataSource.kt](file:///D:/AndroidProjects/SmartShopper/domain/src/main/java/com/example/domain/repository/OpenWebNinjaDataSource.kt)
- Update `searchProducts` signature to accept `language: String?` and `country: String?`.

---

### [data] Layer

#### [NEW] [SystemSettingsProviderImpl.kt](file:///D:/AndroidProjects/SmartShopper/data/src/main/java/com/example/data/repository/SystemSettingsProviderImpl.kt)
- Implement `SystemSettingsProvider` using `java.util.Locale.getDefault()`.

#### [MODIFY] [ProductApiService.kt](file:///D:/AndroidProjects/SmartShopper/data/src/main/java/com/example/data/remote/ProductApiService.kt)
- Update `searchProducts` to include `@Query("hl") language: String?` and `@Query("gl") country: String?`.

#### [MODIFY] [OpenWebNinjaDataSourceImpl.kt](file:///D:/AndroidProjects/SmartShopper/data/src/main/java/com/example/data/repository/OpenWebNinjaDataSourceImpl.kt)
- Update implementation to pass `language` and `country` to the `apiService`.

#### [MODIFY] [ChatRepositoryImpl.kt](file:///D:/AndroidProjects/SmartShopper/data/src/main/java/com/example/data/repository/ChatRepositoryImpl.kt)
- Inject `SystemSettingsProvider`.
- Inside `sendMessage`, call `systemSettingsProvider.getLanguageCode()` and `getCountryCode()` before invoking `shoppingDataSource.searchProducts`.

#### [MODIFY] [RepositoryModule.kt](file:///D:/AndroidProjects/SmartShopper/data/src/main/java/com/example/data/di/RepositoryModule.kt)
- Bind `SystemSettingsProvider` to `SystemSettingsProviderImpl`.

## Verification Plan

### Automated Tests
- `gradle_build app:assembleDebug` to verify compilation.

### Manual Verification
- Check Retrofit logs (via `HttpLoggingInterceptor`) to ensure `hl` and `gl` parameters correctly reflect the device's system settings in the search request.
