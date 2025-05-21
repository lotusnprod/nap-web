package net.nprod.nap.pages.home

import net.nprod.nap.pages.defaultPage

/**
 * View for the home page
 */
object HomeView {
    /**
     * Render the home page HTML
     * @param data The view data for the home page
     * @return HTML string of the home page
     */
    fun render(data: HomeViewData): String {
        return defaultPage(data.title)
    }
}