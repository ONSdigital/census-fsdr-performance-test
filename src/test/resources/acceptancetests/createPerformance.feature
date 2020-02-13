@PerformanceTest
Feature: Create action performance test

  Scenario Outline: Test data should be stored in a database in Performance environment.
    Given you have 1 FSDRService Pod
    And the latency on snow mock is "<latency>" ms
    And the latency on gusite mock is "<latency>" ms
    And the latency on adecco mock is "<latency>" ms
    And the latency on xma mock is "<latency>" ms
    And Adecco has sent "1" number of new records
    When confirm FSDR runs and has completed
    Then confirm that an FSDR report has been created
    And details of latency and cucumber report are saved to files

    Examples:
      | latency | employees
      | 0       | 500
#      |100      |1000
#      |500      |1000
#      |1000     |1000