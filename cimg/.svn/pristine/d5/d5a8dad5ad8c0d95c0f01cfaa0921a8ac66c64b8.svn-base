package cn.yesway.cimg.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统监控类
 * 
 * @author 实现加载配置文件
 */
public class InitListener implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(InitListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		log.info("【管理系统  关闭】");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		log.info("【管理系统   启动】");
	}
}
