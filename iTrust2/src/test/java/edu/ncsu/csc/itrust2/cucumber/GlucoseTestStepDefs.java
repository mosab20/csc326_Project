package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.GlucoseToleranceTest;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Class for Cucumber Testing of Oral Glucose Tolerance Test feature.
 *
 * @author mharkne
 */
public class GlucoseTestStepDefs extends CucumberTest {
    private final String baseUrl = "http://localhost:8080/iTrust2";

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Fills in the date and time fields with the specified date and time.
     *
     * @param date
     *            The date to enter.
     * @param time
     *            The time to enter.
     */
    private void fillInDateTime ( final String dateField, final String date, final String timeField,
            final String time ) {
        fillInDate( dateField, date );
        fillInTime( timeField, time );
    }

    /**
     * Fills in the date field with the specified date.
     *
     * @param date
     *            The date to enter.
     */
    private void fillInDate ( final String dateField, final String date ) {
        driver.findElement( By.name( dateField ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( dateField ) );
        dateElement.sendKeys( date.replace( "/", "" ) );
    }

    /**
     * Fills in the time field with the specified time.
     *
     * @param time
     *            The time to enter.
     */
    private void fillInTime ( final String timeField, String time ) {
        // Zero-pad the time for entry
        if ( time.length() == 7 ) {
            time = "0" + time;
        }

        driver.findElement( By.name( timeField ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( timeField ) );
        timeElement.sendKeys( time.replace( ":", "" ).replace( " ", "" ) );
    }

    /**
     * Login as HCP Shelly Vang.
     */
    @Given ( "I log in to iTrust2 as HCP" )
    public void loginAsShelly2 () {
        attemptLogout();

        HibernateDataGenerator.generateTestLOINC();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "svang" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as lab tech Larry Teacher.
     */
    @Given ( "I log in to iTrust2 as Lab Tech" )
    public void livingLikeLarry2 () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "larrytech" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as admin Al Minister.
     */
    @Given ( "I log in to iTrust2 as Admin" )
    public void loginAsAl2 () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "alminister" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Create and fill out new Office Visit for patient Nellie Sanderson.
     */
    @When ( "I create an Office Visit" )
    public void createOfficeVisit2 () {

        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );

        waitForAngular();
        setTextField( By.name( "notes" ), "Billy has been experiencing symptoms of a cold or flu" );
        waitForAngular();

        driver.findElement( By.id( "patient" ) ).click();
        driver.findElement( By.name( "GENERAL_CHECKUP" ) ).click();
        driver.findElement( By.name( "hospital" ) ).click();

        fillInDateTime( "date", "10/17/2018", "time", "9:30 AM" );

        waitForAngular();
        setTextField( By.name( "height" ), "62.3" );

        waitForAngular();
        setTextField( By.name( "weight" ), "125" );

        waitForAngular();
        setTextField( By.name( "systolic" ), "110" );

        waitForAngular();
        setTextField( By.name( "diastolic" ), "75" );

        waitForAngular();
        setTextField( By.name( "hdl" ), "65" );

        waitForAngular();
        setTextField( By.name( "ldl" ), "102" );

        waitForAngular();
        setTextField( By.name( "tri" ), "147" );

        waitForAngular();
        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        waitForAngular();
        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.FORMER.toString() + "\"]" ) );
        patientSmokeElement.click();
    }

    /**
     * Add lab procedure to Office Visit, assigned to Larry Teacher.
     */
    @And ( "I add a glucose test to that visit" )
    public void addGlucoseTest2 () {
        // add the lab proc
        waitForAngular();
        driver.findElement( By.name( "Glucose Test" ) ).click();
        final Select pri = new Select( driver.findElement( By.name( "priority" ) ) );
        pri.selectByVisibleText( "High" );

        waitForAngular();
        driver.findElement( By.id( "radio-larrytech" ) ).click();
        driver.findElement( By.name( "addProcedure" ) ).click();

        waitForAngular();
        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Navigate Lab Tech from home page to assigned procedures.
     */
    @When ( "I go to Assigned Procedures" )
    public void navigateToAssigned2 () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('LTprocedures').click();" );

        waitForAngular();
    }

    /**
     * Try and submit a non-numerical value
     */
    @And ( "I submit an invalid result" )
    public void invalidResult () {
        waitForAngular();
        driver.findElement( By.id( "update-49689-3" ) ).click();
        waitForAngular();

        final Select status = new Select( driver.findElement( By.id( "selectStatus" ) ) );
        status.selectByVisibleText( "COMPLETED" );
        waitForAngular();
        setTextField( By.id( "resultsID" ), "lorem" );

        waitForAngular();
        driver.findElement( By.id( "updateStatus" ) ).click();
    }

    /**
     * Try and submit a numerical value
     */
    @And ( "I submit a valid result" )
    public void validResult () {
        driver.navigate().refresh();
        waitForAngular();
        driver.findElement( By.id( "update-49689-3" ) ).click();
        waitForAngular();

        final Select status = new Select( driver.findElement( By.id( "selectStatus" ) ) );
        status.selectByVisibleText( "COMPLETED" );
        waitForAngular();
        setTextField( By.id( "resultsID" ), "100" );

        waitForAngular();
        driver.findElement( By.id( "updateStatus" ) ).click();
    }

    /**
     * Navigate Admin from home page to procedures.
     */
    @When ( "I go to Admin Procedures" )
    public void navigateToAdminProc2 () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageLOINCCodes').click();" );

        waitForAngular();
    }

    /**
     * Create and fill-out a new lab procedure
     */
    @When ( "I add glucose test" )
    public void addGlucoseTest () {
        waitForAngular();
        setTextField( By.name( "iCode" ), "49689-3" );
        setTextField( By.name( "iComName" ), "Glucose Test" );
        setTextField( By.name( "iComponent" ), "Glucose tolerance^post 100 g glucose PO" );
        setTextField( By.name( "iProperty" ), "Imp" );

        driver.findElement( By.id( "submitLOINC" ) ).click();
    }

    /**
     * Verify success message of "Office Visit created successfully".
     */
    @Then ( "I recieve a message that office visit details were successfully changed" )
    public void successOffice2 () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.name( "success" ) ).getText()
                    .contains( "Office visit created successfully" ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Verify that glucose test was submitted successfully.
     */
    @Then ( "the results are submitted successfully" )
    public void checkSubmitSuccess () {
        waitForAngular();
        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.id( "succUpd" ) ).getText().contains( "Results submitted." ) );

        }

        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Verify that input was invalid
     */
    @Then ( "I receive an error message" )
    public void checkInvalidResults () {
        waitForAngular();

        try {
            assertTrue( driver.findElement( By.id( "errR" ) ).getText()
                    .contains( "Must enter a numerical result value." ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * HCP Navigates to edit offcie visits page.
     */
    @When ( "I navigate to Edit Office Visits" )
    public void editHCPOfficeVisit () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editOfficeVisit').click();" );

        assertEquals( "iTrust2: Edit Office Visit", driver.getTitle() );
    }

    /**
     * HCP Clicks submit.
     */
    @When ( "HCP clicks submit button to update the office visit" )
    public void updateOV () {

        final WebElement ele = driver.findElement( By.xpath( "//*[@id=\"finalEdit\"]" ) );
        final JavascriptExecutor executor = driver;
        executor.executeScript( "arguments[0].click();", ele );
    }

    /**
     * After HCP submits, they get success message.
     */
    @Then ( "I recieve a message that results were confirmed successfully" )
    public void successfulConfirm () {
        // confirm that the message is displayed
        waitForAngular();
        waitForAngular();
        try {
            driver.findElement( By.name( "success" ) ).getText().contains( "Office visit edited successfully" );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Patient logs in to view the diabetes diagnosis.
     */
    @Given ( "I log in to iTrust2 as Patient" )
    public void patientLogsIn () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "patient" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Patient goes to the dianoses tab under appointments.
     */
    @When ( "I navigate to diagnoses" )
    public void patientNavigatesToDiagnoses () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewDiagnoses').click();" );

        waitForAngular();
        assertEquals( "iTrust2: View Diagnoses", driver.getTitle() );
    }

    /**
     * Patient sees the diagnosis is present in the landing page.
     */
    @Then ( "I can see my diagnosis results" )
    public void patientSeesDiagnosis () {
        waitForAngular();
        assertTextPresent( "NORMAL" );
    }

    // }

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        try {
            assertTrue( driver.getPageSource().contains( text ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * HCP clicks confirm
     *
     * @throws InterruptedException
     */
    @When ( "select oral glucose test office visit to click on Confirm Oral Glucose Test" )
    public void clickConfirm () throws InterruptedException {
        waitForAngular();

        final List<OfficeVisit> visits = OfficeVisit.getOfficeVisits();
        final List<GlucoseToleranceTest> gtts = GlucoseToleranceTest.getGlucoseToleranceTests();
        long targetId = 0;

        for ( int i = 0; i < visits.size(); i++ ) {
            if ( visits.get( i ).getType().equals( AppointmentType.GENERAL_CHECKUP )
                    && visits.get( i ).getPatient().getUsername().equals( "patient" ) ) {
                if ( visits.get( i ).getId().equals( gtts.get( 0 ).getVisit().getId() ) ) {
                    targetId = visits.get( i ).getId();
                    break;
                }
            }
        }

        if ( targetId != 0 ) {
            final WebElement elem = driver.findElement( By.cssSelector( "input[value=\"" + targetId + "\"]" ) );
            elem.click();
            waitForAngular();
        }

        waitForAngular();
        driver.findElement( By.name( "D11" ) ).click();

        waitForAngular();
        final WebElement confirmBtn = driver.findElement( By.id( "confirmDiagnosisBtn" ) );
        waitForAngular();
        confirmBtn.click();
    }

    /**
     * Tears down the objects this test created
     */
    @AfterClass
    public void teardown () {
        DomainObject.deleteAll( LabProcedure.class );
        DomainObject.deleteAll( GlucoseToleranceTest.class );
        DomainObject.deleteAll( OfficeVisit.class );
    }

}
