package ru.mobileup.template.core.permissions

sealed interface PermissionResult {
    /**
     * Permission has been granted by user
     */
    data object Granted : PermissionResult

    sealed interface Denied : PermissionResult {
        /**
         * User denied permission in the system dialog.
         * Permission can be requested again later.
         */
        data object TemporarilyByUser : Denied

        /**
         * User denied permission in the system dialog,
         * and future requests are impossible.
         *
         * Common on iOS after first denial, possible on Android after "Don't ask again"
         */
        data object PermanentlyByUser : Denied

        /**
         * Permission was already permanently denied before this request.
         * System dialog was not shown.
         */
        data object PermanentlyWithoutPrompt : Denied
    }
}
