package ru.mobileup.template.features.places.data

import me.aartikov.replica.single.Replica
import ru.mobileup.template.features.places.domain.Place

interface PlacesRepository {
    val placesReplica: Replica<List<Place>>
}
