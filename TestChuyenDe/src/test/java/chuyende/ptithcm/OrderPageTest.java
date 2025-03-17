package chuyende.ptithcm;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class OrderPageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ExtentSparkReporter reporter = new ExtentSparkReporter("order-report.html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @BeforeEach
    public void setupTest(TestInfo testInfo) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Tạo test log cho từng test case
        test = extent.createTest(testInfo.getDisplayName());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush(); // Ghi log vào file
    }

    @Test
    @DisplayName("Kiểm tra khi không có sản phẩm được chọn")
    public void testNoProductSelected() {
        test.info("Truy cập vào trang đặt hàng");
        driver.get("http://localhost:3000/order");

        try {
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[text()='Không có sản phẩm nào được chọn!']")));
            assertNotNull(message);
            assertEquals("Không có sản phẩm nào được chọn!", message.getText());
            test.pass("Hiển thị đúng thông báo khi không có sản phẩm được chọn");
        } catch (Exception e) {
            test.fail("Không tìm thấy thông báo khi không có sản phẩm được chọn: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Kiểm tra input và nút tăng giảm số lượng")
    public void testInputFieldsAndQuantityChange() {
        test.info("Truy cập vào trang đặt hàng");
        driver.get("http://localhost:3000/order");

        try {
            WebElement recipientNameInput = driver.findElement(By.xpath("//input[@placeholder='Họ tên người nhận']"));
            recipientNameInput.clear();
            recipientNameInput.sendKeys("Nguyen Van A");
            assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"));

            WebElement quantityDisplay = driver.findElement(By.xpath("//span[contains(@class, 'px-6') and text()='1']"));
            assertEquals("1", quantityDisplay.getText());

            WebElement increaseButton = driver.findElement(By.xpath("//button[contains(text(), '+')]"));
            increaseButton.click();
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//span[contains(@class, 'px-6')]"), "2"));

            test.pass("Các input và nút tăng giảm số lượng hoạt động đúng");
        } catch (Exception e) {
            test.fail("Lỗi khi kiểm tra input và nút tăng giảm số lượng: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Kiểm tra hành động đặt hàng")
    public void testCreateOrderButton() {
        test.info("Truy cập vào trang đặt hàng");
        driver.get("http://localhost:3000/order");

        try {
            WebElement orderButton = driver.findElement(By.xpath("//button[contains(text(), 'Đặt hàng')]"));
            orderButton.click();
            wait.until(ExpectedConditions.urlContains("/order-success"));
            assertTrue(driver.getCurrentUrl().contains("/order-success"));

            test.pass("Đặt hàng thành công, điều hướng đến trang 'order-success'");
        } catch (Exception e) {
            test.fail("Lỗi khi đặt hàng: " + e.getMessage());
        }
    }
}
