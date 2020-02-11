package uk.gov.ons.fsdr.tests.performance.factory;

import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseContact;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseJob;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseWorker;
import uk.gov.ons.fsdr.tests.performance.dto.Employee;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AdeccoEmployeeFactory {

  public static AdeccoResponse buildAdeccoResponse(Employee employee) {
    AdeccoResponseJob job = AdeccoResponseJob.builder()
        .jobRole(employee.getJobRole())
        .roleId(employee.getRoleId())
        .build();
    AdeccoResponseContact contact = AdeccoResponseContact.builder()
        .employeeId(String.valueOf(UUID.randomUUID()))
        .firstName(employee.getFirstName())
        .lastName(employee.getSurname())
        .welshLanguageSpeaker(String.valueOf(employee.getWelshLanguageSpeaker()))
        .languages(employee.getAnyLanguagesSpoken())
        .mobileStaff(String.valueOf(employee.getMobileStaff()))
        .reasonableAdjustments(employee.getReasonableAdjustments())
        .addressLine1(employee.getAddress1())
        .addressLine2(employee.getAddress2())
        .town(employee.getTown())
        .county(employee.getCounty())
        .postcode(employee.getPostcode())
        .personalEmail(employee.getPersonalEmailAddress())
        .telephoneNo1(employee.getTelephoneNumberContact1())
        .emergencyContact(employee.getEmergencyContactFirstName() + " " + employee.getEmergencyContactSurname())
        .emergencyContactNumber1(employee.getEmergencyContactMobileNo())
        .mobility(employee.getMobility())
        .dob(String.valueOf(employee.getDob()))
        .drivingInfo(employee.getDrivingInformation())
        .build();
    AdeccoResponseWorker worker = AdeccoResponseWorker.builder().employeeId(employee.getUniqueEmployeeId()).build();

    return AdeccoResponse.builder()
        .contractStartDate(LocalDate.now().minus(5, ChronoUnit.DAYS).toString())
        .contractEndDate(LocalDate.now().plus(5, ChronoUnit.DAYS).toString())
        .operationalEndDate(LocalDate.now().plus(5, ChronoUnit.DAYS).toString())
        .responseContact(contact)
        .adeccoResponseWorker(worker)
        .responseJob(job)
        .build();
  }
}
