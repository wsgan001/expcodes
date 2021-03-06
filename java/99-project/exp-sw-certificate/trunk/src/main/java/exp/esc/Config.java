package exp.esc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.esc.bean.App;
import exp.libs.envm.Charset;
import exp.libs.warp.conf.xml.XConfig;
import exp.libs.warp.conf.xml.XConfigFactory;


/**
 * <PRE>
 * 程序配置
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Config {
	
	private final static Logger log = LoggerFactory.getLogger(Config.class);
	
	public final static String DEFAULT_CHARSET = Charset.UTF8;
	
	private final static String CONF_PATH = "./conf/esc_conf.xml";
	
	private static volatile Config instance;
	
	private XConfig xConf;
	
	private Config() {
		this.xConf = XConfigFactory.createConfig("EscConf");
		xConf.loadConfFile(CONF_PATH);
	}
	
	public static Config getInstn() {
		if(instance == null) {
			synchronized (Config.class) {
				if(instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public List<App> getApps() {
		List<App> appList = new LinkedList<App>();
		try {
			Element root = xConf.loadConfFile(CONF_PATH);
			Element apps = root.element("apps");
			Iterator<Element> appIts = apps.elementIterator("app");
			while(appIts.hasNext()) {
				Element app = appIts.next();
				String name = app.elementText("name");
				String versions = app.elementText("versions");
				String time = app.elementText("time");
				String blacklist = app.elementText("blacklist");
				
				appList.add(new App(name, versions, time, blacklist));
			}
			
		} catch(Exception e) {
			log.error("加载配置文件失败: {}", CONF_PATH, e);
		}
		return appList;
	}
	
	
	
}
