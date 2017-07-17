package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {
	private Map<String,Object> viewMap = new HashMap<>();
	public void set(String key,Object value)
	{
		viewMap.put(key, value);
	}
	public Object get(String key)
	{
		return viewMap.get(key);
	}
}
