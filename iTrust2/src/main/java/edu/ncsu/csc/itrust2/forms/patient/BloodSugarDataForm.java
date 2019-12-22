package edu.ncsu.csc.itrust2.forms.patient;

import org.hibernate.validator.constraints.NotEmpty;

public class BloodSugarDataForm {

    /**
     * Empty constructor to make a BloodSugarDataForm for the user to fill out
     */
    public BloodSugarDataForm () {
    }

    /**
     * The date as milliseconds since epoch for the entry
     */
    private String  date;

    /**
     * Blood sugar level, in mg/dl, right after waking up
     */
    @NotEmpty
    private Integer fastingLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating breakfast
     */
    @NotEmpty
    private Integer breakfastLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating lunch
     */
    @NotEmpty
    private Integer lunchLevel;

    /**
     * Blood sugar level, in mg/dl, right after eating dinner
     */
    @NotEmpty
    private Integer dinnerLevel;

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
        this.dinnerLevel = dinnerLevel;
    }

    /**
     * Gets the date
     *
     * @return the diary date
     */
    public String getDate () {
        return date;
    }

    /**
     * Sets the date
     *
     * @param date
     *            the diary date to set
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

}
