package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.itrust2.adapters.LocalDateAdapter;
import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDataForm;

/**
 * Class representing a BloodSugarData object. This deals with any information
 * that will be stored in the DB to describe a patient's blood sugar levels.
 *
 * @author Griffin Buising (grbuisin)
 *
 */
@Entity
@Table ( name = "BloodSugarData" )
public class BloodSugarData extends DomainObject<BloodSugarData> implements Serializable {

    /**
     * Randomly generated ID.
     */
    private static final long serialVersionUID = 424094327895115508L;

    /**
     * Get a specific diary entry by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific BloodSugarData with the desired ID
     */
    public static BloodSugarData getById ( final Long id ) {
        try {
            return (BloodSugarData) getWhere( BloodSugarData.class, eqList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get a specific diary entry by the database date
     *
     * @param date
     *            the database date
     * @return the specific BloodSugarData with the desired date
     */
    public static BloodSugarData getByDate ( final LocalDate date ) {
        try {
            return (BloodSugarData) getWhere( BloodSugarData.class, eqList( "date", date ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get a list of BloodSugarData by patient.
     *
     * @param patient
     *            the username of the patient whose entries are being searched
     *            for
     * @return a list of BloodSugarData for the given patient
     */
    @SuppressWarnings ( "unchecked" )
    public static List<BloodSugarData> getByPatient ( final String patient ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( eq( "patient", patient ) );

        return (List<BloodSugarData>) getWhere( BloodSugarData.class, criteria );
    }

    /**
     * Default constructor for making a BloodSugarData that will have its values
     * set without a form.
     */
    public BloodSugarData () {
    }

    /**
     * Creates a BloodSugarData based on the BloodSugarDataForm
     *
     * @param def
     *            BloodSugarData to create
     */
    public BloodSugarData ( final BloodSugarDataForm def ) {
        setDate( LocalDate.parse( def.getDate() ) );
        setFastingLevel( def.getFastingLevel() );
        setBreakfastLevel( def.getBreakfastLevel() );
        setLunchLevel( def.getLunchLevel() );
        setDinnerLevel( def.getDinnerLevel() );
    }

    /**
     * The date as milliseconds since epoch of this BloodSugarData
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate date;

    /**
     * Blood sugar level, in mg/dl, right after waking up
     */
    @NotNull
    private Integer   fastingLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating breakfast
     */
    @NotNull
    private Integer   breakfastLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating lunch
     */
    @NotNull
    private Integer   lunchLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating dinner
     */
    @NotNull
    private Integer   dinnerLevel;

    /**
     * The username of the patient for this DiaryEntry
     */
    @Length ( max = 20 )
    private String    patient;

    /**
     * The id of this DiaryEntry
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long      id;

    /**
     * Gets the fasting BSL
     *
     * @return the fastingLevel
     */
    public Integer getFastingLevel () {
        return fastingLevel;
    }

    /**
     * Sets the fasting BSL
     *
     * @param fastingLevel
     *            the fastingLevel to set
     */
    public void setFastingLevel ( final Integer fastingLevel ) {
        if ( fastingLevel < 0 ) {
            throw new IllegalArgumentException( "Fasting level must be a non-negative integer!" );
        }
        this.fastingLevel = fastingLevel;
    }

    /**
     * Gets the breakfast BSL
     *
     * @return the breakfastLevel
     */
    public Integer getBreakfastLevel () {
        return breakfastLevel;
    }

    /**
     * Sets the breakfast BSL
     *
     * @param breakfastLevel
     *            the breakfastLevel to set
     */
    public void setBreakfastLevel ( final Integer breakfastLevel ) {
        if ( breakfastLevel < 0 ) {
            throw new IllegalArgumentException( "Breakfast level must be a non-negative integer!" );
        }
        this.breakfastLevel = breakfastLevel;
    }

    /**
     * Gets the lunch BSL
     *
     * @return the lunchLevel
     */
    public Integer getLunchLevel () {
        return lunchLevel;
    }

    /**
     * Sets the lunch BSL
     *
     * @param lunchLevel
     *            the lunchLevel to set
     */
    public void setLunchLevel ( final Integer lunchLevel ) {
        if ( lunchLevel < 0 ) {
            throw new IllegalArgumentException( "Lunch level must be a non-negative integer!" );
        }
        this.lunchLevel = lunchLevel;
    }

    /**
     * Gets the dinner BSL
     *
     * @return the dinnerLevel
     */
    public Integer getDinnerLevel () {
        return dinnerLevel;
    }

    /**
     * Sets the dinner BSL
     *
     * @param dinnerLevel
     *            the dinnerLevel to set
     */
    public void setDinnerLevel ( final Integer dinnerLevel ) {
        if ( dinnerLevel < 0 ) {
            throw new IllegalArgumentException( "Dinner level must be a non-negative integer!" );
        }
        this.dinnerLevel = dinnerLevel;
    }

    /**
     * Gets the date for this DiaryEntry
     *
     * @return the date as milliseconds since epoch
     */
    public LocalDate getDate () {
        return date;
    }

    /**
     * Sets the date for this DiaryEntry
     *
     * @param date
     *            the diary date
     */
    public void setDate ( final LocalDate date ) {
        if ( date.isAfter( LocalDate.now() ) ) {
            throw new IllegalArgumentException( "Date must be before current date" );
        }
        this.date = date;
    }

    /**
     * Gets the patient for this DiaryEntry
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the patient for this DiaryEntry
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Get the ID of this DiaryEntry
     *
     * @return the ID of this DiaryEntry
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this DiaryEntry
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }
}
