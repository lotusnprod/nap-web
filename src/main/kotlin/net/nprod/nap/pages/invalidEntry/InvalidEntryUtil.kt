package net.nprod.nap.pages.invalidEntry

/**
 * Utility class for invalid entry pages
 */
object InvalidEntryUtil {
    /**
     * Create an invalid entry page
     * 
     * @param type The entity type
     * @param value The invalid value
     * @return HTML string representation of the invalid entry page
     */
    fun createInvalidEntryPage(type: String, value: String): String {
        val data = InvalidEntryViewData(type, value)
        return InvalidEntryView.render(data)
    }
}