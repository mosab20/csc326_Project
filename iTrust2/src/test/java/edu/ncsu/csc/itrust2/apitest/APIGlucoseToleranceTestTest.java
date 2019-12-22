package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.personnel.GlucoseToleranceTestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.GlucoseToleranceTest;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Test for the API functionality for interacting with GlucoseToleranceTest
 *
 * @author mgdoughe
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIGlucoseToleranceTestTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     *
     * @throws Exception
     */
    @Before
    public void setup () throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        HibernateDataGenerator.refreshDB();
    }

    /**
     * Creates a GlucoseToleranceTest through the backend to allow LabTech to
     * update it.
     *
     * @return The GlucoseToleranceTest that was created.
     *
     * @throws Exception
     */
    private GlucoseToleranceTest createGlucoseToleranceTest () throws Exception {
        // Create LabProcedureForm
        final GlucoseToleranceTestForm form = new GlucoseToleranceTestForm();
        form.setPatient( "patient" );
        assertEquals( "patient", form.getPatient() );
        form.setComments( "These are some fancy comments!" );
        assertEquals( "These are some fancy comments!", form.getComments() );
        form.setPriority( "1" );
        assertEquals( "1", form.getPriority() );
        form.setStatus( "1" );
        assertEquals( "1", form.getStatus() );
        form.setAssignedTech( "assignedTech" );
        assertEquals( "assignedTech", form.getAssignedTech() );
        form.setGlucoseToleranceTestResults( 155 );
        form.setHcpConfirmation( false );

        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();
        form.setLoincId( l.getId() );
        assertEquals( form.getLoincId(), l.getId() );
        final GeneralCheckup of = makeGeneralCheckup();
        form.setVisitId( of.getId() );
        assertEquals( of.getId(), form.getVisitId() );

        // Make a GlucoseToleranceTest from the form
        final GlucoseToleranceTest gtt = new GlucoseToleranceTest( form );
        gtt.save();
        return gtt;
    }

    /**
     * Makes an GeneralCheckup for testing
     *
     * @return The newly created GeneralCheckup
     */
    private GeneralCheckup makeGeneralCheckup () {
        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final GeneralCheckup visit = new GeneralCheckup();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        pres.setEndDate( LocalDate.now().plusDays( 10 ) );
        pres.setPatient( User.getByName( "AliceThirteen" ) );
        pres.setStartDate( LocalDate.now() );
        pres.setRenewals( 5 );

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();
        return visit;
    }

    /**
     * Tests LabProcedure API For HCP
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP", "ADMIN" } )
    public void testGlucoseToleranceTestHCPAPI () throws Exception {

        mvc.perform( delete( "/api/v1/labprocedures" ) );

        /* Assert the LabProcedure was created correctly */
        /*
         * Create a HCP, LabTech, and a Patient to use. If they already exist,
         * this will do nothing
         */
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final UserForm labtech = new UserForm( "labtech", "123456", Role.ROLE_LABTECH, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( labtech ) ) );

        /* Create a Hospital to use too */
        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();

        final GeneralCheckup visit = new GeneralCheckup();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hospital );
        visit.setPatient( User.getByName( "patient" ) );
        visit.setHcp( User.getByName( "patient" ) );
        visit.setDate( ZonedDateTime.now() );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        pres.setEndDate( LocalDate.now().plusDays( 10 ) );
        pres.setPatient( User.getByName( "patient" ) );
        pres.setStartDate( LocalDate.now() );
        pres.setRenewals( 5 );

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();
        final GlucoseToleranceTestForm form = new GlucoseToleranceTestForm();
        form.setPatient( "patient" );
        form.setComments( "These are some fancy comments!" );
        form.setPriority( "1" );
        form.setStatus( "1" );
        form.setAssignedTech( labtech.getUsername() );
        form.setLoincId( l.getId() );
        form.setVisitId( visit.getId() );

        /* Create the Lab Procedure */
        mvc.perform( post( "/api/v1/glucoseTest" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );

        final GlucoseToleranceTest gtt = GlucoseToleranceTest.getByVisit( form.getVisitId() ).get( 0 );
        assertEquals( "patient", gtt.getPatient().getUsername() );
        assertEquals( "These are some fancy comments!", gtt.getComments() );
        assertEquals( 1, gtt.getPriority().getCode() );
        assertEquals( 1, gtt.getStatus().getCode() );
        assertEquals( "labtech", gtt.getAssignedTech().getUsername() );
        assertEquals( form.getLoincId(), gtt.getLoinc().getId() );
        assertEquals( form.getVisitId(), gtt.getVisit().getId() );

        /* Attempt to repost GlucoseToleranceTest */
        form.setId( gtt.getId() );
        mvc.perform( post( "/api/v1/glucoseTest" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isConflict() );

        /* Trigger the invalid LabProcedure Path */
        form.setId( -1L );
        mvc.perform( post( "/api/v1/glucoseTest" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );

        /* Make sure we can get all the glucoseTest */
        mvc.perform( get( "/api/v1/glucoseTest" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

    }

    /**
     * Tests LabProcedure API For LabTech
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "labtech", roles = { "USER", "LABTECH", "HCP" } )
    public void testGlucoseToleranceTestLabTechAPI () throws Exception {

        final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        final User assignedTech = new User( "assignedTech",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_LABTECH, 1 );
        final User assignedTech2 = new User( "assignedTech2",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_LABTECH, 1 );
        final User patient2 = new User( "patient2", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_LABTECH, 1 );

        assignedTech.save();
        assignedTech2.save();
        patient.save();
        patient2.save();
        GlucoseToleranceTest gtt = createGlucoseToleranceTest();

        /* Update the LabProcedure */
        final GlucoseToleranceTestForm form = new GlucoseToleranceTestForm();
        form.setComments( "Comments." );
        form.setStatus( "2" );
        form.setAssignedTech( assignedTech2.getUsername() );

        /*
         * We need the ID of the office visit that actually got _saved_ when
         * calling the API above. This will get it
         */
        final Long id = GlucoseToleranceTest.getByVisit( gtt.getVisit().getId() ).get( 0 ).getId();

        form.setId( id );
        mvc.perform( put( "/api/v1/glucoseTest/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        /* Assert the LabProcedure was updated */
        final GeneralCheckup visit = gtt.getVisit();
        final LOINC l = gtt.getLoinc();
        gtt = GlucoseToleranceTest.getByVisit( visit.getId() ).get( 0 );

    }

}
