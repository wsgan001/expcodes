package exp.bilibili.plugin.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.bilibili.plugin.bean.pdm.SendGift;
import exp.bilibili.plugin.core.back.MsgSender;
import exp.bilibili.plugin.utils.UIUtils;
import exp.libs.utils.num.NumUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.warp.thread.LoopThread;

/**
 * <PRE>
 * 在线聊天管理器:
 *  1.自动晚安
 *  2.自动感谢投喂
 *  3.定时打call
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class ChatMgr extends LoopThread {

	private final static Logger log = LoggerFactory.getLogger(ChatMgr.class);
	
	private final static String NOTICE_KEY = "【公告】";
	
	private final static String NIGHT_KEY = "晚安(´▽`)ﾉ  ";
	
	/** 同一时间可以感谢的最大用户数（避免刷屏） */
	private final static int THX_USER_LIMIT = 2;
	
	/** 发送消息间隔 */
	private final static long SEND_TIME = 500;
	
	/** 自动感谢周期 */
	private final static long THX_TIME = 30000;
	
	/** 滚屏公告周期 */
	private final static long NOTICE_TIME = 120000;
	
	/** 自动打call周期 */
	private final static long CALL_TIME = 180000;
	
	/** 检测待发送消息间隔 */
	private final static long SLEEP_TIME = 1000;
	
	private final static int THX_LIMIT = (int) (THX_TIME / SLEEP_TIME);
	
	private final static int NOTICE_LIMIT = (int) (NOTICE_TIME / SLEEP_TIME);
	
	private final static int CALL_LIMIT = (int) (CALL_TIME / SLEEP_TIME);
	
	private int thxCnt;
	
	private int noticeCnt;
	
	private int callCnt;
	
	/** 自动答谢 */
	private boolean autoThankYou;
	
	/** 自动公告 */
	private boolean autoNotice;
	
	/** 自动打call */
	private boolean autoCall;
	
	/** 自动晚安 */
	private boolean autoGoodNight;
	
	/** 已经被晚安过的用户 */
	private Set<String> nightedUsers;
	
	/**
	 * 一段时间内，每个用户赠送的礼物清单.
	 *  username -> giftName -> giftName
	 */
	private Map<String, Map<String, Integer>> userGifts;
	
	private static volatile ChatMgr instance;
	
	private ChatMgr() {
		super("自动发言姬");
		this.thxCnt = 0;
		this.noticeCnt = 0;
		this.callCnt = 0;
		this.autoThankYou = false;
		this.autoNotice = false;
		this.autoCall = false;
		this.autoGoodNight = false;
		this.nightedUsers = new HashSet<String>();
		this.userGifts = new LinkedHashMap<String, Map<String, Integer>>();
	}
	
	public static ChatMgr getInstn() {
		if(instance == null) {
			synchronized (ChatMgr.class) {
				if(instance == null) {
					instance = new ChatMgr();
				}
			}
		}
		return instance;
	}
	
	private void clear() {
		nightedUsers.clear();
		userGifts.clear();
	}
	
	public boolean sendNotice(String msg) {
		boolean isOk = false;
		if(isAutoThankYou()) {
			isOk = MsgSender.sendChat(StrUtils.concat(NOTICE_KEY, msg), 
					UIUtils.getCurChatColor());
		}
		return isOk;
	}
	
	public void helloLive(String roomId) {
		long hour = ((System.currentTimeMillis() % 86400000) / 3600000);
		hour = (hour + 8) % 24;	// 时差
		
		String card = RandomUtils.randomElement(MsgKwMgr.getCards());
		String msg = "滴~".concat(card);
		if(hour >= 6 && hour < 12) {
			msg = msg.concat("早上好");
			
		} else if(hour >= 12 && hour < 18) {
			msg = msg.concat("下午好");
			
		} else if(hour >= 18 && hour < 24) {
			msg = msg.concat("晚上好");
			
		} else {
			msg = msg.concat("还在浪吗?");
		}
		MsgSender.sendChat(msg, roomId);
	}
	
	public void addThxGift(SendGift msgBean) {
		if(!isAutoThankYou()) {
			return;
		}
		
		int num = NumUtils.toInt(msgBean.getNum(), 0);
		if(num <= 0) {
			return;
		}
		
		String username = msgBean.getUname();
		String giftName = msgBean.getGiftName();
		
		synchronized (userGifts) {
			Map<String, Integer> gifts = userGifts.get(username);
			if(gifts == null) {
				gifts = new HashMap<String, Integer>();
				userGifts.put(username, gifts);
			}
			
			Integer sum = gifts.get(giftName);
			sum = (sum == null ? 0 : sum);
			gifts.put(giftName, (sum + num));
		}
	}
	
	public void addThxGuard(String msg) {
		if(!isAutoThankYou()) {
			return;
		}
		
		MsgSender.sendChat(StrUtils.concat(NOTICE_KEY, "感谢 ", msg), 
				UIUtils.getCurChatColor());
	}

	public void addNight(String username, String msg) {
		if(!isAutoGoodNight() || 
				msg.startsWith(NIGHT_KEY) ||		// 避免跟机器人对话
				nightedUsers.contains(username)) { 	// 避免重复晚安
			return;
		}
		
		if(MsgKwMgr.containsNight(msg)) {
			String chatMsg = StrUtils.concat(NIGHT_KEY, ", ", username);
			MsgSender.sendChat(chatMsg, UIUtils.getCurChatColor());
			nightedUsers.add(username);
		}
	}
	
	public void setAutoThankYou() {
		autoThankYou = !autoThankYou;
		userGifts.clear();	// 切换状态时, 清空已投喂的礼物列表
	}
	
	public boolean isAutoThankYou() {
		return autoThankYou;
	}
	
	public void setAutoNotice() {
		autoNotice = !autoNotice;
	}
	
	public boolean isAutoNotice() {
		return autoNotice;
	}
	
	public void setAutoCall() {
		autoCall = !autoCall;
	}
	
	public boolean isAutoCall() {
		return autoCall;
	}
	
	public void setAutoGoodNight() {
		autoGoodNight = !autoGoodNight;
		nightedUsers.clear();	// 切换状态时, 清空已晚安的用户列表
	}
	
	public boolean isAutoGoodNight() {
		return autoGoodNight;
	}

	@Override
	protected void _before() {
		log.info("{} 已启动", getName());
	}

	@Override
	protected void _loopRun() {
		
		// 定时感谢礼物投喂
		if(thxCnt++ >= THX_LIMIT) {
			thxCnt = 0;
			toThxGift();
		}
		
		// 定时公告
		if(noticeCnt++ >= NOTICE_LIMIT) {
			noticeCnt = 0;
			toNotice();
		}
		
		// 定时打call（支持主播公告）
		if(callCnt++ >= CALL_LIMIT) {
			callCnt = 0;
			toCall();
		}
		
		_sleep(SLEEP_TIME);
	}

	@Override
	protected void _after() {
		clear();
		log.info("{} 已停止", getName());
	}
	
	/**
	 * 感谢一段时间内所有用户的投喂
	 */
	private void toThxGift() {
		Map<String, Map<String, Integer>> tmp = 
				new LinkedHashMap<String, Map<String,Integer>>();
		synchronized (userGifts) {
			tmp.putAll(userGifts);
			userGifts.clear();
		}
		
		// 若短时间内投喂用户过多, 则不逐一感谢, 避免刷屏
		int userNum = tmp.keySet().size();
		if(userNum >= THX_USER_LIMIT) {
			String msg = StrUtils.concat(NOTICE_KEY, "感谢前面[", userNum, 
					"]个大佬的投喂d(´ω｀*)");
			MsgSender.sendChat(msg, UIUtils.getCurChatColor());
			
		// 分别合并每个用户的投喂礼物再感谢
		} else {
			Iterator<String> userIts = tmp.keySet().iterator();
			while(userIts.hasNext()) {
				String username = userIts.next();
				Map<String, Integer> gifts = tmp.get(username);
				
				toThxGift(username, gifts);
				_sleep(SEND_TIME);
				
				userIts.remove();
			}
			tmp.clear();
		}
	}
	
	/**
	 * 感谢某个用户的投喂
	 * @param username
	 * @param gifts
	 */
	private void toThxGift(String username, Map<String, Integer> gifts) {
		if(gifts.size() <= 0) {
			return;
			
		// 1个礼物多份
		} else if(gifts.size() == 1) {
			Iterator<String> giftIts = gifts.keySet().iterator();
			if(giftIts.hasNext()) {
				String giftName = giftIts.next();
				Integer num = gifts.get(giftName);
				if(num != null && num > 0) {
					String msg = StrUtils.concat(NOTICE_KEY, "感谢[", username, "]", 
							MsgKwMgr.getAdj(), "投喂", num, "个[", giftName, "]");
					MsgSender.sendChat(msg, UIUtils.getCurChatColor());
				}
			}
			
		// 多个礼物多份
		} else {
			StringBuilder sb = new StringBuilder();
			Iterator<String> giftIts = gifts.keySet().iterator();
			while(giftIts.hasNext()) {
				String giftName = giftIts.next();
				sb.append(giftName).append(",");
			}
			sb.setLength(sb.length() - 1);
			
			String msg = StrUtils.concat(NOTICE_KEY, "感谢[", username, "]", 
					MsgKwMgr.getAdj(), "投喂[", sb.toString(), "]");
			MsgSender.sendChat(msg, UIUtils.getCurChatColor());
		}
		
		gifts.clear();
	}
	
	/**
	 * 定时公告
	 */
	private void toNotice() {
		if(!isAutoNotice() || ListUtils.isEmpty(MsgKwMgr.getNotices())) {
			return;
		}
		
		String msg = NOTICE_KEY.concat(RandomUtils.randomElement(MsgKwMgr.getNotices()));
		MsgSender.sendChat(msg, UIUtils.getCurChatColor());
	}
	
	/**
	 * 定时打call
	 */
	private void toCall() {
		if(!isAutoCall() || ListUtils.isEmpty(MsgKwMgr.getCalls())) {
			return;
		}
		
		String msg = NOTICE_KEY.concat(RandomUtils.randomElement(MsgKwMgr.getCalls()));
		MsgSender.sendChat(msg, UIUtils.getCurChatColor());
	}
	
}
