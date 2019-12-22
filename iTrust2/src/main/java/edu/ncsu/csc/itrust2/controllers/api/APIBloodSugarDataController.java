package edu.ncsu.csc.itrust2.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDataForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarData;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the BloodSugarData model. Uses a
 * patient's username to create and retrieve entries.
 *
 * @author Griffin Buising (grbuisin)
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
@RestController
public class APIBloodSugarDataController extends APIController {

    /**
     * Creates a new BloodSugarData object and saves it to the DB
     *
     * @param entry
     *            the form being used to create a DiaryEntry object
     * @return a response containing results of creating a new entry
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "bloodsugar" )
    public ResponseEntity addEntry ( @RequestBody final BloodSugarDataForm entry ) {
        try {

            final BloodSugarData dEntry = new BloodSugarData( entry );
            final BloodSugarData existingEntry = BloodSugarData.getByDate( dEntry.getDate() );

            if ( existingEntry != null ) {

                existingEntry.setPatient( LoggerUtil.currentUser() );
                existingEntry.setFastingLevel( entry.getFastingLevel() );
                existingEntry.setBreakfastLevel( entry.getBreakfastLevel() );
                existingEntry.setLunchLevel( entry.getLunchLevel() );
                existingEntry.setDinnerLevel( entry.getDinnerLevel() );
                existingEntry.save();

                LoggerUtil.log( TransactionType.PATIENT_BLOOD_SUGAR_LEVELS_ENTERED, LoggerUtil.currentUser() );
                return new ResponseEntity( existingEntry, HttpStatus.OK );
            }
            else {
                dEntry.setPatient( LoggerUtil.currentUser() );
                dEntry.save();

                LoggerUtil.log( TransactionType.PATIENT_BLOOD_SUGAR_LEVELS_ENTERED, LoggerUtil.currentUser() );
                return new ResponseEntity( dEntry, HttpStatus.OK );
            }

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not create Blood Sugar Data provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Retrieves a list of patient BSL Data for the current patient
     *
     * @return a list of patient's food diary entries
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "bloodsugar" )
    public ResponseEntity getBloodSugarLevelsPatient () {
        LoggerUtil.log( TransactionType.PATIENT_BLOOD_SUGAR_LEVELS_VIEWED, LoggerUtil.currentUser() );
        return new ResponseEntity( BloodSugarData.getByPatient( LoggerUtil.currentUser() ), HttpStatus.OK );
    }

    /**
     * Retrieves a list of patient DiaryEntries for the specified patient.
     *
     * @param patient
     *            The username of the patient for which to get entries
     * @return a list of patient's food diary entries
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    @GetMapping ( BASE_PATH + "bloodsugar/{patient}" )
    public ResponseEntity getBloodSugarLevelsHCP ( @PathVariable final String patient ) {
        if ( null == Patient.getByName( patient ) ) {
            return new ResponseEntity( errorResponse( "No patients found with username " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.HCP_BLOOD_SUGAR_LEVELS_VIEWED, User.getByName( LoggerUtil.currentUser() ),
                User.getByName( patient ) );
        return new ResponseEntity( BloodSugarData.getByPatient( patient ), HttpStatus.OK );
    }

}
