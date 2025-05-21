package net.nprod.nap.pages.naiveData

import kotlinx.serialization.Serializable
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdf.model.Resource

/**
 * Data class to hold naive data information for the view
 */
@Serializable
data class NaiveDataViewData(
    val type: String,
    val identifier: String,
    // Since we can't directly serialize these, we'll need to convert them in the controller
    val outNodes: Map<String, List<String>>,
    val inNodes: Map<String, List<String>>
)