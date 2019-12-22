package edu.ncsu.csc.itrust2.forms.personnel;

import edu.ncsu.csc.itrust2.models.enums.DiabetesDiagnosisType;
import edu.ncsu.csc.itrust2.models.persistent.GlucoseToleranceTest;

/**
 * LabProcedureForm used to document a LabProcedure. This will be validated and
 * converted to a LabProcedure to be stored in the database.
 *
 * @author Alex Phelps
 *
 */
public class GlucoseToleranceTestForm extends LabProcedureForm {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long     serialVersionUID = 1L;

    /**
     * The glucose Tolerance Test Results (mg/dl)
     */
    private int                   glucoseToleranceTestResults;

    /**
     * The diagnosis of the patient's test
     */
    private DiabetesDiagnosisType diabetesDiagnosis;

    /**
     * Whether or not the hcp has Confirmed
     */
    private boolean               hcpConfirmation;

    /**
     * Empty constructor so that we can create an GlucoseToleranceTestForm for
     * the user to fill out
     */
    public GlucoseToleranceTestForm () {

    }

    /**
     * Creates a LabProcedureForm from the LabProcedure provided
     *
     * @param gtt
     *            LabProcedure to turn into a LabProcedureForm
     */
    public GlucoseToleranceTestForm ( final GlucoseToleranceTest gtt ) {
        super.setLoincId( gtt.getLoinc().getId() );
        super.setPatient( gtt.getPatient().getUsername() );
        super.setPriority( Integer.toString( gtt.getPriority().getCode() ) );
        super.setComments( gtt.getComments() );
        super.setStatus( Integer.toString( gtt.getStatus().getCode() ) );
        super.setId( gtt.getId() );
        super.setAssignedTech( gtt.getAssignedTech().getUsername() );
        super.setVisitId( gtt.getVisit().getId() );

        setGlucoseToleranceTestResults( gtt.getGlucoseToleranceTestResults() );
        setDiabetesDiagnosis( gtt.getDiabetesDiagnosis() );

        if ( gtt.getLoinc().getCode().equals( "49689-3" ) ) { // is oral test
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
        else if ( gtt.getLoinc().getCode().equals( "49689-4" ) ) { // is blood
                                                                   // test
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
        else if ( gtt.getLoinc().getCode().equals( "49689-5" ) ) { // is
                                                                   // gestational
                                                                   // test
            if ( getGlucoseToleranceTestResults() < 140 ) {
                setDiabetesDiagnosis( DiabetesDiagnosisType.NORMAL );
            }
            else {
                setDiabetesDiagnosis( DiabetesDiagnosisType.DIABETES );
            }
        }
        setHcpConfirmation( gtt.isHcpConfirmation() );
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

}
