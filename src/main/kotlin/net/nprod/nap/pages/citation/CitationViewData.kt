package net.nprod.nap.pages.citation

import kotlinx.serialization.Serializable
import net.nprod.nap.types.Citation

/**
 * Data class to hold citation information for the view
 */
@Serializable
data class CitationViewData(
    val identifier: String,
    val citation: Citation,
    val formattedCitation: String,
    val organismData: List<OrganismData>
)

/**
 * Data class to represent an organism and its associated experiments
 */
@Serializable
data class OrganismData(
    val uri: String,
    val genus: String?,
    val species: String?,
    val family: String?,
    val experiments: List<ExperimentData>
)

/**
 * Data class to represent an experiment
 */
@Serializable
data class ExperimentData(
    val uri: String,
    val number: String?,
    val pharmacologyName: String?,
    val worktypes: List<WorktypeData>,
    val qualitativeResults: List<QualitativeResultData>,
    val compounds: List<CompoundData>
)

/**
 * Data class to represent a worktype
 */
@Serializable
data class WorktypeData(
    val uri: String,
    val name: String
)

/**
 * Data class to represent a qualitative result
 */
@Serializable
data class QualitativeResultData(
    val uri: String,
    val name: String
)

/**
 * Data class to represent a compound
 */
@Serializable
data class CompoundData(
    val uri: String,
    val name: String
)