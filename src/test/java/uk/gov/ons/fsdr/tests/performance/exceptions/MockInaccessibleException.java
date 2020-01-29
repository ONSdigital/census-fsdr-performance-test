package uk.gov.ons.fsdr.tests.performance.exceptions;

public class MockInaccessibleException extends RuntimeException {
  public MockInaccessibleException(String reason) {
    super(reason);
  }
}
