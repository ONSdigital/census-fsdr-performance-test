@PerformanceTest
Feature: Create action performance test

  Scenario Outline: Test data should be stored in a database in Performance environment.
    Given you have 1 FSDRService Pod
    And the latency on snow mock is "<snowLatency>" ms
    And the latency on gusite mock is "<gsuiteLatency>" ms
    And the latency on adecco mock is "<adeccoLatency>" ms
    And the latency on xma mock is "<xmaLatency>" ms
    And Adecco has sent "<employees>" number of new records
    When confirm FSDR runs and has completed
    Then confirm that an FSDR report has been created
    And details of latency and cucumber report are saved to files

    Examples:
      |xmaLatency| adeccoLatency | gsuiteLatency | snowLatency | employees  |
      |      0   |      0        |        0      |      0      |    251     |
    #  |     10   |     10        |       10      |     10      |    10     |
   #   |     10   |     10        |       10      |     10      |    100    |
     # |     15   |     15        |       15      |     15      |    1000   |