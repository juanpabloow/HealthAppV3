# Project Guidelines

## Tech Stack
- Kotlin + Jetpack Compose (no XML layouts)
- Firebase Firestore as database
- MVVM Architecture
- Hilt for Dependency Injection

---

## Naming Conventions
- **Composable Functions:** Name as nouns or adjective+noun, never verbs. Use PascalCase.
- **Field Variables:** Use camelCase for data class fields. No underscores, even if backend JSON uses them.
- **ViewModel State:** Prefix private mutable state with underscore (`_state`), expose public read-only property without it.
- **Use Cases:** Verb in present tense + "UseCase" suffix (e.g., `GetRestaurantsUseCase`).

## Code Structure
- Define composable functions directly in Kotlin files, not inside classes.
- Organize packages combining feature-based and layer-based strategies (e.g., `feature/presentation`, `feature/domain`, `feature/data`).
- Separate Domain models (pure Kotlin) from DTOs used for network/database operations.
- Use Hilt to centralize dependency creation and eliminate duplicated infrastructure code.

## Compose-Specific Patterns
- Build complex UIs by composing smaller components, not through inheritance.
- Every composable must accept a `Modifier` parameter for external layout control.
- Chain modifiers carefully — order matters, applied outermost to innermost.
- Use `LazyColumn`/`LazyRow` for large or dynamic lists to render only visible items.
- Wrap one-time side effects (e.g., network triggers) inside `LaunchedEffect`.

## State Management
- Follow unidirectional data flow: state flows down, events flow up.
- Hoist state to callers or ViewModels to keep composables stateless, reusable, and testable.
- Use `remember` to preserve state across recompositions.
- Use `rememberSaveable` or `SavedStateHandle` in ViewModels for process death recovery.
- Use immutable data classes with `.copy()` for state updates to ensure proper recomposition.

## Coroutines
- Use `viewModelScope` for coroutines in ViewModels — auto-canceled on ViewModel clear.
- Run I/O on `Dispatchers.IO`, update UI state on `Dispatchers.Main`.
- Inject `CoroutineDispatcher` instead of hardcoding for better test isolation.
- Use Kotlin `Flow` for sequential async data streams instead of one-shot suspend functions for paginated data.

## Error Handling
- Pass `CoroutineExceptionHandler` to coroutine scopes for centralized error management.
- Wrap network requests in try-catch, catch specific exceptions (e.g., `UnknownHostException`) for meaningful UI feedback.
- Return `LoadResult.Error` in custom Paging sources to signal failures to the UI layer.

## Architecture Patterns
- **MVVM:** Separate UI (composables) from presentation logic (ViewModels).
- **Repository Pattern:** Abstract data access, hide coordination of multiple data sources.
- **Single Source of Truth:** Use local database (Room) as SSOT for online/offline consistency.
- **Domain Layer:** Encapsulate business logic in Use Case classes, independent of UI and data sources.
- **Dependency Rule:** Dependencies point inward toward Domain layer. Business logic never depends on implementation details.
- **UI Decoupling:** Pass UI state and event callbacks as function parameters to screen composables, not direct ViewModel references.
