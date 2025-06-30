package com.dhu.smartmed.service;

import com.dhu.smartmed.entity.Medicine;
import com.dhu.smartmed.entity.MedicinePrice;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;

@Service
public class MedicinePriceCrawlerService {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicinePriceService medicinePriceService;

    private static final String SEARCH_URL = "https://www.315jiage.cn/search?keyword=";
    private static final String DETAIL_URL_PREFIX = "https://www.315jiage.cn/";

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void updateMedicinePrices() {
        List<Medicine> medicines = medicineService.getAllMedicines();

        for (Medicine medicine : medicines) {
            try {
                List<MedicinePrice> prices = crawlMedicinePrices(medicine);
                System.out.println("1");
                if (!prices.isEmpty()) {
                    // 删除该药品的旧价格记录
                    medicinePriceService.deletePricesByMedicineId(medicine.getMedicineId());
                    System.out.println("2");
                    // 保存新的价格记录
                    for(MedicinePrice price:prices){
                    medicinePriceService.savePrice(price);}
                    System.out.println("3");
                }
            } catch (Exception e) {
                System.err.println("Failed to update prices for medicine: " + medicine.getName());
            }
        }
    }

    private List<MedicinePrice> crawlMedicinePrices(Medicine medicine) {
        try {
            // 配置 Edge 选项
            EdgeOptions options = new EdgeOptions();
            //options.addArguments("--headless"); // 无头模式
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");

            // 创建 WebDriver
            WebDriver driver = new EdgeDriver(options);

            // 搜索药品并获取第一个结果的详情页
            String searchUrl = SEARCH_URL + URLEncoder.encode(medicine.getName(), "UTF-8");
            driver.get(searchUrl);

            // 使用显式等待确保元素加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".flex.flex-row.flex-wrap.justify-start.items-center.grow.gap-3.flex-shrink-0 a[target='_blank']")));
            if (firstResult == null) {
                System.err.println("No search results found for: " + medicine.getName());
                driver.quit();
                return Collections.emptyList();
            }
            String detailUrl = firstResult.getAttribute("href");
            driver.get(detailUrl);

            wait.until((WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("prGrid")));

            // 提取包装规格信息
            WebElement specElement = driver.findElement(By.cssSelector(".block-info-prop > span:nth-child(6)"));
            String parentText = specElement.findElement(By.xpath("..")).getText();
            String specification = parentText.split("：")[3].split(" ")[0];
            System.out.println("Specification text: " + specification); // 打印调试信息

            // 等待页面完全加载
            wait.until((WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));

            // 等待动态内容加载完成
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("prGrid")));
            List<WebElement> priceElements = driver.findElements(By.cssSelector("#prGrid"));

            System.out.println("Number of price elements found: " + priceElements.size());
            if (priceElements.isEmpty()) {
                System.err.println("No data available for extraction.");
            }
            List<MedicinePrice> prices = new ArrayList<>();
            for (WebElement element : priceElements) {
                for (int i = 1; i <= 10; i++) {
                    // 创建一个新的 price 对象
                    MedicinePrice price = new MedicinePrice();
                    price.setMedicineId(medicine.getMedicineId());
                    price.setSpecification(specification); // 设置包装规格
                    // 提取商家名称
                    String storeNameSelector = "table#prGrid tbody tr:nth-child(" + i + ") td:nth-child(1) a";
                    price.setStoreName(element.findElement(By.cssSelector(storeNameSelector)).getText());

                    // 提取价格
                    String priceSelector = "table#prGrid tbody tr:nth-child(" + i + ") span";
                    String priceText = element.findElement(By.cssSelector(priceSelector)).getText();
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
            for (MedicinePrice price : prices) {
                System.out.println("Store: " + price.getStoreName());
                System.out.println("Specification: " + price.getSpecification());
                System.out.println("Price: " + price.getPrice());
                System.out.println("URL: " + price.getUrl());
            }


            driver.quit();
            return prices;
        } catch (Exception e) {
            System.err.println("Error crawling prices for: " + medicine.getName());
            return Collections.emptyList();
        }
    }
}