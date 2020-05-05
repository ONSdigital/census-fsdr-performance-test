package uk.gov.ons.fsdr.tests.performance.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import uk.gov.ons.census.fwmt.events.utils.GatewayEventMonitor;
import uk.gov.ons.fsdr.tests.performance.utils.MockUtils;
import uk.gov.ons.fsdr.tests.performance.utils.PerformanceTestUtils;

public class CreatePerformanceTestSteps {

  private static final String FSDR_REPORT_READY = "FSDR_REPORT_READY";

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

  @Autowired
  private MockUtils mockUtils;

  private GatewayEventMonitor gatewayEventMonitor = new GatewayEventMonitor();

  long timeout;

  @Before
  public void setup() throws Exception {
    performanceTestUtils.setReportDestination();

    List<String> eventsToListen = Arrays.asList(new String[]{FSDR_PROCESS_COMPLETE, FSDR_REPORT_CREATED, FSDR_REPORT_READY});
    gatewayEventMonitor.enableEventMonitor(rabbitLocation, rabbitUsername, rabbitPassword, rabbitPort, eventsToListen);
    performanceTestUtils.clearDown();
  }

  @After
  public void stop() throws Exception {
    performanceTestUtils.stopScheduler();
    performanceTestUtils.clearDown();
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
    mockUtils.addMultipleAdecco(Integer.parseInt(records));
  }

  @When("FSDR runs")
  public void confirm_FSDR_runs() {
    performanceTestUtils.runFsdr();
  }

  @When("has completed")
  public void has_completed() {
    boolean fsdrProcessCompleteHasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", FSDR_PROCESS_COMPLETE, timeout);
    assertThat(fsdrProcessCompleteHasBeenTriggered).isTrue();
  }
  @Then("create {string} FSDR report with {string} employers")
  public void create_FSDR_report(String reportPrefix, String numOfEmployees) throws IOException {
    boolean fsdrProcessCompleteHasBeenTriggered = gatewayEventMonitor.hasEventTriggered("<N/A>", FSDR_REPORT_READY, timeout);
    assertThat(fsdrProcessCompleteHasBeenTriggered).isTrue();
    Boolean hasFileCreated = performanceTestUtils.createFsdrReport(reportPrefix);
    assertThat(hasFileCreated).isTrue();

    performanceTestUtils.createLatencyReport(reportPrefix, Integer.parseInt(numOfEmployees));
  }


  @Then("confirm that an FSDR report has been created")
  public void confirmThatAnFsdrReportHasBeenCreated() throws IOException {
  }

  @And("details of latency and cucumber report of {string} employees are saved to files")
  public void detailsOfLatencyAndCucumberReportAreSavedToFiles() {
  }
}
