package exp.bilibli.plugin.core.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import exp.libs.utils.other.StrUtils;
import exp.libs.warp.ui.SwingUtils;
import exp.libs.warp.ui.cpt.win.PopChildWindow;

class QrcodeUI extends PopChildWindow {

	/** serialVersionUID */
	private static final long serialVersionUID = 3032128610929327304L;

	private final static String TIPS_PATH = LoginMgr.IMG_DIR.concat("/tips.png");
	
	private JLabel imgLabel;
	
	private JLabel timeLabel;
	
	protected QrcodeUI() {
		super("手机B站APP扫码登陆", 300, 320);
	}
	
	@Override
	protected void initComponents(Object... args) {
		this.imgLabel = new JLabel(new ImageIcon(TIPS_PATH));
		this.timeLabel = new JLabel("正在更新二维码...");
		timeLabel.setForeground(Color.RED);
	}

	@Override
	protected void setComponentsLayout(JPanel rootPanel) {
		rootPanel.add(imgLabel, BorderLayout.CENTER);
		JPanel btnPanel = SwingUtils.getHFlowPanel(
				new JLabel(" "), timeLabel, new JLabel(" "));
		rootPanel.add(btnPanel, BorderLayout.SOUTH);
	}

	@Override
	protected void setComponentsListener(JPanel rootPanel) {
		// Undo
	}

	protected void updateImg() {
		File dir = new File(LoginMgr.IMG_DIR);
		File[] files = dir.listFiles();
		for(File file : files) {
			if(file.getName().contains(LoginMgr.IMG_NAME)) {
				
				// 注意: 这里不能通过new ImageIcon(ImgPath)的方式更新图片
				// 因为这种方式会因为图片路径没有变化, 而不去更新缓存, 导致显示的二维码一直不变
				Image img = Toolkit.getDefaultToolkit().createImage(file.getPath());
				imgLabel.setIcon(new ImageIcon(img));
				break;
			}
		}
	}
	
	protected void updateTime(int time) {
		if(time < 0) {
			timeLabel.setText("正在更新二维码...");
			
		} else {
			String sTime = StrUtils.leftPad(String.valueOf(time), '0', 3);
			timeLabel.setText(StrUtils.concat("有效时间 : ", sTime, " 秒"));
		}
	}
	
}