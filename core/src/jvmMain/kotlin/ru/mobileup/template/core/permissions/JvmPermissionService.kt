package ru.mobileup.template.core.permissions

class JvmPermissionService : PermissionService {

    override suspend fun requestPermission(permission: Permission): PermissionResult {
        return PermissionResult.Denied.PermanentlyWithoutPrompt
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return false
    }
}
