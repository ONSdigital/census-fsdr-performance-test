package uk.gov.ons.fsdr.tests.performance.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @CsvBindByName(column = "role_id")
    private String roleId;

    @CsvBindByName(column = "field_device_phone_number")
    private String phoneNumber;

    @CsvBindByName(column = "status")
    private String status;

}
