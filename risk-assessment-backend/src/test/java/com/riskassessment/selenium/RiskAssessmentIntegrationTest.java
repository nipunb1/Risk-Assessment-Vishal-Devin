package com.riskassessment.selenium;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskassessment.entity.Risk;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RiskAssessmentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    void testGetAllRisks() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/risks", String.class);
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().startsWith("["));
    }

    @Test
    @Order(2)
    void testCreateRisk() throws Exception {
        Risk newRisk = new Risk();
        newRisk.setRiskDate(LocalDate.now());
        newRisk.setRiskType(Risk.RiskType.MARKET_PRACTICE);
        newRisk.setRiskProbability(Risk.RiskProbability.HIGH);
        newRisk.setRiskDesc("Integration test risk - Market volatility affecting pricing strategies");
        newRisk.setRiskStatus(Risk.RiskStatus.OPEN);
        newRisk.setRiskRemarks("Created via integration test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Risk> request = new HttpEntity<>(newRisk, headers);

        ResponseEntity<Risk> response = restTemplate.postForEntity(baseUrl + "/api/risks", request, Risk.class);
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getRiskId());
        Assertions.assertEquals(Risk.RiskType.MARKET_PRACTICE, response.getBody().getRiskType());
        Assertions.assertEquals(Risk.RiskProbability.HIGH, response.getBody().getRiskProbability());
    }

    @Test
    @Order(3)
    void testGetRiskById() throws Exception {
        Risk newRisk = new Risk();
        newRisk.setRiskDate(LocalDate.now());
        newRisk.setRiskType(Risk.RiskType.REGULATORY);
        newRisk.setRiskProbability(Risk.RiskProbability.MEDIUM);
        newRisk.setRiskDesc("Test risk for retrieval");
        newRisk.setRiskStatus(Risk.RiskStatus.IN_PROGRESS);
        newRisk.setRiskRemarks("Test remarks");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Risk> createRequest = new HttpEntity<>(newRisk, headers);

        ResponseEntity<Risk> createResponse = restTemplate.postForEntity(baseUrl + "/api/risks", createRequest, Risk.class);
        Long riskId = createResponse.getBody().getRiskId();

        ResponseEntity<Risk> getResponse = restTemplate.getForEntity(baseUrl + "/api/risks/" + riskId, Risk.class);
        
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(riskId, getResponse.getBody().getRiskId());
        Assertions.assertEquals(Risk.RiskType.REGULATORY, getResponse.getBody().getRiskType());
    }

    @Test
    @Order(4)
    void testUpdateRisk() throws Exception {
        Risk newRisk = new Risk();
        newRisk.setRiskDate(LocalDate.now());
        newRisk.setRiskType(Risk.RiskType.PRICING);
        newRisk.setRiskProbability(Risk.RiskProbability.LOW);
        newRisk.setRiskDesc("Original description");
        newRisk.setRiskStatus(Risk.RiskStatus.OPEN);
        newRisk.setRiskRemarks("Original remarks");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Risk> createRequest = new HttpEntity<>(newRisk, headers);

        ResponseEntity<Risk> createResponse = restTemplate.postForEntity(baseUrl + "/api/risks", createRequest, Risk.class);
        Long riskId = createResponse.getBody().getRiskId();

        Risk updatedRisk = createResponse.getBody();
        updatedRisk.setRiskDesc("Updated description");
        updatedRisk.setRiskStatus(Risk.RiskStatus.CLOSED);
        updatedRisk.setRiskRemarks("Updated remarks");

        HttpEntity<Risk> updateRequest = new HttpEntity<>(updatedRisk, headers);
        restTemplate.put(baseUrl + "/api/risks/" + riskId, updateRequest);

        ResponseEntity<Risk> getResponse = restTemplate.getForEntity(baseUrl + "/api/risks/" + riskId, Risk.class);
        
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertEquals("Updated description", getResponse.getBody().getRiskDesc());
        Assertions.assertEquals(Risk.RiskStatus.CLOSED, getResponse.getBody().getRiskStatus());
        Assertions.assertEquals("Updated remarks", getResponse.getBody().getRiskRemarks());
    }

    @Test
    @Order(5)
    void testDeleteRisk() throws Exception {
        Risk newRisk = new Risk();
        newRisk.setRiskDate(LocalDate.now());
        newRisk.setRiskType(Risk.RiskType.GOVERNANCE);
        newRisk.setRiskProbability(Risk.RiskProbability.HIGH);
        newRisk.setRiskDesc("Risk to be deleted");
        newRisk.setRiskStatus(Risk.RiskStatus.OPEN);
        newRisk.setRiskRemarks("Will be deleted");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Risk> createRequest = new HttpEntity<>(newRisk, headers);

        ResponseEntity<Risk> createResponse = restTemplate.postForEntity(baseUrl + "/api/risks", createRequest, Risk.class);
        Long riskId = createResponse.getBody().getRiskId();

        restTemplate.delete(baseUrl + "/api/risks/" + riskId);

        ResponseEntity<Risk> getResponse = restTemplate.getForEntity(baseUrl + "/api/risks/" + riskId, Risk.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @Order(6)
    void testGetRisksByStatus() throws Exception {
        Risk openRisk = new Risk();
        openRisk.setRiskDate(LocalDate.now());
        openRisk.setRiskType(Risk.RiskType.CONFLICT_OF_INTEREST);
        openRisk.setRiskProbability(Risk.RiskProbability.MEDIUM);
        openRisk.setRiskDesc("Open risk for filtering test");
        openRisk.setRiskStatus(Risk.RiskStatus.OPEN);
        openRisk.setRiskRemarks("Open status");

        Risk closedRisk = new Risk();
        closedRisk.setRiskDate(LocalDate.now());
        closedRisk.setRiskType(Risk.RiskType.MARKET_PRACTICE);
        closedRisk.setRiskProbability(Risk.RiskProbability.LOW);
        closedRisk.setRiskDesc("Closed risk for filtering test");
        closedRisk.setRiskStatus(Risk.RiskStatus.CLOSED);
        closedRisk.setRiskRemarks("Closed status");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        restTemplate.postForEntity(baseUrl + "/api/risks", new HttpEntity<>(openRisk, headers), Risk.class);
        restTemplate.postForEntity(baseUrl + "/api/risks", new HttpEntity<>(closedRisk, headers), Risk.class);

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/risks?status=OPEN", String.class);
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("OPEN"));
    }

    @Test
    @Order(7)
    void testGetRisksByType() throws Exception {
        Risk marketRisk = new Risk();
        marketRisk.setRiskDate(LocalDate.now());
        marketRisk.setRiskType(Risk.RiskType.MARKET_PRACTICE);
        marketRisk.setRiskProbability(Risk.RiskProbability.HIGH);
        marketRisk.setRiskDesc("Market practice risk for type filtering");
        marketRisk.setRiskStatus(Risk.RiskStatus.OPEN);
        marketRisk.setRiskRemarks("Market type");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        restTemplate.postForEntity(baseUrl + "/api/risks", new HttpEntity<>(marketRisk, headers), Risk.class);

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/risks?type=MARKET_PRACTICE", String.class);
        
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("MARKET_PRACTICE"));
    }

    @Test
    @Order(8)
    void testInvalidRiskId() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/risks/99999", String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    void testCreateRiskWithInvalidData() throws Exception {
        Risk invalidRisk = new Risk();
        invalidRisk.setRiskDate(LocalDate.now());
        invalidRisk.setRiskType(null);
        invalidRisk.setRiskProbability(null);
        invalidRisk.setRiskDesc("");
        invalidRisk.setRiskStatus(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Risk> request = new HttpEntity<>(invalidRisk, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/risks", request, String.class);
        
        Assertions.assertTrue(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
    }

    @Test
    @Order(10)
    void testCorsHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:4200");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/risks", 
            HttpMethod.GET, 
            entity, 
            String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        HttpHeaders responseHeaders = response.getHeaders();
        Assertions.assertTrue(responseHeaders.getAccessControlAllowOrigin() != null || 
                            responseHeaders.get("Access-Control-Allow-Origin") != null);
    }
}
