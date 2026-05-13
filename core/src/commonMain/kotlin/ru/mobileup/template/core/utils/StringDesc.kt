@file:Suppress("FunctionName")

package ru.mobileup.template.core.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import ru.mobileup.kmm_form_validation.validation.control.ValidationError

/**
 * Localized string descriptor for Compose Multiplatform Resources.
 */
interface StringDesc : ValidationError {

    /**
     * Resolve using the current Compose resource environment.
     */
    @Composable
    fun resolve(): String

    /**
     * Resolve outside Compose.
     */
    suspend fun resolveString(): String

    companion object
}

/**
 * Empty string singleton.
 */
private object EmptyStringDesc : StringDesc {
    @Composable
    override fun resolve(): String = ""

    override suspend fun resolveString(): String = ""
}

/**
 * Raw hardcoded text.
 */
private data class RawStringDesc(
    val value: CharSequence
) : StringDesc {
    @Composable
    override fun resolve(): String = value.toString()

    override suspend fun resolveString(): String = value.toString()
}

/**
 * String resource with optional format args.
 *
 * Args may contain nested StringDesc values.
 */
private data class ResourceStringDesc(
    val resource: StringResource,
    val args: List<Any>
) : StringDesc {
    @Composable
    override fun resolve(): String {
        val resolvedArgs = args.resolveArgsInCompose()
        return if (resolvedArgs.isEmpty()) {
            stringResource(resource)
        } else {
            stringResource(resource, *resolvedArgs)
        }
    }

    override suspend fun resolveString(): String {
        val resolvedArgs = args.resolveArgsSuspend()
        return if (resolvedArgs.isEmpty()) {
            getString(resource)
        } else {
            getString(resource, *resolvedArgs)
        }
    }
}

/**
 * Plural resource with quantity and optional format args.
 *
 * Args may contain nested StringDesc values.
 */
private data class PluralStringDesc(
    val resource: PluralStringResource,
    val quantity: Int,
    val args: List<Any>
) : StringDesc {
    @Composable
    override fun resolve(): String {
        val resolvedArgs = args.resolveArgsInCompose()
        return if (resolvedArgs.isEmpty()) {
            pluralStringResource(resource, quantity)
        } else {
            pluralStringResource(resource, quantity, *resolvedArgs)
        }
    }

    override suspend fun resolveString(): String {
        val resolvedArgs = args.resolveArgsSuspend()
        return if (resolvedArgs.isEmpty()) {
            getPluralString(resource, quantity)
        } else {
            getPluralString(resource, quantity, *resolvedArgs)
        }
    }
}

/**
 * Concatenation of multiple StringDesc values.
 */
private data class CompositionStringDesc(
    val parts: List<StringDesc>,
    val separator: String = ""
) : StringDesc {
    @Composable
    override fun resolve(): String {
        val resolvedParts = ArrayList<String>(parts.size)
        for (part in parts) {
            resolvedParts += part.resolve()
        }
        return resolvedParts.joinToString(separator)
    }

    override suspend fun resolveString(): String {
        return parts
            .map { it.resolveString() }
            .joinToString(separator = separator)
    }
}

/* ---------- factories on StringDesc.Companion ---------- */

fun StringDesc.Companion.Empty(): StringDesc = EmptyStringDesc

fun StringDesc.Companion.Raw(value: CharSequence): StringDesc =
    if (value.isEmpty()) EmptyStringDesc else RawStringDesc(value)

fun StringDesc.Companion.Resource(
    resource: StringResource,
    vararg args: Any
): StringDesc = ResourceStringDesc(resource, args.toList())

fun StringDesc.Companion.Plural(
    resource: PluralStringResource,
    quantity: Int,
    vararg args: Any
): StringDesc = PluralStringDesc(resource, quantity, args.toList())

fun StringDesc.Companion.Composition(
    parts: List<StringDesc>,
    separator: String = ""
): StringDesc {
    return when (parts.size) {
        0 -> EmptyStringDesc
        1 -> parts.first()
        else -> CompositionStringDesc(parts, separator)
    }
}

/* ---------- convenience extensions ---------- */

fun String.desc(): StringDesc = StringDesc.Raw(this)

fun StringResource.resourceDesc(vararg args: Any): StringDesc = StringDesc.Resource(this, *args)

fun PluralStringResource.pluralDesc(quantity: Int, vararg args: Any): StringDesc =
    StringDesc.Plural(this, quantity, *args)

operator fun StringDesc.plus(other: StringDesc): StringDesc {
    val left = when (this) {
        is CompositionStringDesc -> this.parts
        else -> listOf(this)
    }
    val right = when (other) {
        is CompositionStringDesc -> other.parts
        else -> listOf(other)
    }
    return StringDesc.Composition(left + right)
}

fun List<StringDesc>.joinToStringDesc(separator: String = ", "): StringDesc =
    StringDesc.Composition(this, separator)

/* ---------- private helpers ---------- */

@Composable
private fun List<Any>.resolveArgsInCompose(): Array<Any> {
    return map { arg ->
        when (arg) {
            is StringDesc -> arg.resolve()
            else -> arg
        }
    }.toTypedArray()
}

private suspend fun List<Any>.resolveArgsSuspend(): Array<Any> {
    return map { arg ->
        when (arg) {
            is StringDesc -> arg.resolveString()
            else -> arg
        }
    }.toTypedArray()
}
