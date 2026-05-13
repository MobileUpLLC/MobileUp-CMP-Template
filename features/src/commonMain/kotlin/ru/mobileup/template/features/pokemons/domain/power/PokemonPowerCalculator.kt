package ru.mobileup.template.features.pokemons.domain.power

import ru.mobileup.template.features.pokemons.domain.PokemonBaseStats
import ru.mobileup.template.features.pokemons.domain.PokemonPowerScore
import ru.mobileup.template.features.pokemons.domain.PowerTier
import kotlin.math.roundToInt

object PokemonPowerCalculator {

    private val STAT_RANGE = 1..255

    fun calculate(stats: PokemonBaseStats): PokemonPowerScore {
        validateStats(stats)

        val offenseRaw = 0.45 * stats.attack + 0.35 * stats.specialAttack + 0.20 * stats.speed
        val defenseRaw = 0.40 * stats.hp + 0.30 * stats.defense + 0.30 * stats.specialDefense
        val mobilityRaw = stats.speed.toDouble()

        val offense = offenseRaw.roundToInt()
        val defense = defenseRaw.roundToInt()
        val mobility = mobilityRaw.roundToInt()

        val totalRaw = 0.50 * offenseRaw + 0.35 * defenseRaw + 0.15 * mobilityRaw
        val total = totalRaw.roundToInt()

        return PokemonPowerScore(
            total = total,
            offense = offense,
            defense = defense,
            mobility = mobility,
            tier = total.toPowerTier()
        )
    }

    private fun validateStats(stats: PokemonBaseStats) {
        checkStat("hp", stats.hp)
        checkStat("attack", stats.attack)
        checkStat("defense", stats.defense)
        checkStat("specialAttack", stats.specialAttack)
        checkStat("specialDefense", stats.specialDefense)
        checkStat("speed", stats.speed)
    }

    private fun checkStat(statName: String, value: Int) {
        require(value in STAT_RANGE) {
            "$statName must be in ${STAT_RANGE.first}..${STAT_RANGE.last}, was $value"
        }
    }

    private fun Int.toPowerTier(): PowerTier {
        return when (this) {
            in 0..39 -> PowerTier.E
            in 40..59 -> PowerTier.D
            in 60..79 -> PowerTier.C
            in 80..99 -> PowerTier.B
            in 100..114 -> PowerTier.A
            else -> PowerTier.S
        }
    }
}
