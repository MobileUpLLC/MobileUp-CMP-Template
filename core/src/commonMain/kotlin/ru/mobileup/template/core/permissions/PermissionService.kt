package ru.mobileup.template.core.permissions

interface PermissionService {

    suspend fun requestPermission(permission: Permission): PermissionResult

    suspend fun isPermissionGranted(permission: Permission): Boolean
}
