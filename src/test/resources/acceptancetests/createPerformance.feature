@Performance test
Feature: Create performance test

  Scenario: Test data should be stored in a database in Performance environment.
    Given you have 1 FSDRService Pod
    And the latency on snow mock is "latency" ms
    And the latency on gusite mock is "latency" ms
    And the latency on adecco mock is "latency" ms
    And the latency on xma mock is "latency" ms
    And Adecco has sent "latency" number of new records
    And Adecco has sent "latency" number of ID badges
    When FSDR runs and is completed
    Then a csv report of performance figures is created
    And details of latency settings is saved