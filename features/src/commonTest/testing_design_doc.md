# Авто-тесты

## 1. Стиль тестирования

В качестве основного стиля написания тестов используем `FunSpec` из библиотеки Kotest. Это самый
простой и читаемый формат для наших сценариев.
С `FunSpec` нам будет легко сделать тесты изолированными и безопасными для параллельного запуска.

### Основные правила

- **Не используем базовые классы.** Отказываемся от наследования вроде `BaseSpec`. Вся тестовая
  инфраструктура должна предоставляться через DSL и композицию.
- **Используем `context` только когда он помогает группировке.** В простых спеках `componentTest`,
  `test` и `withTests` можно объявлять прямо внутри `FunSpec`. Если в одной спецификации нужно
  сгруппировать несколько наборов сценариев, используем `context` с именем сущности или экрана на
  английском языке.
- **Один тест — одно наблюдаемое поведение.** Каждый тест должен проверять один внешний сценарий, а
  не набор несвязанных проверок.

### Именование тестов

Имя теста должно:

- описывать наблюдаемое поведение, а не внутреннюю реализацию;
- быть коротким законченным предложением на английском языке;
- использовать единый нейтральный стиль без `should`;
- по возможности формулироваться в present simple.

Хорошие примеры:

- `loads the default type pokemon list`
- `shows loading while fetching data`
- `emits pokemon details output when a pokemon is clicked`

Нежелательные примеры:

- `should call repository twice`
- `test refresh logic`
- `checks state and output and retry`

### Структура теста

Тесты организуются по принципу Arrange-Act-Assert, но технические названия блоков мы не используем.
Вместо этого каждый шаг описывается с точки зрения сценария.

Правила:

- имя теста — одно предложение, описывающее поведение;
- логические блоки отделяются **одной пустой строкой**;
- каждый блок предваряется **коротким комментарием на английском языке** с иконкой стадии;
- слова `Arrange`, `Act` и `Assert` в комментариях не используем.
- для unit-тестов чистых функций предпочитаем классическую трехблочную структуру:
  подготовить входные данные, выполнить проверяемое действие, проверить результат.

Иконки стадий:

- `🛠️` — подготовка данных, исходных состояний и тестируемого объекта;
- `▶️` — действие или продвижение сценария во времени;
- `✅` — проверка наблюдаемого результата.

### Пример структуры теста

```kotlin
class MyComponentTest : FunSpec({
    componentTest("loads data successfully") {
        // 🛠️ Prepare data for initial loading
        <...настройка моков...>
        <...создание компонента...>

        // ▶️ Wait for the initial loading to complete
        <...прокрутка виртуального времени...>

        // ✅ Verify the loaded data is shown
        <...проверки состояния...>
    }

    <...остальные тесты этого экрана...>
})
```

Для unit-тестов действие должно быть выделено в отдельный блок, даже если это один вызов функции.
Например, для калькулятора: `Prepare input -> Calculate result -> Verify result`.

```kotlin
test("calculates pokemon power score") {
    // 🛠️ Prepare pokemon stats
    val stats = <...входные данные...>

    // ▶️ Calculate pokemon power score
    val actualScore = PokemonPowerCalculator.calculate(stats)

    // ✅ Verify calculated pokemon power score
    actualScore shouldBe <...ожидаемый результат...>
}
```

Если одна и та же unit-логика проверяется на нескольких входных наборах, используем table-style
тесты через Kotest `withTests`. Имя каждого кейса также должно описывать наблюдаемое поведение на
английском языке.

### Многоблочные сценарии

Для component-level и компонентных тестов нормально использовать больше трех логических блоков.
Такие тесты часто описывают сценарий во времени: начальная загрузка, проверка исходного состояния,
действие пользователя, промежуточное состояние и финальный результат.

Классический Arrange-Act-Assert остается ориентиром, но может разворачиваться в сценарную структуру:
`prepare -> wait -> verify initial state -> act -> verify intermediate state -> wait -> verify final state`.

Это особенно полезно, когда промежуточное состояние является частью наблюдаемого поведения компонента:
например, показ `loading` во время `refresh`, retry после ошибки или смена выбранного фильтра.
Главное ограничение остается тем же: один тест должен проверять один внешний сценарий, а не набор
несвязанных поведений.

```kotlin
componentTest("shows loading during refresh and loads new data") {
    // 🛠️ Prepare initial and refreshed data
    <...начальный сетап...>

    // ▶️ Wait for the initial loading to complete
    <...прокрутка виртуального времени...>

    // ✅ Verify loaded data state
    <...проверка исходного состояния...>

    // ▶️ Refresh the current data
    <...вызов метода обновления...>

    // ✅ Verify loading is shown during refresh
    <...проверка показа лоадера...>

    // ▶️ Wait for the refresh to complete
    <...прокрутка виртуального времени...>

    // ✅ Verify loading is hidden and new data is shown
    <...проверка финального состояния...>
}
```

---

## 2. Что считать компонентным тестом

В рамках этого проекта **компонентный тест** — это test для Decompose-компонента через общий
DSL `componentTest`.

Есть два основных стиля:

1. Экранный компонент с production dependency graph: DI-граф, реальный репозиторий, Replica,
   парсинг данных и моковый сервер. Обычно такой тест проверяет цепочку
   `Component -> Repository -> Network (mock server) -> Parsing -> State / Output`.
2. Router/root компонент с подмененной `childComponentFactory`: тест проверяет `ChildStack`,
   обработку child output, передачу параметров, conditional navigation и кастомный back/close без
   реальной загрузки дочерних экранов.
---

## 3. Обвязка для компонентных тестов

Поскольку ручное создание всех зависимостей для component-level сценариев трудоемко, для таких
тестов используется отдельный DSL-метод `componentTest`.

Синтаксически он работает так же, как стандартный `test` из Kotest, но дает доступ к тестовой
инфраструктуре: mock server, виртуальному времени и хелперам создания компонентов.

### Ограничение

Метод `componentTest` доступен внутри `FunSpec` и синтаксически похож на обычный `test`.

### Сигнатура

```kotlin
fun FunSpecRootScope.componentTest(
    name: String,
    block: suspend ComponentTestScope.() -> Unit
)
```

### Пример использования

```kotlin
componentTest("loads screen data successfully") {
    // this: ComponentTestScope
}
```

### Возможности `ComponentTestScope`

```kotlin
interface ComponentTestScope : TestScope {
    val mockServer: MockServer
    val messageService: TestMessageService
    val permissionService: TestPermissionService
    val externalAppService: TestExternalAppService
    val networkConnectivityProvider: TestNetworkConnectivityProvider

    fun advanceUntilIdle()
    fun advanceTimeBy(delayTime: Duration)

    fun <T> setupComponent(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): T

    fun <T> setupComponentWithContext(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): Pair<T, TestComponentContext>
}
```

`ComponentTestScope` предоставляет:

1. `mockServer` — доступ к тестовому серверу для мокирования сетевых ответов;
2. `messageService` — тестовую реализацию сервиса сообщений;
3. `permissionService` — тестовую реализацию сервиса разрешений;
4. `externalAppService` — тестовую реализацию сервиса внешних приложений;
5. `networkConnectivityProvider` — управляемую тестовую реализацию сетевой доступности;
6. методы управления виртуальным временем: `advanceUntilIdle()`, `advanceTimeBy()`;
7. хелперы создания компонентов - о них позже.

---

## 4. Архитектура под капотом

### Виртуальное время

Реализация `componentTest` строится на `kotlinx-coroutines-test`, который дает механизм
виртуального времени. Благодаря этому тесты выполняются быстро и детерминированно, без реальных
ожиданий.

### Изоляция DI

Для каждого теста создается отдельный экземпляр `Koin`, а не используется глобальный
`startKoin(...)`. Это дает изолированную среду исполнения и делает тесты безопасными для
параллельного запуска.

### Не используем подмену диспетчера

Не используем `Dispatchers.setMain(...)` в компонентных тестах.

Причина - этот вызов мутирует глобальное состояние, что делает тесты зависимыми друг от друга и
создает проблемы при параллельном запуске.

Вместо этого мы:

- создаем `UnconfinedTestDispatcher(testScheduler)`;
- передаем его явно через DI во все зависимости, которым нужен диспетчер.

### Пример реализации

```kotlin
interface ComponentTestScope : TestScope {
    val mockServer: MockServer
    val messageService: TestMessageService
    val permissionService: TestPermissionService
    val externalAppService: TestExternalAppService
    val networkConnectivityProvider: TestNetworkConnectivityProvider
    
    fun advanceUntilIdle()
    fun advanceTimeBy(delayTime: Duration)

    // setupComponent helpers
}

class ComponentTestScopeImpl(
    koin: Koin,
    private val kotestScope: TestScope,
    private val testScheduler: TestCoroutineScheduler,
    private val replicaBehaviourScheduler: TestCoroutineScheduler,
    private val testDispatcher: TestDispatcher
) : ComponentTestScope, TestScope by kotestScope {

    override val mockServer: MockServer = koin.get()

    override val messageService: TestMessageService =
        koin.get<MessageService>() as TestMessageService

    override val permissionService: TestPermissionService =
        koin.get<PermissionService>() as TestPermissionService

    override val externalAppService: TestExternalAppService =
        koin.get<ExternalAppService>() as TestExternalAppService

    override val networkConnectivityProvider: TestNetworkConnectivityProvider =
        koin.get<NetworkConnectivityProvider>() as TestNetworkConnectivityProvider

    private val componentFactory = ComponentFactory(koin)

    override fun advanceUntilIdle() {
        val startTime = testScheduler.currentTime
        testScheduler.advanceUntilIdle()
        val endTime = testScheduler.currentTime
        replicaBehaviourScheduler.advanceTimeBy(endTime - startTime)
    }

    override fun advanceTimeBy(delayTime: Duration) {
        testScheduler.advanceTimeBy(delayTime)
        replicaBehaviourScheduler.advanceTimeBy(delayTime)
    }

    // setupComponent helpers
}

fun FunSpecRootScope.componentTestImpl(
    name: String,
    featureModules: List<Module>,
    block: suspend ComponentTestScope.() -> Unit
) {
    test(name).config(coroutineTestScope = true) {
        val testDispatcher = UnconfinedTestDispatcher(testCoroutineScheduler)
        val replicaBehaviourScheduler = TestCoroutineScheduler()
        val replicaBehaviourDispatcher = UnconfinedTestDispatcher(replicaBehaviourScheduler)
        val koin = createKoin(
            testScheduler = testCoroutineScheduler,
            testDispatcher = testDispatcher,
            replicaBehaviourDispatcher = replicaBehaviourDispatcher,
            featureModules = featureModules
        )

        val componentScope = ComponentTestScopeImpl(
            koin = koin,
            kotestScope = this,
            testScheduler = testCoroutineScheduler,
            replicaBehaviourScheduler = replicaBehaviourScheduler,
            testDispatcher = testDispatcher
        )

        var primaryFailure: Throwable? = null
        try {
            componentScope.block()
        } catch (throwable: Throwable) {
            primaryFailure = throwable
            throw throwable
        } finally {
            try {
                componentScope.finishTest(primaryFailure)
            } finally {
                koin.close()
            }
        }
    }
}

private suspend fun ComponentTestScope.finishTest(primaryFailure: Throwable?) {
    try {
        advanceUntilIdle()
        mockServer.verify()
    } catch (throwable: Throwable) {
        if (primaryFailure == null) {
            throw throwable
        } else {
            primaryFailure.addSuppressed(throwable)
        }
    }
}

private fun createKoin(
    testScheduler: TestCoroutineScheduler,
    testDispatcher: TestDispatcher,
    replicaBehaviourDispatcher: TestDispatcher,
    featureModules: List<Module>
): Koin {
    return Koin().apply {
        loadModules(coreTestModule(testScheduler, testDispatcher, replicaBehaviourDispatcher) + featureModules)
        createEagerInstances()
    }
}

private fun coreTestModule(
    testScheduler: TestCoroutineScheduler,
    testDispatcher: TestDispatcher,
    replicaBehaviourDispatcher: TestDispatcher
) = module {
    single { MockServer() }
    // ...
    single {
        ReplicaClient(
            networkConnectivityProvider = get(),
            timeProvider = TestReplicaTimeProvider(testScheduler),
            mainDispatcher = testDispatcher,
            behaviourDispatcher = replicaBehaviourDispatcher
        )
    }
}

```

В модуле `features` используется тонкая обертка над общей реализацией из `core-testing`: она
автоматически передает `featureModules`, поэтому в тестах не нужно указывать их вручную.

```kotlin
fun FunSpecRootScope.componentTest(
    name: String,
    block: suspend ComponentTestScope.() -> Unit
) = componentTestImpl(name, featureModules, block)
```

`componentTest` всегда выполняет финальный `advanceUntilIdle()` и `mockServer.verify()` перед
закрытием `Koin`. Если основной блок теста уже упал, ошибка финальной проверки добавляется как
suppressed и не затирает исходную причину падения.

---

## 5. Хелперы для создания компонентов

Для компонентов Decompose важен строгий порядок инициализации:

`создание контекста -> создание компонента -> перевод жизненного цикла в нужное состояние`

Чтобы убрать бойлерплейт и исключить ошибки в сетапе, в `ComponentTestScope` добавлены специальные
хелперы.

### Интерфейсы

```kotlin
interface ComponentTestScope : TestScope {

    fun <T> setupComponent(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): T

    fun <T> setupComponentWithContext(
        targetState: Lifecycle.State = Lifecycle.State.RESUMED,
        create: ComponentFactory.(TestComponentContext) -> T
    ): Pair<T, TestComponentContext>
}
```

### Когда какой хелпер использовать

| Инструмент                       | Когда использовать                                            |
|----------------------------------|---------------------------------------------------------------|
| `setupComponent(...)`            | Нужен только компонент, lifecycle вручную менять не требуется |
| `setupComponentWithContext(...)` | Нужно вручную двигать lifecycle во время теста                |

### Примеры использования

**1. Стандартный сценарий**

```kotlin
componentTest("loads screen data successfully") {
    val component = setupComponent { createPokemonListComponent(it, {}) }
    ...
}
```

**2. Router/root сценарий с подменой child component factory**

Test child component factory для router/root тестов возвращает фейковые component interfaces.
Если child component сообщает события родителю через `Output`, fake принимает callback создания и
дает тесту ручку `emitOutput(...)`.
Router/root компонент сам оборачивает их в `XxxComponent.Child.*` для элементов `ChildStack`.

```kotlin
componentTest("opens pokemon details when pokemon is requested from list") {
    // 🛠️ Prepare pokemons flow
    val childComponentFactory = TestPokemonsChildComponentFactory()
    val component = setupComponent {
        createPokemonsComponent(it, childComponentFactory)
    }

    // ✅ Verify pokemon list screen is shown
    val listChild =
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.List>()

    // ▶️ Request pokemon details from the list screen
    val pokemonId = PokemonId("77")
    val listComponent = listChild.component.shouldBeInstanceOf<FakePokemonListComponent>()
    listComponent.emitOutput(
        PokemonListComponent.Output.PokemonDetailsRequested(pokemonId)
    )

    // ✅ Verify pokemon details screen is shown for requested pokemon
    val detailsChild =
        component.childStack.activeChild.shouldBeInstanceOf<PokemonsComponent.Child.Details>()
    detailsChild.component.pokemonId shouldBe pokemonId
}
```

**3. Lifecycle-сценарий**

```kotlin
componentTest("stops loading when the screen moves to background") {
    val (component, context) = setupComponentWithContext {
        createPokemonListComponent(it, {})
    }

    // ▶️ Move the screen to background state
    context.moveToState(Lifecycle.State.CREATED)
    ...
}
```

### Ограничение доступа к `ComponentFactory`

`ComponentFactory` намеренно скрыта внутри приватной реализации скоупа. Это исключает создание
компонента в обход хелперов. Она не регистрируется в Koin, а создается внутри test infrastructure
как `ComponentFactory(koin)`. Даже router/root тесты создают SUT через
`ComponentFactory.createXxxComponent(..., childComponentFactory = testChildComponentFactory)`, а не
через прямой вызов `RealXxxComponent(...)`.

---

## 6. Mock Server

Вместо реальной сети в тестах используется кастомный `MockServer` поверх `Ktor MockEngine`.

Он хранит единый список запланированных ответов, записывает исходящие запросы и поддерживает
задержки, интегрированные с виртуальным временем.
Каждый запланированный ответ снабжен `RequestMatcher`, который определяет, к какому `HttpRequest` он
применим.  
`RequestMatcher` конфигурируется с помощью удобного DSL.

### Модели и интерфейс

```kotlin
val DEFAULT_HTTP_RESPONSE_DELAY: Duration = 500.milliseconds

data class HttpResponse(
    val body: String = "",
    val status: HttpStatusCode = HttpStatusCode.OK,
    val delay: Duration = DEFAULT_HTTP_RESPONSE_DELAY,
    val headers: Map<String, String> = mapOf("Content-Type" to "application/json")
)

data class HttpRequest(
    val method: HttpMethod,
    val path: String,
    val fullUrl: String,
    val body: String,
    val headers: Map<String, String>
) {
    fun queryParam(name: String): String?
}

fun interface RequestMatcher {
    fun matches(request: HttpRequest): Boolean

    companion object : RequestMatcher {
        override fun matches(request: HttpRequest): Boolean = true
    }
}

fun RequestMatcher.containsPath(value: String): RequestMatcher
fun RequestMatcher.exactPath(value: String): RequestMatcher
fun RequestMatcher.method(value: HttpMethod): RequestMatcher

class MockServer {
    suspend fun enqueue(
        matcher: RequestMatcher,
        response: HttpResponse
    )

    suspend fun getRecordedRequests(
        matcher: RequestMatcher = RequestMatcher
    ): List<HttpRequest>

    suspend fun verify()
}
```

### Правила использования

- `RequestMatcher` одновременно является типом и стартовым значением DSL, по аналогии с `Modifier` в
  Compose.
- Чейнинг означает логическое `AND`: каждый следующий matcher только сужает условие.
- Реализации `RequestMatcher` - приватные. Публично используем только DSL.
- Каждый `enqueue` добавляет один одноразовый ответ и должен быть использован ровно один раз.
  Незамоканные запросы и неиспользованные
  ответы считаются ошибкой теста.
- Каждый `HttpResponse` по умолчанию задерживается на `DEFAULT_HTTP_RESPONSE_DELAY` виртуального
  времени, чтобы в тестах можно было проверять промежуточные loading-state без ручной настройки задержки.
- Обычно `verify()` вручную не вызываем: `componentTest` делает это автоматически в `finally`
  после финального `advanceUntilIdle()`.

### Примеры

**Базовый мок**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(readTestResource("pokemons/ponyta_details.json"))
)
```

**Мок без задержки**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(
        body = readTestResource("pokemons/ponyta_details.json"),
        delay = Duration.ZERO
    )
)
```

**Retry-сценарий**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(status = HttpStatusCode.NotFound)
)
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(readTestResource("pokemons/ponyta_details.json"))
)
```

**Проверка исходящего запроса**

```kotlin
componentTest("sends pokemon data when the form is submitted") {
    // 🛠️ Prepare an empty pokemon form
    mockServer.enqueue(
        RequestMatcher.containsPath("pokemons").method(HttpMethod.Post),
        HttpResponse(status = HttpStatusCode.Created)
    )
    val component = setupComponent { createPokemonFormComponent(it, {}) }

    // ▶️ Fill the form and submit it
    component.onNameChange("Pikachu")
    component.onSubmitClick()
    advanceUntilIdle()

    // ✅ Verify the created pokemon request
    val request = mockServer
        .getRecordedRequests(
            matcher = RequestMatcher.containsPath("pokemons").method(HttpMethod.Post)
        )
        .first()

    request.method shouldBe HttpMethod.Post
    request.body shouldContain "\"name\":\"Pikachu\""
}
```

---

## 7. Виртуальное время

Все компонентные тесты выполняются внутри с использованием `UnconfinedTestDispatcher`, чтобы
имитировать поведение `Main.immediate`. Корутинный код стартует сразу и выполняется до первой
приостановки, а задержки и отложенные задачи остаются под контролем виртуального времени.

Внутри test infrastructure используются два scheduler'а:

- основной `testCoroutineScheduler` для корутин теста, компонентов и `MockServer`;
- отдельный `replicaBehaviourScheduler` для behaviour timers Replica.

`advanceUntilIdle()` и `advanceTimeBy(...)` продвигают оба scheduler'а согласованно, поэтому в
тестах обычно не нужно знать об этом разделении.

### Инструменты

| Инструмент                 | Когда использовать                                                 |
|----------------------------|--------------------------------------------------------------------|
| `advanceUntilIdle()`       | Довести очередь до полного завершения всех запланированных задач   |
| `advanceTimeBy(delayTime)` | Сдвинуть виртуальные часы на точное значение                       |

### Практическая матрица выбора

| Сценарий                                                        | Инструмент           |
|-----------------------------------------------------------------|----------------------|
| Проверка финального состояния после загрузки                    | `advanceUntilIdle()` |
| Проверка синхронного промежуточного состояния, например лоадера | Проверка сразу после action |
| Debounce, таймауты, прогресс-бары, точные задержки              | `advanceTimeBy(...)` |

### Сценарий 1. Проверка финального результата

Если промежуточные состояния не важны, используем обычный мок и `advanceUntilIdle()`.

```kotlin
componentTest("loads the default type pokemon list") {
    // 🛠️ Prepare default type pokemon list data
    val pokemons = PokemonListFixture.fire
    mockServer.enqueuePokemonList(pokemons)
    val component = setupComponent { createPokemonListComponent(it, {}) }

    // ▶️ Wait for the initial loading to complete
    advanceUntilIdle()

    // ✅ Verify the default type list is loaded
    component.pokemonsState.value.loading shouldBe false
    component.pokemonsState.value.data shouldBe pokemons.domain
}
```

### Сценарий 2. Проверка промежуточного loading-state

Если нужно проверить показ лоадера, используем дефолтную задержку ответа и проверяем состояние
сразу после action, который запускает загрузку.

```kotlin
componentTest("shows loading while fetching the pokemon list") {
    // 🛠️ Prepare pokemon list data
    val pokemons = PokemonListFixture.fire
    mockServer.enqueuePokemonList(pokemons)
    val component = setupComponent { createPokemonListComponent(it, {}) }

    // ✅ Verify loading is shown
    component.pokemonsState.value.loading shouldBe true

    // ▶️ Wait for the loading to complete
    advanceUntilIdle()
    
    // ✅ Verify the pokemon list is loaded
    component.pokemonsState.value.loading shouldBe false
    component.pokemonsState.value.data shouldBe pokemons.domain
}
```

### Когда `advanceTimeBy(...)` действительно нужен

`advanceTimeBy(...)` используем только в сценариях, где важно точное положение на временной шкале:

- debounce в поиске;
- отложенные side-effect'ы;
- прогресс-бары;
- таймеры и countdown-механики.

Для обычной проверки loading-state обычно достаточно проверки сразу после action и финального
`advanceUntilIdle()`.

### Антипаттерны при проверке `StateFlow`

Ниже правила относятся именно к **компонентным тестам с виртуальным временем**.

**Не используем блокирующие ожидания вроде:**

```kotlin
state.first { !it.loading }
```

Такие конструкции могут заблокировать тестовый поток и привести к дедлоку.

**Не используем Turbine для `StateFlow` в этом типе тестов.**

Не используем Turbine в компонентных тестах, так как проверки через
`state.value` вместе с управлением виртуальным временем проще, понятнее и лучше отражают пользовательский сценарий.
---

## 8. Тестирование output-событий

Для проверки событий, которые компонент отправляет наружу через `onOutput`, не используем библиотеки
мокирования вроде MockK. Вместо этого применяем простой хелпер `OutputCapturer`.

### Интерфейс

```kotlin
class OutputCapturer<T> : (T) -> Unit {
    val all: List<T>
    val last: T?
    val first: T?
    val isEmpty: Boolean

    override fun invoke(output: T)
}
```

### Пример использования

```kotlin
componentTest("emits pokemon details output when a pokemon is clicked") {
    // 🛠️ Prepare loaded pokemon list data
    val pokemons = PokemonListFixture.fire
    val pokemon = PokemonDetailsFixture.ponyta
    mockServer.enqueuePokemonList(pokemons)
    val capturer = OutputCapturer<PokemonListComponent.Output>()
    val component = setupComponent { createPokemonListComponent(it, capturer) }
    advanceUntilIdle()

    // ▶️ Click a pokemon item in the list
    component.onPokemonClick(pokemon.id)

    // ✅ Verify the pokemon details output is emitted
    capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(pokemon.id)
}
```

Для негативных сценариев используем:

```kotlin
capturer.isEmpty shouldBe true
```

---

## 9. Работа с тестовыми данными

Большие JSON-строки не хардкодим в Kotlin-коде. Моковые ответы храним в
`features/src/commonTest/resources/`, группируя их по предметной области. Сейчас pokemon-ответы
лежат в `features/src/commonTest/resources/pokemons/`.
Так тесты остаются читаемыми и ответы удобнее переиспользовать между сценариями.
Для загрузки используем fixtures и `readTestResource(...)` из `core-testing`.

### Пример

```kotlin
data class PokemonListFixture(
    val typeId: PokemonTypeId,
    val resourcePath: String,
    val domain: List<Pokemon>
) {
    companion object {
        val fire = PokemonListFixture(
            typeId = PokemonType.Fire.id,
            resourcePath = "pokemons/fire_pokemons.json",
            domain = listOf(
                Pokemon(id = PokemonId("4"), name = "Charmander"),
                Pokemon(id = PokemonId("77"), name = "Ponyta")
            )
        )
    }
}
```

### Использование

```kotlin
suspend fun MockServer.enqueuePokemonList(
    fixture: PokemonListFixture,
    isError: Boolean = false
) {
    enqueue(
        matcher = RequestMatcher.containsPath("type/${fixture.typeId.value}"),
        response = if (isError) {
            HttpResponse(status = HttpStatusCode.NotFound)
        } else {
            HttpResponse(readTestResource(fixture.resourcePath))
        }
    )
}
```

---

## 10. Минимальный набор сценариев для компонента

Для большинства обычных экранных компонентов ожидается, что набор компонентных тестов покрывает
как минимум:

- initial load success;
- initial load error;
- retry после ошибки;
- выполнение ключевых пользовательских действий (публичных методов компонента);
- обработка output-сценариев, если компонент что-то эмитит наружу;
- loading-state, если он влияет на UX (например, проверка, что в сценарии поиска показываем
  предыдущие данные, пока обрабатывается новый запрос);
- lifecycle-сценарий, если поведение зависит от состояния жизненного цикла.

Это не жесткий чеклист для каждого компонента, но именно от него стоит отталкиваться при
проектировании тестового набора.

---

## 11. Полный пример компонентных тестов

```kotlin
package ru.mobileup.template.features.pokemons.presentation.list

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import ru.mobileup.template.core_testing.utils.OutputCapturer
import ru.mobileup.template.features.pokemons.createPokemonListComponent
import ru.mobileup.template.features.pokemons.fixtures.PokemonDetailsFixture
import ru.mobileup.template.features.pokemons.fixtures.PokemonListFixture
import ru.mobileup.template.features.pokemons.fixtures.enqueuePokemonList
import ru.mobileup.template.features.utils.componentTest

class PokemonListComponentTest : FunSpec({

    componentTest("loads pokemon list successfully") {
        // 🛠️ Prepare pokemon list data for initial loading
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ✅ Verify initial loading is shown
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe pokemons.domain
    }

    componentTest("emits pokemon details output when a pokemon is clicked") {
        // 🛠️ Prepare pokemon list data for initial loading
        val pokemons = PokemonListFixture.fire
        val pokemon = PokemonDetailsFixture.ponyta
        mockServer.enqueuePokemonList(pokemons)
        val capturer = OutputCapturer<PokemonListComponent.Output>()
        val component = setupComponent { createPokemonListComponent(it, capturer) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ▶️ Click a pokemon item in the list
        component.onPokemonClick(pokemon.id)

        // ✅ Verify the pokemon details output is emitted
        capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(pokemon.id)
    }

    componentTest("shows error when pokemon list loading fails") {
        // 🛠️ Prepare failed pokemon list loading
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons, isError = true)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()
    }

    componentTest("reloads pokemon list after error") {
        // 🛠️ Prepare failed initial loading and successful retry
        val pokemons = PokemonListFixture.fire
        mockServer.enqueuePokemonList(pokemons, isError = true)
        mockServer.enqueuePokemonList(pokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ✅ Verify failed pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.data shouldBe null
        component.pokemonsState.value.error.shouldNotBeNull()

        // ▶️ Refresh pokemon list
        component.onRefresh()

        // ✅ Verify loading starts again
        component.pokemonsState.value.loading shouldBe true

        // ▶️ Wait for retry loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded pokemon list state after retry
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe pokemons.domain
    }

    componentTest("loads selected type pokemon list while keeping previous data") {
        // 🛠️ Prepare default and selected type pokemon list data
        val initialPokemons = PokemonListFixture.fire
        val selectedTypePokemons = PokemonListFixture.water
        mockServer.enqueuePokemonList(initialPokemons)
        mockServer.enqueuePokemonList(selectedTypePokemons)
        val component = setupComponent { createPokemonListComponent(it, {}) }

        // ▶️ Wait for the initial loading to complete
        advanceUntilIdle()

        // ▶️ Select another pokemon type
        component.onTypeClick(selectedTypePokemons.typeId)

        // ✅ Verify previous pokemon list is kept while selected type is loading
        component.selectedTypeId.value shouldBe selectedTypePokemons.typeId
        component.pokemonsState.value.loading shouldBe true
        component.pokemonsState.value.data shouldBe initialPokemons.domain

        // ▶️ Wait for selected type loading to complete
        advanceUntilIdle()

        // ✅ Verify loaded selected pokemon list state
        component.pokemonsState.value.loading shouldBe false
        component.pokemonsState.value.error shouldBe null
        component.pokemonsState.value.data shouldBe selectedTypePokemons.domain
    }
})
```

---

## 12. Границы применимости компонентных тестов

Несмотря на удобство `componentTest`, не стоит использовать его для всех сценариев. Поднятие
Koin-графа и MockServer — тяжелая инфраструктура. Сложную логику с множеством ветвлений — мапперы,
валидацию, расчеты, краевые случаи — нужно покрывать быстрыми юнит-тестами через обычный `test`,
прямое создание объекта и fake-зависимости. 
