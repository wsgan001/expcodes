package exp.libs.mrp;

public class Log {

	public static void info(String msg) {
		System.out.println("[INFO] ".concat(msg));
	}
	
	public static void warn(String msg) {
		System.out.println("[WARN] ".concat(msg));
	}

	public static void error(String msg) {
		System.err.println("[ERROR] ".concat(msg));
	}
	
	public static void error(String msg, Throwable e) {
		error(msg);
		e.printStackTrace();
	}
	
}
