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
import uk.gov.ons.fsdr.tests.performance.factory.AdeccoEmployeeFactory;

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
import java.util.UUID;

@Slf4j
@Component
public final class PerformanceTestUtils {

  private static List<AdeccoResponse> adeccoResponseList = new ArrayList<>();

  private static List<Device> allocatedDevices = new ArrayList<>();

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

  @Autowired
  private QueueClient queueClient;

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

  public void clearDown() throws IOException {
    mockUtils.clearDatabase();
    mockUtils.clearMock();
    queueClient.clearQueues();
    xmaMockUtils.clearMock();
  }

  public void stopScheduler() {
    fsdrUtils.stopScheduler();
  }

  public void runFsdr() {
    fsdrUtils.startFsdr();
  }

  public void setTimestamp() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyMMddHmmss");
    LocalDateTime now = LocalDateTime.now();
    timestamp = dateTimeFormatter.format(now);
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setupEmployees(int numberOfEmployees) throws IOException {
    List<Employee> employees = getEmployeesFromCsv();
    List<Device> devices = getDevicesFromCsv();

    int count = 0;
    for (int i = 0; i < numberOfEmployees; i++) {
      Device device = devices.get(count);
      Employee employee = employees.get(count);
      employee.setUniqueEmployeeId(String.valueOf(UUID.randomUUID()));
      employee.setRoleId(device.getRoleId());
      employee.setJobRole(setJobRole(device));
      AdeccoResponse adeccoResponse = AdeccoEmployeeFactory.buildAdeccoResponse(employee);
      adeccoResponseList.add(adeccoResponse);
      allocatedDevices.add(device);
      count++;
      if (count == employees.size()) {
        count = 0;
      }
    }
    log.info("Employees setup: {}", numberOfEmployees);
    mockUtils.addUsersAdecco(adeccoResponseList);
  }

  public void createDevices() {
    for (Device device: allocatedDevices) {
      xmaMockUtils.postDevice(device.getRoleId(), device.getPhoneNumber(), device.getStatus());
    }
    log.info("Devices added to XMA: {}", allocatedDevices.size());
  }

  public void createLatencyReport(Map<String, String> latencyMap) {
    File file = null;
    try {
      file = File.createTempFile("latency_report-" + getTimestamp(), ".txt");
    } catch (IOException ignored) {
    }
    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      writer.write("Latency Report \n");
      writer.write(adeccoResponseList.size() + " : Number of Adecco responses \n");
      writer.write("Adecco latency: " + latencyMap.get("adecco") + "ms \n");
      writer.write("Service Now latency: " + latencyMap.get("snow") + "ms \n");
      writer.write("G Suite latency: " + latencyMap.get("gsuite") + "ms \n");
      writer.write("XMA latency: " + latencyMap.get("xma") + "ms \n");
    } catch (IOException ignored) {
    }
    storageUtils.move(file.toURI(), URI.create(reportDestination + "/" + getTimestamp() + "/" + file.getName()));
    file.deleteOnExit();
  }

  public void createCucumberReports() {
    List<URI> cucumberReportFileList = storageUtils.getFilenamesInFolder(URI.create("files/report"));
    for (URI uri : cucumberReportFileList) {
      storageUtils.move(uri, URI.create(reportDestination + getTimestamp() + "/"));
    }
  }

  public boolean createFsdrReport() throws IOException {
    byte[] csv = reportUtils.createCsv();
    File file = File.createTempFile("fsdr_report-" + getTimestamp(), ".csv");
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(csv);
    } catch (IOException ignored) {
      return false;
    }
    storageUtils.move(file.toURI(), URI.create(reportDestination + "/" + getTimestamp() + "/" + file.getName()));
    file.deleteOnExit();
    reportUtils.clearReportDatabase();
    return true;
  }

  private List<Device> getDevicesFromCsv() throws IOException {
    File file = new File(getClass().getClassLoader().getResource("files/csv/device_data.csv").getFile());
    return (List<Device>) new CsvToBeanBuilder(new FileReader(file))
        .withType(Device.class)
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
}

