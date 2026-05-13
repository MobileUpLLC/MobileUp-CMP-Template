package ru.mobileup.template.core.common_data

import me.aartikov.replica.paged.Page

data class SimplePage<out I : Any>(
    override val items: List<I>,
    override val hasNextPage: Boolean,
    override val hasPreviousPage: Boolean = false
) : Page<I> {
    companion object {
        fun <I : Any> emptyPage() = SimplePage(emptyList(), false)
    }
}

data class PageWithTotalAmount<out I : Any>(
    override val items: List<I>,
    val totalAmount: Int,
    override val hasNextPage: Boolean,
    override val hasPreviousPage: Boolean = false
) : Page<I> {
    companion object {
        fun <I : Any> emptyPage() = PageWithTotalAmount(emptyList(), 0, false)
    }
}
