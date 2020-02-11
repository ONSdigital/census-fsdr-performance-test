package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.fsdr.tests.performance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public final class XmaMockUtils {

  @Value("${xma.baseUrl}")
  private String mockXmaUrl;

  public void clearMock() throws IOException {
    URL url = new URL(mockXmaUrl + "messages/reset");
    log.info("clear-mock_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("DELETE");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }

  public String[] getRecords() {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockXmaUrl + "messages";
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

  public String getId(String roleId) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockXmaUrl + "id";
    log.info("getId-mock_url:" + url);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("roleId", roleId);

    HttpEntity<?> entity = new HttpEntity<>(headers);

    HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

    return response.getBody();
  }

  public String[] getRecords(String email) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockXmaUrl + "messages/" + email;
    log.info("getRecords-mock_url:" + url);
    ResponseEntity<String[]> responseEntity;
    responseEntity = restTemplate.getForEntity(url, String[].class);
    return responseEntity.getBody();
  }

  public void postDevice(String roleId, String phoneNumber, String status) {
    RestTemplate restTemplate = new RestTemplate();
    String url = mockXmaUrl + "devices/create";
    log.info("createDevice-mock_url:" + url);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("roleId", roleId)
        .queryParam("phoneNumber", phoneNumber)
        .queryParam("Status", status);

    HttpEntity<?> entity = new HttpEntity<>(headers);

    restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, HttpStatus.class);
  }
}
