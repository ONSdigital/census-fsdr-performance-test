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
    clearQueue("Action.Result");
    clearQueue("Action.ResultDLQ");
    clearQueue("Adecco.Action");
    clearQueue("Adecco.ActionDLQ");
    clearQueue("Adecco.Events");
    clearQueue("FSDR.Events");
    clearQueue("FSDR.EventsDLQ");
    clearQueue("Gsuite.Action");
    clearQueue("Gsuite.ActionDLQ");
    clearQueue("Lws.Action");
    clearQueue("Lws.ActionDLQ");
    clearQueue("Snow.Action");
    clearQueue("Snow.Events");
    clearQueue("Snow.Leaver");
    clearQueue("Snow.Mover");
    clearQueue("Xma.AreaManager");
    clearQueue("Xma.Coordiantor");
    clearQueue("Xma.Events");
    clearQueue("Xma.FieldOfficer");
    clearQueue("Xma.Leaver");
    clearQueue("xma.transient.error");
    clearQueue("snow.transient.error");
    clearQueue("report.events");
  }

  private void clearQueue(String queueName) {
    queueUtils.deleteMessage(queueName);
  }
}
