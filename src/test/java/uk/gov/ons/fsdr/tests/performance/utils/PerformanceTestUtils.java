package uk.gov.ons.fsdr.tests.performance.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.census.ffa.storage.utils.StorageUtils;

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

  private static String dayFolderName;
  private static String timeFolderName;

  private final static DateTimeFormatter dayFolderFmt = DateTimeFormatter.ofPattern("yyyMMdd");
  private final static DateTimeFormatter timeFolderFmt = DateTimeFormatter.ofPattern("HHmm");

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

  public void clearDown() throws Exception {
    queueClient.clearQueues();
    mockUtils.clearDatabase();
    mockUtils.clearMock();
    xmaMockUtils.clearMock();
    reportUtils.clearReportDatabase();
  }

  public void stopScheduler() {
    fsdrUtils.stopScheduler();
  }

  public void runFsdr() {
    fsdrUtils.startFsdr();
  }

  public void setReportDestination() {
    if (dayFolderName == null) {
      LocalDateTime now = LocalDateTime.now();
      dayFolderName = dayFolderFmt.format(now);
      timeFolderName = timeFolderFmt.format(now);
    }
  }

  public void createLatencyReport(String reportPrefix, int numOfEmployees) {
    File file = null;
    try {
      file = File.createTempFile("latency_report-" + UUID.randomUUID(), ".txt");
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
    storageUtils.move(file.toURI(), URI.create(reportDestination + "/" + dayFolderName + "/" + timeFolderName + "/"
        + reportPrefix + "_latency_report" + ".txt"));
    file.deleteOnExit();
  }

  public boolean createFsdrReport(String reportPrefix) throws IOException {
    log.info("======================BUILDING CSV======================");
    log.info("======================BUILDING CSV======================");
    byte[] csv = reportUtils.createCsv();
    File file = File.createTempFile("fsdr_report-" + UUID.randomUUID(), ".csv");
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(csv);
    } catch (IOException e) {
      log.error("Problem creating Performance Report", e);
      return false;
    }

    String fileDestination = reportDestination + "/" + dayFolderName + "/" + timeFolderName + "/" + reportPrefix+ "_fsdr_report"  + ".csv";
    storageUtils.move(file.toURI(), URI.create(fileDestination));
    log.info("file create: {}", fileDestination);
    file.deleteOnExit();
    return true;
  }

}
