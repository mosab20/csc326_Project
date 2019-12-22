package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum representing the status of patient as a diabetic .
 *
 * @author mrgray4
 */
public enum DiabetesDiagnosisType {

    /**
     * This field is unrelated
     */
    NONAPPLICABLE ( 0 ),
    /**
     * NORMAL glucose levels
     */
    NORMAL ( 1 ),
    /**
     * Prediabetes
     */
    PREDIABETES ( 2 ),
    /**
     * Diabetes
     */
    DIABETES ( 3 ),

    ;

    /**
     * Code of the status
     */
    private int code;

    /**
     * Create a Status from the numerical code.
     *
     * @param code
     *            Code of the Status
     */
    private DiabetesDiagnosisType ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the Status
     *
     * @return Code of the Status
     */
    public int getCode () {
        return code;
    }

    /**
     * Converts a code to a named diabetes status.
     *
     * @param code
     *            The smoking code.
     * @return The string represented by the code.
     */
    public static String getName ( final int code ) {
        return DiabetesDiagnosisType.parseValue( code ).toString();
    }

    /**
     * Returns the DiabetesDiagnosisType enum that matches the given code.
     *
     * @param code
     *            The code to match
     * @return Corresponding DiabetesDiagnosisType object.
     */
    public static DiabetesDiagnosisType parseValue ( final int code ) {
        for ( final DiabetesDiagnosisType status : values() ) {
            if ( status.getCode() == code ) {
                return status;
            }
        }
        return DiabetesDiagnosisType.NONAPPLICABLE;
    }

}
