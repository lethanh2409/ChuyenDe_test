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
        test.info("Truy cập vào login");
        driver.get("http://localhost:3000/login");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/home"));

        test.info("Truy cập vào trang order");
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
    @DisplayName("Kiểm tra các input thông tin nhận hàng")
    public void testInputFields() {
        test.info("Truy cập vào login");
        driver.get("http://localhost:3000/login");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();


        try {
            // Chờ nút "Mua ngay" hiển thị và nhấn vào
            WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div/div[1]/div/button")));
            buyNowButton.click();

            // Chờ điều hướng sang trang đặt hàng
            wait.until(ExpectedConditions.urlContains("/order"));
            test.info("Chuyển sang trang đặt hàng thành công");

            // Kiểm tra input Họ tên người nhận
            WebElement recipientNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[1]")));
            recipientNameInput.clear();
            recipientNameInput.sendKeys("Nguyen Van A");
            assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"), "Tên người nhận không đúng!");

            // Kiểm tra input Số điện thoại
            WebElement recipientPhoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[2]")));
            recipientPhoneInput.clear();
            recipientPhoneInput.sendKeys("0987654321");
            assertEquals("0987654321", recipientPhoneInput.getAttribute("value"), "Số điện thoại không đúng!");

            // Kiểm tra textarea Địa chỉ giao hàng
            WebElement addressInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[1]")));
            addressInput.clear();
            addressInput.sendKeys("123 Đường ABC, Quận XYZ, TP HCM");
            assertEquals("123 Đường ABC, Quận XYZ, TP HCM", addressInput.getAttribute("value"), "Địa chỉ giao hàng không đúng!");

            // Kiểm tra textarea Ghi chú
            WebElement noteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[2]")));
            noteInput.clear();
            noteInput.sendKeys("Giao hàng trước 10h sáng");
            assertEquals("Giao hàng trước 10h sáng", noteInput.getAttribute("value"), "Ghi chú không đúng!");

            test.pass("Tất cả các input thông tin nhận hàng hoạt động chính xác");
        } catch (Exception e) {
            test.fail("Lỗi khi kiểm tra input thông tin nhận hàng: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Kiểm tra nút tăng giảm số lượng sản phẩm")
    public void testQuantityChange() {
        test.info("Truy cập vào đăng nhập");
        driver.get("http://localhost:3000/login");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        try {
            // Chờ nút "Mua ngay" hiển thị và nhấn vào
            WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div/div[1]/div/button")));
            buyNowButton.click();

            // Chờ điều hướng sang trang đặt hàng
            wait.until(ExpectedConditions.urlContains("/order"));
            test.info("Chuyển sang trang đặt hàng thành công");

            // Chờ số lượng hiển thị ban đầu
            WebElement quantityDisplay = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/span")));
            assertEquals("1", quantityDisplay.getText(), "Số lượng ban đầu không phải là 1!");

            // Tăng số lượng
            WebElement increaseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/button[2]")));
            increaseButton.click();

            // Chờ số lượng cập nhật thành 2
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/span"), "2"));

            // Kiểm tra số lượng hiển thị đúng
            quantityDisplay = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/span"));
            assertEquals("2", quantityDisplay.getText(), "Số lượng sau khi tăng không đúng!");

            // Giảm số lượng
            WebElement decreaseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/button[1]")));
            decreaseButton.click();

            // Chờ số lượng giảm về 1
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/span"), "1"));

            // Kiểm tra lại số lượng
            quantityDisplay = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[1]/div[1]/div/div[2]/span"));
            assertEquals("1", quantityDisplay.getText(), "Số lượng sau khi giảm không đúng!");

            test.pass("Các nút tăng giảm số lượng hoạt động chính xác");
        } catch (Exception e) {
            test.fail("Lỗi khi kiểm tra nút tăng giảm số lượng: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Kiểm tra hành động đặt hàng")
    public void testCreateOrderButton() {
        test.info("Truy cập vào trang đăng nhập");
        driver.get("http://localhost:3000/login");

        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        try {

            WebElement productDiv = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div/div[1]")));
            productDiv.click();

            // Chờ điều hướng sang trang đặt hàng
            wait.until(ExpectedConditions.urlContains("/product/P001"));
            test.info("Chuyển sang trang đặt hàng thành công");
            Thread.sleep(1000);


            // Chờ nút "Mua ngay" hiển thị và nhấn vào
            WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/div/div[2]/div/button")));
            buyNowButton.click();
            Thread.sleep(1000);

            // Chờ điều hướng sang trang đặt hàng
            wait.until(ExpectedConditions.urlContains("/order"));
            test.info("Chuyển sang trang đặt hàng thành công");
            Thread.sleep(1000);

            // Kiểm tra input Họ tên người nhận
            WebElement recipientNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[1]")));
            recipientNameInput.clear();
            Thread.sleep(1000);
            recipientNameInput.sendKeys("Nguyen Van A");
            Thread.sleep(1000);
            assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"), "Tên người nhận không đúng!");

            // Kiểm tra input Số điện thoại
            WebElement recipientPhoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[2]")));
            recipientPhoneInput.clear();
            Thread.sleep(1000);
            recipientPhoneInput.sendKeys("0987654321");
            Thread.sleep(1000);
            assertEquals("0987654321", recipientPhoneInput.getAttribute("value"), "Số điện thoại không đúng!");

            // Kiểm tra textarea Địa chỉ giao hàng
            WebElement addressInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[1]")));
            addressInput.clear();
            Thread.sleep(1000);
            addressInput.sendKeys("123 Đường ABC, Quận XYZ, TP HCM");
            Thread.sleep(1000);
            assertEquals("123 Đường ABC, Quận XYZ, TP HCM", addressInput.getAttribute("value"), "Địa chỉ giao hàng không đúng!");

            // Kiểm tra textarea Ghi chú
            WebElement noteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[2]")));
            noteInput.clear();
            Thread.sleep(1000);
            noteInput.sendKeys("Giao hàng trước 10h sáng");
            Thread.sleep(1000);
            assertEquals("Giao hàng trước 10h sáng", noteInput.getAttribute("value"), "Ghi chú không đúng!");

            // Nhấn nút đặt hàng
            WebElement orderButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div/div[2]/button")));
            orderButton.click();
            Thread.sleep(1000);

            // Chờ điều hướng đến trang thành công
            wait.until(ExpectedConditions.urlContains("/order-success"));
            Thread.sleep(1000);
            assertTrue(driver.getCurrentUrl().contains("/order-success"));

            test.pass("Đặt hàng thành công, điều hướng đến trang 'order-success'");
        } catch (Exception e) {
            test.fail("Lỗi khi đặt hàng: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Kiểm tra khi họ tên trống, các input khác được nhập")
    public void testBlankRecipientName() {
        test.info("Truy cập vào trang login");
        driver.get("http://localhost:3000/login");
        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        test.info("Chờ nút \"Mua ngay\" hiển thị và nhấn vào");
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div/div/div[1]/div/button")));
        buyNowButton.click();

        test.info("Chờ điều hướng sang trang đặt hàng");
        wait.until(ExpectedConditions.urlContains("/order"));
        test.info("Chuyển sang trang đặt hàng thành công");

        test.info("Nhập thông tin giao hàng");
        WebElement recipientPhoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[2]")));
        recipientPhoneInput.clear();
        recipientPhoneInput.sendKeys("0987654321");
        assertEquals("0987654321", recipientPhoneInput.getAttribute("value"), "Số điện thoại không đúng!");

        // Kiểm tra textarea Địa chỉ giao hàng
        WebElement addressInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[1]")));
        addressInput.clear();
        addressInput.sendKeys("123 Đường ABC, Quận XYZ, TP HCM");
        assertEquals("123 Đường ABC, Quận XYZ, TP HCM", addressInput.getAttribute("value"), "Địa chỉ giao hàng không đúng!");

        // Kiểm tra textarea Ghi chú
        WebElement noteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[2]")));
        noteInput.clear();
        noteInput.sendKeys("Giao hàng trước 10h sáng");
        assertEquals("Giao hàng trước 10h sáng", noteInput.getAttribute("value"), "Ghi chú không đúng!");

        WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/button"));
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/p")
        ));
        assertEquals("Recipient name cannot be blank", errorMessage.getText(), "Thông báo lỗi không đúng!");

    }

    @Test
    @DisplayName("Kiểm tra khi họ tên trống, các input khác được nhập")
    public void testBlankRecipientPhone() {
        test.info("Truy cập vào trang login");
        driver.get("http://localhost:3000/login");
        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        test.info("Chờ nút \"Mua ngay\" hiển thị và nhấn vào");
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div/div/div[1]/div/button")));
        buyNowButton.click();

        test.info("Chờ điều hướng sang trang đặt hàng");
        wait.until(ExpectedConditions.urlContains("/order"));
        test.info("Chuyển sang trang đặt hàng thành công");

        test.info("Nhập thông tin giao hàng");
        // Kiểm tra input Họ tên người nhận
        WebElement recipientNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[1]")));
        recipientNameInput.clear();
        recipientNameInput.sendKeys("Nguyen Van A");
        assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"), "Tên người nhận không đúng!");

        // Kiểm tra textarea Địa chỉ giao hàng
        WebElement addressInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[1]")));
        addressInput.clear();
        addressInput.sendKeys("123 Đường ABC, Quận XYZ, TP HCM");
        assertEquals("123 Đường ABC, Quận XYZ, TP HCM", addressInput.getAttribute("value"), "Địa chỉ giao hàng không đúng!");

        // Kiểm tra textarea Ghi chú
        WebElement noteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[2]")));
        noteInput.clear();
        noteInput.sendKeys("Giao hàng trước 10h sáng");
        assertEquals("Giao hàng trước 10h sáng", noteInput.getAttribute("value"), "Ghi chú không đúng!");

        WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/button"));
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/p")
        ));
        assertEquals("Recipient phone cannot be blank", errorMessage.getText(), "Thông báo lỗi không đúng!");

    }

    @Test
    @DisplayName("Kiểm tra khi họ tên trống, các input khác được nhập")
    public void testBlankAddress() {
        test.info("Truy cập vào trang login");
        driver.get("http://localhost:3000/login");
        WebElement usernameInput = driver.findElement(By.name("user_login"));
        WebElement passwordInput = driver.findElement(By.name("user_password"));
        WebElement loginButton = driver.findElement(By.name("submit"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("admin");
        loginButton.click();

        test.info("Chờ nút \"Mua ngay\" hiển thị và nhấn vào");
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/div/div/div[1]/div/button")));
        buyNowButton.click();

        test.info("Chờ điều hướng sang trang đặt hàng");
        wait.until(ExpectedConditions.urlContains("/order"));
        test.info("Chuyển sang trang đặt hàng thành công");

        test.info("Nhập thông tin giao hàng");
        WebElement recipientPhoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[2]")));
        recipientPhoneInput.clear();
        recipientPhoneInput.sendKeys("0987654321");
        assertEquals("0987654321", recipientPhoneInput.getAttribute("value"), "Số điện thoại không đúng!");

        // Kiểm tra input Họ tên người nhận
        WebElement recipientNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/input[1]")));
        recipientNameInput.clear();
        recipientNameInput.sendKeys("Nguyen Van A");
        assertEquals("Nguyen Van A", recipientNameInput.getAttribute("value"), "Tên người nhận không đúng!");

        // Kiểm tra textarea Ghi chú
        WebElement noteInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/textarea[2]")));
        noteInput.clear();
        noteInput.sendKeys("Giao hàng trước 10h sáng");
        assertEquals("Giao hàng trước 10h sáng", noteInput.getAttribute("value"), "Ghi chú không đúng!");

        WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/button"));
        submitButton.click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/p")
        ));
        assertEquals("Address cannot be blank", errorMessage.getText(), "Thông báo lỗi không đúng!");

    }

}
