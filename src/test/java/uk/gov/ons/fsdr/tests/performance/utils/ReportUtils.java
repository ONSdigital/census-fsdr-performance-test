package uk.gov.ons.fsdr.tests.performance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.fsdr.tests.performance.exceptions.MockInaccessibleException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public final class ReportUtils {

  @Value("${report.baseUrl}")
  private String reportBaseUrl;

  public byte[] createCsv() {
    RestTemplate restTemplate = new RestTemplate();
    String url = reportBaseUrl + "csv";
    log.info("getCsv-report_url:" + url);
    ResponseEntity<byte[]> responseEntity;
    responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
    return responseEntity.getBody();
  }

  public void clearReportDatabase() throws IOException {
    URL url = new URL(reportBaseUrl + "clearDB");
    log.info("clear-report-database_url:" + url.toString());
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestMethod("GET");
    if (httpURLConnection.getResponseCode() != 200) {
      throw new MockInaccessibleException("Failed : HTTP error code : " + httpURLConnection.getResponseCode());
    }
  }
}
