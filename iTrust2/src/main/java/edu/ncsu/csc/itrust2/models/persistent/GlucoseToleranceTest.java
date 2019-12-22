package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.personnel.GlucoseToleranceTestForm;
import edu.ncsu.csc.itrust2.models.enums.DiabetesDiagnosisType;
import edu.ncsu.csc.itrust2.models.enums.LabStatus;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * This is the validated database-persisted GlucoseToleranceTest representation.
 * Used LabProcedure.java as a templete.
 *
 * @author mgdoughe
 *
 */
@Entity
@Table ( name = "GlucoseToleranceTest" )
public class GlucoseToleranceTest extends DomainObject<GlucoseToleranceTest> {
    /**
     * The LOINC of this Lab Procedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "LOINC_code" )
    private LOINC                 loinc;

    /**
     * The Priority of this LabProcedure
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private Priority              priority;

    /**
     * Comments on this LabProcedure
     */
    private String                comments;

    /**
     * The Status of this LabProcedure
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private LabStatus             status;

    /**
     * The id of this LabProcedure
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                  id;

    /**
     * The Lab Tech assigned to this LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "labtech", columnDefinition = "varchar(100)" )
    private User                  labtech;

    /**
     * The Office Visit of the LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "visit", nullable = false )
    private GeneralCheckup        visit;

    /**
     * The User assigned to this LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient", columnDefinition = "varchar(100)" )
    private User                  patient;

    /**
     * The glucose Tolerance Test Results (mg/dl)
     */
    @NotNull
    @Min ( value = 0 )
    private int                   glucoseToleranceTestResults;

    /**
     * The diagnosis of the patient's test
     */
    @Enumerated ( EnumType.STRING )
    private DiabetesDiagnosisType diabetesDiagnosis;

    /**
     * Whether or not the hcp has Confirmed
     */
    private boolean               hcpConfirmation;

    /**
     * Get a specific lab procedure by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific lab procedure with the desired ID
     */
    public static GlucoseToleranceTest getById ( final Long id ) {
        try {
            return getWhere( eqList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get a specific lab procedure by the office visit ID
     *
     * @param id
     *            the database ID
     * @return the specific lab procedure with the desired visit ID
     */
    public static List<GlucoseToleranceTest> getByVisit ( final Long id ) {
        try {
            return getWhere( eqList( "visit", GeneralCheckup.getById( id ) ) );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get all lab procedures for a specific patient
     *
     * @param patientName
     *            the name of the patient
     * @return the lab procedures associated with the queried patient
     */
    public static List<GlucoseToleranceTest> getForPatient ( final String patientName ) {
        return getWhere( eqList( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Get all lab procedures for a specific patient
     *
     * @param id
     *            the id of the Office Visit
     * @return the lab procedures associated with the queried GeneralCheckup
     */
    public static List<GlucoseToleranceTest> getForVisit ( final Long id ) {
        return getWhere( eqList( "visit", GeneralCheckup.getById( id ) ) );
    }

    /**
     * Get all lab procedures for a specific Lab Tech
     *
     * @param techName
     *            the name of the Lab Tech
     * @return the lab procedures of the queried Lab Tech
     */
    public static List<GlucoseToleranceTest> getForLabtech ( final String techName ) {
        return getWhere( eqList( "labtech", User.getByNameAndRole( techName, Role.ROLE_LABTECH ) ) );
    }

    /**
     * Get all lab procedures done by a specific Lab Tech for a specific patient
     *
     * @param techName
     *            the name of the Lab Tech
     * @param patientName
     *            the name of the patient
     * @return the lab procedures of the queried Lab Tech, patient combo
     */
    public static List<GlucoseToleranceTest> getForTechAndPatient ( final String techName, final String patientName ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( eq( "labtech", User.getByNameAndRole( techName, Role.ROLE_LABTECH ) ) );
        criteria.add( eq( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
        return getWhere( criteria );

    }

    /**
     * Get all lab procedures in the database
     *
     * @return all lab procedures in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<GlucoseToleranceTest> getGlucoseToleranceTests () {
        final List<GlucoseToleranceTest> procedures = (List<GlucoseToleranceTest>) getAll( GlucoseToleranceTest.class );
        return procedures;
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<LabProcedure> Because get all
     *                   just returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    private static List<GlucoseToleranceTest> getWhere ( final List<Criterion> where ) {
        return (List<GlucoseToleranceTest>) getWhere( GlucoseToleranceTest.class, where );
    }

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public GlucoseToleranceTest () {
    }

    /**
     * Creates an SatisfactionSurvey from the SatisfactionSurveyForm provided
     *
     * @param gttf
     *            SatisfactionSurveyForm The SatisfactionSurveyForm to create an
     *            SatisfactionSurvey out of
     * @throws ParseException
     *             If the date & time in the OfficeVisitForm cannot be parsed
     *             properly
     * @throws NumberFormatException
     *             If the ID cannot be parsed to a Long.
     */
    public GlucoseToleranceTest ( final GlucoseToleranceTestForm gttf ) throws Exception, ParseException {
        setLoinc( LOINC.getById( gttf.getLoincId() ) );
        setPatient( User.getByName( gttf.getPatient() ) );
        try {
            setPriority( Priority.parseIntValue( ( Integer.parseInt( gttf.getPriority() ) ) ) );
        }
        catch ( final NumberFormatException nfe ) {
            setPriority( Priority.parseStrValue( gttf.getPriority() ) );
        }
        setComments( gttf.getComments() );
        try {
            setStatus( LabStatus.parseValue( Integer.parseInt( gttf.getStatus() ) ) );
        }
        catch ( final NumberFormatException nfe ) {
            setStatus( LabStatus.parseStrValue( gttf.getStatus() ) );
        }
        if ( gttf.getId() != null ) {
            setId( gttf.getId() );
        }
        setAssignedTech( User.getByName( gttf.getAssignedTech() ) );
        setVisit( GeneralCheckup.getById( gttf.getVisitId() ) );

        setGlucoseToleranceTestResults( gttf.getGlucoseToleranceTestResults() );
        setDiabetesDiagnosis( gttf.getDiabetesDiagnosis() );

        if ( loinc.getCode().equals( "49689-3" ) ) { // is oral test
            if ( getGlucoseToleranceTestResults() < 140 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.NORMAL );
            }
            else if ( getGlucoseToleranceTestResults() < 200 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.PREDIABETES );
            }
            else {
                setDiabetesDiagnosis( DiabetesDiagnosisType.DIABETES );
            }
        }
        else if ( loinc.getCode().equals( "49689-4" ) ) { // is blood test
            if ( getGlucoseToleranceTestResults() < 100 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.NORMAL );
            }
            else if ( getGlucoseToleranceTestResults() < 126 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.PREDIABETES );
            }
            else {
                setDiabetesDiagnosis( DiabetesDiagnosisType.DIABETES );
            }
        }
        else if ( loinc.getCode().equals( "49689-5" ) ) { // is gestational test
            if ( getGlucoseToleranceTestResults() < 140 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.NORMAL );
            }
            else {
                setDiabetesDiagnosis( DiabetesDiagnosisType.DIABETES );
            }
        }

        setHcpConfirmation( gttf.isHcpConfirmation() );
    }

    /**
     * Gets the LOINC object of this LabProcedure
     *
     * @return The LOINC object used for this LabProcedure
     */
    public LOINC getLoinc () {
        return loinc;
    }

    /**
     * Sets the LOINC object of this LabProcedure
     *
     * @param loinc
     *            The LOINC object to set for this LabProcedure
     */
    public void setLoinc ( final LOINC loinc ) {
        this.loinc = loinc;
    }

    /**
     * Get this LabProcedure's priority
     *
     * @return The priority of this LabProcedure
     */
    public Priority getPriority () {
        return priority;
    }

    /**
     * Sets the priority of this LabProcedure
     *
     * @param priority
     *            The priority of the LabProcedure
     */
    public void setPriority ( final Priority priority ) {
        this.priority = priority;
    }

    /**
     * Gets the comment(s) on the LabProcedure
     *
     * @return The comment(s) on the LabProcedure
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the comment(s) on the LabProcedure
     *
     * @param comments
     *            The comment(s) to set for the LabProcedure
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the status of the LabProcedure
     *
     * @return The status of this LabProcedure
     */
    public LabStatus getStatus () {
        return status;
    }

    /**
     * Sets/updates the status of the LabProcedure
     *
     * @param status
     *            The status of the LabProcedure
     */
    public void setStatus ( final LabStatus status ) {
        this.status = status;
    }

    /**
     * Get's the id of this LabProcedure
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this LabProcedure
     *
     * @param id
     *            The id of this LabProcedure
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the assigned Lab Tech of this LabProcedure
     *
     * @return The assigned Lab Tech of this LabProcedure
     */
    public User getAssignedTech () {
        return labtech;
    }

    /**
     * Assigns a Lab Tech to this LabProcedure
     *
     * @param labtech
     *            The Lab Tech to assign to this LabProcedure
     */
    public void setAssignedTech ( final User labtech ) {
        this.labtech = labtech;
    }

    /**
     * Gets the GeneralCheckup associated with this LabProcedure
     *
     * @return The GeneralCheckup associated with this LabProcedure
     */
    public GeneralCheckup getVisit () {
        return visit;
    }

    /**
     * Sets the GeneralCheckup for this LabProcedure
     *
     * @param visit
     *            The GeneralCheckup to set for this LabProcedure
     */
    public void setVisit ( final GeneralCheckup visit ) {
        this.visit = visit;
    }

    /**
     * Get the patient of this LabProcedure
     *
     * @return the patient of this LabProcedure
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Set the patient of this LabProcedure
     *
     * @param patient
     *            the patient to set this LabProcedure for
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Deletes the LabProcedure
     */
    @Override
    public void delete () {
        super.delete();
    }

    /**
     * Deletes all LabProcedures
     */
    public static void deleteAll () {
        DomainObject.deleteAll( LabProcedure.class );
    }

    /**
     * get the glucoseToleranceTestResults
     *
     * @return the glucoseToleranceTestResults
     */
    public int getGlucoseToleranceTestResults () {
        return glucoseToleranceTestResults;
    }

    /**
     * set the glucoseToleranceTestResults
     *
     * @param glucoseToleranceTestResults
     *            the glucoseToleranceTestResults to set
     */
    public void setGlucoseToleranceTestResults ( final int glucoseToleranceTestResults ) {
        this.glucoseToleranceTestResults = glucoseToleranceTestResults;
    }

    /**
     * get the diabetesDiagnosis
     *
     * @return the diabetesDiagnosis
     */
    public DiabetesDiagnosisType getDiabetesDiagnosis () {
        return diabetesDiagnosis;
    }

    /**
     * set the diabetesDiagnosis
     *
     * @param diabetesDiagnosis
     *            the diabetesDiagnosis to set
     */
    public void setDiabetesDiagnosis ( final DiabetesDiagnosisType diabetesDiagnosis ) {
        this.diabetesDiagnosis = diabetesDiagnosis;
    }

    /**
     * if the hcpConfirmation
     *
     * @return the hcpConfirmation
     */
    public boolean isHcpConfirmation () {
        return hcpConfirmation;
    }

    /**
     * set the hcpConfirmation
     *
     * @param hcpConfirmation
     *            the hcpConfirmation to set
     */
    public void setHcpConfirmation ( final boolean hcpConfirmation ) {
        this.hcpConfirmation = hcpConfirmation;
    }

    /**
     * Saves the basic domain object.
     */
    @Override
    public void save () {
        try {
            super.save();
        }
        catch ( final Exception ex ) {
            throw ex;
        }

    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof GlucoseToleranceTest ) {
            final GlucoseToleranceTest v = (GlucoseToleranceTest) o;
            return getId().equals( v.getId() );
        }
        return false;
    }

}
