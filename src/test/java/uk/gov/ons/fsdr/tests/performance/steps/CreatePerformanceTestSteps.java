package uk.gov.ons.fsdr.tests.performance.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.performance.utils.PerformanceTestUtils;

public class CreatePerformanceTestSteps {

  private static final String FSDR_PROCESS_COMPLETE = "FSDR_PROCESS_COMPLETE";

  private static final String FSDR_REPORT_CREATED = "FSDR_REPORT_CREATED";


  @Value("${service.rabbit.url}")
  private String rabbitLocation;

  @Value("${service.rabbit.port}")
  private int rabbitPort;

  @Value("${service.rabbit.username}")
  private String rabbitUsername;

  @Value("${service.rabbit.password}")
  private String rabbitPassword;

  @Autowired
  private PerformanceTestUtils performanceTestUtils;

  private GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

  long timeout;

  @Before
  public void setup() throws IOException, TimeoutException {
    List<String> eventsToListen = Arrays.asList(new String[]{FSDR_PROCESS_COMPLETE, FSDR_REPORT_CREATED});
    gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword, rabbitPort, eventsToListen);
    performanceTestUtils.clearDown();
    performanceTestUtils.setTimestamp();
  }

  @After
  public void stop() {
    performanceTestUtils.stopScheduler();
  }

  @Given("you have {int} FSDRService Pod")
  public void youHaveFSDRServicePod(int pods) {
  }

  @And("the latency on snow mock is {string} ms")
  public void theLatencyOnSnowMockIsMs(String latency) {
    performanceTestUtils.setLatency(latency, "snow");
  }

  @And("the latency on gusite mock is {string} ms")
  public void theLatencyOnGusiteMockIsMs(String latency) {
    performanceTestUtils.setLatency(latency, "gsuite");
  }

  @And("the latency on adecco mock is {string} ms")
  public void theLatencyOnAdeccoMockIsMs(String latency) {
    performanceTestUtils.setLatency(latency, "adecco");
  }

  @And("the latency on xma mock is {string} ms")
  public void theLatencyOnXmaMockIsMs(String latency) {
    performanceTestUtils.setLatency(latency, "xma");
  }

  @And("Adecco has sent {string} number of new records")
  public void adeccoHasSentNumberOfNewRecords(String records) throws IOException {
    timeout = Integer.parseInt(records) * 10000;
    performanceTestUtils.setupEmployees(Integer.parseInt(records));
  }

  @When("confirm FSDR runs and has completed")
  public void confirmFsdrRunsAndHasCompleted() {
    performanceTestUtils.runFsdr();
    boolean fsdrProcessCompleteHasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", FSDR_PROCESS_COMPLETE, timeout);
    assertThat(fsdrProcessCompleteHasBeenTriggered).isTrue();
  }

  @Then("confirm that an FSDR report has been created")
  public void confirmThatAnFsdrReportHasBeenCreated() throws IOException {
    Boolean hasFileCreated = performanceTestUtils.createFsdrReport();
    assertThat(hasFileCreated).isTrue();
  }

  @And("details of latency and cucumber report are saved to files")
  public void detailsOfLatencyAndCucumberReportAreSavedToFiles() {
    Map<String, String> latencyMap = performanceTestUtils.getLatencyMap();
    performanceTestUtils.createLatencyReport(latencyMap);
  }
}
