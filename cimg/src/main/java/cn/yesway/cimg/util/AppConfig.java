package cn.yesway.cimg.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 配置设置
 * 
 * @author
 * 
 */
public class AppConfig {
	private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
	private static Map<String, String> propertyCache;

	public static String getParameter(String name) {
		if (propertyCache != null && propertyCache.containsKey(name)) {
			return (String) propertyCache.get(name);
		}
		return null;
	}
	
	public static Map<String, String> getPropertyCache() {
		return propertyCache;
	}
	public static boolean loadSetupProperties(String filename) {
		boolean result = true;
		if (filename != null && filename.trim().length() > 0) {
			if (propertyCache != null) {
				propertyCache.clear();
			} else {
				propertyCache = new HashMap<String, String>();
			}
			try {
				SAXReader saxReader = new SAXReader();
				org.dom4j.Document doc = saxReader.read(AppConfig.class.getClassLoader().getResourceAsStream(filename));
				Iterator<Element> elements = doc.getRootElement().elementIterator();
				while (elements.hasNext()) {
					Element subelement = (Element) elements.next();
					String subelementName = subelement.getName();
					if ("param".equals(subelementName)) {
						Attribute key = subelement.attribute("key");
						propertyCache.put(key.getValue(), subelement.getText());
					}
				}
			} catch (Exception e) {
				log.error("configure error:", e);
				result = false;
			}
		} else {
			log.error("configure error: appConfigpath is null");
			result = false;
		}
		return result;
	}

	
}
