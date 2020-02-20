package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.common.dto.AdeccoResponse;
import uk.gov.ons.fsdr.common.dto.AdeccoResponseList;
import uk.gov.ons.fsdr.tests.performance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Component
public final class MockUtils {

  @Value("${mock.baseUrl}")
  private String baseMockUrl;

  @Value("${service.fsdrservice.url}")
  private String fsdrServiceUrl;

  @Value("${service.fsdrservice.username}")
  private String fsdrServiceUsername;

  @Value("${service.fsdrservice.password}")
  private String fsdrServicePassword;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  public void startFsdr() {
    String url = fsdrServiceUrl + "/fsdr/startFsdr";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = createBasicAuthHeaders(fsdrServiceUsername, fsdrServicePassword);
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    if (responseEntity.getStatusCodeValue() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + responseEntity.getStatusCodeValue());
    }
  }

  public void clearMock() throws IOException {
    URL url = new URL(baseMockUrl + "mock/reset");
    log.info("clear-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public void setLatency(String service, int latency) {
    String url = baseMockUrl + "/mock/latency/" + service + "/" + latency;
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = createBasicAuthHeaders("user", "password");
    headers.setContentType(MediaType.APPLICATION_JSON);
    restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
  }

  public void clearDatabase() {
    Statement stmt = null;
    try (Connection conn = DriverManager.getConnection(url, username, password)) {
      if (conn != null) {
        log.info("Connected to the database!");
        stmt = conn.createStatement();
        String sql = "DELETE FROM action_indicator";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM device ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM device_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM job_role ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM job_role_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM employee ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM employee_history ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM request_log ";
        stmt.executeUpdate(sql);
        sql = "DELETE FROM user_authentication ";
        stmt.executeUpdate(sql);

      } else {
        log.error("Failed to make connection!");
      }
    } catch (SQLException ignored) {
    } finally {
      try {
        if (stmt != null)
          stmt.close();
      } catch (SQLException ignored) {
      }
    }
  }

  public void addUsersAdecco(List<AdeccoResponse> adeccoResponseList) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = createBasicAuthHeaders(fsdrServiceUsername, fsdrServicePassword);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<List<AdeccoResponse>> response = new HttpEntity<>(adeccoResponseList, headers);
    String postUrl = baseMockUrl + "mock/postResponse";
    restTemplate.exchange(postUrl, HttpMethod.POST, response, AdeccoResponseList.class);
  }

  private HttpHeaders createBasicAuthHeaders(String username, String password) {
    HttpHeaders headers = new HttpHeaders();
    final String plainCreds = username + ":" + password;
    byte[] plainCredsBytes = plainCreds.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);
    headers.add("Authorization", "Basic " + base64Creds);
    return headers;
  }
}
