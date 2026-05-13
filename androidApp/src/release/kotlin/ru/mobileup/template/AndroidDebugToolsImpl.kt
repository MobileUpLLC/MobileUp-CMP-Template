package ru.mobileup.template

import android.content.Context
import me.aartikov.replica.client.ReplicaClient
import okhttp3.Interceptor

@Suppress("UNUSED_PARAMETER")
class AndroidDebugToolsImpl(context: Context) : AndroidDebugTools {

    override val interceptors: List<Interceptor> = emptyList()

    override fun launch(replicaClient: ReplicaClient) {
        // do nothing
    }
}