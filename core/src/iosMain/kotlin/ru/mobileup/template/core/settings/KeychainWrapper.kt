/*
 * Copyright 2020 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(
    UnsafeNumber::class, // Due to CFIndex usage.
    ExperimentalForeignApi::class // Due to lots of cinterop internals (and one public-facing constructor)
)

package ru.mobileup.template.core.settings

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFArrayGetCount
import platform.CoreFoundation.CFArrayGetValueAtIndex
import platform.CoreFoundation.CFArrayRefVar
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryGetValue
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanFalse
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSKeyedUnarchiver
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.numberWithBool
import platform.Foundation.numberWithDouble
import platform.Foundation.numberWithFloat
import platform.Foundation.numberWithInt
import platform.Foundation.numberWithLongLong
import platform.Security.SecCopyErrorMessageString
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecDuplicateItem
import platform.Security.errSecItemNotFound
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitAll
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnAttributes
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.Cleaner
import kotlin.native.ref.createCleaner

internal class KeychainWrapper(service: String) {

    @OptIn(ExperimentalNativeApi::class)
    private val cleaner: Cleaner?

    private val defaultProperties: Map<CFStringRef?, CFTypeRef?>

    init {
        val cfService = CFBridgingRetain(service)
        defaultProperties =
            mapOf(kSecClass to kSecClassGenericPassword, kSecAttrService to cfService)
        @OptIn(ExperimentalNativeApi::class)
        cleaner = createCleaner(cfService) { CFBridgingRelease(it) }
    }

    private val keys: Set<String>
        get() = memScoped {
            val attributes = alloc<CFArrayRefVar>()
            val status = keyChainOperation(
                kSecMatchLimit to kSecMatchLimitAll,
                kSecReturnAttributes to kCFBooleanTrue
            ) { SecItemCopyMatching(it, attributes.ptr.reinterpret()) }
            status.checkError(errSecItemNotFound)
            if (status == errSecItemNotFound) {
                return emptySet()
            }

            return buildSet {
                for (i in 0..<CFArrayGetCount(attributes.value)) {
                    val item: CFDictionaryRef? =
                        CFArrayGetValueAtIndex(attributes.value, i.convert())?.reinterpret()
                    val cfKey: CFStringRef? =
                        CFDictionaryGetValue(item, kSecAttrAccount)?.reinterpret()
                    if (cfKey != null) {
                        val nsKey = CFBridgingRelease(cfKey) as NSString
                        add(nsKey.toKString())
                    }
                }
            }
        }

    fun clear(): Unit = keys.forEach { remove(it) }

    fun remove(key: String): Unit = removeKeychainItem(key)

    fun putInt(key: String, value: Int): Unit =
        addOrUpdateKeychainItem(key, archiveNumber(NSNumber.numberWithInt(value)))

    fun getInt(key: String): Int? = unarchiveNumber(getKeychainItem(key))?.intValue

    fun putLong(key: String, value: Long): Unit =
        addOrUpdateKeychainItem(key, archiveNumber(NSNumber.numberWithLongLong(value)))

    fun getLong(key: String): Long? = unarchiveNumber(getKeychainItem(key))?.longLongValue

    fun putString(key: String, value: String): Unit =
        addOrUpdateKeychainItem(key, value.toNSString().dataUsingEncoding(NSUTF8StringEncoding))

    @OptIn(BetaInteropApi::class)
    fun getString(key: String): String? =
        getKeychainItem(key)?.let { NSString.create(it, NSUTF8StringEncoding)?.toKString() }

    fun putFloat(key: String, value: Float): Unit =
        addOrUpdateKeychainItem(key, archiveNumber(NSNumber.numberWithFloat(value)))

    fun getFloat(key: String): Float? = unarchiveNumber(getKeychainItem(key))?.floatValue

    fun putDouble(key: String, value: Double): Unit =
        addOrUpdateKeychainItem(key, archiveNumber(NSNumber.numberWithDouble(value)))

    fun getDouble(key: String): Double? = unarchiveNumber(getKeychainItem(key))?.doubleValue

    fun putBoolean(key: String, value: Boolean): Unit =
        addOrUpdateKeychainItem(key, archiveNumber(NSNumber.numberWithBool(value)))

    fun getBoolean(key: String): Boolean? = unarchiveNumber(getKeychainItem(key))?.boolValue

    private fun unarchiveNumber(data: NSData?): NSNumber? =
        data?.let { NSKeyedUnarchiver.unarchiveObjectWithData(it) } as? NSNumber

    private fun archiveNumber(number: NSNumber): NSData? =
        NSKeyedArchiver.archivedDataWithRootObject(number, true, null)

    private fun addOrUpdateKeychainItem(key: String, value: NSData?) {
        if (!addKeychainItem(key, value)) {
            updateKeychainItem(key, value)
        }
    }

    private fun addKeychainItem(key: String, value: NSData?): Boolean =
        cfRetain(key, value) { cfKey, cfValue ->
            val status = keyChainOperation(
                kSecAttrAccount to cfKey,
                kSecValueData to cfValue
            ) { SecItemAdd(it, null) }
            status.checkError(errSecDuplicateItem)

            status != errSecDuplicateItem
        }

    private fun removeKeychainItem(key: String): Unit = cfRetain(key) { cfKey ->
        val status = keyChainOperation(
            kSecAttrAccount to cfKey,
        ) { SecItemDelete(it) }
        status.checkError(errSecItemNotFound)
    }

    private fun updateKeychainItem(key: String, value: NSData?): Unit =
        cfRetain(key, value) { cfKey, cfValue ->
            val status = keyChainOperation(
                kSecAttrAccount to cfKey,
                kSecReturnData to kCFBooleanFalse
            ) {
                val attributes = cfDictionaryOf(kSecValueData to cfValue)
                val output = SecItemUpdate(it, attributes)
                CFBridgingRelease(attributes)
                output
            }
            status.checkError()
        }

    private fun getKeychainItem(key: String): NSData? = cfRetain(key) { cfKey ->
        val cfValue = alloc<CFTypeRefVar>()
        val status = keyChainOperation(
            kSecAttrAccount to cfKey,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        ) { SecItemCopyMatching(it, cfValue.ptr) }
        status.checkError(errSecItemNotFound)
        if (status == errSecItemNotFound) {
            return@cfRetain null
        }
        CFBridgingRelease(cfValue.value) as? NSData
    }

    private fun hasKeychainItem(key: String): Boolean = cfRetain(key) { cfKey ->
        val status = keyChainOperation(
            kSecAttrAccount to cfKey,
            kSecMatchLimit to kSecMatchLimitOne
        ) { SecItemCopyMatching(it, null) }

        status != errSecItemNotFound
    }

    private inline fun MemScope.keyChainOperation(
        vararg input: Pair<CFStringRef?, CFTypeRef?>,
        operation: (query: CFDictionaryRef?) -> OSStatus,
    ): OSStatus {
        val query = cfDictionaryOf(defaultProperties + mapOf(*input))
        val output = operation(query)
        CFBridgingRelease(query)
        return output
    }

    private fun OSStatus.checkError(vararg expectedErrors: OSStatus) {
        if (this != 0 && this !in expectedErrors) {
            val cfMessage = SecCopyErrorMessageString(this, null)
            val nsMessage = CFBridgingRelease(cfMessage) as? NSString
            val message = nsMessage?.toKString() ?: "Unknown error"
            error("Keychain error $this: $message")
        }
    }
}

internal fun MemScope.cfDictionaryOf(vararg items: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? =
    cfDictionaryOf(mapOf(*items))

internal fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
    val size = map.size
    val keys = allocArrayOf(*map.keys.toTypedArray())
    val values = allocArrayOf(*map.values.toTypedArray())
    return CFDictionaryCreate(
        kCFAllocatorDefault,
        keys.reinterpret(),
        values.reinterpret(),
        size.convert(),
        null,
        null
    )
}

// Turn casts into dot calls for better readability
@Suppress("CAST_NEVER_SUCCEEDS")
internal fun String.toNSString() = this as NSString

@Suppress("CAST_NEVER_SUCCEEDS")
internal fun NSString.toKString() = this as String

internal inline fun <T> cfRetain(value: Any?, block: MemScope.(CFTypeRef?) -> T): T = memScoped {
    val cfValue = CFBridgingRetain(value)
    return try {
        block(cfValue)
    } finally {
        CFBridgingRelease(cfValue)
    }
}

internal inline fun <T> cfRetain(
    value1: Any?,
    value2: Any?,
    block: MemScope.(CFTypeRef?, CFTypeRef?) -> T
): T =
    memScoped {
        val cfValue1 = CFBridgingRetain(value1)
        val cfValue2 = CFBridgingRetain(value2)
        return try {
            block(cfValue1, cfValue2)
        } finally {
            CFBridgingRelease(cfValue1)
            CFBridgingRelease(cfValue2)
        }
    }
