package exp.bilibili.plugin.utils;

import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.net.http.HttpUtils;
import exp.libs.warp.ver.VersionMgr;

public class SafetyUtils {

	// FIXME 加密
	/** 软件授权页 */
	private final static String URL = "https://lyy289065406.github.io/certificate/";
	
	/** 授权码正则 */
	private final static String REGEX = "[a-zA-Z]\\d[a-zA-Z]\\d";
	
	/** 授权时间单位 */
	private final static long DAY_MILLIS = 86400000L;
	
	/** 授权码文件 */
	private final static String A_PATH = "./data/ac/authorization";
	
	/** 授权时间证书 */
	private final static String C_PATH = "./data/ac/certificate";
	
	/** 私有化构造函数 */
	protected SafetyUtils() {}
	
	/**
	 * 校验软件的证书(授权码和授权时间)是否有效.
	 *  管理员:拥有全部功能, 不受授权码和证书影响
	 *  主播:拥有全部功能, 仅有授权时间
	 *  普通用户:拥有部分功能, 受授权码和授权时间影响
	 * @param code 授权码(UPLIVE:主播; 匹配正则:普通用户)
	 * @param isAdmin 是否为管理员
	 * @return true:有效; false:无效
	 */
	public static String checkAC(String code) {
		String errMsg = "";
		if(!checkAuthorization(code)) {
			errMsg = "无效的授权码";
		}
		
		if(!checkCertificate()) {
			errMsg = "软件授权已过期";
		}
		return errMsg;
	}
	
	/**
	 * 检查输入的授权码是否有效
	 * @param code 授权码
	 * @return true:有效; false:无效
	 */
	private static boolean checkAuthorization(String code) {
		boolean isOk = false;
		if(!code.matches(REGEX)) {
			return isOk;
		}
		
		String authorization = fileToAuthorization();
		if(StrUtils.isEmpty(authorization)) {
			authorizationToFile(code);
			isOk = true;
			
		} else {
			isOk = authorization.equalsIgnoreCase(code);
		}
		return isOk;
	}
	
	/**
	 * 生成授权码到文件
	 * @return 授权码
	 */
	private static String authorizationToFile(String code) {
		String authorization = CryptoUtils.toDES(code);
		FileUtils.write(A_PATH, authorization, Charset.ISO, false);
		return authorization;
	}
	
	/**
	 * 从文件还原授权码
	 * @return 授权码
	 */
	private static String fileToAuthorization() {
		String authorization = FileUtils.read(A_PATH, Charset.ISO);
		return CryptoUtils.deDES(authorization);
	}
	
	/**
	 * 检测软件是否在授权有效期内
	 * @return
	 */
	private static boolean checkCertificate() {
		return (System.currentTimeMillis() < fileToCertificate());
	}
	
	/**
	 * 生成从现在开始一直到day天的之后的授权时间, 并写入文件
	 * @param day 有效期
	 * @return 授权截止时间
	 */
	public static String certificateToFile(int day) {
		day = (day < 0 ? 0 : day);
		long time = System.currentTimeMillis() + DAY_MILLIS * day;
		String certificate = CryptoUtils.toDES(String.valueOf(time));
		FileUtils.write(C_PATH, certificate, Charset.ISO, false);
		return certificate;
	}
	
	/**
	 * 从文件还原授权时间
	 * @return 授权截止时间
	 */
	private static long fileToCertificate() {
		String certificate = FileUtils.read(C_PATH, Charset.ISO);
		return NumUtils.toLong(CryptoUtils.deDES(certificate), 0);
	}
	
	
	public static void foo() {
		String ps = HttpUtils.getPageSource(URL);
		System.out.println(ps);
	}
	
	public static void main(String[] args) {
		foo();
		String verInfo = VersionMgr.exec("-p");
		System.err.println(verInfo);
	}
}
