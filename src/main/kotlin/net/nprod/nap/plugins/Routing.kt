package net.nprod.nap.plugins

import PartConditionPage
import compoundPage
import countryPage
import geographicalAreaPage
import homePage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.nprod.nap.pages.animalPage
import net.nprod.nap.pages.citationPage
import net.nprod.nap.pages.compoundCodePage
import net.nprod.nap.pages.extractPage
import net.nprod.nap.pages.naiveDataPage
import net.nprod.nap.pages.organismPage
import net.nprod.nap.pages.organismSearchPage
import net.nprod.nap.pages.pharmacologySearchPage
import net.nprod.nap.pages.pharmacyByTaxaSearch
import net.nprod.nap.pages.pharmacyPage
import net.nprod.nap.pages.worktypePage
import net.nprod.nap.pages.administrationRoutePage
import net.nprod.nap.pages.compoundSearchPage
import org.apache.commons.codec.binary.Base64.encodeBase64
import pharmacologyPage
import queryPage
import java.io.File

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")

        get("/compound/search") {
            val query = call.request.queryParameters["query"]
            call.respondText(compoundSearchPage(query), ContentType.Text.Html)
        }

        get("/compound/{id}") {
            call.respondText(compoundPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/pharmacology/search") {
            val query = call.request.queryParameters["query"]
            call.respondText(pharmacologySearchPage(query), ContentType.Text.Html)
        }

        get("/pharmacology/{id}") {
            call.respondText(pharmacologyPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/organism/search") {
            val query = call.request.queryParameters["query"]
            call.respondText(organismSearchPage(query), ContentType.Text.Html)
        }

        get("/organism/{id}") {
            call.respondText(organismPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/pharmacy/{id}") {
            call.respondText(pharmacyPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/worktype/{id}") {
            call.respondText(worktypePage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/citation/{id}") {
            call.respondText(citationPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/animal/{id}") {
            call.respondText(animalPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/geographicalarea/{id}") {
            call.respondText(geographicalAreaPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/country/{id}") {
            call.respondText(countryPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/partcondition/{id}") {
            call.respondText(PartConditionPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/extract/{id}") {
            call.respondText(extractPage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/compoundcode/{id}") {
            call.respondText(compoundCodePage(call.parameters["id"]), ContentType.Text.Html)
        }

        get("/administrationroute/{id}") {
            call.respondText(administrationRoutePage(call.parameters["id"]), ContentType.Text.Html)
        }

        // Taxa search

        get("/pharmacy_search") {
            val taxonId = call.parameters["taxon_id"]
            call.respondText(pharmacyByTaxaSearch(taxonId ?: ""), ContentType.Text.Html)
        }

        // Generic matcher

        get("/{type}/{id}") {
            call.respondText(
                naiveDataPage(call.parameters["type"] ?: "unknown", call.parameters["id"]),
                ContentType.Text.Html
            )
        }

        get("/") {
            call.respondText(homePage(), ContentType.Text.Html)
        }


        get("/sparql") {
            call.respondText(queryPage(), ContentType.Text.Html)
        }
        // Only for development
        // Proxy to nap.nprod.net
        post("/raw/sparql") {
            val client = HttpClient(CIO) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60_000
                }
                followRedirects = true
            }

            val response: HttpResponse = client.post(System.getenv("SPARQL_SERVER")) {
                headers {
                    val user = System.getenv("HTTP_AUTH_SPARQL_USER") ?: ""
                    val password = System.getenv("HTTP_AUTH_SPARQL_PASSWORD") ?: ""
                    append(
                        HttpHeaders.Authorization,
                        "Basic " + encodeBase64("$user:$password".toByteArray()).decodeToString()
                    )
                    append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                }

                val body = call.receiveText()
                setBody(body)
            }
            call.respondText(response.bodyAsText(), response.contentType())
        }

        get("/raw/sparql") {
            val client = HttpClient(CIO) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60_000
                }
                followRedirects = true
            }

            val response: HttpResponse = client.get(System.getenv("SPARQL_SERVER")) {
                headers {
                    val user = System.getenv("HTTP_AUTH_SPARQL_USER") ?: ""
                    val password = System.getenv("HTTP_AUTH_SPARQL_PASSWORD") ?: ""
                    append(
                        HttpHeaders.Authorization,
                        "Basic " + encodeBase64("$user:$password".toByteArray()).decodeToString()
                    )
                }

                url {
                    call.request.queryParameters.entries().forEach {
                        parameters.append(it.key, it.value.firstOrNull() ?: "")
                    }
                }

            }
            call.respondText(response.bodyAsText(), response.contentType())
        }

        staticFiles("/assets", File("assets"))
    }
}
