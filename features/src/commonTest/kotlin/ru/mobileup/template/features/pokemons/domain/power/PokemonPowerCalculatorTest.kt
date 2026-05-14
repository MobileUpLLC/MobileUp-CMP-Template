package ru.mobileup.template.features.pokemons.domain.power

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import ru.mobileup.template.features.pokemons.domain.PokemonBaseStats
import ru.mobileup.template.features.pokemons.domain.PokemonPowerScore
import ru.mobileup.template.features.pokemons.domain.PowerTier

class PokemonPowerCalculatorTest : FunSpec({

    withTests(
        nameFn = { "calculates score for ${it.name} stats" },
        ts = listOf(
            ScoreCalculationCase(
                name = "Pikachu",
                stats = PokemonBaseStats(
                    hp = 35,
                    attack = 55,
                    defense = 40,
                    specialAttack = 50,
                    specialDefense = 50,
                    speed = 90
                ),
                expectedScore = PokemonPowerScore(
                    offense = 60,
                    defense = 41,
                    mobility = 90,
                    total = 58,
                    tier = PowerTier.D
                )
            ),
            ScoreCalculationCase(
                name = "Charizard",
                stats = PokemonBaseStats(
                    hp = 78,
                    attack = 84,
                    defense = 78,
                    specialAttack = 109,
                    specialDefense = 85,
                    speed = 100
                ),
                expectedScore = PokemonPowerScore(
                    offense = 96,
                    defense = 80,
                    mobility = 100,
                    total = 91,
                    tier = PowerTier.B
                )
            ),
            ScoreCalculationCase(
                name = "Mewtwo",
                stats = PokemonBaseStats(
                    hp = 106,
                    attack = 110,
                    defense = 90,
                    specialAttack = 154,
                    specialDefense = 90,
                    speed = 130
                ),
                expectedScore = PokemonPowerScore(
                    offense = 129,
                    defense = 96,
                    mobility = 130,
                    total = 118,
                    tier = PowerTier.S
                )
            )
        )
    ) { case ->
        // 🛠️ Prepare pokemon stats
        val stats = case.stats

        // ▶️ Calculate pokemon power score
        val actualScore = PokemonPowerCalculator.calculate(stats)

        // ✅ Verify calculated pokemon power score
        actualScore shouldBe case.expectedScore
    }

    test("throws on invalid stats below range") {
        // 🛠️ Prepare stats with hp below allowed range
        val stats = PokemonBaseStats(
            hp = 0,
            attack = 55,
            defense = 40,
            specialAttack = 50,
            specialDefense = 50,
            speed = 90
        )

        // ▶️ Calculate pokemon power score
        val error = shouldThrow<IllegalArgumentException> {
            PokemonPowerCalculator.calculate(stats)
        }

        // ✅ Verify validation message for the invalid stat
        error.message shouldBe "hp must be in 1..255, was 0"
    }

    test("throws on invalid stats above range") {
        // 🛠️ Prepare stats with speed above allowed range
        val stats = PokemonBaseStats(
            hp = 35,
            attack = 55,
            defense = 40,
            specialAttack = 50,
            specialDefense = 50,
            speed = 300
        )

        // ▶️ Calculate pokemon power score
        val error = shouldThrow<IllegalArgumentException> {
            PokemonPowerCalculator.calculate(stats)
        }

        // ✅ Verify validation message for the invalid stat
        error.message shouldBe "speed must be in 1..255, was 300"
    }
})

private data class ScoreCalculationCase(
    val name: String,
    val stats: PokemonBaseStats,
    val expectedScore: PokemonPowerScore
)
