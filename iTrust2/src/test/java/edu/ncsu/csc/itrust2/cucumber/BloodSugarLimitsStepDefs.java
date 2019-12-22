package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Step definitions for FoodDiaryEntry feature.
 *
 * @author Jack MacDonald (jmmacdo4)
 */
public class BloodSugarLimitsStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

    private void setTextField ( final By byval, final String value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value );
    }

    @When ( "she changes the blood sugar limits to: (\\d+), (\\d+)" )
    public void changeLimits ( final int fasting, final int aftermeal ) throws Exception {
        waitForAngular();
        setTextField( By.name( "fastingBSL" ), Integer.toString( fasting ) );
        setTextField( By.name( "mealsBSL" ), Integer.toString( aftermeal ) );
    }

    @Then ( "a limit error message is displayed" )
    public void checkErrorMessage () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.name( "error" ) ).getText()
                    .contains( "After meal the blood sugar levels should be between 120 to 180 mg/dl" ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

}
