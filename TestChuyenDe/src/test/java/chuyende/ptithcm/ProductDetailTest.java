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

public class ProductDetailTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();

        // Khởi tạo báo cáo
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("product-report.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @BeforeEach
    public void setupTest(TestInfo testInfo) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Tạo test trong báo cáo
        test = extent.createTest(testInfo.getDisplayName());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        extent.flush(); // Lưu báo cáo
    }

    @Test
    @DisplayName("Kiểm tra trang chi tiết sản phẩm hiển thị đúng thông tin")
    public void testProductDetailDisplaysCorrectInfo() {
        try {
            driver.get("http://localhost:3000/product/DH00000002");
            test.info("Truy cập trang sản phẩm");

            WebElement productName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[2]/h2")));
            assertNotNull(productName, "Tên sản phẩm không được null");
            assertFalse(productName.getText().isEmpty(), "Tên sản phẩm không được rỗng");
            test.pass("Tên sản phẩm hiển thị đúng");

            WebElement productPrice = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/p[2]"));
            assertNotNull(productPrice, "Giá sản phẩm không được null");
            assertTrue(productPrice.getText().contains("đ"), "Giá sản phẩm phải có ký hiệu 'đ'");
            test.pass("Giá sản phẩm hiển thị đúng");

            WebElement productDescription = driver.findElement(By.xpath("//p[not(contains(@class, 'text-xl')) and not(contains(@class, 'font-bold'))]"));
            assertNotNull(productDescription, "Mô tả sản phẩm không được null");
            assertFalse(productDescription.getText().isEmpty(), "Mô tả sản phẩm không được rỗng");
            test.pass("Mô tả sản phẩm hiển thị đúng");

            WebElement buyNowButton = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/button"));
            assertNotNull(buyNowButton, "Nút 'Mua ngay' phải tồn tại");
            test.pass("Nút 'Mua ngay' hiển thị đúng");

        } catch (Exception e) {
            test.fail("Lỗi: " + e.getMessage());
            throw e;
        }
    }

    @Test
    @DisplayName("Kiểm tra nút 'Mua ngay' chuyển hướng đến trang đặt hàng")
    public void testBuyNowButtonNavigatesToOrderPage() {
        try {
            driver.get("http://localhost:3000/product/DH00000002");
            test.info("Truy cập trang sản phẩm");

            WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/button")));

            assertNotNull(buyNowButton, "Nút 'Mua ngay' không được null");
            test.pass("Nút 'Mua ngay' tìm thấy");

            buyNowButton.click();
            test.info("Đã click nút 'Mua ngay'");

            wait.until(ExpectedConditions.urlContains("/order"));
            String currentUrl = driver.getCurrentUrl();
            assertTrue(currentUrl.contains("/order"), "Sau khi click 'Mua ngay', URL phải chứa '/order'");
            test.pass("Chuyển hướng đúng đến trang đặt hàng");

        } catch (Exception e) {
            test.fail("Lỗi: " + e.getMessage());
            throw e;
        }
    }
}
