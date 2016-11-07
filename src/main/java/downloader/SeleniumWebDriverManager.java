package downloader;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;


/**
 * Created by guanxiaoda on 6/7/16.
 * singleton
 */
public class SeleniumWebDriverManager {

    private static SeleniumWebDriverManager webDriver = new SeleniumWebDriverManager();


    private SeleniumWebDriverManager() {
    }

    public PhantomJSDriver getPhantomJSDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs/osx/phantomjs");
        PhantomJSDriver driver = new PhantomJSDriver(caps);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;
    }

    public static SeleniumWebDriverManager getInstance() {
        return webDriver;
    }
}
