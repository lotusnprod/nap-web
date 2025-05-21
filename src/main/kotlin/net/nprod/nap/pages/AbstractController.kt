package net.nprod.nap.pages

import genURI
import net.nprod.nap.rdf.SparqlConnector

/**
 * Abstract base implementation of BaseController that provides common functionality
 * for entity controllers
 * 
 * @param DataType The type of data this controller handles
 */
abstract class AbstractController<DataType> : BaseController<DataType> {
    /**
     * Get the entity type for this controller
     * Used for URI generation and error messages
     */
    abstract override fun getEntityType(): String
    
    /**
     * Create the data object from the entity URI and related data
     * 
     * @param identifier The entity identifier
     * @param sparqlConnector The SPARQL connector to use for queries
     * @param uri The entity URI
     * @return The data object or null if not found
     */
    abstract fun createData(identifier: String, sparqlConnector: SparqlConnector, uri: String): DataType?
    
    /**
     * Get the view that handles HTML rendering for this controller
     * 
     * @return A function that renders the data as HTML
     */
    abstract fun getView(): (DataType) -> String
    
    /**
     * Get data by identifier
     * Default implementation that handles common pattern:
     * 1. Validate identifier
     * 2. Generate URI
     * 3. Create SPARQL connector
     * 4. Get data using createData
     * 
     * @param identifier The entity identifier
     * @return The data or null if not found
     */
    override fun getData(identifier: String?): DataType? {
        if (identifier == null) {
            return null
        }

        val uri = genURI(getEntityType(), identifier)
        val sparqlConnector = SparqlConnector()
        
        return createData(identifier, sparqlConnector, uri)
    }
    
    /**
     * Render data as HTML using the configured view
     * 
     * @param data The data to render
     * @return HTML string representation
     */
    override fun renderHtml(data: DataType): String {
        return getView()(data)
    }
}