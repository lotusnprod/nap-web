import kotlinx.html.*
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import net.nprod.nap.pages.defaultPage
import net.nprod.nap.pages.genericHead
import net.nprod.nap.pages.navbar
import net.nprod.nap.pages.home.HomeController

/**
 * @deprecated Use HomeController instead
 */
@Deprecated("Use HomeController instead", ReplaceWith("HomeController"))
fun homePage(): String {
    return defaultPage()
}
