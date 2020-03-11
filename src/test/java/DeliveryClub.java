import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/*Тест разработан для проверки функционала сайта "https://www.delivery-club.ru". В данном тесте пользователь заказывает еду
 выбранного ресторана. Предусмотрен негативный сценарий при нажатии на кнопку "Заказ"*/

@Epic("Доставка еды Delivery-club")
@Feature("Заказ выбранных блюд")
@Owner("Черномырдин В.А.")

public class DeliveryClub {
    private WebDriver webDriver;

    @BeforeClass
    public void beforeClass() {
        System.setProperty(
                "webdriver.chrome.driver", "C:\\Users\\ITPLUSCONS\\IdeaProjects\\DeliveryClub\\src\\test" +
                        "\\resources\\chromedriver.exe");
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
        webDriver.get("https://www.delivery-club.ru");
        Assert.assertEquals(webDriver.getTitle(), "Доставка еды из ресторанов Москвы за 15-30 минут! Delivery Club");
    }

    @Test(description = "Заказ 'Level Kitchen'")
    public void levelKitchen() throws Exception {
        restaurantChoice("Москва, Новокосинская улица, 12к1", "Level Kitchen");
        webDriver.findElement(By.xpath("//ul[@data-id='300488298']//li[4]")).click();
        // Окно заказа
        webDriver.findElement(By.xpath("//li[1][@class='basket-product']//button[2]")).click();
        webDriver.findElement(By.xpath("//li[1][@class='basket-product']//button[2]")).click();
        Thread.sleep(3000);
        webDriver.findElement(By.xpath("//div[@class='basket-button-submit']/button")).click();
        doneOrder();
    }

    @Test(description = "Заказ 'PRONTO'")
    public void prontoPizza() throws Exception {
        restaurantChoice("Москва, Кутузовский проспект, 12с1", "PRONTO");
        webDriver.findElement(By.xpath("//ul[@class='vendor-categories__list']//li[3]")).click();
        webDriver.findElement(By.xpath("//ul[@data-id='300123300']//li[1]")).click();
        // Выбираем размер
        webDriver.findElement(By.xpath("//*[text()='Стандартная']")).click();
        // Выбираем ингредиенты
        webDriver.findElement(By.xpath("//*[text()='Болгарский перец']")).click();
        webDriver.findElement(By.xpath("//*[text()='Маслины']")).click();
        webDriver.findElement(By.xpath("//*[text()='Оливки']")).click();
        webDriver.findElement(By.xpath("//*[text()='Бекон']")).click();
        webDriver.findElement(By.xpath("//div[@class='product-quantity-controls__container" +
                " product-popup__quantity-controls']//button[2]"))
                .click();
        webDriver.findElement(By.xpath("//div[@class='product-popup__add-label']")).click();
        Thread.sleep(3000);
        // Окно заказа
        webDriver.findElement(By.xpath("//div[@class='basket-button-submit']/button")).click();
        doneOrder();
    }

    @Step("Проверка ввода адреса в поле 'Укажите адрес доставки'. Поиск ресторана")
    private void restaurantChoice(String addressHome, String name) throws Exception {
        webDriver.findElement(By.xpath("//span[@class='address-input__location']")).click();
        WebElement address = webDriver.findElement(By.xpath("//input[@placeholder='Укажите адрес доставки']"));
        String nameAddress = addressHome;
        for (char adr : nameAddress.toCharArray()) {
            Thread.sleep(300);
            address.sendKeys(String.valueOf(adr));
        }
        address.sendKeys(Keys.ENTER);
        Thread.sleep(3000);
        webDriver.findElement(By.xpath("//span[@class='delivery-time-button__status']")).click();
        webDriver.findElement(By.xpath("//div[@class='delivery-time-form__wrap']//label[2]")).click();
        webDriver.findElement(By.xpath("//button[@class='delivery-time-form__apply gray-btn--md']")).click();
        WebElement searchField = webDriver.findElement(By.xpath("//input[@placeholder='Поиск блюд и ресторанов']"));
        searchField.sendKeys(name);
        searchField.sendKeys(Keys.ENTER);
        searchField.findElement(By.xpath("//div[@class='vendor-item-products__logo']")).click();
        Assert.assertEquals(webDriver.findElement(By.xpath("//h1[@class='vendor-headline__title']"))
                .getText(), name);
    }


    @Step("Оформление заказа. Негативный сценарий")
    private void doneOrder() throws Exception {
        WebElement data = webDriver.findElement(By.xpath("//input[@type='tel']"));
        data.sendKeys("9169999999"); // Телефон для связи
        data.findElement(By.xpath("//input[@inputmode='numeric']")).sendKeys("1433"); // Код из СМС
        Thread.sleep(2000);
        Assert.assertEquals(webDriver.findElement(By.xpath("//div[@class='verification__note']"))
                .getText(), "Код неверный. Пожалуйста, попробуйте еще раз");
        data.findElement(By.xpath("//div[@class='checkout-form']/div[1]/div[4]//input[1]"))
                .sendKeys("14"); // Подъезд
        data.findElement(By.xpath("//div[@class='checkout-form']/div[1]/div[5]//input[1]"))
                .sendKeys("90"); // Домофон
        data.findElement(By.xpath("//div[@class='checkout-form']/div[1]/div[6]//input[1]"))
                .sendKeys("7"); // Этаж
        data.findElement(By.xpath("//div[@class='checkout-form']/div[1]/div[7]//input[1]"))
                .sendKeys("47"); // Квартира
        data.findElement(By.xpath("//div[@class='checkout-form-comment__container']"))
                .sendKeys("Побольше салфеток ;)"); // Комментарий
        data.findElement(By.xpath("//div[@class='form-selection']/label")).click(); // Онлайн оплата
        data.findElement(By.xpath("//label[@class='label--def checkout-form__short checkout-form__short--promocode']"))
                .sendKeys("311133"); // Промокод
        Assert.assertEquals(webDriver.findElement(By.xpath("//span[@class='label--def__error']"))
                .getText(), "Введите номер телефона и код из смс");
        data.findElement(By.xpath("//button[@class='checkout-form-button__area green-btn--lg']")).click(); // Заказать
    }

    @AfterClass
    public void quitBrowser() {
        webDriver.quit();
    }
}