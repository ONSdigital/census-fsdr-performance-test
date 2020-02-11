package uk.gov.ons.fsdr.tests.performance.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports"},
    features = {"src/test/resources/acceptancetests/createPerformance.feature"},
    glue = {"uk.gov.ons.fsdr.tests.performance.steps"})
public class CreatePerformanceTestRunner {
}
