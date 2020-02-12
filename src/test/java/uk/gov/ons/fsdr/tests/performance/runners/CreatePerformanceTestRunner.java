package uk.gov.ons.fsdr.tests.performance.runners;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports"},
    features = {"src/test/resources/acceptancetests/createPerformance.feature"},
    glue = {"uk.gov.ons.fsdr.tests.performance.steps"})
@Configuration
@ComponentScan({"uk.gov.census.ffa.storage.utils"})
public class CreatePerformanceTestRunner {
}
