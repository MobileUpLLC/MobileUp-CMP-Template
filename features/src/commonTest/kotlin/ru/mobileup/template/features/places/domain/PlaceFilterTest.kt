package ru.mobileup.template.features.places.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import ru.mobileup.template.core.location.GeoCoordinate

class PlaceFilterTest : FunSpec({

    withTests(
        nameFn = { "keeps only places inside Moscow region: ${it.name}" },
        ts = listOf(
            PlaceFilterCase(
                name = "Moscow center",
                places = listOf(place("moscow-center", GeoCoordinate.KREMLIN)),
                expectedPlaces = listOf(place("moscow-center", GeoCoordinate.KREMLIN))
            ),
            PlaceFilterCase(
                name = "latitude boundary",
                places = listOf(
                    place(
                        id = "latitude-boundary",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude + 1,
                            longitude = GeoCoordinate.KREMLIN.longitude
                        )
                    )
                ),
                expectedPlaces = listOf(
                    place(
                        id = "latitude-boundary",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude + 1,
                            longitude = GeoCoordinate.KREMLIN.longitude
                        )
                    )
                )
            ),
            PlaceFilterCase(
                name = "longitude boundary",
                places = listOf(
                    place(
                        id = "longitude-boundary",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude,
                            longitude = GeoCoordinate.KREMLIN.longitude - 1
                        )
                    )
                ),
                expectedPlaces = listOf(
                    place(
                        id = "longitude-boundary",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude,
                            longitude = GeoCoordinate.KREMLIN.longitude - 1
                        )
                    )
                )
            ),
            PlaceFilterCase(
                name = "outside latitude",
                places = listOf(
                    place(
                        id = "outside-latitude",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude + 1.000001,
                            longitude = GeoCoordinate.KREMLIN.longitude
                        )
                    )
                ),
                expectedPlaces = emptyList()
            ),
            PlaceFilterCase(
                name = "outside longitude",
                places = listOf(
                    place(
                        id = "outside-longitude",
                        coordinate = GeoCoordinate(
                            latitude = GeoCoordinate.KREMLIN.latitude,
                            longitude = GeoCoordinate.KREMLIN.longitude - 1.000001
                        )
                    )
                ),
                expectedPlaces = emptyList()
            ),
            PlaceFilterCase(
                name = "mixed list",
                places = listOf(
                    place("moscow-center", GeoCoordinate.KREMLIN),
                    place(
                        "outside-latitude",
                        GeoCoordinate(
                            GeoCoordinate.KREMLIN.latitude + 1.000001,
                            GeoCoordinate.KREMLIN.longitude
                        )
                    ),
                    place(
                        "outside-longitude",
                        GeoCoordinate(
                            GeoCoordinate.KREMLIN.latitude,
                            GeoCoordinate.KREMLIN.longitude - 1.000001
                        )
                    )
                ),
                expectedPlaces = listOf(place("moscow-center", GeoCoordinate.KREMLIN))
            )
        )
    ) { case ->
        // 🛠️ Prepare places with coordinates for the selected case
        val places = case.places

        // ▶️ Filter places by Moscow region
        val filteredPlaces = places.filterInMoscowRegion()

        // ✅ Verify only places inside the Moscow region remain
        filteredPlaces shouldBe case.expectedPlaces
    }
})

private data class PlaceFilterCase(
    val name: String,
    val places: List<Place>,
    val expectedPlaces: List<Place>
)

private fun place(id: String, coordinate: GeoCoordinate): Place {
    return Place(
        id = PlaceId(id),
        name = id,
        coordinate = coordinate
    )
}
