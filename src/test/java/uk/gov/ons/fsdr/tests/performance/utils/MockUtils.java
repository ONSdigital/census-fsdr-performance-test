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
import org.springframework.web.util.UriComponentsBuilder;
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

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

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
    System.out.println("CLEARDB" + url + username + password);
    Statement stmt = null;
    try (Connection conn = DriverManager.getConnection(url, username, password)) {

      if (conn != null) {
        System.out.println("Connected to the database!");
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
        System.out.println("Failed to make connection!");
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

  public AdeccoResponseList getRecords() {
    RestTemplate restTemplate = new RestTemplate();
    String url = baseMockUrl + "adecco/records";
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<AdeccoResponseList> responseEntity;
    responseEntity = restTemplate.getForEntity(url, AdeccoResponseList.class);
    return responseEntity.getBody();
  }

  public void addUsersAdecco(List<AdeccoResponse> adeccoResponseList) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = createBasicAuthHeaders("user", "password");
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

  public void enableRequestRecorder() throws IOException {
    URL url = new URL(baseMockUrl + "mock/enable");
    log.info("enableRequestRecorder-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public void disableRequestRecorder() throws IOException {
    URL url = new URL(baseMockUrl + "mock/disable");
    log.info("disableRequestRecorder-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public ResponseEntity<AdeccoResponseList> getEmployeeBySource(String source) {
    RestTemplate restTemplate = new RestTemplate();
    String postHit = baseMockUrl + "/getResponse";
    ResponseEntity<AdeccoResponseList> results = restTemplate.exchange(postHit, HttpMethod.GET, null,
        AdeccoResponseList.class);
    return results;
  }

  public ResponseEntity<AdeccoResponseList> getEmployeeById(String employeeId) {
    RestTemplate restTemplate = new RestTemplate();
    String postHit = baseMockUrl + "fsdr/getEmployee/";
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(postHit)
        .queryParam("employeeId", employeeId);
    ResponseEntity<AdeccoResponseList> results = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null,
        AdeccoResponseList.class);
    return results;
  }
}
