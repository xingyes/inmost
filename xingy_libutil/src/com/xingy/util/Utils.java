package com.xingy.util;

import java.util.List;

public class Utils {
	public static Object getObjectSafely(List<?> list, int pos) {
		if(list == null) {
			return null;
		}
		
		if(pos >= 0 && pos < list.size()) {
			return list.get(pos);
		}
		
		return null;
	}
}
