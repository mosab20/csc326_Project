#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Glucose Test
  As a Lab Tech
  I want to enter results of glucose test
  So a diabetes diagnosis can be made by a HCP
  	
Scenario: Add glucose test to office visit
Given I log in to iTrust2 as Admin
When I go to Admin Procedures
And I add glucose test
Given I log in to iTrust2 as HCP
When I create an Office Visit
And I add a glucose test to that visit
Then I recieve a message that office visit details were successfully changed
    
Scenario: Submit invalid glucose test result
Given I log in to iTrust2 as Lab Tech
When I go to Assigned Procedures
And I submit an invalid result
Then I receive an error message

Scenario: Submit valid glucose test result
And I submit a valid result
Then the results are submitted successfully

Scenario: Confirm the lab results from lab tech
Given I log in to iTrust2 as HCP
When I navigate to Edit Office Visits
And select oral glucose test office visit to click on Confirm Oral Glucose Test
And HCP clicks submit button to update the office visit
Then I recieve a message that results were confirmed successfully

Scenario: Patient sees the diagnosis results after confirmation
Given I log in to iTrust2 as Patient
When I navigate to diagnoses
Then I can see my diagnosis results

Scenario Outline: Valid Blood Sugar Entry
   Given there exists a patient in the system
   And the patient has been diagnosed as diabetic or pre-diabetic
   Then the patient navigates to the blood sugar level entries page
   When I submit valid values for <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
   Then my blood sugar levels for the day are succesfully added

Examples:
  | date       | fasting | breakfast | lunch   | dinner   | 
  | 10/27/2019 | 160     | 180       | 200     | 199      | 
  | 10/27/2019 | 130     | 220       | 250     | 199      | 
  | 10/27/2019 | 150     | 190       | 200     | 160      | 

Scenario Outline: Invalid Blood Sugar Entry
   Given there exists a patient in the system
   And the patient has been diagnosed as diabetic or pre-diabetic
   Then the patient navigates to the blood sugar level entries page
   When I submit invalid values for <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
   Then my blood sugar levels entry is not added
   
	 #Given I log in to iTrust2 as Admin
	 #When I go to Admin Procedures
	 #And I add glucose test
	 #Given I log in to iTrust2 as HCP
	 #When I create an Office Visit
	 #And I add a glucose test to that visit
	 #Then I recieve a message that office visit details were successfully changed
	 #And I submit a valid result
	 #Then the results are submitted successfully
	 #Given I log in to iTrust2 as HCP
	 #When I navigate to Edit Office Visits
 	 #And select oral glucose test office visit to click on Confirm Oral Glucose Test
	 #And HCP clicks submit button to update the office visit
	 #Then I recieve a message that results were confirmed successfully
	 #Then log in as patient
   #And the patient has been diagnosed as diabetic or pre-diabetic
   #Then the patient navigates to the blood sugar level entries page
   #When I submit invalid values for <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
   #Then my blood sugar levels entry is not added
Examples:
  | date       | fasting | breakfast | lunch   | dinner   | 
  | 10/27/2019 | -160    | 180       | 200     | 199      | 
  | 10/27/2019 | 160     | -180      | 200     | 199      | 
  | 10/27/2019 | 160     | 180       | -200    | 199      | 
  | 10/27/2019 | 160     | 180       | 200     | -199     | 
  | 10/27/2019 | 0       | 180       | 200     | 199      | 
  | 10/27/2019 | 160     | null      | 200     | 199      | 
  | 10/27/2019 | 160     | 180       | two     | 199      | 
  | 10/27/2019 | **      | 180       | 200     | 200      | 
  | 10/27/2019 | 160     | 180       | 200     | dindin   | 


Scenario: Valid Blood Sugar CSV Entry
   Given there exists a patient in the system
   And the patient has been diagnosed as diabetic or pre-diabetic
   Then the patient navigates to the blood sugar table page
	 When the patient clicks on Choose File and selects the valid CSV file
	 Then new entries from the valid CSV file is displayed on the table
 

Scenario: Invalid Blood Sugar CSV Entry
   Given there exists a patient in the system
   And the patient has been diagnosed as diabetic or pre-diabetic
   Then the patient navigates to the blood sugar table page
   Then the patient clicks on Choose File and selects the invalid CSV file


#Scenario: HCP Views Patient's Blood Sugar Table
   #Given there exists a patient in the system
   #And the patient has been diagnosed as diabetic or pre-diabetic
   #Given there exists an HCP in the system
   #Then the HCP navigates to the blood sugar table page


#Scenario Outline: View entry as a Patient
#	Given there exists a patient in the system
# And the patient has been diagnosed as diabetic or pre-diabetic
#	And the patient has added an entry <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
#	And the patient views the entry <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
#	And this patient behavior is logged on the iTrust2 homepage.

#Examples:
  #| date       | fasting | breakfast | lunch   | dinner   | 
  #| 10/27/2019 | 160     | 180       | 200     | 199      | 

Scenario Outline: View entry as an HCP
	Given there exists an HCP in the system
	And there exists a patient in the system
	And the patient has added an entry <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
	Then the HCP has logged in and navigated to blood sugar level entries page
  #And the HCP views the entry <date>, <fasting>, <breakfast>, <lunch>, and <dinner>
	#And this HCP behavior is logged on the iTrust2 homepage.

Examples:
  | date       | fasting | breakfast | lunch   | dinner   | 
  | 2019-10-27 | 160     | 180       | 200     | 199      | 