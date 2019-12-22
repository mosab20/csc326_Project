
package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDataForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarData;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Test for the API functionality for interacting with blood sugar entries
 *
 * @author Dalton Teague (dnteague)
 * @author Griffin Buising (grbuisin)
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIBloodSugarDataTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        final Patient p = Patient.getByName( "patient" );
        if ( p != null ) {
            p.delete();
        }
    }

    /**
     * Tests APIBloodSugarDataController's addEntry Endpoint with an invalid
     * entry
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testBloodSugarAPIInvalidEntry () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final BloodSugarDataForm bsd = new BloodSugarDataForm();
        bsd.setDate( "2018-09-03" );
        bsd.setFastingLevel( -190 );
        bsd.setBreakfastLevel( 70 );
        bsd.setLunchLevel( 60 );
        bsd.setDinnerLevel( 80 );

        mvc.perform( post( "/api/v1/bloodsugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( bsd ) ) ).andExpect( status().isBadRequest() );

        mvc.perform( post( "/api/v1/bloodsugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( null ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Tests APIBloodSugarDataController's endpoints as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testBloodSugarAPIAsPatient () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final BloodSugarDataForm bsd = new BloodSugarDataForm();
        bsd.setDate( "2018-09-03" );
        bsd.setFastingLevel( 190 );
        bsd.setBreakfastLevel( 190 );
        bsd.setLunchLevel( 190 );
        bsd.setDinnerLevel( 190 );

        try {
            mvc.perform( get( "/api/v1/bloodsugar/patient" ) ).andExpect( status().isForbidden() );
            fail();
        }
        catch ( final Exception e ) {
            assertTrue( e.getCause() instanceof AccessDeniedException );
        }

        mvc.perform( post( "/api/v1/bloodsugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( bsd ) ) ).andExpect( status().isOk() );

        List<BloodSugarData> bsdEntries = BloodSugarData.getByPatient( "patient" );
        assertEquals( 1, bsdEntries.size() );

        List<LogEntry> entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.PATIENT_BLOOD_SUGAR_LEVELS_ENTERED,
                entries.get( entries.size() - 1 ).getLogCode() );

        mvc.perform( get( "/api/v1/bloodsugar" ) ).andExpect( status().isOk() )
        .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.PATIENT_BLOOD_SUGAR_LEVELS_VIEWED,
                entries.get( entries.size() - 1 ).getLogCode() );

        // Posting on same date should update previous values
        bsd.setFastingLevel( 200 );
        bsd.setBreakfastLevel( 150 );
        bsd.setLunchLevel( 220 );
        bsd.setDinnerLevel( 180 );
        mvc.perform( post( "/api/v1/bloodsugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( bsd ) ) ).andExpect( status().isOk() );

        // Make sure size is still 1
        bsdEntries = BloodSugarData.getByPatient( "patient" );
        assertEquals( 1, bsdEntries.size() );

        // Add entry with different date
        bsd.setDate( "2018-09-04" );

        mvc.perform( post( "/api/v1/bloodsugar" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( bsd ) ) ).andExpect( status().isOk() );

        // Make sure size is now 2
        bsdEntries = BloodSugarData.getByPatient( "patient" );
        assertEquals( 2, bsdEntries.size() );

    }

    /**
     * Tests APIBloodSugarDataController's endpoints as an HCP.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testBloodSugarAPIAsHCP () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        try {
            mvc.perform( get( "/api/v1/bloodsugar" ) ).andExpect( status().isForbidden() );
            fail();
        }
        catch ( final Exception e ) {
            assertTrue( e.getCause() instanceof AccessDeniedException );
        }

        mvc.perform( get( "/api/v1/bloodsugar/patientdne" ) ).andExpect( status().isNotFound() );

        mvc.perform( get( "/api/v1/bloodsugar/patient" ) ).andExpect( status().isOk() )
        .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        final List<LogEntry> entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.HCP_BLOOD_SUGAR_LEVELS_VIEWED, entries.get( entries.size() - 1 ).getLogCode() );
    }

}
