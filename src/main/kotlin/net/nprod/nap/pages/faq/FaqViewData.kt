package net.nprod.nap.pages.faq

/**
 * Data class for the FAQ page
 *
 * The FAQ is static content, so the only thing that varies is the page title.
 */
data class FaqViewData(
    val title: String = "FAQ"
)