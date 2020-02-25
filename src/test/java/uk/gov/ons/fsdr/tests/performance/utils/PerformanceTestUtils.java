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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public final class PerformanceTestUtils {

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

  public void createLatencyReport(Map<String, String> latencyMap, int numOfEmployees) {
    File file = null;
    try {
      file = File.createTempFile("latency_report-" + getTimestamp(), ".txt");
    } catch (IOException ignored) {
    }
    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      writer.write("Latency Report \n");
      writer.write(numOfEmployees + " : Number of Adecco responses \n");
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

}

