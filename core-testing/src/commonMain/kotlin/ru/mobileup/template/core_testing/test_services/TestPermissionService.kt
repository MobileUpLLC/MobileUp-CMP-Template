package ru.mobileup.template.core_testing.test_services

import ru.mobileup.template.core.permissions.Permission
import ru.mobileup.template.core.permissions.PermissionResult
import ru.mobileup.template.core.permissions.PermissionService

/**
 * Test implementation of [ru.mobileup.template.core.permissions.PermissionService].
 *
 * Allows tests to control the permission result.
 */
class TestPermissionService : PermissionService {

    var result: PermissionResult = PermissionResult.Granted

    override suspend fun requestPermission(permission: Permission): PermissionResult {
        return result
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean {
        return result == PermissionResult.Granted
    }
}