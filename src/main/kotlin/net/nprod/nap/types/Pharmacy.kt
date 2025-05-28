package net.nprod.nap.types

import kotlinx.serialization.Serializable
import net.nprod.nap.rdf.SparqlConnector

@Serializable
data class Pharmacy(
    val uri: String,
    val worktypes: MutableSet<Worktype> = mutableSetOf(),
    var organism: Organism? = null,
    var pharmacology: Pharmacology? = null,
    val compounds: MutableSet<Compound> = mutableSetOf(),
    var doseamount: String? = null,
    var activitylevel: String? = null,
    var disease: String? = null,
    var cmpdiso: String? = null,
    var number: String? = null,
    var cmpdisounit: String? = null,
    var rawyield: String? = null,
    var amtutilunit: String? = null,
    var pharmactivity: String? = null,
    var percentyield: String? = null,
    var quantitativeresult: String? = null,
    var amtutil: String? = null,
    var alerts: MutableSet<Alert> = mutableSetOf(),
    var administrationRoute: AdministrationRoute? = null,
    var qualitativeResult: QualitativeResult? = null,
    var animal: Animal? = null,
    var perUnit: PerUnit? = null,
    var doseUnit: DoseUnit? = null,
    var doseExpression: DoseExpression? = null,
    var extract: Extract? = null,
    var pathologicalSystem: PathologicalSystem? = null,
    var gender: Gender? = null,
    var expressionResult: ExpressionResult? = null
) {
    companion object {
        fun fromSparql(sparqlConnector: SparqlConnector, uri: String): Pharmacy {
            var new: Pharmacy? = null

            val query = """
            PREFIX n: <https://nap.nprod.net/>
            SELECT ?pharmacy ?worktype ?compound ?compound_name ?pharmacology ?doseamount ?activitylevel ?disease ?cmpdiso ?number ?cmpdisounit ?organism
                ?rawyield ?amtutilunit ?pharmactivity ?percentyield ?quantitativeresult ?amtutil ?compound ?alert ?administration_route ?qualitative_result 
                ?animal ?perunit ?doseunit ?doseexpression ?extract ?pathologicalsystem ?gender ?expression_result
            WHERE {
                ?pharmacy a n:pharmacy;
                          n:doseamount ?doseamount; 
                          n:activitylevel ?activitylevel;
                          n:disease ?disease;
                          n:cmpdiso ?cmpdiso;
                          n:number ?number;
                          n:cmpdisounit ?cmpdisounit;
                          n:has_organism ?organism;
                          n:rawyield ?rawyield;
                          n:amtutilunit ?amtutilunit;
                          n:pharmactivity ?pharmactivity;
                          n:percentyield ?percentyield;
                          n:quantitativeresult ?quantitativeresult;
                          n:amtutil ?amtutil.
                OPTIONAL {
                    ?pharmacy n:has_pharmacology ?pharmacology.
                }
                OPTIONAL {
                    ?pharmacy n:administeredBy ?administration_route.
                }
                OPTIONAL {
                    ?pharmacy n:has_qualitativeresult ?qualitative_result.
                }
                OPTIONAL {
                    ?pharmacy n:has_worktype ?worktype.
                }
                OPTIONAL {
                    ?pharmacy n:has_participant ?compound.
                    ?compound n:name ?compound_name.
                    ?compound a n:compound.
                }
                OPTIONAL {
                    ?pharmacy n:has_alert ?alert.
                }
                OPTIONAL {
                    ?pharmacy n:has_animal ?animal.
                }
                OPTIONAL {
                    ?pharmacy n:has_perunit ?perunit.
                }
                OPTIONAL {
                    ?pharmacy n:has_doseunit ?doseunit.
                }
                OPTIONAL {
                    ?pharmacy n:has_doseexpression ?doseexpression.
                }
                OPTIONAL {
                    ?pharmacy n:has_extract ?extract.
                }
                OPTIONAL {
                    ?pharmacy n:has_pathologicalsystem ?pathologicalsystem.
                }
                OPTIONAL {
                    ?pharmacy n:has_gender ?gender.
                }
                OPTIONAL {
                    ?pharmacy n:has_expressionresult ?expression_result.
                }
                VALUES ?pharmacy { <$uri> }
            }
            
        """.trimIndent()
            val result = sparqlConnector.getResultsOfQuery(query, logQuery = false)
            if (result != null) {
                while (result.hasNext()) {
                    val solution = result.nextSolution()
                    val pharmacyUri = solution["pharmacy"].asResource().uri
                    if (new == null) {
                        new = Pharmacy(pharmacyUri)
                        solution["organism"]?.asResource()?.let {
                            new.organism = Organism.fromSparql(sparqlConnector, it.uri)
                        }
                        solution["pharmacology"]?.asResource()?.let {
                            new.pharmacology = Pharmacology.Cache[it.uri]
                        }
                        new.doseamount = solution["doseamount"].asLiteral()?.string?.ifEmpty { null }
                        new.activitylevel = solution["activitylevel"].asLiteral().string.ifEmpty { null }
                        new.disease = solution["disease"].asLiteral().string.ifEmpty { null }
                        new.cmpdiso = solution["cmpdiso"].asLiteral().string.ifEmpty { null }
                        new.number = solution["number"].asLiteral().string.ifEmpty { null }
                        new.cmpdisounit = solution["cmpdisounit"].asLiteral().string.ifEmpty { null }
                        new.rawyield = solution["rawyield"].asLiteral().string.ifEmpty { null }
                        new.amtutilunit = solution["amtutilunit"].asLiteral().string.ifEmpty { null }
                        new.pharmactivity = solution["pharmactivity"].asLiteral().string.ifEmpty { null }
                        new.percentyield = solution["percentyield"].asLiteral().string.ifEmpty { null }
                        new.quantitativeresult = solution["quantitativeresult"].asLiteral().string.ifEmpty { null }
                        new.amtutil = solution["amtutil"].asLiteral().string.ifEmpty { null }
                        solution["administration_route"]?.asResource()?.let {
                            new.administrationRoute = AdministrationRoute.Cache[it.uri]
                        }
                        solution["qualitative_result"]?.asResource()?.let {
                            new.qualitativeResult = QualitativeResult.Cache[it.uri]
                        }
                        solution["animal"]?.asResource()?.let {
                            new.animal = Animal.Cache[it.uri]
                        }
                        solution["perunit"]?.asResource()?.let {
                            new.perUnit = PerUnit.Cache[it.uri]
                        }
                        solution["doseunit"]?.asResource()?.let {
                            new.doseUnit = DoseUnit.Cache[it.uri]
                        }
                        solution["doseexpression"]?.asResource()?.let {
                            new.doseExpression = DoseExpression.Cache[it.uri]
                        }
                        solution["extract"]?.asResource()?.let {
                            new.extract = Extract.Cache[it.uri]
                        }
                        solution["pathologicalsystem"]?.asResource()?.let {
                            new.pathologicalSystem = PathologicalSystem.Cache[it.uri]
                        }
                        solution["gender"]?.asResource()?.let {
                            new.gender = Gender.Cache[it.uri]
                        }
                        solution["expression_result"]?.asResource()?.let {
                            new.expressionResult = ExpressionResult.Cache[it.uri]
                        }
                    }

                    new.worktypes.add(Worktype.Cache[solution["worktype"].asResource().uri]!!)
                    solution["compound"]?.asResource()?.let {
                        var cmpd = Compound.fromSparql(sparqlConnector, it.uri)
                        cmpd.name = solution["compound_name"].asLiteral().string
                        new.compounds.add(cmpd)
                    }

                    solution["alert"]?.asResource()?.let {
                        Alert.Cache[it.uri]?.let {
                            new.alerts.add(it)
                        }
                    }
                }
            }
            if (new == null) throw Exception("No pharmacy found for $uri")
            return new
        }
    }
}
