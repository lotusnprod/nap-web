import java.net.URLEncoder
import java.nio.charset.StandardCharsets


fun generatePharmacyTaxaSearchURL(family: String?, genus: String?, species: String?): String {
    val params = mutableListOf<String>()

    if (family != null && family != "") {
        val encodedFamily = URLEncoder.encode(family, StandardCharsets.UTF_8.toString())
        params.add("family=$encodedFamily")
    }

    if (genus != null) {
        val encodedGenus = URLEncoder.encode(genus, StandardCharsets.UTF_8.toString())
        params.add("genus=$encodedGenus")
    }

    if (species != null) {
        val encodedSpecies = URLEncoder.encode(species, StandardCharsets.UTF_8.toString())
        params.add("species=$encodedSpecies")
    }

    return "/pharmacy_search?" + params.joinToString("&")
}
