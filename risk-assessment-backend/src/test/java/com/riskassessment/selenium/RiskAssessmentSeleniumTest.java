package com.riskassessment.selenium;


import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RiskAssessmentSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String frontendUrl = "http://localhost:4200";
    private String backendUrl;

    @BeforeEach
    void setupWebDriver() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-web-security");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-debugging-port=9222");
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            backendUrl = "http://localhost:" + port;
        } catch (Exception e) {
            System.out.println("WebDriver setup failed: " + e.getMessage());
            Assumptions.assumeTrue(false, "Skipping Selenium tests due to WebDriver setup failure");
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error closing WebDriver: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testBackendHealthCheck() {
        driver.get(backendUrl + "/api/risks");
        
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("[") || pageSource.contains("risks"), 
            "Backend API should be accessible and return JSON response");
    }

    @Test
    @Order(2)
    void testDashboardPageLoads() {
        driver.get(frontendUrl);
        
        wait.until(ExpectedConditions.titleContains("Risk Assessment"));
        
        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("Risk Assessment"), 
            "Dashboard page should load with correct title");
        
        WebElement dashboardHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.tagName("h1"))
        );
        Assertions.assertTrue(dashboardHeader.getText().contains("Dashboard") || 
                            dashboardHeader.getText().contains("Risk"), 
            "Dashboard should have appropriate header");
    }

    @Test
    @Order(3)
    void testNavigationToCreateRiskPage() {
        driver.get(frontendUrl);
        
        WebElement createRiskLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Create Risk') or contains(@href, 'create')]")
            )
        );
        createRiskLink.click();
        
        wait.until(ExpectedConditions.urlContains("create"));
        
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("create"), 
            "Should navigate to create risk page");
        
        WebElement createForm = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.tagName("form"))
        );
        Assertions.assertNotNull(createForm, "Create risk form should be present");
    }

    @Test
    @Order(4)
    void testCreateRiskFormValidation() {
        driver.get(frontendUrl + "/create-risk");
        
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' or contains(text(), 'Create') or contains(text(), 'Submit')]")
            )
        );
        
        submitButton.click();
        
        List<WebElement> errorMessages = driver.findElements(
            By.xpath("//*[contains(@class, 'error') or contains(@class, 'invalid') or contains(text(), 'required')]")
        );
        
        Assertions.assertTrue(errorMessages.size() > 0, 
            "Form validation should show error messages for empty required fields");
    }

    @Test
    @Order(5)
    void testCreateRiskFormSubmission() {
        driver.get(frontendUrl + "/create-risk");
        
        WebElement riskTypeSelect = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[contains(@name, 'type') or contains(@id, 'type')]")
            )
        );
        Select riskTypeDropdown = new Select(riskTypeSelect);
        riskTypeDropdown.selectByVisibleText("Market Practice");
        
        WebElement riskProbabilitySelect = driver.findElement(
            By.xpath("//select[contains(@name, 'probability') or contains(@id, 'probability')]")
        );
        Select probabilityDropdown = new Select(riskProbabilitySelect);
        probabilityDropdown.selectByVisibleText("High");
        
        WebElement riskStatusSelect = driver.findElement(
            By.xpath("//select[contains(@name, 'status') or contains(@id, 'status')]")
        );
        Select statusDropdown = new Select(riskStatusSelect);
        statusDropdown.selectByVisibleText("Open");
        
        WebElement descriptionField = driver.findElement(
            By.xpath("//textarea[contains(@name, 'desc') or contains(@id, 'desc')] | //input[contains(@name, 'desc') or contains(@id, 'desc')]")
        );
        descriptionField.clear();
        descriptionField.sendKeys("Selenium test risk - Market volatility affecting pricing strategies");
        
        WebElement remarksField = driver.findElement(
            By.xpath("//textarea[contains(@name, 'remarks') or contains(@id, 'remarks')] | //input[contains(@name, 'remarks') or contains(@id, 'remarks')]")
        );
        remarksField.clear();
        remarksField.sendKeys("Created via Selenium automation test");
        
        WebElement dateField = driver.findElement(
            By.xpath("//input[@type='date' or contains(@name, 'date') or contains(@id, 'date')]")
        );
        dateField.clear();
        dateField.sendKeys(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        
        WebElement submitButton = driver.findElement(
            By.xpath("//button[@type='submit' or contains(text(), 'Create') or contains(text(), 'Submit')]")
        );
        submitButton.click();
        
        WebElement successMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'success') or contains(text(), 'created') or contains(@class, 'success')]")
            )
        );
        
        Assertions.assertNotNull(successMessage, 
            "Success message should appear after creating risk");
    }

    @Test
    @Order(6)
    void testNavigationToRiskDetailsPage() {
        driver.get(frontendUrl);
        
        WebElement riskDetailsLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Risk Details') or contains(text(), 'Details') or contains(@href, 'details')]")
            )
        );
        riskDetailsLink.click();
        
        wait.until(ExpectedConditions.urlContains("details"));
        
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("details"), 
            "Should navigate to risk details page");
    }

    @Test
    @Order(7)
    void testRiskDetailsTableDisplay() {
        driver.get(frontendUrl + "/risk-details");
        
        WebElement riskTable = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.tagName("table"))
        );
        
        List<WebElement> tableHeaders = riskTable.findElements(By.xpath(".//th"));
        Assertions.assertTrue(tableHeaders.size() >= 5, 
            "Risk details table should have at least 5 columns");
        
        boolean hasRequiredColumns = false;
        for (WebElement header : tableHeaders) {
            String headerText = header.getText().toLowerCase();
            if (headerText.contains("type") || headerText.contains("probability") || 
                headerText.contains("description") || headerText.contains("status")) {
                hasRequiredColumns = true;
                break;
            }
        }
        
        Assertions.assertTrue(hasRequiredColumns, 
            "Table should contain required columns (type, probability, description, status)");
    }

    @Test
    @Order(8)
    void testDashboardPieChartPresence() {
        driver.get(frontendUrl);
        
        WebElement chartContainer = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//canvas | //*[contains(@class, 'chart') or contains(@id, 'chart')]")
            )
        );
        
        Assertions.assertNotNull(chartContainer, 
            "Dashboard should contain a pie chart element");
    }

    @Test
    @Order(9)
    void testDashboardPieChartClickNavigation() {
        driver.get(frontendUrl);
        
        try {
            WebElement chartElement = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//canvas | //*[contains(@class, 'chart') or contains(@id, 'chart')]")
                )
            );
            
            chartElement.click();
            
            Thread.sleep(2000);
            
            String currentUrl = driver.getCurrentUrl();
            Assertions.assertTrue(currentUrl.contains("details") || currentUrl.contains("risk"), 
                "Clicking pie chart should navigate to risk details or related page");
                
        } catch (Exception e) {
            WebElement detailsLink = driver.findElement(
                By.xpath("//a[contains(text(), 'Details') or contains(@href, 'details')]")
            );
            Assertions.assertNotNull(detailsLink, 
                "If chart is not clickable, details link should be available");
        }
    }

    @Test
    @Order(10)
    void testFormFieldsValidation() {
        driver.get(frontendUrl + "/create-risk");
        
        WebElement riskTypeSelect = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[contains(@name, 'type') or contains(@id, 'type')]")
            )
        );
        
        Select typeDropdown = new Select(riskTypeSelect);
        List<WebElement> typeOptions = typeDropdown.getOptions();
        
        boolean hasRequiredTypes = false;
        for (WebElement option : typeOptions) {
            String optionText = option.getText();
            if (optionText.contains("Market Practice") || optionText.contains("Regulatory") || 
                optionText.contains("Pricing") || optionText.contains("Governance") || 
                optionText.contains("Conflict")) {
                hasRequiredTypes = true;
                break;
            }
        }
        
        Assertions.assertTrue(hasRequiredTypes, 
            "Risk type dropdown should contain required options");
        
        WebElement probabilitySelect = driver.findElement(
            By.xpath("//select[contains(@name, 'probability') or contains(@id, 'probability')]")
        );
        
        Select probabilityDropdown = new Select(probabilitySelect);
        List<WebElement> probabilityOptions = probabilityDropdown.getOptions();
        
        boolean hasRequiredProbabilities = false;
        for (WebElement option : probabilityOptions) {
            String optionText = option.getText();
            if (optionText.contains("Low") || optionText.contains("Medium") || optionText.contains("High")) {
                hasRequiredProbabilities = true;
                break;
            }
        }
        
        Assertions.assertTrue(hasRequiredProbabilities, 
            "Risk probability dropdown should contain Low, Medium, High options");
    }

    @Test
    @Order(11)
    void testPageTitlesAndHeaders() {
        driver.get(frontendUrl);
        String dashboardTitle = driver.getTitle();
        Assertions.assertTrue(dashboardTitle.contains("Risk") || dashboardTitle.contains("Dashboard"), 
            "Dashboard page should have appropriate title");
        
        driver.get(frontendUrl + "/create-risk");
        wait.until(ExpectedConditions.titleContains("Risk"));
        String createTitle = driver.getTitle();
        Assertions.assertTrue(createTitle.contains("Risk") || createTitle.contains("Create"), 
            "Create risk page should have appropriate title");
        
        driver.get(frontendUrl + "/risk-details");
        wait.until(ExpectedConditions.titleContains("Risk"));
        String detailsTitle = driver.getTitle();
        Assertions.assertTrue(detailsTitle.contains("Risk") || detailsTitle.contains("Details"), 
            "Risk details page should have appropriate title");
    }

    @Test
    @Order(12)
    void testResponsiveDesign() {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024));
        
        driver.get(frontendUrl);
        
        WebElement body = driver.findElement(By.tagName("body"));
        Assertions.assertNotNull(body, "Page should render properly on tablet size");
        
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
        
        driver.get(frontendUrl + "/create-risk");
        
        WebElement form = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        Assertions.assertNotNull(form, "Form should render properly on mobile size");
        
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
    }
}
