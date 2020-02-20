package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Slf4j
@Component
public final class QueueClient {

  @Autowired
  private QueueUtils queueUtils;

  public long getMessageCount(String queueName) {
    Long messageCount = queueUtils.getMessageCount(queueName);
    return messageCount;
  }

  public String getMessage(String queueName) throws InterruptedException {
    return getMessage(queueName, 10000, 10);
  }

  public String getMessage(String queueName, int msTimeout) throws InterruptedException {
    return getMessage(queueName, msTimeout, 10);
  }

  public String getMessage(String queueName, int msTimeout, int msInterval) throws InterruptedException {
    String message = null;
    int iterations = (msTimeout + msInterval - 1) / msInterval; // division rounding up
    for (int i = 0; i < iterations; i++) {
      message = queueUtils.getMessageOffQueue(queueName);
      if (message != null) {
        break;
      }
      Thread.sleep(msInterval);
    }
    return message;
  }

  public void clearQueues() {
    clearQueue("FSDR.Events");
    clearQueue("FSDR.EventsDLQ");
    clearQueue("report.events");
  }

  private void clearQueue(String queueName) {
    queueUtils.deleteMessage(queueName);
  }
}
