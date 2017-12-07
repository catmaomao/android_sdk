package com.heyijoy.gamesdk.operatorpay;

import java.io.InputStream;
import java.util.Properties;

public class PropertyConfig {
	
	private static PropertyConfig config;
	
	public static PropertyConfig getInstance() {
		if (config == null) {
			config = new PropertyConfig();
		}
		return config;
	}
	
	private Properties properties = null;
	
	private PropertyConfig() {
		properties = loadFile();
	}
	private Properties loadFile() {
		InputStream in = PropertyConfig.class.getClassLoader().getResourceAsStream("system.properties");
		Properties prop = new Properties();
		try
		{
			prop.load(in);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return prop;
	}
	
	public String getString(String key) {
		return properties.getProperty(key);
	}
	/**
	 * 获取整型属性值
	 * @param key 属性对应的key
	 * @param defaultValue 缺省值。
	 * @return 返回对应属性key的值。如果找不到对应的属性，返回缺省值defaultValue.
	 */
	public int getIntProperty(String key, int defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				return defaultValue;
			}
		}
	}
	/**
	 * 信息重新加载.
	 */
	public void reload() {
		properties = loadFile();
	}
}
