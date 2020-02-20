package uk.gov.ons.fsdr.tests.performance.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class QueueUtils {

  @Value("${service.rabbit.url}")
  private String rabbitmqHost;

  @Value("${service.rabbit.username}")
  private String rabbitmqUsername;

  @Value("${service.rabbit.password}")
  private String rabbitmqPassword;

  @Value("${service.rabbit.virtualHost:/}")
  private String rabbitmqVirtualHost;

  public Boolean deleteMessage(String qname) {
    Connection connection = null;
    Channel channel = null;
    try {
      ConnectionFactory factory = getRabbitMQConnectionFactory();
      connection = factory.newConnection();
      channel = connection.createChannel();

      channel.queuePurge(qname);
      log.info("Purged Queue: " + qname);

      return true;
    } catch (IOException | TimeoutException e) {
      log.error("Issue deleting message from {} queue.", e, qname);
      return false;
    } finally {
      try {
        if (channel != null)
          channel.close();
        if (connection != null)
          connection.close();
      } catch (IOException | TimeoutException e) {
        log.error("Issue closing RabbitMQ connections", e);
      }
    }
  }

  private ConnectionFactory getRabbitMQConnectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(rabbitmqHost);
    factory.setUsername(rabbitmqUsername);
    factory.setPassword(rabbitmqPassword);
    factory.setVirtualHost(rabbitmqVirtualHost);
    return factory;
  }
}
