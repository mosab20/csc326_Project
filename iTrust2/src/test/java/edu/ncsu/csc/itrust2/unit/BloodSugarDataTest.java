package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDataForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarData;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class for testing BloodSugarData and BloodSugarDataForm
 *
 * @author Griffin Buising (grbuisin)
 *
 */
public class BloodSugarDataTest {

    /** Patient to be used for testing */
    final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_PATIENT, 1 );

    /**
     * Sets up data base for testing
     */
    @Before
    public void setUp () {
        DomainObject.deleteAll( BloodSugarData.class );
    }

    /**
     * Testing BloodSugarData with a default constructor and with a
     * BloodSugarDataForm passed into the constructor. Also tests all Setters
     * and getById.
     */
    @Test
    public void testBloodSugarData () {
        final BloodSugarData entry = new BloodSugarData();
        final LocalDate entryDate = LocalDate.parse( "2018-09-03" );

        entry.setDate( entryDate );
        entry.setFastingLevel( 40 );
        entry.setBreakfastLevel( 70 );
        entry.setLunchLevel( 60 );
        entry.setDinnerLevel( 80 );
        entry.setPatient( "patient" );
        entry.save();

        final BloodSugarData copy = BloodSugarData.getById( entry.getId() );
        assertEquals( entry.getId(), copy.getId() );
        assertEquals( entry.getBreakfastLevel(), copy.getBreakfastLevel() );
        assertEquals( entry.getLunchLevel(), copy.getLunchLevel() );
        assertEquals( entry.getDinnerLevel(), copy.getDinnerLevel() );
        assertEquals( entry.getFastingLevel(), copy.getFastingLevel() );
        assertEquals( entry.getPatient(), copy.getPatient() );

        final BloodSugarDataForm def = new BloodSugarDataForm();
        def.setDate( entryDate.toString() );
        def.setBreakfastLevel( 70 );
        def.setLunchLevel( 60 );
        def.setDinnerLevel( 80 );
        def.setFastingLevel( 40 );
        final BloodSugarData entry2 = new BloodSugarData( def );
        entry2.setPatient( "patient" );
        entry2.save();

        assertNotEquals( entry.getId(), entry2.getId() );
        assertEquals( entry.getBreakfastLevel(), entry2.getBreakfastLevel() );
        assertEquals( entry.getLunchLevel(), entry2.getLunchLevel() );
        assertEquals( entry.getDinnerLevel(), entry2.getDinnerLevel() );
        assertEquals( entry.getFastingLevel(), entry2.getFastingLevel() );
        assertEquals( entry.getPatient(), entry2.getPatient() );
    }

    /**
     * Tests giving invalid values for setter methods.
     */
    @Test
    public void testBloodSugarDataInvalid () {
        final BloodSugarData entry = new BloodSugarData();

        try {
            entry.setDate( LocalDate.parse( "3000-09-03" ) );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Date must be before current date", e.getMessage() );
        }
        try {
            entry.setFastingLevel( -10 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Fasting level must be a non-negative integer!", e.getMessage() );
        }
        try {
            entry.setBreakfastLevel( -1 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Breakfast level must be a non-negative integer!", e.getMessage() );
        }
        try {
            entry.setLunchLevel( -900 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Lunch level must be a non-negative integer!", e.getMessage() );
        }
        try {
            entry.setDinnerLevel( -45 );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Dinner level must be a non-negative integer!", e.getMessage() );
        }
    }

    /**
     * Test getUniqueDatesByPatient method.
     */
    @Test
    public void testGetByPatient () {
        final BloodSugarData entry = new BloodSugarData();
        final LocalDate entryDate = LocalDate.parse( "2018-09-03" );

        entry.setDate( entryDate );
        entry.setFastingLevel( 40 );
        entry.setBreakfastLevel( 70 );
        entry.setLunchLevel( 60 );
        entry.setDinnerLevel( 80 );
        entry.setPatient( "patient" );
        entry.save();

        assertEquals( 1, BloodSugarData.getByPatient( "patient" ).size() );

        final BloodSugarData entry2 = new BloodSugarData();

        entry2.setDate( entryDate );
        entry2.setFastingLevel( 40 );
        entry2.setBreakfastLevel( 70 );
        entry2.setLunchLevel( 60 );
        entry2.setDinnerLevel( 80 );
        entry2.setPatient( "patient" );
        entry2.save();

        assertEquals( 2, BloodSugarData.getByPatient( "patient" ).size() );
    }

}
