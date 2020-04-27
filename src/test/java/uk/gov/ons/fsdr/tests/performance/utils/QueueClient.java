package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class QueueClient {

  @Autowired
  private QueueUtils queueUtils;

  public void clearQueues() {
    clearQueue("FSDR.Events");
    clearQueue("FSDR.EventsDLQ");
    clearQueue("Gateway.Actions");
    clearQueue("Gateway.ActionsDLQ");
    clearQueue("Lws.Action");
    clearQueue("Lws.ActionDLQ");
    clearQueue("Snow.Action");
    clearQueue("Snow.ActionDLQ");
    clearQueue("Snow.Events");
    clearQueue("Snow.Leaver");
    clearQueue("Snow.Mover");
    clearQueue("Xma.ActionDLQ");
    clearQueue("Xma.Coordiantor");
    clearQueue("Xma.Events");
    clearQueue("Xma.FieldOfficer");
    clearQueue("Xma.Leaver");
    clearQueue("xma.transient.error");
    clearQueue("report.events");
  }

  private void clearQueue(String queueName) {
    queueUtils.deleteMessage(queueName);
  }
}
