package your.package.name;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDetailTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void setupClass() {
        // Tự động setup ChromeDriver bằng WebDriverManager
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Thiết lập explicit wait với timeout 10 giây
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Test case 1: Kiểm tra trang chi tiết sản phẩm hiển thị đúng thông tin
    @Test
    public void testProductDetailDisplaysCorrectInfo() {
        // Giả sử có sản phẩm với productId = 1
        driver.get("http://localhost:3000/product/1");

        // Chờ đến khi tên sản phẩm được hiển thị
        WebElement productName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(@class, 'text-3xl') and contains(@class, 'font-bold')]")));
        assertNotNull(productName, "Tên sản phẩm không được null");
        String nameText = productName.getText();
        assertFalse(nameText.isEmpty(), "Tên sản phẩm không được rỗng");

        // Kiểm tra giá sản phẩm (với giả định phần giá có chứa "đ")
        WebElement productPrice = driver.findElement(By.xpath("//p[contains(@class, 'text-xl') and contains(@class, 'font-semibold')]"));
        assertNotNull(productPrice, "Giá sản phẩm không được null");
        String priceText = productPrice.getText();
        assertTrue(priceText.contains("đ"), "Giá sản phẩm phải có ký hiệu 'đ'");

        // Kiểm tra mô tả sản phẩm (giả sử có thẻ chứa mô tả không thuộc nhóm tiêu đề hay giá)
        WebElement productDescription = driver.findElement(By.xpath("//p[not(contains(@class, 'text-xl')) and not(contains(@class, 'font-bold'))]"));
        assertNotNull(productDescription, "Mô tả sản phẩm không được null");
        String descriptionText = productDescription.getText();
        assertFalse(descriptionText.isEmpty(), "Mô tả sản phẩm không được rỗng");

        // Kiểm tra sự tồn tại của nút "Mua ngay"
        WebElement buyNowButton = driver.findElement(By.xpath("//button[contains(text(), 'Mua ngay')]"));
        assertNotNull(buyNowButton, "Nút 'Mua ngay' phải tồn tại");
    }

    // Test case 2: Kiểm tra hành động nhấn nút "Mua ngay" chuyển hướng đến trang đặt hàng
    @Test
    public void testBuyNowButtonNavigatesToOrderPage() {
        driver.get("http://localhost:3000/product/1");

        // Chờ và tìm nút "Mua ngay"
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Mua ngay')]")));
        assertNotNull(buyNowButton, "Nút 'Mua ngay' không được null");

        // Click vào nút "Mua ngay"
        buyNowButton.click();

        // Chờ cho URL chuyển hướng có chứa "/order"
        wait.until(ExpectedConditions.urlContains("/order"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/order"), "Sau khi click 'Mua ngay', URL phải chứa '/order'");
    }
}
