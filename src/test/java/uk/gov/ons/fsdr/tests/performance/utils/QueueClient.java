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
    clearQueue("Gsuite.Action");
    clearQueue("Gsuite.ActionDLQ");
    clearQueue("Lws.Action");
    clearQueue("Lws.ActionDLQ");
    clearQueue("ServiceNow.Action");
    clearQueue("ServiceNow.Leaver");
    clearQueue("ServiceNow.Mover");
    clearQueue("Xma.AreaManager");
    clearQueue("Xma.Coordiantor");
    clearQueue("Xma.FieldOfficer");
    clearQueue("Xma.Leaver");
    clearQueue("Xma.Transient.Error");
    clearQueue("Servicenow.Transient.Error");

    clearQueue("FSDR.Events");
    clearQueue("ServiceNow.Events");
    clearQueue("Xma.Events");
    clearQueue("Report.Events");
    clearQueue("Gsuite.Events");  
  }

  private void clearQueue(String queueName) {
    queueUtils.deleteMessage(queueName);
  }
}
