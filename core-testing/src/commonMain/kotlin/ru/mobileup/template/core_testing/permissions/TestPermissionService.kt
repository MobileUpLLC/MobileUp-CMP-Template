package ru.mobileup.template.core_testing.permissions

import ru.mobileup.template.core.permissions.Permission
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.permissions.PermissionService

class TestPermissionService : PermissionService {

    var result: PermissionResult = PermissionResult.Granted

    override suspend fun requestPermission(permission: Permission): PermissionResult {
        return result
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return result == PermissionResult.Granted
    }
}
