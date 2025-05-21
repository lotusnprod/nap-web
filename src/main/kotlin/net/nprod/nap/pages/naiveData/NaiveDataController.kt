package net.nprod.nap.pages.naiveData

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import genURI
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import net.nprod.nap.pages.invalidEntryPage
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

/**
 * Controller for handling NaiveData data retrieval and presentation
 */
class NaiveDataController : AbstractController<NaiveDataViewData>() {
    /**
     * Convert a Map<Resource, List<RDFNode>> to a Map<String, List<String>> for serialization
     */
    private fun convertRdfNodesToStrings(nodes: Map<Resource, List<RDFNode>>): Map<String, List<String>> {
        return nodes.mapKeys { it.key.uri }.mapValues { entry ->
            entry.value.map { node ->
                if (node.isResource) {
                    node.asResource().uri
                } else {
                    node.toString()
                }
            }
        }
    }

    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): NaiveDataViewData? {
        try {
            // Get outgoing nodes
            val outNodes = sparqlConnector.predicateAndObjectsOf(uri)
            // Get incoming nodes
            val inNodes = sparqlConnector.subjectAndPredicatesOf(uri)
            
            return NaiveDataViewData(
                type = getEntityType(),
                identifier = identifier,
                outNodes = convertRdfNodesToStrings(outNodes),
                inNodes = convertRdfNodesToStrings(inNodes)
            )
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Handle generic data requests with custom type
     */
    suspend fun handleGenericRequest(call: ApplicationCall, type: String, id: String?) {
        if (id == null || id.toIntOrNull() == null) {
            call.respondText(invalidEntryPage(type, id ?: "null"), ContentType.Text.Html)
            return
        }

        if (type.contains("/")) {
            call.respondText(invalidEntryPage("type", type), ContentType.Text.Html)
            return
        }

        val uri = genURI(type, id)
        val sparqlConnector = SparqlConnector()
        
        // Get outgoing nodes
        val outNodes = sparqlConnector.predicateAndObjectsOf(uri)
        // Get incoming nodes
        val inNodes = sparqlConnector.subjectAndPredicatesOf(uri)
        
        val data = NaiveDataViewData(
            type = type,
            identifier = id,
            outNodes = convertRdfNodesToStrings(outNodes),
            inNodes = convertRdfNodesToStrings(inNodes)
        )
        
        call.respondText(NaiveDataView.render(data), ContentType.Text.Html)
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (NaiveDataViewData) -> String = NaiveDataView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "naivedata"

    /**
     * Register naivedata routes - note that this is a generic catch-all route for unknown types
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = NaiveDataController()
            
            // Special handler for generic routes
            routing.get("/{type}/{id}") {
                val type = call.parameters["type"] ?: "unknown"
                val id = call.parameters["id"]
                controller.handleGenericRequest(call, type, id)
            }
        }
    }
}