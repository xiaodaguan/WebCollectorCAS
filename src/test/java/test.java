import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.File;

import java.io.IOException;

/**
 * Created by guanxiaoda on 16/7/26.
 */
public class test {
    private static Logger logger = LoggerFactory.getLogger(test.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        String uname = "13473742743";
        String passwd = "a123456";
        FirefoxProfile profile = new FirefoxProfile();
        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(false);
        FirefoxDriver driver = new FirefoxDriver();


        while (!login(uname, passwd, driver)) {
            Thread.sleep(3);
        }

        int i = 0;
        while (true) {
            logger.info("{}", i++);
            driver.get("http://s.weibo.com/weibo/%25E7%25BD%2591%25E7%25BA%25A2%25E5%2588%25B6%25E9%2580%25A0%25E6%25B5%2581%25E6%25B0%25B4%25E7%25BA%25BF?topnav=1&wvr=6&Refer=top_button");
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("title")));

//            Thread.sleep(1000 * 10);

            if (driver.getPageSource().contains("行为有些异常") && driver.getPageSource().contains("验证码")) {
                logger.error("第" + i + "次搜索，被封了");
                logger.info("解封..");
                System.in.read();
//                login(uname, passwd, driver);
                logger.info("重新开始...");
                i = 0;
            }
        }

    }

    private static boolean login(String uname, String passwd, FirefoxDriver driver) {
        driver.get("http://weibo.com/login.php");
        WebDriverWait driverWait = new WebDriverWait(driver, 10);

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[suda-uatrack*=ordinary_login]")));
        try {
            File.writeTxt("tmphtml/tmp.html", driver.getPageSource());
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver.findElementByCssSelector("a[suda-uatrack*=ordinary_login]").click();

        driver.findElementByCssSelector("#pl_login_form > div > div.info_header > div > a:nth-child(2)").click();
        driver.findElementByCssSelector("#loginname").clear();
        driver.findElementByCssSelector("#loginname").sendKeys(uname);
        driver.findElementByCssSelector("div.info_list.password > div > input").sendKeys(passwd);


        driver.findElementByCssSelector("#pl_login_form > div > div:nth-child(3) > div.info_list.login_btn > a").click();
        driverWait = new WebDriverWait(driver, 20);
        try {
            driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#skin_cover_s > div")));
        } catch (org.openqa.selenium.TimeoutException e) {

        }
        String result = null;
        if (driver.getTitle().contains("我的首页")) {
            logger.info("user [{}] login succeed.", uname);
            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : driver.manage().getCookies()) {
                sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
            }
            result = sb.toString();

            return true;
        } else {
            logger.error("user [{}] login failed.", uname);
            return false;
        }
    }
}
