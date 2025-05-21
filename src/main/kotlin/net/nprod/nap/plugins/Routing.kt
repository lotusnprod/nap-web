package net.nprod.nap.plugins

import net.nprod.nap.pages.partCondition.PartConditionController
import net.nprod.nap.pages.country.CountryController
import net.nprod.nap.pages.geographicalArea.GeographicalAreaController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import net.nprod.nap.pages.animal.AnimalController
import net.nprod.nap.pages.citation.CitationController
import net.nprod.nap.pages.compoundCode.CompoundCodeController
import net.nprod.nap.pages.extract.ExtractController
import net.nprod.nap.pages.naiveData.NaiveDataController
import net.nprod.nap.pages.organism.OrganismController
import net.nprod.nap.pages.pharmacy.PharmacyController
import net.nprod.nap.pages.administrationRoute.AdministrationRouteController
import net.nprod.nap.pages.compound.CompoundController
import net.nprod.nap.pages.compoundSearch.CompoundSearchController
import net.nprod.nap.pages.compoundSynonym.CompoundSynonymController
import net.nprod.nap.pages.home.HomeController
import net.nprod.nap.pages.organismSearch.OrganismSearchController
import net.nprod.nap.pages.pharmacologySearch.PharmacologySearchController
import net.nprod.nap.pages.pharmacyByTaxaSearch.PharmacyByTaxaSearchController
import net.nprod.nap.pages.query.QueryController
import net.nprod.nap.pages.worktype.WorktypeController
import net.nprod.nap.pages.sparqlProxy.SparqlProxyController
import net.nprod.nap.pages.pharmacology.PharmacologyController
import java.io.File

fun Application.configureRouting() {
    routing {
        staticResources("/static", "static")

        // Register controllers for pages transformed to MVC pattern
        CompoundController.registerRoutes(this)
        PharmacologyController.registerRoutes(this)
        OrganismController.registerRoutes(this)
        PharmacyController.registerRoutes(this)
        WorktypeController.registerRoutes(this)
        CitationController.registerRoutes(this)
        AnimalController.registerRoutes(this)
        GeographicalAreaController.registerRoutes(this)
        CountryController.registerRoutes(this)
        PartConditionController.registerRoutes(this)
        ExtractController.registerRoutes(this)
        CompoundCodeController.registerRoutes(this)
        AdministrationRouteController.registerRoutes(this)
        CompoundSynonymController.registerRoutes(this)
        NaiveDataController.registerRoutes(this)
        
        // Register search controllers
        CompoundSearchController.registerRoutes(this)
        PharmacologySearchController.registerRoutes(this)
        OrganismSearchController.registerRoutes(this)
        PharmacyByTaxaSearchController.registerRoutes(this)
        
        // Register home and query controllers
        HomeController.registerRoutes(this)
        QueryController.registerRoutes(this)
        
        // Register SPARQL Proxy routes using the controller
        // This handles both GET and POST requests to /raw/sparql
        SparqlProxyController.registerRoutes(this)

        staticFiles("/assets", File("assets"))
    }
}
