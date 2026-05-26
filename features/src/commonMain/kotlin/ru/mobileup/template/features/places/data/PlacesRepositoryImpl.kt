package ru.mobileup.template.features.places.data

import kotlinx.coroutines.delay
import me.aartikov.replica.client.ReplicaClient
import me.aartikov.replica.single.Replica
import me.aartikov.replica.single.ReplicaSettings
import ru.mobileup.template.features.places.domain.Place
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class PlacesRepositoryImpl(
    replicaClient: ReplicaClient
) : PlacesRepository {

    override val placesReplica: Replica<List<Place>> = replicaClient.createReplica(
        name = "places",
        settings = ReplicaSettings(staleTime = 1.hours),
        fetcher = {
            delay(2.seconds)
            Place.MOCKS
        }
    )
}
