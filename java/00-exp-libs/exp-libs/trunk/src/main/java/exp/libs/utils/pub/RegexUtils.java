package exp.libs.utils.pub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * 正则表达式工具包
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RegexUtils {

	/** 私有化构造方法 */
	protected RegexUtils() {}
	
	/**
	 * 完全匹配
	 * @param str
	 * @param regex
	 * @return
	 */
	public static boolean matches(String str, String regex) {
		boolean isMatch = false;
		if(StrUtils.isNotEmpty(str) && StrUtils.isNotEmpty(regex)) {
			isMatch = Pattern.compile(regex).matcher(str).matches();
		}
		return isMatch;
	}
	
	/**
	 * 部分匹配
	 * @param str
	 * @param regex
	 * @return
	 */
	public static boolean contains(String str, String regex) {
		boolean isMatch = false;
		if(StrUtils.isNotEmpty(str) && StrUtils.isNotEmpty(regex)) {
			Pattern ptn = Pattern.compile(regex);
			Matcher mth = ptn.matcher(str);
			isMatch = mth.find();
		}
		return isMatch;
	}
	
	public static String findFirst(String str, String regex) {
		String value = "";
		if(StrUtils.isNotEmpty(str) && StrUtils.isNotEmpty(regex)) {
			Pattern ptn = Pattern.compile(regex);
			Matcher mth = ptn.matcher(str);
			if(mth.find()) {
				value = (mth.groupCount() >= 1 ? mth.group(1) : "");
			}
		}
		return value;
	}
	
	public static List<String> findFirsts(String str, String regex) {
		List<String> list = new LinkedList<String>();
		if(StrUtils.isNotEmpty(str) && StrUtils.isNotEmpty(regex)) {
			Pattern ptn = Pattern.compile(regex);
			Matcher mth = ptn.matcher(str);
			
			while(mth.find()) {
				String value = (mth.groupCount() >= 1 ? mth.group(1) : "");
				list.add(value);
			}
		}
		return list;
	}
	
	public static List<List<String>> findAll(String str, String regex) {
		List<List<String>> list = new LinkedList<List<String>>();
		if(StrUtils.isNotEmpty(str) && StrUtils.isNotEmpty(regex)) {
			Pattern ptn = Pattern.compile(regex);
			Matcher mth = ptn.matcher(str);
			int groupCount = mth.groupCount();
			
			while(mth.find()) {
				List<String> groups = new ArrayList<String>(groupCount);
				for (int i = 0; i <= groupCount; i++) {
					groups.add(mth.group(i));
				}
				list.add(groups);
			}
		}
		return list;
	}
	
}