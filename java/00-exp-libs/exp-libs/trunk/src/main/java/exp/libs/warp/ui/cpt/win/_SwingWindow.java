package exp.libs.warp.ui.cpt.win;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import exp.libs.warp.ui.SwingUtils;

/**
 * <PRE>
 * swing通用窗口
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-08-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
@SuppressWarnings("serial")
abstract class _SwingWindow extends JFrame {

	/** 系统托盘 */
	private final static SystemTray SYSTEM_TRAY = SystemTray.getSystemTray();
	
	/** 最小化状态：未初始化 */
	private final static int UNINIT = 0;
	
	/** 最小化状态：到系统托盘 */
	private final static int TO_TRAY = 1;
	
	/** 最小化状态：到任务栏 */
	private final static int TO_MINI = -1;
	
	/** 窗口出现位置（屏幕中心:默认） */
	protected final static int LOCATION_CENTER = 0;
	
	/** 窗口出现位置（屏幕左上） */
	protected final static int LOCATION_LU = 1;
	
	/** 窗口出现位置（屏幕左下） */
	protected final static int LOCATION_LB = 2;
	
	/** 窗口出现位置（屏幕右上） */
	protected final static int LOCATION_RU = 3;
	
	/** 窗口出现位置（屏幕右下） */
	protected final static int LOCATION_RB = 4;
	
	/** 屏宽 */
	protected final int WIN_WIDTH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	
	/** 屏高 */
	protected final int WIN_HIGH = 
			(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	/** 窗口名称 */
	private String name;
	
	/** 窗口宽 */
	protected final int width;
	
	/** 窗口高 */
	protected final int high;
	
	/** 最小化模式 */
	private int miniMode;
	
	/** 基面板 */
	private JPanel basePanel;
	
	/** 根面板 */
	protected final JPanel rootPanel;
	
	/** 系统托盘图标 */
	private TrayIcon trayIcon;
	
	/**
	 * 全屏初始化
	 * @param name 窗口名称
	 */
	protected _SwingWindow() {
		this("Window", 0, 0, true);
	}
	
	/**
	 * 全屏初始化
	 * @param name 窗口名称
	 */
	protected _SwingWindow(String name) {
		this(name, 0, 0, true);
	}
	
	/**
	 * 限定大小初始化
	 * @param name 窗口名称
	 * @param width 初始窗宽
	 * @param high 初始窗高
	 */
	protected _SwingWindow(String name, int width, int high) {
		this(name, width, high, false);
	}
	
	/**
	 * 限定大小初始化
	 * @param name 窗口名称
	 * @param width 初始窗宽(relative=true时, width<=0; relative=false时, width>0)
	 * @param high 初始窗高(relative=true时, high<=0; relative=false时, high>0)
	 * @param relative 相对尺寸（当此值为true时, width/high为相对全屏宽度的大小）
	 */
	protected _SwingWindow(String name, int width, int high, boolean relative) {
		this(name, width, high, relative, new Object[0]);
	}
	
	/**
	 * 全参初始化
	 * @param name 窗口名称
	 * @param width 初始窗宽(relative=true时, width<=0; relative=false时, width>0)
	 * @param high 初始窗高(relative=true时, high<=0; relative=false时, high>0)
	 * @param relative 相对尺寸（当此值为true时, width/high为相对全屏宽度的大小）
	 * @param args 从外部传入的其他参数
	 */
	protected _SwingWindow(String name, int width, int high, boolean relative, Object... args) {
		super(name);
		this.name = name;
		if(relative == true) {
			this.width = WIN_WIDTH - (width > 0 ? width : -width);
			this.high = WIN_HIGH - (high > 0 ? high : -high);
		} else {
			this.width = (width <= 0 ? -width : width);
			this.high = (high <= 0 ? -high : high);
		}
		
		// 初始化界面
		this.miniMode = UNINIT;
		this.setSize(this.width, this.high);
		_setLocation();	// 设置窗口位置
		setAlwaysOnTop(WIN_ON_TOP());	// 设置窗口置顶
		this.basePanel = new JPanel(new GridLayout(1, 1));
		this.rootPanel = new JPanel(new BorderLayout());
		this.setContentPane(basePanel);
		basePanel.add(rootPanel, 0);
		
		initComponents(args);				// 初始化组件
		setComponentsLayout(rootPanel);		// 设置组件布局
		setComponentsListener(rootPanel);	// 设置组件监听器
		initSystemTrayEvent();				// 初始化系统托盘事件
		initCloseWindowMode();				// 初始化窗口事件
	}
	
	/**
	 * 是否主窗口, 影响关闭窗口模式.
	 *  是: 使用主窗口模式(点击右上角x会关闭所有进程)
	 *  是: 使用子窗口模式(点击右上角x会隐藏当前窗口)
	 * @return 
	 */
	protected abstract boolean isMainWindow();
	
	/**
	 * 窗口出现位置
	 * @return LOCATION_CENTER: 屏幕中心(默认)
	 * 		   LOCATION_LU: 屏幕左上
	 * 		   LOCATION_LB: 屏幕左下
	 * 		   LOCATION_RU: 屏幕右上
	 * 		   LOCATION_RB: 屏幕右下
	 */
	protected int LOCATION() {
		return LOCATION_CENTER;
	}
	
	/**
	 * 设置窗口位置
	 */
	private void _setLocation() {
		final int TASK_HEIGHT = 30;	// 底部任务栏高度(避免遮挡)
		
		switch(LOCATION()) {
			case LOCATION_LU: {	// 左上
				setLocation(0, 0);
				break;
			}
			case LOCATION_LB: {	// 左下
				setLocation(0, (WIN_HIGH - this.high - TASK_HEIGHT));
				break;
			}
			case LOCATION_RU: {	// 右上
				setLocation((WIN_WIDTH - this.width), 0);
				break;
			}
			case LOCATION_RB: {	// 右下
				setLocation((WIN_WIDTH - this.width), 
						(WIN_HIGH - this.high - TASK_HEIGHT));
				break;
			}
			default: {
				setLocation((WIN_WIDTH / 2 - this.width / 2), 
						(WIN_HIGH / 2 - this.high / 2));
			}
		}
	}
	
	/**
	 * 初始化窗口事件
	 */
	protected void initCloseWindowMode() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //避免一点击x就关闭
		
		// 主窗口模式
		if(isMainWindow()) {
			_view();	// 默认显示窗口
			this.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					toExit();
				}
				
				@Override
				public void windowIconified(WindowEvent e) {
					// 在执行下面代码之前, 窗口已最小化
					
					toMini();
				}
			});
			
		// 子窗口模式(需要主动调用view显示窗口)
		} else {
			this.addWindowListener(new WindowAdapter() {
				
				@Override
				public void windowClosing(WindowEvent e) {
					_hide();
				}
			});
		}
	}
	
	/**
	 * 初始化系统托盘事件
	 */
	private void initSystemTrayEvent() {
		ImageIcon trayImg = new ImageIcon(_SwingWindow.class.
				getResource("/exp/libs/warp/ui/cpt/win/tray.png"));	// 托盘图标
		PopupMenu trayPop = new PopupMenu();		// 托盘右键菜单
		MenuItem resume = new MenuItem("Resume");
		MenuItem exit = new MenuItem("Exit");
		trayPop.add(resume);
		trayPop.add(exit);
		
		this.trayIcon = new TrayIcon(trayImg.getImage(), name, trayPop);
		trayIcon.setImageAutoSize(true);
		
		trayIcon.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) { 
				if(e.getClickCount() == 2) {	// 鼠标双击
					toResume();
				}
			}
		});
		
		resume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toResume();
			}
		});
		
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toExit();
			}
		});
		
	}
	
	/**
	 * 退出程序
	 */
	private void toExit() {
		if(SwingUtils.confirm("Exit (退出) ?")) {
			_hide();
			beforeExit();
			System.exit(0);
		}
	}
	
	/**
	 * 最小化窗口
	 */
	private void toMini() {
		
		// 不重复询问
		if(miniMode == UNINIT) {
			miniMode = SwingUtils.confirm("请选择最小化模式 : " , "到系统托盘", "到任务栏") ? 
					TO_TRAY : TO_MINI;
		}
		
		// 最小化到系统托盘
		if(miniMode == TO_TRAY) {
			_hide();
			try {
				SYSTEM_TRAY.add(trayIcon);	// 添加到系统托盘
			} catch (AWTException ex) {
				miniMode = TO_MINI;		// 添加到系统托盘失败, 之后就不再最小化到托盘了
			}
		} else {
			// Undo: 默认模式: 最小化到系统任务栏
		}
	}
	
	/**
	 * 从托盘恢复窗口
	 */
	private void toResume() {
		SYSTEM_TRAY.remove(trayIcon); 		// 移去托盘图标
		_view();
		setExtendedState(JFrame.NORMAL);	// 还原窗口
		toFront();	// 窗口在最前
	}
	
	/**
	 * 显示窗口
	 */
	public final void _view() {
		setVisible(true);
	}
	
	/**
	 * 隐藏窗口
	 */
	public final void _hide() {
		dispose();
	}

	/**
	 * 窗口置顶
	 */
	protected boolean WIN_ON_TOP() {
		return false;
	}
	
	/**
	 * 提供给子类个性化组件的初始化方法.
	 *  (由于父类构造函数先于子类构造函数执行, 
	 *  而initComponentsLayout 与 setComponentsListener 由父类构造函数调用,
	 *  因此子类不能够在自身构造函数中初始化组件, 然后在上述两个方法中使用, 否则会报NPE异常.)
	 *  @param args 从外部传入的其他参数
	 */
	protected abstract void initComponents(final Object... args);
	
	/**
	 * 初始化组件布局
	 * @param rootPanel 根面板（已设定布局样式为BorderLayout）
	 */
	protected abstract void setComponentsLayout(final JPanel rootPanel);

	/**
	 * 初始化组件监听器
	 * @param rootPanel 根面板（已设定布局样式为BorderLayout）
	 */
	protected abstract void setComponentsListener(final JPanel rootPanel);

	
	/**
	 * 关闭界面前行为(仅主窗口模式会触发)
	 */
	protected abstract void beforeExit();
	
	
}
