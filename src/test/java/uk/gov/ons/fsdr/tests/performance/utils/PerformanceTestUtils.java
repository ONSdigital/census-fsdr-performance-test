package uk.gov.ons.fsdr.tests.performance.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.tests.performance.dto.Device;
import uk.gov.ons.fsdr.tests.performance.dto.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public final class PerformanceTestUtils {

  public static AdeccoResponse adeccoResponse = new AdeccoResponse();

  public static List<AdeccoResponse> adeccoResponseList = new ArrayList<>();

  @Autowired
  private MockUtils mockUtils;

  @Autowired
  private FsdrUtils fsdrUtils;

  @Autowired
  private XmaMockUtils xmaMockUtils;

  @Autowired
  private ReportUtils reportUtils;

  @Autowired
  private StorageUtils storageUtils;

  @Value("${output.directory}")
  private URI reportDestination;

  private Map<String, String> latencyMap;

  private String timestamp;

  public PerformanceTestUtils(Map<String, String> latencyMap) {
    this.latencyMap = latencyMap;
  }

  public void setLatency(String latency, String service) {
    latencyMap.put(service, latency);
    mockUtils.setLatency(service, Integer.parseInt(latency));
  }

  public Map<String, String> getLatencyMap() {
    return latencyMap;
  }

  public void runFsdr() throws IOException {
    fsdrUtils.ingestAdecco();
  }

  public void setupEmployees(int numberOfEmployees) throws IOException {
    List<Employee> employees = getEmployeesFromCsv();
    List<Device> devices = getDevicesFromCsv();

    int count = 0;
    for (int i = 0; i < numberOfEmployees; i++) {
      Device device = devices.get(count);
      Employee employee = employees.get(count);
      employee.setRoleId(device.getRoleId());
      employee.setJobRole(setJobRole(device));
      adeccoResponse = AdeccoEmployeeFactory.buildAdeccoResponse(employee);
      adeccoResponseList.add(adeccoResponse);
      xmaMockUtils.postDevice(device.getRoleId(), device.getPhoneNumber(), device.getStatus());
      count++;
      if (count == employees.size()) {
        count = 0;
      }
    }
    log.info("Employees setup: {}", numberOfEmployees);
    mockUtils.addUsersAdecco(adeccoResponseList);
  }

  private List<Device> getDevicesFromCsv() throws IOException {
    File file = new File(getClass().getClassLoader().getResource("files/csv/device_data.csv").getFile());
    return (List<Device>) new CsvToBeanBuilder(new FileReader(file))
        .withType(Employee.class)
        .build()
        .parse();
  }

  private List<Employee> getEmployeesFromCsv() throws FileNotFoundException {
    File file = new File(getClass().getClassLoader().getResource("files/csv/employee_data.csv").getFile());
    return (List<Employee>) new CsvToBeanBuilder(new FileReader(file))
        .withType(Employee.class)
        .build()
        .parse();
  }

  private String setJobRole(Device device) {
    if (device.getRoleId().length() == 10) {
      return "Field officer";
    } else if (device.getRoleId().length() == 7) {
      return "Coordinator";
    } else if (device.getRoleId().length() == 4) {
      return "Area Manager";
    }
    return null;
  }

  public void createLatencyReport(Map<String, String> latencyMap) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyMMddHmmss");
    LocalDateTime now = LocalDateTime.now();
    timestamp = dateTimeFormatter.format(now);
    File file = null;
    try {
      file = File.createTempFile("latency_report-" + timestamp, ".txt");
    } catch (IOException ignored) {
    }
    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      writer.write("Latency Report");
      writer.write("Adecco latency: " + latencyMap.get("adecco") + "ms");
      writer.write("Service Now latency: " + latencyMap.get("snow") + "ms");
      writer.write("G Suite latency: " + latencyMap.get("gsuite") + "ms");
      writer.write("XMA latency: " + latencyMap.get("xma") + "ms");
    } catch (IOException ignored) {
    }
    storageUtils.move(file.toURI(), URI.create(reportDestination + timestamp + "/"));
    file.deleteOnExit();
  }

  public void createCucumberReports() {
    List<URI> cucumberReportFileList = storageUtils.getFilenamesInFolder(URI.create("target/cucumber-reports"));
    for (URI uri : cucumberReportFileList) {
      storageUtils.move(uri, URI.create(reportDestination + timestamp + "/"));
    }
  }

  public void createFsdrReport() throws IOException {
    byte[] csv = reportUtils.createCsv();
    File file = File.createTempFile("fsdr_report-" + timestamp, ".csv");
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(csv);
    } catch (IOException ignored) {
    }
    storageUtils.move(file.toURI(), URI.create(reportDestination + timestamp + "/"));
    file.deleteOnExit();
    reportUtils.clearReportDatabase();
  }
}

