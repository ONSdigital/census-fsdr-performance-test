package uk.gov.ons.fsdr.tests.performance.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

  @CsvBindByName(column = "unique_employee_id")
  private String uniqueEmployeeId;

  @CsvBindByName(column = "first_name")
  private String firstName;

  @CsvBindByName(column = "surname")
  private String surname;

  @CsvBindByName(column = "preferred_name")
  private String preferredName;

  @CsvBindByName(column = "address_1")
  private String address1;

  @CsvBindByName(column = "address_2")
  private String address2;

  @CsvBindByName(column = "town")
  private String town;

  @CsvBindByName(column = "county")
  private String county;

  @CsvBindByName(column = "postcode")
  private String postcode;

  @CsvBindByName(column = "country")
  private String country;

  @CsvBindByName(column = "personal_email_address")
  private String personalEmailAddress;

  @CsvBindByName(column = "telephone_number_contact_1")
  private String telephoneNumberContact1;

  @CsvBindByName(column = "telephone_number_contact_2")
  private String telephoneNumberContact2;

  @CsvBindByName(column = "emergency_contact_first_name")
  private String emergencyContactFirstName;

  @CsvBindByName(column = "emergency_contact_surname")
  private String emergencyContactSurname;

  @CsvBindByName(column = "emergency_contact_mobile_no")
  private String emergencyContactMobileNo;

  @CsvBindByName(column = "emergency_contact_first_name_2")
  private String emergencyContactFirstName2;

  @CsvBindByName(column = "emergency_contact_surname_2")
  private String emergencyContactSurname2;

  @CsvBindByName(column = "emergency_contact_mobile_no_2")
  private String emergencyContactMobileNo2;

  @CsvBindByName(column = "welsh_language_speaker")
  private Boolean welshLanguageSpeaker;

  @CsvBindByName(column = "any_languages_spoken")
  private String anyLanguagesSpoken;

  @CsvBindByName(column = "mobility")
  private String mobility;

  @CsvBindByName(column = "mobile_staff")
  private Boolean mobileStaff;

  @CsvBindByName(column = "id_badge_no")
  private String idBadgeNo;

  @CsvBindByName(column = "work_restrictions")
  private String workRestrictions;

  @CsvBindByName(column = "weekly_hours")
  private Double weeklyHours;

  @CsvBindByName(column = "reasonable_adjustments")
  private String reasonableAdjustments;

  @CsvBindByName(column = "current_civil_servant")
  private Boolean currentCivilServant;

  @CsvBindByName(column = "previous_civil_servant")
  private Boolean previousCivilServant;

  @CsvBindByName(column = "civil_service_pension_recipient")
  private Boolean civilServicePensionRecipient;

  @CsvBindByName(column = "dob")
  private String dob;

  @CsvBindByName(column = "driving_information")
  private String drivingInformation;

  @CsvBindByName(column = "age")
  private String age;

  @CsvBindByName(column = "ethnicity")
  private String ethnicity;

  @CsvBindByName(column = "disability")
  private String disability;

  @CsvBindByName(column = "ethnicity_notes")
  private String ethnicityNotes;

  @CsvBindByName(column = "disability_notes")
  private String disabilityNotes;

  @CsvBindByName(column = "nationality")
  private String nationality;

  @CsvBindByName(column = "gender")
  private String gender;

  @CsvBindByName(column = "sexual_orientation")
  private String sexualOrientation;

  @CsvBindByName(column = "religion")
  private String religion;

  @CsvBindByName(column = "sexual_orientation_notes")
  private String sexualOrientationNotes;

  @CsvBindByName(column = "religion_notes")
  private String religionNotes;

  @CsvBindByName(column = "venue_address")
  private String venueAddress;

  @CsvBindByName(column = "hr_case_data")
  private String hrCaseData;

  @CsvBindByName(column = "hr_individual_contract")
  private String hrIndividualContract;

  @CsvBindByName(column = "role_id")
  private String roleId;

  @CsvBindByName(column = "job_role")
  private String jobRole;

}
