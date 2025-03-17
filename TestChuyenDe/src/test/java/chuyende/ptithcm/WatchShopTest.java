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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WatchShopTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();

        // Khởi tạo ExtentReports
        ExtentSparkReporter spark = new ExtentSparkReporter("watchshop-report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown(TestInfo testInfo) {
        if (driver != null) {
            driver.quit();
        }

        // Lưu kết quả vào báo cáo
        extent.flush();
    }

    // Test case 1: Kiểm tra danh sách sản phẩm hiển thị đúng
    @Test
    public void testProductListDisplayed() {
        test = extent.createTest("Test Danh Sách Sản Phẩm");
        driver.get("http://localhost:3000/home");

        WebElement productGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.grid.grid-cols-4")));
        List<WebElement> products = productGrid.findElements(By.cssSelector("div.p-4"));

        assertFalse(products.isEmpty(), "Danh sách sản phẩm không được rỗng");
        test.pass("Danh sách sản phẩm được hiển thị thành công");
    }

    @Test
    public void testProductDetailNavigation() {
        test = extent.createTest("Test Chuyển Hướng Trang Chi Tiết");

        // Mở trang Home
        driver.get("http://localhost:3000/home");

        // Chờ danh sách sản phẩm hiển thị
        WebElement productList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='root']/div/div/div/div[1]")));

        // Tìm tất cả sản phẩm trong danh sách
        List<WebElement> products = productList.findElements(By.cssSelector("div.p-4 a"));
        assertFalse(products.isEmpty(), "Không tìm thấy sản phẩm nào trong danh sách.");

        // Click vào sản phẩm đầu tiên
        WebElement firstProduct = products.get(0);
        firstProduct.click();

        // Chờ URL thay đổi thành đúng format: "http://localhost:3000/product/{product_id}"
        wait.until(ExpectedConditions.urlMatches("http://localhost:3000/product/[A-Za-z0-9]+$"));

        // Kiểm tra URL hợp lệ
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.matches("http://localhost:3000/product/[A-Za-z0-9]+$"), "URL không hợp lệ: " + currentUrl);

        test.pass("Chuyển hướng từ trang Home đến trang chi tiết sản phẩm thành công.");
    }



    // Test case 3: Kiểm tra nút "Mua ngay" chuyển hướng đến trang đặt hàng
    @Test
    public void testBuyNowButtonNavigation() {
        test = extent.createTest("Test Nút 'Mua ngay'");
        driver.get("http://localhost:3000/product/DH00000001");

        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/button")));
        buyNowButton.click();

        wait.until(ExpectedConditions.urlContains("/order"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/order"), "URL phải chứa '/order'");

        test.pass("Nút 'Mua ngay' hoạt động chính xác");
    }
}
