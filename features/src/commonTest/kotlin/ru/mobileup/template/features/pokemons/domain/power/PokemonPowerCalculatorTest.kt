package ru.mobileup.template.features.pokemons.domain.power

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.mobileup.template.features.pokemons.domain.PokemonBaseStats
import ru.mobileup.template.features.pokemons.domain.PowerTier

class PokemonPowerCalculatorTest : FunSpec({

    context("Pokemon power calculation") {

        test("calculates score for Pikachu-like stats") {
            // Prepare Pikachu-like base stats
            val stats = PokemonBaseStats(
                hp = 35,
                attack = 55,
                defense = 40,
                specialAttack = 50,
                specialDefense = 50,
                speed = 90
            )

            // Calculate power score
            val score = PokemonPowerCalculator.calculate(stats)

            // Verify score parts and resulting tier
            score.offense shouldBe 60
            score.defense shouldBe 41
            score.mobility shouldBe 90
            score.total shouldBe 58
            score.tier shouldBe PowerTier.D
        }

        test("calculates score for Charizard-like stats") {
            // Prepare Charizard-like base stats
            val stats = PokemonBaseStats(
                hp = 78,
                attack = 84,
                defense = 78,
                specialAttack = 109,
                specialDefense = 85,
                speed = 100
            )

            // Calculate power score
            val score = PokemonPowerCalculator.calculate(stats)

            // Verify score parts and resulting tier
            score.offense shouldBe 96
            score.defense shouldBe 80
            score.mobility shouldBe 100
            score.total shouldBe 91
            score.tier shouldBe PowerTier.B
        }

        test("calculates score for Mewtwo-like stats") {
            // Prepare Mewtwo-like base stats
            val stats = PokemonBaseStats(
                hp = 106,
                attack = 110,
                defense = 90,
                specialAttack = 154,
                specialDefense = 90,
                speed = 130
            )

            // Calculate power score
            val score = PokemonPowerCalculator.calculate(stats)

            // Verify score parts and resulting tier
            score.offense shouldBe 129
            score.defense shouldBe 96
            score.mobility shouldBe 130
            score.total shouldBe 118
            score.tier shouldBe PowerTier.S
        }

        test("maps exact boundary totals to expected tiers") {
            // Prepare boundary totals and expected tiers
            val expectedByTotal = listOf(
                39 to PowerTier.E,
                40 to PowerTier.D,
                59 to PowerTier.D,
                60 to PowerTier.C,
                79 to PowerTier.C,
                80 to PowerTier.B,
                99 to PowerTier.B,
                100 to PowerTier.A,
                114 to PowerTier.A,
                115 to PowerTier.S
            )

            // Verify each boundary total maps to the expected tier
            expectedByTotal.forEach { (expectedTotal, expectedTier) ->
                val stats = PokemonBaseStats(
                    hp = expectedTotal,
                    attack = expectedTotal,
                    defense = expectedTotal,
                    specialAttack = expectedTotal,
                    specialDefense = expectedTotal,
                    speed = expectedTotal
                )
                val score = PokemonPowerCalculator.calculate(stats)

                score.total shouldBe expectedTotal
                score.tier shouldBe expectedTier
            }
        }

        test("throws on invalid stats below range") {
            // Prepare stats with hp below allowed range
            val error = shouldThrow<IllegalArgumentException> {
                PokemonPowerCalculator.calculate(
                    PokemonBaseStats(
                        hp = 0,
                        attack = 55,
                        defense = 40,
                        specialAttack = 50,
                        specialDefense = 50,
                        speed = 90
                    )
                )
            }

            // Verify validation message for the invalid stat
            error.message shouldBe "hp must be in 1..255, was 0"
        }

        test("throws on invalid stats above range") {
            // Prepare stats with speed above allowed range
            val error = shouldThrow<IllegalArgumentException> {
                PokemonPowerCalculator.calculate(
                    PokemonBaseStats(
                        hp = 35,
                        attack = 55,
                        defense = 40,
                        specialAttack = 50,
                        specialDefense = 50,
                        speed = 300
                    )
                )
            }

            // Verify validation message for the invalid stat
            error.message shouldBe "speed must be in 1..255, was 300"
        }

        test("is deterministic for identical input") {
            // Prepare stable input stats
            val stats = PokemonBaseStats(
                hp = 78,
                attack = 84,
                defense = 78,
                specialAttack = 109,
                specialDefense = 85,
                speed = 100
            )

            // Calculate score twice for the same input
            val first = PokemonPowerCalculator.calculate(stats)
            val second = PokemonPowerCalculator.calculate(stats)

            // Verify results are identical
            first shouldBe second
        }
    }
})
