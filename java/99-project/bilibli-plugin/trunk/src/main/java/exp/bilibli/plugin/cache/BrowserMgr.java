package exp.bilibli.plugin.cache;

import exp.bilibli.plugin.bean.ldm.BrowserDriver;
import exp.bilibli.plugin.envm.WebDriverType;

public class BrowserMgr {

	private BrowserDriver browser;
	
	private static volatile BrowserMgr instance;
	
	private BrowserMgr() {
		this.browser = new BrowserDriver(WebDriverType.PHANTOMJS, 10);	// FIXME: 配置文件设置等待时间
	}
	
	public static BrowserMgr getInstn() {
		if(instance == null) {
			synchronized (BrowserMgr.class) {
				if(instance == null) {
					instance = new BrowserMgr();
				}
			}
		}
		return instance;
	}
	
	public void open(String url) {
		browser.open(url);
	}
	
	public BrowserDriver getBrowser() {
		return browser;
	}

	public void close() {
		browser.close();
	}
	
}