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
    And details of latency and cucumber report of "<employees>" employees are saved to files

    Examples:
      |xmaLatency  | adeccoLatency | gsuiteLatency | snowLatency | employees  |
      |      0     |         0     |           0   |         0   |    40      |
      |    100     |       100     |         100   |       100   |    40      |
      |   1000     |      1000     |        1000   |      1000   |    40      |
      |      0     |         0     |           0   |         0   |    400     |
      |    100     |       100     |         100   |       100   |    400     |
      |   1000     |      1000     |        1000   |      1000   |    400     |
      |      0     |         0     |           0   |         0   |    4000    |
      |    100     |       100     |         100   |       100   |    4000    |
      |   1000     |      1000     |        1000   |      1000   |    4000    |
      |      0     |         0     |           0   |         0   |    40000   |
      |    100     |       100     |         100   |       100   |    40000   |
      |   1000     |      1000     |        1000   |      1000   |    40000   |
