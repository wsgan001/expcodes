package exp.libs.warp.ui.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <PRE>
 * 自定义的动态增减行组件的差异化组件
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class _MyADComponent extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -8543279289759734486L;

	private JTextField testField;
	
	private JButton button;
	
	protected _MyADComponent() {
		super();
		this.testField = new JTextField();
		this.button = new JButton("...");
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		add(testField, BorderLayout.CENTER);
		add(button, BorderLayout.EAST);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public String getText() {
		return testField.getText();
	}
	
}
