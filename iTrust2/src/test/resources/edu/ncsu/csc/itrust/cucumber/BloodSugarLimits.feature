Feature: Blood Sugar Limits
  As an HCP
  I want to enter blood sugar limits for diabetic patients
  So they know what sugar levels to maintain
  

  Scenario Outline: enterSugarLevelBoundaries
    Given the required users exist
	And Dr Shelly Vang has logged in and chosen to edit a patient
	When she selects the patient with first name: <first> and last name: <last>
	And she changes the blood sugar limits to: <fasting>, <aftermeal>
	And she submits the changes
	Then a success message is displayed
	
Examples:
	| first | last | fasting | aftermeal |
	| Jim   | Bean | 110     | 135       |

  Scenario Outline: enterSugarLevelBoundariesInvalid
	Given the required users exist
	And Dr Shelly Vang has logged in and chosen to edit a patient
	When she selects the patient with first name: <first> and last name: <last>
	And she changes the blood sugar limits to: <fasting>, <aftermeal>
	And she submits the changes
	Then a limit error message is displayed

Examples:
	| first | last | fasting | aftermeal |
	| Jim   | Bean | 110      | 190       |
	