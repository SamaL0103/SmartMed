package com.dhu.smartmed;

import com.dhu.smartmed.entity.MedicinePrice;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MedicinePriceCrawlerTest {

    public static void main(String[] args) {
        try {
            // 配置 Edge 选项
            EdgeOptions options = new EdgeOptions();
            //options.addArguments("--headless"); // 无头模式
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            //options.addArguments("--window-size=1920,1080"); // 设置窗口大小
            //options.addArguments("--start-maximized"); // 最大化窗口
            //options.addArguments("--page-load-strategy=normal"); // 设置页面加载策略
            //options.addArguments("--disable-dev-shm-usage"); // 禁用共享内存
            //options.addArguments("--remote-debugging-port=9222"); // 启用远程调试

            // 创建 WebDriver
            WebDriverManager.edgedriver().setup();
            WebDriver driver = new EdgeDriver(options);

            // 搜索药品并获取第一个结果的详情页
            String searchUrl = "https://www.315jiage.cn/search?keyword=" + URLEncoder.encode("复方氨酚烷胺胶囊", "UTF-8");
            driver.get(searchUrl);

            // 确保页面完全加载
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                    (WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete")
            );

            // 使用 FluentWait 等待元素
            FluentWait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class);

            WebElement firstResult = wait.until(d -> d.findElement(By.cssSelector(".flex.flex-row.flex-wrap.justify-start.items-center.grow.gap-3.flex-shrink-0 a[target='_blank']")));
            if (firstResult == null) {
                System.err.println("No search results found for: 阿司匹林");
                driver.quit();
                return;
            }
            String detailUrl = firstResult.getAttribute("href");
            driver.get(detailUrl);

            // 确保页面完全加载
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                    (WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete")
            );

            WebElement specElement = driver.findElement(By.cssSelector(".block-info-prop > span:nth-child(6)"));
            String parentText = specElement.findElement(By.xpath("..")).getText();

            // 提取包装规格信息
            String specification = parentText.split("：")[3].split(" ")[0];
            System.out.println("Specification text: " + specification); // 打印调试信息

            // 等待动态内容加载完成
            // 等待动态内容加载完成
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("prGrid")));
            List<WebElement> priceElements = driver.findElements(By.cssSelector("#prGrid "));
            List<MedicinePrice> prices = new ArrayList<>();
            System.out.println("Number of price elements found: " + priceElements.size());
            if (priceElements.isEmpty()) {
                System.err.println("No data available for extraction.");
            }
            for (WebElement element : priceElements) {
                for (int i = 1; i <= 10; i++) {
                    // 创建一个新的 price 对象
                    MedicinePrice price = new MedicinePrice();
                    price.setSpecification(specification); // 设置包装规格
                    // 提取商家名称
                    String storeNameSelector = "tr:nth-child(" + i + ") td:nth-child(1) a";
                    price.setStoreName(element.findElement(By.cssSelector(storeNameSelector)).getText());

                    // 提取价格
                    String priceSelector = "table#prGrid tbody tr:nth-child(" + i + ") span";
                    String priceText = element.findElement(By.cssSelector(priceSelector)).getText();
                    System.out.println(priceText);
                    double priceValue = Double.parseDouble(priceText.replaceAll("[^0-9.]", ""));
                    price.setPrice(priceValue);

                    // 设置 URL
                    price.setUrl(detailUrl);

                    // 添加到列表
                    prices.add(price);
                }
            }

            // 按价格排序并只保留前三条
            prices.sort(Comparator.comparing(MedicinePrice::getPrice));
            prices = prices.subList(0, Math.min(3, prices.size()));

            // 打印结果
            for (MedicinePrice price : prices) {
                System.out.println("Store: " + price.getStoreName());
                System.out.println("Specification: " + price.getSpecification());
                System.out.println("Price: " + price.getPrice());
                System.out.println("URL: " + price.getUrl());
            }
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}