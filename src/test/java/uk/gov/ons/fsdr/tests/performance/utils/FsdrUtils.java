package uk.gov.ons.fsdr.tests.performance.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.performance.exceptions.MockInaccessibleException;

@Component
public final class FsdrUtils {

  @Value("${service.fsdrservice.url}")
  private String fsdrServiceUrl;

  @Value("${service.fsdrservice.username}")
  private String fsdrServiceUsername;

  @Value("${service.fsdrservice.password}")
  private String fsdrServicePassword;

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

  public void stopScheduler() {
    String url = fsdrServiceUrl + "/fsdr/disableScheduler";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = createBasicAuthHeaders(fsdrServiceUsername, fsdrServicePassword);
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    if (responseEntity.getStatusCodeValue() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + responseEntity.getStatusCodeValue());
    }
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
