package exp.libs.utils.pub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <PRE>
 * 字符集处理工具包.
 * 
 * 在仅有 中文字符 和 ASCII字符 的环境下，
 * 已知的无错误的编码转换方向如下：
 * GB2312 -> GBK
 * GB2312 -> UTF8
 * GBK -> UTF8
 * UTF8 -> GBK
 * UNICODE -> GBK
 * GBK -> UNICODE
 * UNICODE -> UTF8
 * UTF8 -> UNICODE
 * 
 * 使用ISO-8859-1存储编码的转换方向如下（其他编码雷同）：
 * GBKstr -> GBKbyte -> ISOstr -> ISObyte -> GBKstr
 * UTF8str -> UTF8byte -> ISOstr -> ISObyte -> UTF8str
 * UNICODEstr -> UNICODEbyte -> ISOstr -> ISObyte -> UNICODEstr
 * </PRE>
 * 
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CharsetUtils {

	/** 日志器 */
	private static Logger log = LoggerFactory.getLogger(CharsetUtils.class);
	
	/** 私有化构造函数 */
	protected CharsetUtils() {}
	
	/**
	 * 检测字符编码是否有效
	 * @param charset 被检测的字符编码
	 * @return true:有效; false:无效
	 */
	public static boolean isVaild(String charset) {
		boolean isVaild = true;
		try {
			"test".getBytes(charset);
		} catch (Exception e) {
			isVaild = false;
		}
		return isVaild;
	}
	
	public static boolean isInvalid(String charset) {
		return !isVaild(charset);
	}
	
	/**
	 * <pre>
	 * 把以charset编码的bytes的字节数组，变成以charset编码的String形式
	 * 
	 * charset byte[] -> charset String
	 * </pre>
	 * @param bytes 源字节数组
	 * @param charset 源字节数组的编码
	 * @return 以charset编码的字符串
	 */
	public static String toStr(byte[] bytes, String charset) {
		String str = "";
		try {
			str = new String(bytes, charset);
		} catch (Exception e) {
			log.error("把字节数组转换成 [{}] 编码字符串失败.", charset, e);
		}
		return str;
	}
	
	/**
	 * <pre>
	 * 把srcStr转换为使用destCharset编码的byte[]
	 * 
	 * ?charset String -> destCharset byte[] 
	 * 
	 * 在不知道[源字符串]的编码时，慎用。
	 * 因为[目标编码]可能不兼容[源字符串]的编码，导致乱码。
	 * </pre>
	 * @param srcStr 源字符串
	 * @param destCharset 目标字节数组的编码
	 * @return 目标字节数组
	 */
	public static byte[] toBytes(String srcStr, String destCharset) {
		byte[] bytes = {};
		try {
			bytes = srcStr.getBytes(destCharset);
		} catch (Exception e) {
			log.error("把字符串 [{}] 转换成 [{}] 编码字节数组失败.", 
					srcStr, destCharset, e);
		}
		return bytes;
	}
	
	/**
	 * <pre>
	 * 把使用srcCharset编码的srcBytes，转换为使用destCharset编码的byte[]
	 * 
	 * srcCharset byte[] -> destCharset byte[]
	 * 
	 * 在不知道[源字节数组]的编码时，慎用。
	 * 因为[目标编码]可能不兼容[源字节数组]的编码，导致乱码。
	 * </pre>
	 * @param srcBytes 源字节数组
	 * @param srcCharset 源字节数组的编码
	 * @param destCharset 目标字节数组的编码
	 * @return 目标字节数组
	 */
	public static byte[] tracnscode(byte[] srcBytes, 
			String srcCharset, String destCharset) {
		String srcStr = toStr(srcBytes, srcCharset);
		return toBytes(srcStr, destCharset);
	}
	
}