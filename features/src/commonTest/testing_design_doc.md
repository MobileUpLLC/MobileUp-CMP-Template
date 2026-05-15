# Авто-тесты

## 1. Стиль тестирования

В качестве основного стиля написания тестов используем `FunSpec` из библиотеки Kotest. Это самый
простой и читаемый формат для наших сценариев.
С `FunSpec` нам будет легко сделать тесты изолированными и безопасными для параллельного запуска.

### Основные правила

- **Не используем базовые классы.** Отказываемся от наследования вроде `BaseSpec`. Вся тестовая
  инфраструктура должна предоставляться через DSL и композицию.
- **Используем блок `context`.** Внутри `FunSpec` объявляется блок `context` (как правило, один на
  спецификацию), который задает имя тестируемой сущности или экрана на английском языке.
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
    context("My screen") {
        integrationTest("loads data successfully") {
            // 🛠️ Prepare data for initial loading
            <...настройка моков...>
            <...создание компонента...>

            // ▶️ Wait for the initial loading to complete
            <...прокрутка виртуального времени...>

            // ✅ Verify the loaded data is shown
            <...проверки состояния...>
        }

        <...остальные тесты этого экрана...>
    }
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

### Многоблочные сценарии

Для component-level и интеграционных тестов нормально использовать больше трех логических блоков.
Такие тесты часто описывают сценарий во времени: начальная загрузка, проверка исходного состояния,
действие пользователя, промежуточное состояние и финальный результат.

Классический Arrange-Act-Assert остается ориентиром, но может разворачиваться в сценарную структуру:
`prepare -> wait -> verify initial state -> act -> verify intermediate state -> wait -> verify final state`.

Это особенно полезно, когда промежуточное состояние является частью наблюдаемого поведения компонента:
например, показ `loading` во время `refresh`, retry после ошибки или смена выбранного фильтра.
Главное ограничение остается тем же: один тест должен проверять один внешний сценарий, а не набор
несвязанных поведений.

```kotlin
integrationTest("shows loading during refresh and loads new data") {
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

## 2. Что считать интеграционным тестом

В рамках этого проекта **интеграционный тест** — это **component-level test** с DI-графом, реальным
репозиторием и парсингом данных, но моковым сервером.
Обычно такой тест проверяет работу цепочки:
`Component -> Repository -> Network (mock server) -> Parsing -> State / Output`
---

## 3. Обвязка для интеграционных тестов

Поскольку ручное создание всех зависимостей для component-level сценариев трудоемко, для таких
тестов используется отдельный DSL-метод `integrationTest`.

Синтаксически он работает так же, как стандартный `test` из Kotest, но дает доступ к тестовой
инфраструктуре: mock server, виртуальному времени и хелперам создания компонентов.

### Ограничение

Метод `integrationTest` доступен только внутри блока `context` через `FunSpecContainerScope`. Это
позволяет гарантировать единый формат спецификаций.

### Сигнатура

```kotlin
suspend fun FunSpecContainerScope.integrationTest(
    name: String,
    block: suspend IntegrationTestScope.() -> Unit
)
```

### Пример использования

```kotlin
context("My screen") {
    integrationTest("loads screen data successfully") {
        // this: IntegrationTestScope
    }
}
```

### Возможности `IntegrationTestScope`

```kotlin
interface IntegrationTestScope : TestScope {
    val mockServer: MockServer
    val testMessageService: TestMessageService

    fun advanceUntilIdle()
    fun advanceTimeBy(delayTime: Duration)

    // setupComponent helpers
}
```

`IntegrationTestScope` предоставляет:

1. `mockServer` — доступ к тестовому серверу для мокирования сетевых ответов;
2. `testMessageService` — тестовую реализацию сервиса сообщений;
3. методы управления виртуальным временем: `advanceUntilIdle()`, `advanceTimeBy()`;
4. хелперы создания компонентов - о них позже.

---

## 4. Архитектура под капотом

### Виртуальное время

Реализация `integrationTest` строится на `kotlinx-coroutines-test`, который дает механизм
виртуального времени. Благодаря этому тесты выполняются быстро и детерминированно, без реальных
ожиданий.

### Изоляция DI

Для каждого теста создается отдельный экземпляр `Koin`, а не используется глобальный
`startKoin(...)`. Это дает изолированную среду исполнения и делает тесты безопасными для
параллельного запуска.

### Не используем подмену диспетчера

Не используем `Dispatchers.setMain(...)` в интеграционных тестах.

Причина - этот вызов мутирует глобальное состояние, что делает тесты зависимыми друг от друга и
создает проблемы при параллельном запуске.

Вместо этого мы:

- создаем `UnconfinedTestDispatcher(testScheduler)`;
- передаем его явно через DI во все зависимости, которым нужен диспетчер.

### Пример реализации

```kotlin
interface IntegrationTestScope : TestScope {
    val mockServer: MockServer
    val testMessageService: TestMessageService
    
    fun advanceUntilIdle()
    fun advanceTimeBy(delayTime: Duration)

    // setupComponent helpers
}

class IntegrationTestScopeImpl(
    koin: Koin,
    private val kotestScope: TestScope,
    private val testScheduler: TestCoroutineScheduler,
    private val testDispatcher: TestDispatcher
) : IntegrationTestScope, TestScope by kotestScope {

    override val mockServer: MockServer = koin.get()
    override val testMessageService: TestMessageService =
        koin.get<MessageService>() as TestMessageService
    private val componentFactory: ComponentFactory = koin.get()

    override fun advanceUntilIdle() = testScheduler.advanceUntilIdle()
    override fun advanceTimeBy(delayTime: Duration) = testScheduler.advanceTimeBy(delayTime)

    // setupComponent helpers
}

suspend fun FunSpecContainerScope.integrationTest(
    name: String,
    featureModules: List<Module>,
    block: suspend IntegrationTestScope.() -> Unit
) {
    test(name).config(coroutineTestScope = true) {
        val testDispatcher = UnconfinedTestDispatcher(testCoroutineScheduler)
        val koin = createKoin(testDispatcher, featureModules)

        val integrationScope = IntegrationTestScopeImpl(
            koin = koin,
            kotestScope = this,
            testScheduler = testCoroutineScheduler,
            testDispatcher = testDispatcher
        )

        var primaryFailure: Throwable? = null
        try {
            integrationScope.block()
        } catch (throwable: Throwable) {
            primaryFailure = throwable
            throw throwable
        } finally {
            try {
                integrationScope.finishTest(primaryFailure)
            } finally {
                koin.close()
            }
        }
    }
}

private suspend fun IntegrationTestScope.finishTest(primaryFailure: Throwable?) {
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

private fun createKoin(testDispatcher: TestDispatcher, featureModules: List<Module>): Koin {
    return Koin().apply {
        loadModules(coreTestModule(testDispatcher) + featureModules)
        declare(ComponentFactory(this))
        createEagerInstances()
    }
}

private fun coreTestModule(testDispatcher: TestDispatcher) = module {
    single { MockServer() }
    // ...
    single { SomeLibraryConfig(dispatcher = testDispatcher) }
}

```

В модуле `features` используется тонкая обертка над общей реализацией из `core-testing`: она
автоматически передает `featureModules`, поэтому в тестах не нужно указывать их вручную.

`integrationTest` всегда выполняет финальный `advanceUntilIdle()` и `mockServer.verify()` перед
закрытием `Koin`. Если основной блок теста уже упал, ошибка финальной проверки добавляется как
suppressed и не затирает исходную причину падения.

---

## 5. Хелперы для создания компонентов

Для компонентов Decompose важен строгий порядок инициализации:

`создание контекста -> создание компонента -> перевод жизненного цикла в нужное состояние`

Чтобы убрать бойлерплейт и исключить ошибки в сетапе, в `IntegrationTestScope` добавлены специальные
хелперы.

### Интерфейсы

```kotlin
interface IntegrationTestScope : TestScope {

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
integrationTest("loads screen data successfully") {
    val component = setupComponent { createPokemonListComponent(it, {}) }
    ...
}
```

**2. Lifecycle-сценарий**

```kotlin
integrationTest("stops loading when the screen moves to background") {
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
компонента в обход хелперов.

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
        vararg responses: HttpResponse
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
- Каждый `enqueue` должен быть использован ровно один раз. Незамоканные запросы и неиспользованные
  ответы считаются ошибкой теста.
- Каждый `HttpResponse` по умолчанию задерживается на `DEFAULT_HTTP_RESPONSE_DELAY` виртуального
  времени, чтобы в тестах можно было проверять промежуточные loading-state без ручной настройки задержки.
- Обычно `verify()` вручную не вызываем: `integrationTest` делает это автоматически в `finally`
  после финального `advanceUntilIdle()`.

### Примеры

**Базовый мок**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(TestPokemons.detailedPonytaJson())
)
```

**Мок без задержки**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(TestPokemons.detailedPonytaJson(), delay = Duration.ZERO)
)
```

**Retry-сценарий**

```kotlin
mockServer.enqueue(
    RequestMatcher.containsPath("pokemon/77"),
    HttpResponse(status = HttpStatusCode.NotFound),
    HttpResponse(TestPokemons.detailedPonytaJson())
)
```

**Проверка исходящего запроса**

```kotlin
integrationTest("sends pokemon data when the form is submitted") {
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

Все интеграционные тесты выполняются внутри с использованием `UnconfinedTestDispatcher`, чтобы
имитировать поведение `Main.immediate`. Корутинный код стартует сразу и выполняется до первой
приостановки, а задержки и отложенные задачи остаются под контролем виртуального времени.

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

Если промежуточные состояния не важны, используем мгновенный мок и `advanceUntilIdle()`.

```kotlin
integrationTest("loads the default type pokemon list") {
    // 🛠️ Prepare default type pokemon list data
    mockServer.enqueue(
        RequestMatcher.containsPath("type/10"),
        HttpResponse(TestPokemons.firePokemonsJson())
    )
    val component = setupComponent { createPokemonListComponent(it, {}) }

    // ▶️ Wait for the initial loading to complete
    advanceUntilIdle()

    // ✅ Verify the default type list is loaded
    component.pokemonsState.value.loading shouldBe false
    component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
}
```

### Сценарий 2. Проверка промежуточного loading-state

Если нужно проверить показ лоадера, используем дефолтную задержку ответа и проверяем состояние
сразу после action, который запускает загрузку.

```kotlin
integrationTest("shows loading while fetching the pokemon list") {
    // 🛠️ Prepare pokemon list data
    mockServer.enqueue(
        RequestMatcher.containsPath("type/10"),
        HttpResponse(TestPokemons.firePokemonsJson())
    )
    val component = setupComponent { createPokemonListComponent(it, {}) }

    // ✅ Verify loading is shown
    component.pokemonsState.value.loading shouldBe true

    // ▶️ Wait for the loading to complete
    advanceUntilIdle()
    
    // ✅ Verify the pokemon list is loaded
    component.pokemonsState.value.loading shouldBe false
    component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
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

Ниже правила относятся именно к **интеграционным тестам с виртуальным временем**.

**Не используем блокирующие ожидания вроде:**

```kotlin
state.first { !it.loading }
```

Такие конструкции могут заблокировать тестовый поток и привести к дедлоку.

**Не используем Turbine для `StateFlow` в этом типе тестов.**

Не используем Turbine в интеграционных тестах, так как проверки через
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
integrationTest("emits pokemon details output when a pokemon is clicked") {
    // 🛠️ Prepare loaded pokemon list data
    mockServer.enqueue(
        RequestMatcher.containsPath("type/10"),
        HttpResponse(TestPokemons.firePokemonsJson())
    )
    val capturer = OutputCapturer<PokemonListComponent.Output>()
    val component = setupComponent { createPokemonListComponent(it, capturer) }
    advanceUntilIdle()

    // ▶️ Click a pokemon item in the list
    component.onPokemonClick(TestPokemons.detailedPonyta.id)

    // ✅ Verify the pokemon details output is emitted
    capturer.last shouldBe PokemonListComponent.Output.PokemonDetailsRequested(TestPokemons.detailedPonyta.id)
}
```

Для негативных сценариев используем:

```kotlin
capturer.isEmpty shouldBe true
```

---

## 9. Работа с тестовыми данными

Большие JSON-строки не хардкодим в Kotlin-коде. Моковые ответы должны храниться в
`features/src/commonTest/resources/responses/`.
Так тесты остаются читаемыми и ответы удобнее переиспользовать между сценариями.
Для загрузки используем централизованные helper-объекты и `readTestResource(...)` из
`core-testing`.

### Пример

```kotlin
object TestPokemons {
    suspend fun firePokemonsJson(): String = readTestResource("responses/fire_pokemons.json")

    val firePokemons = listOf(
        Pokemon(id = PokemonId("4"), name = "Charmander")
    )
}
```

### Использование

```kotlin
HttpResponse(TestPokemons.firePokemonsJson())
```

---

## 10. Минимальный набор сценариев для компонента

Для большинства обычных экранных компонентов ожидается, что набор интеграционных тестов покрывает
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

## 11. Полный пример интеграционных тестов

```kotlin
package ru.mobileup.template.features.pokemons.list

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import ru.mobileup.template.core_testing.network.HttpResponse
import ru.mobileup.template.core_testing.network.RequestMatcher
import ru.mobileup.template.core_testing.network.containsPath
import ru.mobileup.template.core_testing.utils.OutputCapturer
import ru.mobileup.template.features.pokemons.TestPokemons
import ru.mobileup.template.features.pokemons.createPokemonListComponent
import ru.mobileup.template.features.pokemons.domain.PokemonType
import ru.mobileup.template.features.pokemons.presentation.list.PokemonListComponent.Output
import ru.mobileup.template.features.utils.integrationTest

class PokemonListComponentTest : FunSpec({

    context("Pokemon list screen") {

        integrationTest("loads the default type pokemon list") {
            // 🛠️ Prepare default type pokemon list data
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }

            // ▶️ Wait for the initial loading to complete
            advanceUntilIdle()

            // ✅ Verify the default type list is loaded
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
            component.pokemonsState.value.error shouldBe null
        }

        integrationTest("emits pokemon details output when a pokemon is clicked") {
            // 🛠️ Prepare loaded pokemon list data
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val capturer = OutputCapturer<Output>()
            val component = setupComponent { createPokemonListComponent(it, capturer) }
            advanceUntilIdle()

            // ▶️ Click a pokemon item in the list
            component.onPokemonClick(TestPokemons.detailedPonyta.id)

            // ✅ Verify the pokemon details output is emitted
            capturer.last shouldBe Output.PokemonDetailsRequested(TestPokemons.detailedPonyta.id)
        }

        integrationTest("shows an error when initial loading fails") {
            // 🛠️ Prepare failed initial loading
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(status = HttpStatusCode.NotFound)
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }

            // ▶️ Wait for the failed loading to complete
            advanceUntilIdle()

            // ✅ Verify the error state is shown
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.error.shouldNotBeNull()
            component.pokemonsState.value.data shouldBe null
        }

        integrationTest("reloads pokemon list when refresh is requested after error") {
            // 🛠️ Prepare failed initial loading and successful retry
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(status = HttpStatusCode.NotFound),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // ▶️ Refresh loading the current list
            component.onRefresh()

            // ✅ Verify loading starts again
            component.pokemonsState.value.loading shouldBe true

            // ▶️ Wait for the retry loading to complete
            advanceUntilIdle()

            // ✅ Verify the list is loaded after retry
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.error shouldBe null
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
        }

        integrationTest("loads pokemon list for the selected type") {
            // 🛠️ Prepare default and selected pokemon list data
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            mockServer.enqueue(
                RequestMatcher.containsPath("type/11"),
                HttpResponse(TestPokemons.waterPokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // ▶️ Select another type and wait for its list to load
            component.onTypeClick(PokemonType.Water.id)
            advanceUntilIdle()

            // ✅ Verify the selected type and list are updated
            component.selectedTypeId.value shouldBe PokemonType.Water.id
            component.pokemonsState.value.data shouldBe TestPokemons.waterPokemons
        }

        integrationTest("shows loading during refresh and keeps current data") {
            // 🛠️ Prepare initial and refreshed pokemon list data
            mockServer.enqueue(
                RequestMatcher.containsPath("type/10"),
                HttpResponse(TestPokemons.firePokemonsJson()),
                HttpResponse(TestPokemons.firePokemonsJson())
            )
            val component = setupComponent { createPokemonListComponent(it, {}) }
            advanceUntilIdle()

            // ▶️ Refresh the current list
            component.onRefresh()

            // ✅ Verify loading is shown while keeping current data
            component.pokemonsState.value.loading shouldBe true
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons

            // ▶️ Wait for the refresh to complete
            advanceUntilIdle()

            // ✅ Verify loading is hidden and the current list is preserved
            component.pokemonsState.value.loading shouldBe false
            component.pokemonsState.value.data shouldBe TestPokemons.firePokemons
        }
    }
})
```

---

## 12. Границы применимости интеграционных тестов

Несмотря на удобство `integrationTest`, не стоит использовать его для всех сценариев. Поднятие
Koin-графа и MockServer — тяжелая инфраструктура. Сложную логику с множеством ветвлений — мапперы,
валидацию, расчеты, краевые случаи — нужно покрывать быстрыми юнит-тестами через обычный `test`,
прямое создание объекта и fake-зависимости. 
