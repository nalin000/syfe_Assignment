


package introduction;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class SauceDemoTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {

        ChromeOptions options = new ChromeOptions();

        // 🔥 THIS IS THE REAL FIX
        options.addArguments("--incognito");

        // Optional (extra safety)
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.manage().window().maximize();
    }

    
    @BeforeMethod
    public void loginBeforeEachTest() {
        driver.get("https://www.saucedemo.com/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).clear();
        driver.findElement(By.id("user-name")).sendKeys("standard_user");

        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();

        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    // ---------- UTILITY ----------
    public int getCartCount() {
        try {
            return Integer.parseInt(driver.findElement(By.className("shopping_cart_badge")).getText());
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------- TASK 1 ----------
    @Test(priority = 1)
    public void testLoginValidation() {

        driver.get("https://www.saucedemo.com/");

        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))
        );
        Assert.assertTrue(error.getText().contains("Username and password do not match"));
    }

    // ---------- TASK 2 ----------
    @Test(priority = 2)
    public void addItemsFromInventory() {

        Select dropdown = new Select(
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product_sort_container")))
        );
        dropdown.selectByVisibleText("Price (low to high)");

        List<WebElement> addButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[contains(@id,'add-to-cart')]"))
        );

        addButtons.get(0).click();
        addButtons.get(1).click();

        Assert.assertEquals(getCartCount(), 2);
    }

    // ---------- TASK 3 ----------
    @Test(priority = 3)
    public void addFromProductPage() {

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sauce Labs Onesie"))).click();

        WebElement addBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@id,'add-to-cart')]"))
        );
        addBtn.click();

        Assert.assertEquals(getCartCount(), 1);
    }

    // ---------- TASK 4 ----------
    @Test(priority = 4)
    public void removeItemFromCart() {

        // Add 2 items first
        List<WebElement> addButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[contains(@id,'add-to-cart')]"))
        );
        addButtons.get(0).click();
        addButtons.get(1).click();

        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();

        List<WebElement> items = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("cart_item"))
        );

        for (WebElement item : items) {
            String priceText = item.findElement(By.className("inventory_item_price"))
                    .getText().replace("$", "");
            double price = Double.parseDouble(priceText);

            if (price >= 8 && price <= 10) {
                item.findElement(By.tagName("button")).click();
                break;
            }
        }

        Assert.assertEquals(getCartCount(), 1);
    }

    // ---------- TASK 5 ----------
    @Test(priority = 5)
    public void checkoutFlow() {

        // Add items first
        List<WebElement> addButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[contains(@id,'add-to-cart')]"))
        );
        addButtons.get(0).click();
        addButtons.get(1).click();

        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name"))).sendKeys("Nalin");
        driver.findElement(By.id("last-name")).sendKeys("Trivedi");
        driver.findElement(By.id("postal-code")).sendKeys("208001");

        driver.findElement(By.id("continue")).click();

        WebElement total = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("summary_total_label"))
        );
        System.out.println("Total: " + total.getText());

        driver.findElement(By.id("finish")).click();

        WebElement success = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("complete-header"))
        );
        Assert.assertEquals(success.getText(), "THANK YOU FOR YOUR ORDER");
    }

    // ---------- TASK 6 ----------
    @Test(priority = 6)
    public void logoutTest() {

        wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();

        wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/"));
        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"));
    }
}














