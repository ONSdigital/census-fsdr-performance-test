@PerformanceTest
Feature: Create action performance test

  Scenario Outline: Test data should be stored in a database in Performance environment.
    Given you have 1 FSDRService Pod
    And the latency on snow mock is "<snowLatency>" ms
    And the latency on gusite mock is "<gsuiteLatency>" ms
    And the latency on adecco mock is "<adeccoLatency>" ms
    And the latency on xma mock is "<xmaLatency>" ms
    And Adecco has sent "<employees>" number of new records
    When FSDR runs
    And has completed
    Then create "<reportPrefix>" FSDR report

    Examples:
      | reportPrefix   | xmaLatency  | adeccoLatency | gsuiteLatency | snowLatency | employees  |
      | 0l_40emp       |       0     |         0     |           0   |         0   |    40      |
#      | 10l_40emp      |     100     |       100     |         100   |       100   |    40      |
#      | 1000l_40emp    |    1000     |      1000     |        1000   |      1000   |    40      |
#      | 0l_400emp      |       0     |         0     |           0   |         0   |    400     |
#      | 10l_400emp     |     100     |       100     |         100   |       100   |    400     |
#      | 1000l_400emp   |    1000     |      1000     |        1000   |      1000   |    400     |
#      | 0l_4000emp     |       0     |         0     |           0   |         0   |    4000    |
#      | 10l_4000emp    |     100     |       100     |         100   |       100   |    4000    |
#      | 1000l_4000emp  |    1000     |      1000     |        1000   |      1000   |    4000    |
#      | 0l_40000emp    |       0     |         0     |           0   |         0   |    40000   |
#      | 10l_40000emp   |     100     |       100     |         100   |       100   |    40000   |
#      | 1000l_40000emp |    1000     |      1000     |        1000   |      1000   |    40000   |

