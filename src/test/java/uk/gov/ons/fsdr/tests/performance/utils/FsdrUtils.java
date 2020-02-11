package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.performance.dto.Employee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public final class FsdrUtils {

  @Value("${service.fsdrservice.url}")
  private String fsdrServiceUrl;

  @Value("${service.fsdrservice.username}")
  private String fsdrServiceUsername;

  @Value("${service.fsdrservice.password}")
  private String fsdrServicePassword;

  private void addBasicAuthentication(HttpURLConnection httpURLConnection) {
    String encoded = Base64.getEncoder()
        .encodeToString((fsdrServiceUsername + ":" + fsdrServicePassword).getBytes(StandardCharsets.UTF_8));
    httpURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
  }

  private HttpHeaders createHeaders() {
    return new HttpHeaders() {
      {
        String encoded = Base64.getEncoder()
            .encodeToString((fsdrServiceUsername + ":" + fsdrServicePassword).getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encoded;
        set("Authorization", authHeader);
      }
    };
  }

  public void ingestAdecco() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/adeccoIngest");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("POST");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestGsuite() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/gsuite");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Gsuit ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestXma() throws IOException {

    URL url = new URL(fsdrServiceUrl + "/fsdr/xma");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Xma ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestSnow() throws IOException {

    URL url = new URL(fsdrServiceUrl + "/fsdr/serviceNow");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Snow ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestGranby() throws IOException {

    URL url = new URL(fsdrServiceUrl + "/fsdr/logistics");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate granby extract" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void ingestRunFSDRProcess() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/createActions");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate Adecco ingest" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public ResponseEntity<Employee> retrieveEmployee(String id) {
    RestTemplate restTemplate = new RestTemplate();

    String url = fsdrServiceUrl + "/fieldforce/byId/" + id;
    System.out.println(url);
    ResponseEntity<Employee> employeeEntity = restTemplate
        .exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), Employee.class);
    return employeeEntity;
  }

  public void lwsExtract() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/lwsCsv");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate lws extract" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void devices() throws IOException {

    URL url = new URL(fsdrServiceUrl + "/devices/addDevicesXma");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to get devices" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

  public void rcaExtract() throws IOException {
    URL url = new URL(fsdrServiceUrl + "/fsdr/rca");
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    addBasicAuthentication(httpURLConnection);

    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      log.error("failed to initiate rca extract" + httpURLConnection.getResponseCode()
          + httpURLConnection.getResponseMessage());
      throw new RuntimeException(httpURLConnection.getResponseMessage());
    }
  }

}
