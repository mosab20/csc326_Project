package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for FoodDiaryEntry feature.
 *
 * @author Jack MacDonald (jmmacdo4)
 */
public class BloodSugarLevelStepDefs extends CucumberTest {

    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String patientString = "patient";
    private final String hcpString     = "svang";

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

    @Then ( "^there exists a patient in the system$" )
    public void patientExistsSugarLevels () {
        attemptLogout();

        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( patientString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: Patient Home", driver.getTitle() );
    }

    @Given ( "^there exists an HCP in the system$" )
    public void hcpExistsSugarLevels () {
        attemptLogout();

        final User hcp = new User( hcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_HCP, 1 );
        hcp.save();

    }

    @And ( "^the patient has been diagnosed as diabetic or pre-diabetic$" )
    public void patientIsDiabetic () {

        // Patient only has this option if diabetic, might want to check actual
        // patient field when implemented
        assertTextPresent( "Blood Sugar Levels" );

    }

    @Then ( "^the patient navigates to the blood sugar level entries page$" )
    public void navigateToAddLevelsPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addSugarLevelsForm').click();" );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "Blood Sugar Levels" ) );
        assertEquals( "iTrust2: Add Blood Sugar Levels Form", driver.getTitle() );
    }

    @When ( "^I submit valid values for (.+), (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void addSugarLevels ( final String date, final int fasting, final int breakfast, final int lunch,
            final int dinner ) {
        waitForAngular();

        final WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( date.replace( "/", "" ) );

        driver.findElement( By.name( "fasting" ) ).clear();
        driver.findElement( By.name( "fasting" ) ).sendKeys( Integer.toString( fasting ) );
        driver.findElement( By.name( "breakfast" ) ).clear();
        driver.findElement( By.name( "breakfast" ) ).sendKeys( Integer.toString( breakfast ) );
        driver.findElement( By.name( "lunch" ) ).clear();
        driver.findElement( By.name( "lunch" ) ).sendKeys( Integer.toString( lunch ) );
        driver.findElement( By.name( "dinner" ) ).clear();
        driver.findElement( By.name( "dinner" ) ).sendKeys( Integer.toString( dinner ) );

        driver.findElement( By.name( "submit" ) ).click();

    }

    @Then ( "^my blood sugar levels for the day are succesfully added$" )
    public void checkForSuccess () {
        // final WebDriverWait wait = new WebDriverWait(driver, 20 );
        // wait.until( ExpectedConditions.titleContains( "Blood Sugar Levels" )
        // );
        assertTextPresent( "Blood sugar level entry has been added successfully." );
    }

    @When ( "^I submit invalid values for (.+), (.+), (.+), (.+), and (.+)$" )
    public void addSugarLevelsInvalid ( final String date, final String fasting, final String breakfast,
            final String lunch, final String dinner ) {
        waitForAngular();

        final WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( date.replace( "/", "" ) );

        driver.findElement( By.name( "fasting" ) ).clear();
        driver.findElement( By.name( "fasting" ) ).sendKeys( fasting );
        driver.findElement( By.name( "breakfast" ) ).clear();
        driver.findElement( By.name( "breakfast" ) ).sendKeys( breakfast );
        driver.findElement( By.name( "lunch" ) ).clear();
        driver.findElement( By.name( "lunch" ) ).sendKeys( lunch );
        driver.findElement( By.name( "dinner" ) ).clear();
        driver.findElement( By.name( "dinner" ) ).sendKeys( dinner );

        driver.findElement( By.name( "submit" ) ).click();

    }

    @Then ( "^my blood sugar levels entry is not added$" )
    public void checkForFailure () {
        assertTrue( driver.getPageSource().contains( "Could not add blood sugar levels." ) );
    }

    @And ( "^the patient has added an entry (.+), (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void sugarLevelsAdded ( final String date, final int fasting, final int breakfast, final int lunch,
            final int dinner ) {
        navigateToAddLevelsPage();
        addSugarLevels( date, fasting, breakfast, lunch, dinner );
        checkForSuccess();
    }

    @And ( "^the patient views the entry (.+), (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void viewEntryPatient ( final String date, final int fasting, final int breakfast, final int lunch,
            final int dinner ) {

        assertEquals( fasting, Integer.parseInt( driver.findElement( By.id( "fasting" ) ).getText() ) );
        assertEquals( breakfast, Integer.parseInt( driver.findElement( By.id( "breakfast" ) ).getText() ) );
        assertEquals( lunch, Integer.parseInt( driver.findElement( By.id( "lunch" ) ).getText() ) );
        assertEquals( dinner, Integer.parseInt( driver.findElement( By.id( "dinner" ) ).getText() ) );

    }

    @And ( "^this patient behavior is logged on the iTrust2 homepage.$" )
    public void checkLogsPatient () {
        driver.get( baseUrl );

        waitForAngular();
        assertTextPresent( "Patient Views Blood Sugar Levels" );
    }

    @Then ( "^the HCP has logged in and navigated to blood sugar level entries page$" )
    public void onViewPageHCP () {
        attemptLogout();

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( hcpString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('bloodSugarTable').click();" );

        assertEquals( "iTrust2: View Patient Blood Sugar Levels", driver.getTitle() );
    }

    @And ( "^the HCP views the entry (.+), (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void viewEntryHCP ( final String date, final int fasting, final int breakfast, final int lunch,
            final int dinner ) throws Throwable {
        waitForAngular();
        System.out.print( driver.getPageSource() );
        final Select hcpSelect = new Select( driver.findElement( By.name( "patientSelect" ) ) );
        hcpSelect.selectByVisibleText( "patient" );
        waitForAngular();
        assertTextPresent( date );
        assertTextPresent( Integer.toString( fasting ) );
        assertTextPresent( Integer.toString( breakfast ) );
        assertTextPresent( Integer.toString( lunch ) );
        assertTextPresent( Integer.toString( dinner ) );

        // assertEquals( fasting, Integer.parseInt( driver.findElement( By.id(
        // "fasting" ) ).getText() ) );
        // assertEquals( breakfast, Integer.parseInt( driver.findElement( By.id(
        // "breakfast" ) ).getText() ) );
        // assertEquals( lunch, Integer.parseInt( driver.findElement( By.id(
        // "lunch" ) ).getText() ) );
        // assertEquals( dinner, Integer.parseInt( driver.findElement( By.id(
        // "dinner" ) ).getText() ) );

    }

    @And ( "^this HCP behavior is logged on the iTrust2 homepage.$" )
    public void checkLoggingHCP () {
        driver.get( baseUrl );

        waitForAngular();
        assertTextPresent( "HCP Views Blood Sugar Levels" );
    }

    @Then ( "^the patient navigates to the blood sugar table page$" )
    public void navigateToSugarTablePage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('bloodSugarTable').click();" );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "Blood Sugar Table" ) );
        assertEquals( "iTrust2: Blood Sugar Table", driver.getTitle() );
        assertTextPresent( "Choose CSV File to Input Blood Sugar Data:" );
    }

    @When ( "^the patient clicks on Choose File and selects the valid CSV file$" )
    public void selectValidCSVFile () {
        // From:
        // https://stackoverflow.com/questions/9726005/how-to-click-on-input-type-file-across-browsers-using-selenium-webdriver
        // find the input element
        final WebElement elem = driver.findElement( By.xpath( "//input[@type='file']" ) );
        // 'type' the file location to it as it were a usual <input type='text'
        // /> element
        final String projectPath = System.getProperty( "user.dir" );
        System.out.print( projectPath );
        // String url =
        // this.getClass().getClassLoader().getResource("valid.csv").getPath();
        // this.getClass().getClassLoader().getResourceAsStream("SomeTextFile.txt");
        System.out.println( projectPath + File.separator + "valid.csv" );
        final String url = projectPath + File.separator + "valid.csv";
        // String url = this.getClass().getResource("valid.csv").getPath();
        elem.sendKeys( url );
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        System.out.print( "submitButton: " + submit.getText() );
        submit.click();
        waitForAngular();
        assertEquals( "Blood sugar level entry has been added successfully.",
                driver.findElement( By.name( "success" ) ).getText() );

        // assertTrue(driver.findElement( By.name( "success" )
        // ).getText().contains("Blood sugar level entry has been added
        // successfully."));
    }

    @Then ( "^the patient clicks on Choose File and selects the invalid CSV file$" )
    public void selectInvalidCSVFile () {
        // find the input element
        final WebElement elem = driver.findElement( By.xpath( "//input[@type='file']" ) );
        // 'type' the file location to it as it were a usual <input type='text'
        // /> element
        final String projectPath = System.getProperty( "user.dir" );
        System.out.print( projectPath );
        // String url =
        // this.getClass().getClassLoader().getResource("valid.csv").getPath();
        // this.getClass().getClassLoader().getResourceAsStream("SomeTextFile.txt");
        System.out.println( projectPath + File.separator + "invalid.csv" );
        final String url = projectPath + File.separator + "invalid.csv";
        // String url = this.getClass().getResource("valid.csv").getPath();
        elem.sendKeys( url );
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
        waitForAngular();
        assertEquals( "Could not add some blood sugar level entries from the CSV file.",
                driver.findElement( By.name( "errorMsg" ) ).getText() );

    }

    @Then ( "^new entries from the valid CSV file is displayed on the table$" )
    public void entriesDisplayedOnTable () {
        // find the input element
        // System.out.println(driver.getPageSource());
        waitForAngular();
        assertTextPresent( "2019-09-05" );
        assertTextPresent( "10" );
        assertTextPresent( "20" );
        assertTextPresent( "30" );
        assertTextPresent( "40" );
        assertTextPresent( "120" );
        assertTextPresent( "130" );
        assertTextPresent( "150" );
        assertTextPresent( "200" );
        assertTextPresent( "2019-10-06" );
    }
}
