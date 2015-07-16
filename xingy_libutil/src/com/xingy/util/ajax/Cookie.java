package com.xingy.util.ajax;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Cookie {
	
	private final HashMap<String, String> data;
	
	public Cookie(){
		data = new HashMap<String, String>();
	}
	
	public void set(String name, String value){
		data.put(name, value);
	}
	
	public Set<Entry<String, String>> getAll(){
		return data.entrySet();
	}
	
	
	public String get(String name){
		return data.get(name);
	}
	
	public void remove(String name){
		data.remove(name);
	}
	
	public void clear(){
		data.clear();
	}
	
	public String toString(){
		return data.toString();
	}
}
