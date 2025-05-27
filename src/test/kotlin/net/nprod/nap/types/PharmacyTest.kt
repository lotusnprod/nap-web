package net.nprod.nap.types

import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class PharmacyTest {
    
    @Test
    fun testPharmacyCreation() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/1")
        
        assertEquals("https://nap.nprod.net/pharmacy/1", pharmacy.uri)
        assertTrue(pharmacy.worktypes.isEmpty())
        assertNull(pharmacy.organism)
        assertNull(pharmacy.pharmacology)
        assertTrue(pharmacy.compounds.isEmpty())
        assertNull(pharmacy.doseamount)
        assertNull(pharmacy.activitylevel)
        assertNull(pharmacy.disease)
        assertNull(pharmacy.cmpdiso)
        assertNull(pharmacy.number)
        assertNull(pharmacy.cmpdisounit)
        assertNull(pharmacy.rawyield)
        assertNull(pharmacy.amtutilunit)
        assertNull(pharmacy.pharmactivity)
        assertNull(pharmacy.percentyield)
        assertNull(pharmacy.quantitativeresult)
        assertNull(pharmacy.amtutil)
        assertTrue(pharmacy.alerts.isEmpty())
        assertNull(pharmacy.administrationRoute)
        assertNull(pharmacy.qualitativeResult)
        assertNull(pharmacy.animal)
        assertNull(pharmacy.perUnit)
        assertNull(pharmacy.doseUnit)
        assertNull(pharmacy.doseExpression)
        assertNull(pharmacy.extract)
        assertNull(pharmacy.pathologicalSystem)
        assertNull(pharmacy.gender)
        assertNull(pharmacy.expressionResult)
    }
    
    @Test
    fun testPharmacyWithBasicFields() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/2").apply {
            doseamount = "100"
            activitylevel = "High"
            disease = "Malaria"
            cmpdiso = "25"
            number = "EXP-001"
            cmpdisounit = "mg"
            rawyield = "500 mg"
            amtutilunit = "g"
            pharmactivity = "Antimalarial"
            percentyield = "85%"
            quantitativeresult = "IC50 = 5 µM"
            amtutil = "2"
        }
        
        assertEquals("100", pharmacy.doseamount)
        assertEquals("High", pharmacy.activitylevel)
        assertEquals("Malaria", pharmacy.disease)
        assertEquals("25", pharmacy.cmpdiso)
        assertEquals("EXP-001", pharmacy.number)
        assertEquals("mg", pharmacy.cmpdisounit)
        assertEquals("500 mg", pharmacy.rawyield)
        assertEquals("g", pharmacy.amtutilunit)
        assertEquals("Antimalarial", pharmacy.pharmactivity)
        assertEquals("85%", pharmacy.percentyield)
        assertEquals("IC50 = 5 µM", pharmacy.quantitativeresult)
        assertEquals("2", pharmacy.amtutil)
    }
    
    @Test
    fun testPharmacyWithCollections() {
        val worktype1 = Worktype("https://nap.nprod.net/worktype/1", "WT001", "Extraction")
        val worktype2 = Worktype("https://nap.nprod.net/worktype/2", "WT002", "Isolation")
        val compound1 = Compound("https://nap.nprod.net/compound/1", number = 1)
        val compound2 = Compound("https://nap.nprod.net/compound/2", number = 2)
        val alert1 = Alert("https://nap.nprod.net/alert/toxic", "Toxic")
        val alert2 = Alert("https://nap.nprod.net/alert/allergen", "Allergen")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/3").apply {
            worktypes.add(worktype1)
            worktypes.add(worktype2)
            compounds.add(compound1)
            compounds.add(compound2)
            alerts.add(alert1)
            alerts.add(alert2)
        }
        
        assertEquals(2, pharmacy.worktypes.size)
        assertTrue(pharmacy.worktypes.contains(worktype1))
        assertTrue(pharmacy.worktypes.contains(worktype2))
        assertEquals(2, pharmacy.compounds.size)
        assertTrue(pharmacy.compounds.contains(compound1))
        assertTrue(pharmacy.compounds.contains(compound2))
        assertEquals(2, pharmacy.alerts.size)
        assertTrue(pharmacy.alerts.contains(alert1))
        assertTrue(pharmacy.alerts.contains(alert2))
    }
    
    @Test
    fun testPharmacyWithRelatedEntities() {
        val organism = Organism("https://nap.nprod.net/organism/1")
        val pharmacology = Pharmacology("https://nap.nprod.net/pharmacology/antimicrobial", "Antimicrobial")
        val adminRoute = AdministrationRoute("https://nap.nprod.net/adminroute/oral", "Oral")
        val qualResult = QualitativeResult("https://nap.nprod.net/qualresult/positive", "Positive")
        val animal = Animal("https://nap.nprod.net/animal/mouse", "Mouse")
        val perUnit = PerUnit("https://nap.nprod.net/perunit/kg", "per kg")
        val doseUnit = DoseUnit("https://nap.nprod.net/doseunit/mg", "mg")
        val doseExpr = DoseExpression("https://nap.nprod.net/doseexpr/single", "Single dose")
        val extract = Extract("https://nap.nprod.net/extract/aqueous", "EX001", "Aqueous extract")
        val pathSystem = PathologicalSystem("https://nap.nprod.net/pathsys/nervous", "PS001", "Nervous system")
        val gender = Gender("https://nap.nprod.net/gender/male", "G001", "Male")
        val exprResult = ExpressionResult("https://nap.nprod.net/exprresult/upregulated", "ER001", "Upregulated")
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/4").apply {
            this.organism = organism
            this.pharmacology = pharmacology
            this.administrationRoute = adminRoute
            this.qualitativeResult = qualResult
            this.animal = animal
            this.perUnit = perUnit
            this.doseUnit = doseUnit
            this.doseExpression = doseExpr
            this.extract = extract
            this.pathologicalSystem = pathSystem
            this.gender = gender
            this.expressionResult = exprResult
        }
        
        assertEquals(organism, pharmacy.organism)
        assertEquals(pharmacology, pharmacy.pharmacology)
        assertEquals(adminRoute, pharmacy.administrationRoute)
        assertEquals(qualResult, pharmacy.qualitativeResult)
        assertEquals(animal, pharmacy.animal)
        assertEquals(perUnit, pharmacy.perUnit)
        assertEquals(doseUnit, pharmacy.doseUnit)
        assertEquals(doseExpr, pharmacy.doseExpression)
        assertEquals(extract, pharmacy.extract)
        assertEquals(pathSystem, pharmacy.pathologicalSystem)
        assertEquals(gender, pharmacy.gender)
        assertEquals(exprResult, pharmacy.expressionResult)
    }
    
    @Test
    fun testPharmacyEquality() {
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/5").apply {
            disease = "Cancer"
            activitylevel = "Medium"
        }
        
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/5").apply {
            disease = "Cancer"
            activitylevel = "Medium"
        }
        
        val pharmacy3 = Pharmacy("https://nap.nprod.net/pharmacy/6").apply {
            disease = "Cancer"
            activitylevel = "Medium"
        }
        
        assertEquals(pharmacy1, pharmacy2)
        assertNotEquals(pharmacy1, pharmacy3)
    }
    
    @Test
    fun testPharmacyCopy() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/7").apply {
            disease = "Diabetes"
            activitylevel = "Low"
            number = "EXP-002"
        }
        
        val copy = pharmacy.copy(activitylevel = "High")
        
        assertEquals(pharmacy.uri, copy.uri)
        assertEquals(pharmacy.disease, copy.disease)
        assertEquals(pharmacy.number, copy.number)
        assertNotEquals(pharmacy.activitylevel, copy.activitylevel)
        assertEquals("High", copy.activitylevel)
    }
    
    @Test
    fun testPharmacyToString() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/8").apply {
            disease = "Inflammation"
            number = "EXP-003"
        }
        
        val string = pharmacy.toString()
        assertTrue(string.contains("Pharmacy"))
        assertTrue(string.contains("uri=https://nap.nprod.net/pharmacy/8"))
        assertTrue(string.contains("disease=Inflammation"))
        assertTrue(string.contains("number=EXP-003"))
    }
    
    @Test
    fun testPharmacySerialization() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/9").apply {
            disease = "Hypertension"
            activitylevel = "Moderate"
            doseamount = "50"
            cmpdiso = "10"
            cmpdisounit = "mg"
        }
        
        val json = Json.encodeToString(pharmacy)
        assertTrue(json.contains("\"uri\":\"https://nap.nprod.net/pharmacy/9\""))
        assertTrue(json.contains("\"disease\":\"Hypertension\""))
        assertTrue(json.contains("\"activitylevel\":\"Moderate\""))
        assertTrue(json.contains("\"doseamount\":\"50\""))
        assertTrue(json.contains("\"cmpdiso\":\"10\""))
        assertTrue(json.contains("\"cmpdisounit\":\"mg\""))
        
        val decoded = Json.decodeFromString<Pharmacy>(json)
        assertEquals(pharmacy, decoded)
    }
    
    @Test
    fun testPharmacyHashCode() {
        val pharmacy1 = Pharmacy("https://nap.nprod.net/pharmacy/10").apply {
            disease = "Infection"
            pharmactivity = "Antibacterial"
        }
        
        val pharmacy2 = Pharmacy("https://nap.nprod.net/pharmacy/10").apply {
            disease = "Infection"
            pharmactivity = "Antibacterial"
        }
        
        assertEquals(pharmacy1.hashCode(), pharmacy2.hashCode())
    }
    
    @Test
    fun testPharmacyMutableCollections() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/11")
        
        // Test adding to mutable sets
        val worktype = Worktype("https://nap.nprod.net/worktype/3", "WT003", "Testing")
        pharmacy.worktypes.add(worktype)
        assertEquals(1, pharmacy.worktypes.size)
        
        val compound = Compound("https://nap.nprod.net/compound/3", number = 3)
        pharmacy.compounds.add(compound)
        assertEquals(1, pharmacy.compounds.size)
        
        val alert = Alert("https://nap.nprod.net/alert/warning", "Warning")
        pharmacy.alerts.add(alert)
        assertEquals(1, pharmacy.alerts.size)
        
        // Test removing from mutable sets
        pharmacy.worktypes.remove(worktype)
        assertTrue(pharmacy.worktypes.isEmpty())
        
        pharmacy.compounds.clear()
        assertTrue(pharmacy.compounds.isEmpty())
        
        pharmacy.alerts.clear()
        assertTrue(pharmacy.alerts.isEmpty())
    }
    
    @Test
    fun testPharmacyWithNullAndEmptyValues() {
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/12").apply {
            disease = ""  // Empty string
            activitylevel = null  // Already null, but explicitly set
            doseamount = "0"  // Zero value
        }
        
        assertEquals("", pharmacy.disease)
        assertNull(pharmacy.activitylevel)
        assertEquals("0", pharmacy.doseamount)
    }
    
    @Test
    fun testPharmacyWithComplexData() {
        val organism = Organism("https://nap.nprod.net/organism/2").apply {
            genusname = "Aspergillus"
            speciesname = "niger"
        }
        
        val pharmacy = Pharmacy("https://nap.nprod.net/pharmacy/13").apply {
            this.organism = organism
            disease = "Multiple conditions"
            pharmactivity = "Broad spectrum activity"
            quantitativeresult = "IC50 = 2.5 µM; EC50 = 10 µM"
            
            // Add multiple items to collections
            repeat(3) { i ->
                worktypes.add(Worktype("https://nap.nprod.net/worktype/$i", "WT00$i", "Type $i"))
                compounds.add(Compound("https://nap.nprod.net/compound/$i", number = i))
            }
        }
        
        assertNotNull(pharmacy.organism)
        assertEquals("Aspergillus", pharmacy.organism?.genusname)
        assertEquals("Multiple conditions", pharmacy.disease)
        assertEquals(3, pharmacy.worktypes.size)
        assertEquals(3, pharmacy.compounds.size)
    }
}