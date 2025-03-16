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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class OrderPageTest {

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    // Test case 1: Kiểm tra khi không có sản phẩm được chọn (orderProduct = null)
    @Test
    public void testNoProductSelected() {
        // Giả sử rằng khi không có orderProduct, trang OrderPage hiển thị thông báo:
        driver.get("http://localhost:3000/order");
        
        // Kiểm tra thông báo "Không có sản phẩm nào được chọn!"
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[text()='Không có sản phẩm nào được chọn!']")));
        assertNotNull(message);
        assertEquals("Không có sản phẩm nào được chọn!", message.getText());
    }
    
    // Test case 2: Kiểm tra các input và nút tăng giảm số lượng.
    @Test
    public void testInputFieldsAndQuantityChange() {
        // Giả sử môi trường test đã có orderProduct (với các thông tin sản phẩm được hiển thị).
        driver.get("http://localhost:3000/order");
        
        // Chờ cho đến khi phần thông tin sản phẩm hiển thị (ví dụ: tên sản phẩm xuất hiện)
        WebElement productName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.border.p-6 img")));
        assertNotNull(productName);
        
        // Tìm các ô input theo placeholder
        WebElement recipientNameInput = driver.findElement(By.xpath("//input[@placeholder='Họ tên người nhận']"));
        WebElement recipientPhoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại']"));
        WebElement addressInput = driver.findElement(By.xpath("//textarea[@placeholder='Địa chỉ giao hàng']"));
        WebElement noteInput = driver.findElement(By.xpath("//textarea[@placeholder='Ghi chú']"));
        
        // Nhập dữ liệu vào các ô input
        recipientNameInput.clear();
        recipientNameInput.sendKeys("Nguyen Van A");
        assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"));
        
        recipientPhoneInput.clear();
        recipientPhoneInput.sendKeys("0123456789");
        assertEquals("0123456789", recipientPhoneInput.getAttribute("value"));
        
        addressInput.clear();
        addressInput.sendKeys("123 ABC Street");
        assertEquals("123 ABC Street", addressInput.getAttribute("value"));
        
        noteInput.clear();
        noteInput.sendKeys("Giao hàng vào buổi sáng");
        assertEquals("Giao hàng vào buổi sáng", noteInput.getAttribute("value"));
        
        // Kiểm tra nút tăng giảm số lượng
        // Lấy số lượng hiện tại hiển thị (ví dụ: "1")
        WebElement quantityDisplay = driver.findElement(By.xpath("//span[contains(@class, 'px-6') and text()='1']"));
        assertEquals("1", quantityDisplay.getText());
        
        // Tìm nút "+" và "-"
        WebElement increaseButton = driver.findElement(By.xpath("//button[contains(text(), '+')]"));
        WebElement decreaseButton = driver.findElement(By.xpath("//button[contains(text(), '-')]"));
        
        // Nhấn nút tăng số lượng
        increaseButton.click();
        // Chờ cập nhật số lượng thành "2"
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//span[contains(@class, 'px-6')]"), "2"));
        String quantityAfterIncrease = driver.findElement(By.xpath("//span[contains(@class, 'px-6')]")).getText();
        assertEquals("2", quantityAfterIncrease);
        
        // Nhấn nút giảm số lượng để về lại "1"
        decreaseButton.click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//span[contains(@class, 'px-6')]"), "1"));
        String quantityAfterDecrease = driver.findElement(By.xpath("//span[contains(@class, 'px-6')]")).getText();
        assertEquals("1", quantityAfterDecrease);
    }
    
    // Test case 3: Kiểm tra hành động đặt hàng và điều hướng đến trang order-success
    @Test
    public void testCreateOrderButton() {
        // Giả sử môi trường test đã có orderProduct (với thông tin sản phẩm được hiển thị)
        driver.get("http://localhost:3000/order");
        
        // Chờ cho đến khi sản phẩm hiển thị
        WebElement productElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.border.p-6 img")));
        assertNotNull(productElement);
        
        // Điền thông tin nhận hàng
        WebElement recipientNameInput = driver.findElement(By.xpath("//input[@placeholder='Họ tên người nhận']"));
        WebElement recipientPhoneInput = driver.findElement(By.xpath("//input[@placeholder='Số điện thoại']"));
        WebElement addressInput = driver.findElement(By.xpath("//textarea[@placeholder='Địa chỉ giao hàng']"));
        WebElement noteInput = driver.findElement(By.xpath("//textarea[@placeholder='Ghi chú']"));
        
        recipientNameInput.clear();
        recipientNameInput.sendKeys("Nguyen Van A");
        recipientPhoneInput.clear();
        recipientPhoneInput.sendKeys("0123456789");
        addressInput.clear();
        addressInput.sendKeys("123 ABC Street");
        noteInput.clear();
        noteInput.sendKeys("Giao hàng vào buổi sáng");
        
        // Nhấn nút "Đặt hàng"
        WebElement orderButton = driver.findElement(By.xpath("//button[contains(text(), 'Đặt hàng')]"));
        orderButton.click();
        
        // Chờ đợi điều hướng đến trang "/order-success"
        wait.until(ExpectedConditions.urlContains("/order-success"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/order-success"), "Sau khi đặt hàng, URL phải chứa '/order-success'");
    }
}
