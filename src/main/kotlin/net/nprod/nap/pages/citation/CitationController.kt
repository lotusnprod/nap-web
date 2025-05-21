package net.nprod.nap.pages.citation

import io.ktor.server.routing.*
import net.nprod.nap.pages.AbstractController
import net.nprod.nap.rdf.SparqlConnector
import net.nprod.nap.types.Citation
import net.nprod.nap.types.QualitativeResult
import net.nprod.nap.types.Worktype

/**
 * Controller for handling Citation data retrieval and presentation
 */
class CitationController : AbstractController<CitationViewData>() {
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    override fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): CitationViewData? {
        val citation = Citation.fromSparql(sparqlConnector, uri) ?: return null
        val formattedCitation = citation.formatCitation()
        
        // Query for organisms, their pharmacology experiments, and experiment numbers from this citation
        val queryRelated = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?organism ?pharmacy ?genus ?species ?family ?number ?pharmacology ?pharmacology_name ?worktype ?qualitativeResult ?compound ?compound_name
            WHERE {
                ?organism a n:organism ;
                          n:participatesIn <$uri>.
                ?pharmacy n:has_organism ?organism ;
                          n:number ?number .
                OPTIONAL { ?pharmacy n:has_pharmacology ?pharmacology .
                           ?pharmacology n:name ?pharmacology_name . }
                OPTIONAL { ?pharmacy n:has_worktype ?worktype . }
                OPTIONAL { ?pharmacy n:has_qualitativeresult ?qualitativeResult . }
                OPTIONAL { ?organism n:genusname ?genus . }
                OPTIONAL { ?organism n:speciesname ?species . }
                OPTIONAL { ?organism n:familyname ?family . }
                OPTIONAL { ?pharmacy n:has_participant ?compound .
                           ?compound a n:compound ;
                                     n:name ?compound_name . }
            }
            ORDER BY ?organism ?number
        """.trimIndent()

        val result = sparqlConnector.getResultsOfQuery(queryRelated)
        
        // Temporary structures to organize data
        val experimentInfoMap = mutableMapOf<String, MutableMap<String, ExperimentInfo>>()
        val organismInfoMap = mutableMapOf<String, OrganismInfo>()
        val experimentCompoundMap = mutableMapOf<String, MutableMap<String, String>>()
        
        if (result != null) {
            while (result.hasNext()) {
                val solution = result.nextSolution()

                val organismUri = solution["organism"]?.asResource()?.uri ?: continue
                val pharmacyUri = solution["pharmacy"]?.asResource()?.uri ?: continue
                val compoundUri = solution["compound"]?.asResource()?.uri
                val compoundName = solution["compound_name"]?.asLiteral()?.string
                val number = solution["number"]?.asLiteral()?.string
                solution["pharmacology"]?.asResource()?.uri
                val pharmacologyName = solution["pharmacology_name"]?.asLiteral()?.string
                val worktypeUri = solution["worktype"]?.asResource()?.uri
                val qualitativeResultUri = solution["qualitativeResult"]?.asResource()?.uri

                // Initialize organism if not exists
                if (!organismInfoMap.containsKey(organismUri)) {
                    val genus = solution["genus"]?.asLiteral()?.string
                    val species = solution["species"]?.asLiteral()?.string
                    val family = solution["family"]?.asLiteral()?.string
                    organismInfoMap[organismUri] = OrganismInfo(organismUri, genus, species, family)
                    experimentInfoMap[organismUri] = mutableMapOf()
                    experimentCompoundMap[pharmacyUri] = mutableMapOf()
                }

                // Initialize or get experiment map for this organism
                val experimentsForOrganism = experimentInfoMap.getOrPut(organismUri) { mutableMapOf() }
                
                // Initialize or update experiment
                val experimentInfo = experimentsForOrganism.getOrPut(pharmacyUri) {
                    ExperimentInfo(
                        uri = pharmacyUri,
                        number = number,
                        pharmacologyName = pharmacologyName,
                        worktypes = mutableMapOf(),
                        qualitativeResults = mutableMapOf(),
                        compounds = mutableMapOf()
                    )
                }

                // Add worktype if available
                if (worktypeUri != null) {
                    val worktype = Worktype.Cache[worktypeUri]
                    if (worktype != null) {
                        experimentInfo.worktypes[worktypeUri] = worktype.name
                    }
                }

                // Add qualitative result if available
                if (qualitativeResultUri != null) {
                    val result = QualitativeResult.Cache[qualitativeResultUri]
                    if (result != null) {
                        experimentInfo.qualitativeResults[qualitativeResultUri] = result.name
                    }
                }
                
                // Add compound if available
                if (compoundUri != null && compoundName != null) {
                    experimentInfo.compounds[compoundUri] = compoundName
                }
            }
        }

        // Convert the temporary structures to the data model for the view
        val organismData = organismInfoMap.map { (organismUri, info) ->
            val experimentsMap = experimentInfoMap[organismUri] ?: emptyMap()
            
            // Convert experiments to view data
            val experiments = experimentsMap.map { (_, expInfo) ->
                // Convert worktypes to view data
                val worktypes = expInfo.worktypes.map { (uri, name) ->
                    WorktypeData(uri, name)
                }
                
                // Convert qualitative results to view data
                val qualitativeResults = expInfo.qualitativeResults.map { (uri, name) ->
                    QualitativeResultData(uri, name)
                }
                
                // Convert compounds to view data
                val compounds = expInfo.compounds.map { (uri, name) ->
                    CompoundData(uri, name)
                }
                
                ExperimentData(
                    uri = expInfo.uri,
                    number = expInfo.number,
                    pharmacologyName = expInfo.pharmacologyName,
                    worktypes = worktypes,
                    qualitativeResults = qualitativeResults,
                    compounds = compounds
                )
            }
            
            OrganismData(
                uri = info.uri,
                genus = info.genus,
                species = info.species,
                family = info.family,
                experiments = experiments
            )
        }

        return CitationViewData(
            identifier = identifier,
            citation = citation,
            formattedCitation = formattedCitation,
            organismData = organismData
        )
    }

    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    override fun getView(): (CitationViewData) -> String = CitationView::render

    /**
     * Get the entity type for error messages
     * 
     * @return The entity type name
     */
    override fun getEntityType(): String = "citation"

    /**
     * Register citation routes
     */
    companion object {
        fun registerRoutes(routing: Routing) {
            val controller = CitationController()
            controller.registerRoute(routing, "/citation/{id}")
        }
    }
}

/**
 * Helper class for organizing experiment data while processing query results
 */
private data class ExperimentInfo(
    val uri: String,
    val number: String?,
    val pharmacologyName: String?,
    val worktypes: MutableMap<String, String>,
    val qualitativeResults: MutableMap<String, String>,
    val compounds: MutableMap<String, String>
)

/**
 * Helper class for organizing organism data while processing query results
 */
private data class OrganismInfo(
    val uri: String,
    val genus: String?,
    val species: String?,
    val family: String?
)