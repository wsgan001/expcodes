package exp.bilibli.plugin.utils.test;

import java.io.File;

import exp.bilibli.plugin.utils.OCRUtils;

public class TestOCRUtils {

	public static void main(String[] args) {
		File dir = new File("./src/test/java/exp/bilibli/plugin/utils/test/img");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			if(img.getName().endsWith(".jpg")) {
				System.out.println(img.getName() + " : " + OCRUtils.jpgToTxt(img.getPath()));
			}
		}
	}
	
}