package your.package.name;

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

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Thiết lập wait với timeout 10 giây
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Test case 1: Kiểm tra trang danh sách sản phẩm hiển thị danh sách sản phẩm đúng cách
    @Test
    public void testProductListDisplayed() {
        driver.get("http://localhost:3000/");

        // Chờ cho đến khi container chứa danh sách sản phẩm được hiển thị
        WebElement productGrid = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.grid.grid-cols-4")));
        // Tìm tất cả các sản phẩm (giả sử mỗi sản phẩm nằm trong thẻ có class "p-4")
        List<WebElement> products = productGrid.findElements(By.cssSelector("div.p-4"));
        assertFalse(products.isEmpty(), "Danh sách sản phẩm không được rỗng");
    }

    // Test case 2: Kiểm tra chuyển hướng khi nhấn vào 1 sản phẩm (trang chi tiết sản phẩm)
    @Test
    public void testProductDetailNavigation() {
        driver.get("http://localhost:3000/");

        // Tìm liên kết của sản phẩm đầu tiên và click vào đó
        WebElement firstProductLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.p-4 a")));
        firstProductLink.click();

        // Chờ cho đến khi URL chứa "/product/"
        wait.until(ExpectedConditions.urlContains("/product/"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/product/"), "URL phải chứa '/product/' để xác nhận chuyển sang trang chi tiết");
    }

    // Test case 3: Kiểm tra hành động nhấn nút "Mua ngay" chuyển hướng đến trang đặt hàng
    @Test
    public void testBuyNowButtonNavigation() {
        driver.get("http://localhost:3000/");

        // Tìm nút "Mua ngay" của sản phẩm đầu tiên và click vào nó
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Mua ngay')]")));
        buyNowButton.click();

        // Chờ cho đến khi URL chứa "/order"
        wait.until(ExpectedConditions.urlContains("/order"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/order"), "URL phải chứa '/order' sau khi nhấn nút 'Mua ngay'");
    }
}
