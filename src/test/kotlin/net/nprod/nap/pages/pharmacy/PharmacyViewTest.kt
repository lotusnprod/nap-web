package net.nprod.nap.pages.pharmacy

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains
import net.nprod.nap.types.*

class PharmacyViewTest {
    
    @Test
    fun testRenderWithMinimalPharmacy() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/1")
        val viewData = PharmacyViewData("1", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertTrue(html.contains("Experiment 1"))
        assertTrue(html.contains("Details"))
        assertTrue(html.contains("<table"))
    }
    
    @Test
    fun testRenderWithWorktypes() {
        val worktype1 = Worktype("https://nap.nprod.net/worktype/1", "WT001", "Extraction")
        val worktype2 = Worktype("https://nap.nprod.net/worktype/2", "WT002", "Isolation")
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/2").apply {
            worktypes.add(worktype1)
            worktypes.add(worktype2)
        }
        val viewData = PharmacyViewData("2", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Worktypes")
        assertContains(html, "Extraction")
        assertContains(html, "Isolation")
        assertContains(html, "/worktype/1")
        assertContains(html, "/worktype/2")
    }
    
    @Test
    fun testRenderWithCompounds() {
        val compound1 = Compound("https://nap.nprod.net/compound/1", number = 1).apply {
            name = "Aspirin"
        }
        val compound2 = Compound("https://nap.nprod.net/compound/2", number = 2).apply {
            name = "Caffeine"
        }
        val compoundUnknown = Compound("https://nap.nprod.net/compound/3", number = 3)
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/3").apply {
            compounds.add(compound1)
            compounds.add(compound2)
            compounds.add(compoundUnknown)
        }
        val viewData = PharmacyViewData("3", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Compounds isolated")
        assertContains(html, "Aspirin")
        assertContains(html, "Caffeine")
        assertContains(html, "Unknown compound")
        assertContains(html, "/compound/1")
        assertContains(html, "/compound/2")
        assertContains(html, "/compound/3")
    }
    
    @Test
    fun testRenderWithAlerts() {
        val alert1 = Alert("https://nap.nprod.net/alert/toxic", "Toxic")
        val alert2 = Alert("https://nap.nprod.net/alert/allergen", "Allergen")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/4").apply {
            alerts.add(alert1)
            alerts.add(alert2)
        }
        val viewData = PharmacyViewData("4", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Alerts")
        assertContains(html, "Toxic")
        assertContains(html, "Allergen")
        assertContains(html, "/alert/toxic")
        assertContains(html, "/alert/allergen")
    }
    
    @Test
    fun testRenderWithPharmacologyAndAdministrationRoute() {
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antimicrobial", "Antimicrobial")
        val adminRoute = AdministrationRoute("https://nap.nprod.net/adminroute/oral", "Oral")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/5").apply {
            this.pharmacology = pharmacology
            this.administrationRoute = adminRoute
        }
        val viewData = PharmacyViewData("5", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Pharmacology")
        assertContains(html, "Antimicrobial")
        assertContains(html, "/pharmacology/antimicrobial")
        assertContains(html, "Administration route")
        assertContains(html, "Oral")
        assertContains(html, "/adminroute/oral")
    }
    
    @Test
    fun testRenderWithOrganism() {
        val citation = Citation("https://nap.nprod.net/citation/1").apply {
            title = "Test Study"
            authors = "Smith J et al."
            year = "2024"
            doi = "10.1234/test"
        }
        val organism = Organism("https://nap.nprod.net/organism/1").apply {
            genusname = "Aspergillus"
            speciesname = "niger"
            this.citation = citation
        }
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/6").apply {
            this.organism = organism
        }
        val viewData = PharmacyViewData("6", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Organism")
        assertContains(html, "Aspergillus niger")
        assertContains(html, "/organism/1")
        assertContains(html, "Citation")
        assertContains(html, "Smith J et al.")
        assertContains(html, "/citation/1")
        assertContains(html, "DOI: 10.1234/test")
        assertContains(html, "https://doi.org/10.1234/test")
    }
    
    @Test
    fun testRenderWithActivityFields() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/7").apply {
            activitylevel = "High"
            amtutil = "500"
            amtutilunit = "mg"
            cmpdiso = "25"
            cmpdisounit = "mg"
            disease = "Malaria"
        }
        val viewData = PharmacyViewData("7", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Activity level")
        assertContains(html, "High")
        assertContains(html, "Amount utilized")
        // The view always renders these rows, even if null
        assertContains(html, "Disease")
        assertContains(html, "Malaria")
    }
    
    @Test
    fun testRenderWithDoseFields() {
        val doseUnit = DoseUnit("https://nap.nprod.net/doseunit/mg", "mg")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/8").apply {
            doseamount = "100"
            this.doseUnit = doseUnit
        }
        val viewData = PharmacyViewData("8", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Dose amount")
        assertContains(html, "100")
        assertContains(html, "/doseunit/mg")
        assertContains(html, "mg")
    }
    
    @Test
    fun testRenderWithYieldAndResultFields() {
        val qualResult = QualitativeResult("https://nap.nprod.net/qualresult/positive", "Positive")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/9").apply {
            number = "EXP-2024-001"
            percentyield = "85.5%"
            rawyield = "1250 mg"
            pharmactivity = "Strong inhibition"
            quantitativeresult = "IC50 = 2.5 µM"
            qualitativeResult = qualResult
        }
        val viewData = PharmacyViewData("9", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Number")
        assertContains(html, "EXP-2024-001")
        assertContains(html, "Percent yield")
        assertContains(html, "85.5%")
        assertContains(html, "Raw yield")
        assertContains(html, "1250 mg")
        assertContains(html, "Pharmacological activity")
        assertContains(html, "Strong inhibition")
        assertContains(html, "Quantitative result")
        assertTrue(html.contains("IC50 = 2.5"))  // Check without special character
        assertContains(html, "Qualitative result")
        assertContains(html, "Positive")
        assertContains(html, "/qualresult/positive")
    }
    
    @Test
    fun testRenderWithAdditionalEntityFields() {
        val animal = Animal("https://nap.nprod.net/animal/mouse", "Mouse")
        val perUnit = PerUnit("https://nap.nprod.net/perunit/kg", "per kg")
        val doseExpression = DoseExpression("https://nap.nprod.net/doseexpr/single", "Single dose")
        val extract = Extract("https://nap.nprod.net/extract/aqueous", "EX001", "Aqueous extract")
        val pathSystem = PathologicalSystem("https://nap.nprod.net/pathsys/nervous", "PS001", "Nervous system")
        val gender = Gender("https://nap.nprod.net/gender/male", "G001", "Male")
        val exprResult = ExpressionResult("https://nap.nprod.net/exprresult/upregulated", "ER001", "Upregulated")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/10").apply {
            this.animal = animal
            this.perUnit = perUnit
            this.doseExpression = doseExpression
            this.extract = extract
            this.pathologicalSystem = pathSystem
            this.gender = gender
            this.expressionResult = exprResult
        }
        val viewData = PharmacyViewData("10", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Animal")
        assertContains(html, "Mouse")
        assertContains(html, "/animal/mouse")
        
        assertContains(html, "Per unit")
        assertContains(html, "per kg")
        assertContains(html, "/perunit/kg")
        
        assertContains(html, "Dose expression")
        assertContains(html, "Single dose")
        assertContains(html, "/doseexpr/single")
        
        assertContains(html, "Extract")
        assertContains(html, "Aqueous extract")
        assertContains(html, "/extract/aqueous")
        
        assertContains(html, "Pathological system")
        assertContains(html, "Nervous system")
        assertContains(html, "/pathsys/nervous")
        
        assertContains(html, "Gender")
        assertContains(html, "Male")
        assertContains(html, "/gender/male")
        
        assertContains(html, "Expression result")
        assertContains(html, "Upregulated")
        assertContains(html, "/exprresult/upregulated")
    }
    
    @Test
    fun testRenderWithAllSections() {
        // Create a pharmacy with all possible fields populated
        val worktype = Worktype("https://nap.nprod.net/worktype/1", "WT001", "Extraction")
        val compound = Compound("https://nap.nprod.net/compound/1", number = 1).apply {
            name = "Test Compound"
        }
        val alert = Alert("https://nap.nprod.net/alert/warning", "Warning")
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/test", "Test Activity")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/11").apply {
            worktypes.add(worktype)
            compounds.add(compound)
            alerts.add(alert)
            this.pharmacology = pharmacology
        }
        val viewData = PharmacyViewData("11", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        // Verify all main sections are present
        assertContains(html, "Experiment 11")
        assertContains(html, "Worktypes")
        assertContains(html, "Compounds isolated")
        assertContains(html, "Alerts")
        assertContains(html, "Details")
        
        // Verify Bootstrap classes are used
        assertContains(html, "container")
        assertContains(html, "row")
        assertContains(html, "col-md-4")
        assertContains(html, "card")
        assertContains(html, "card-header")
        assertContains(html, "card-body")
        assertContains(html, "list-group")
        assertContains(html, "table-responsive")
        assertContains(html, "table table-striped table-bordered table-hover")
    }
    
    @Test
    fun testRenderWithEmptyLists() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/12")
        // worktypes, compounds, and alerts are already empty by default
        val viewData = PharmacyViewData("12", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        // Empty lists should not create their sections
        assertTrue(!html.contains("Worktypes"))
        assertTrue(!html.contains("Compounds isolated"))
        assertTrue(!html.contains("Alerts"))
        
        // But the details section should still be there
        assertContains(html, "Details")
    }
    
    @Test
    fun testRenderWithCitationButNoDoi() {
        val citation = Citation("https://nap.nprod.net/citation/2").apply {
            title = "Study Without DOI"
            authors = "Author A"
            year = "2023"
            doi = "" // Empty DOI
        }
        val organism = Organism("https://nap.nprod.net/organism/2").apply {
            genusname = "Test"
            speciesname = "species"
            this.citation = citation
        }
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/13").apply {
            this.organism = organism
        }
        val viewData = PharmacyViewData("13", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        assertContains(html, "Citation")
        assertTrue(!html.contains("DOI:")) // Empty DOI should not be displayed
    }
    
    @Test
    fun testRenderWithNullOptionalFields() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/14")
        // All fields are null by default, including amtutil and cmpdiso
        val viewData = PharmacyViewData("14", pharmacy)
        
        val html = PharmacyView.render(viewData)
        
        // These rows are always rendered because the view uses .let instead of ?.let
        // The table rows will be present even with null values
        assertContains(html, "Amount utilized")
        assertContains(html, "Compound isolated")
        // The Details section should always be present
        assertContains(html, "Details")
    }
}