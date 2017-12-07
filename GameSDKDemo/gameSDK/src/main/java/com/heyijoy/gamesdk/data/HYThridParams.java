package com.heyijoy.gamesdk.data;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class HYThridParams {

	private Map<String, String> configs;

	public HYThridParams() {
		configs = new HashMap<String, String>();
	}

	public HYThridParams(Map<String, String> configs) {
		this();

		if (configs != null) {
			for (String key : configs.keySet()) {
//				Log.e("HeyiJoySDK", "key=" + key + "&value=" + configs.get(key));
				put(key, configs.get(key));
			}
		}
	}

	public boolean contains(String key) {

		return configs.containsKey(key);
	}

	public void put(String key, String value) {
		configs.put(key, value);
	}

	public String getString(String key) {
		if (configs.containsKey(key)) {
			return configs.get(key);
		}
		return null;
	}

	public int getInt(String key) {
		String val = getString(key);

		return val == null ? null : Integer.parseInt(val);
	}

	public Float getFloat(String key) {
		String val = getString(key);
		return val == null ? null : Float.parseFloat(val);
	}

	public Double getDouble(String key) {
		String val = getString(key);
		return val == null ? null : Double.parseDouble(val);
	}

	public Long getLong(String key) {
		String val = getString(key);
		return val == null ? null : Long.parseLong(val);
	}

	public Boolean getBoolean(String key) {
		String val = getString(key);
		return val == null ? null : Boolean.parseBoolean(val);
	}

	public Short getShort(String key) {
		String val = getString(key);
		return val == null ? null : Short.parseShort(val);
	}

	public Byte getByte(String key) {
		String val = getString(key);
		return val == null ? null : Byte.parseByte(val);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key : configs.keySet()) {
			sb.append(key).append("=").append(configs.get(key)).append("\n");

		}
		return sb.toString();
	}
}
