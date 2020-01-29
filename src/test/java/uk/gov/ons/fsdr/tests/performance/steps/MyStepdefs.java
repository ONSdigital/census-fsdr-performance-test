package uk.gov.ons.fsdr.tests.performance.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class MyStepdefs {
    @Given("you have {int} FSDRService Pod")
    public void youHaveFSDRServicePod(int arg0) {
    }

    @And("the latency on snow mock is {string} ms")
    public void theLatencyOnSnowMockIsMs(String arg0) {
    }

    @And("the latency on gusite mock is {string} ms")
    public void theLatencyOnGusiteMockIsMs(String arg0) {
    }

    @And("the latency on adecco mock is {string} ms")
    public void theLatencyOnAdeccoMockIsMs(String arg0) {
    }

    @And("the latency on xma mock is {string} ms")
    public void theLatencyOnXmaMockIsMs(String arg0) {
    }

    @And("Adecco has sent {string} number of new records")
    public void adeccoHasSentNumberOfNewRecords(String arg0) {
    }

    @And("Adecco has sent {string} number of ID badges")
    public void adeccoHasSentNumberOfIDBadges(String arg0) {
    }

    @When("FSDR runs and is completed")
    public void fsdrRunsAndIsCompleted() {
    }

    @Then("a csv report of performance figures is created")
    public void aCsvReportOfPerformanceFiguresIsCreated() {
    }

    @And("details of latency settings is saved")
    public void detailsOfLatencySettingsIsSaved() {
    }
}
