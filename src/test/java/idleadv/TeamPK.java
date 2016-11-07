package idleadv;

import downloader.SeleniumWebDriverManager;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Created by guanxiaoda on 16/8/17.
 */
public class TeamPK {
    public static void main(String[] args) {


        PhantomJSDriver driver = SeleniumWebDriverManager.getInstance().getPhantomJSDriver();
        driver.get("");
    }
}
