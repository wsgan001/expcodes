package exp.libs.utils.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <PRE>
 * 线程查询组件.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ThreadUtils {
	
	/** 日志器 */
	private static Logger log = LoggerFactory.getLogger(ThreadUtils.class);
	
	/** 私有化构造函数 */
	protected ThreadUtils() {}
	
	/**
	 * 获得活动线程总数
	 * @return 线程总数
	 */
	public static int getTotalSize() {
		final String MAIN_TH_NAME = "main";	// 主线程名称
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		while(!MAIN_TH_NAME.equals(tg.getName()) && tg.getParent() != null) {
			tg = tg.getParent();
		}
		return tg.activeCount();
	}
	
	/**
	 * 线程休眠(忙等)
	 * @param millis 休眠时间(ms)
	 */
	public static void tSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("线程休眠异常.", e);
		}
	}
	
	/**
	 * 线程阻塞(闲等)
	 * @param o 阻塞对象
	 * @param millis 阻塞时间(ms)
	 */
	public static void tWait(Object o, long millis) {
		try {
			synchronized (o) {
				o.wait(millis);
			}
		} catch (InterruptedException e) {
			log.error("线程阻塞异常.", e);
		}
	}
	
}