package net.nprod.nap.pages

import kotlinx.html.ButtonType
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.UL
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.li

/**
 * Positions of the steps of the guided tour.
 *
 * The tour is assembled at runtime from whatever steps are present and visible on the
 * current page, ordered by these numbers, so a page only has to mark the elements it has.
 * Leave gaps when adding steps rather than renumbering everything.
 */
object TourStep {
    const val HOME_INTRO = 10
    const val SEARCH_TYPE = 20
    const val SEARCH_INPUT = 30
    const val SEARCH_SUBMIT = 40
    const val FACETS = 50
    const val SPARQL = 60
    const val FAQ = 70
}

/**
 * Mark an element as a step of the guided tour.
 *
 * The tour is off unless the visitor turns it on from the navbar, so these attributes are
 * inert for everyone else. See assets/js/tutorial.js for the engine that reads them.
 *
 * @param step Position in the tour, from [TourStep]
 * @param title Short heading of the step
 * @param body What the visitor should do or understand here
 * @param shared True for steps that describe something present on every page, such as the
 *               navbar. Repeating those on every page the visitor opens is noise, so they
 *               only run on the home page and on the page where the tour was turned on.
 */
fun CommonAttributeGroupFacade.tourStep(step: Int, title: String, body: String, shared: Boolean = false) {
    attributes["data-tour-step"] = step.toString()
    attributes["data-tour-title"] = title
    attributes["data-tour-body"] = body
    if (shared) {
        attributes["data-tour-shared"] = "true"
    }
}

/**
 * Navbar control that turns the guided tour on and off.
 *
 * The label is filled in by the engine, which is the only place that knows whether the
 * tour is currently on: that state lives in the browser, not on the server.
 */
fun UL.tutorialToggle() {
    li(classes = "nav-item") {
        button(type = ButtonType.button, classes = "nav-link btn btn-link") {
            id = "tutorial-toggle"
            attributes["aria-pressed"] = "false"
            +"Tutorial"
        }
    }
}
