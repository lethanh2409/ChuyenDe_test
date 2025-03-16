package chuyende.ptithcm;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.Duration;

public class LoginTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    static void setupReport() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-report.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    void testUsernameBlank() {
        test = extent.createTest("Test Username Blank", "Kiểm tra khi username để trống");
        driver.get("http://localhost:3000/");

        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        passwordInput.sendKeys("password123");
        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-red-500')]")));
        String actualMessage = errorMessage.getText();
        String expectedMessage = "Username cannot be blank";

        test.info("Expected: " + expectedMessage);
        test.info("Actual: " + actualMessage);

        if (actualMessage.equals(expectedMessage)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedMessage, actualMessage, "Thông báo không đúng!");
    }

    @Test
    void testUsernameAndPasswordBlank() {
        test = extent.createTest("Test Username and password Blank", "Kiểm tra khi username và password để trống");
        driver.get("http://localhost:3000/");

        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-red-500')]")));
        String actualMessage = errorMessage.getText();
        String expectedMessage = "Username and password cannot be blank";

        test.info("Expected: " + expectedMessage);
        test.info("Actual: " + actualMessage);

        if (actualMessage.equals(expectedMessage)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedMessage, actualMessage, "Thông báo không đúng!");
    }

    @Test
    void testPasswordBlank() {
        test = extent.createTest("Test Password Blank", "Kiểm tra khi password để trống");
        driver.get("http://localhost:3000/");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-red-500')]")));
        String actualMessage = errorMessage.getText();
        String expectedMessage = "Password cannot be blank";

        test.info("Expected: " + expectedMessage);
        test.info("Actual: " + actualMessage);

        if (actualMessage.equals(expectedMessage)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedMessage, actualMessage, "Thông báo không đúng!");
    }

    @Test
    void testInvalidUsername() {
        test = extent.createTest("Test Invalid Username", "Kiểm tra username sai");
        driver.get("http://localhost:3000/");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("wrong_username");
        passwordInput.sendKeys("password123");
        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-red-500')]")));
        String actualMessage = errorMessage.getText();
        String expectedMessage = "Username is incorrect";

        test.info("Expected: " + expectedMessage);
        test.info("Actual: " + actualMessage);

        if (actualMessage.equals(expectedMessage)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedMessage, actualMessage, "Thông báo không đúng!");
    }

    @Test
    void testInvalidPassword() {
        test = extent.createTest("Test Invalid Password", "Kiểm tra password sai");
        driver.get("http://localhost:3000/");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("wrong_password");
        loginButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(@class, 'text-red-500')]")));
        String actualMessage = errorMessage.getText();
        String expectedMessage = "Password is incorrect";

        test.info("Expected: " + expectedMessage);
        test.info("Actual: " + actualMessage);

        if (actualMessage.equals(expectedMessage)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedMessage, actualMessage, "Thông báo không đúng!");
    }

    @Test
    void testValidLogin() {
        test = extent.createTest("Test Valid Login", "Kiểm tra đăng nhập thành công");
        driver.get("http://localhost:3000/");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("password123");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/home"));
        String actualUrl = driver.getCurrentUrl();
        String expectedUrl = "http://localhost:3000/home";

        test.info("Expected URL: " + expectedUrl);
        test.info("Actual URL: " + actualUrl);

        if (actualUrl.equals(expectedUrl)) {
            test.pass("Test Passed");
        } else {
            test.fail("Test Failed");
        }

        assertEquals(expectedUrl, actualUrl, "Không chuyển hướng đúng sau khi đăng nhập thành công!");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    static void closeReport() {
        extent.flush(); // Lưu kết quả vào file HTML
    }
}
