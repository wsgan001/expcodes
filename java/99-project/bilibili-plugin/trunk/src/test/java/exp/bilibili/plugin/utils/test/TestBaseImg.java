package exp.bilibili.plugin.utils.test;

import exp.libs.warp.net.http.HttpUtils;

public class TestBaseImg {

	public static void main(String[] args) {
		String img = "data:image/png;base64,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";
		HttpUtils.convertBase64Img(img, "./log", "a");
		
		img = "data:image/png;base64,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";
		HttpUtils.convertBase64Img(img, "./log", "b");
	}
	
}