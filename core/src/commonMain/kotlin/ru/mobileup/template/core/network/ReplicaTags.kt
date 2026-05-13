package ru.mobileup.template.core.network

import me.aartikov.replica.common.ReplicaTag

object ReplicaTags {
    /**
    Помечайте этим тегом реплики, которые связаны с учеткой пользователя.
    При смене учетки они чистятся и, если у них есть наблюдатель, загружают данные вновь.
     */
    object UserSpecificData : ReplicaTag
}
