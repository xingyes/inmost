package com.xingy.util;
import java.io.UnsupportedEncodingException;

public class StringUtil {

	// private static Log logger = LogFactory.getLog(StringUtil.class);

	// 国标码和区位码转换常量
	private static int GB_SP_DIFF = 160;
	// 存放国标一级汉字不同读音的起始区位码
	private static int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594,
			2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390,
			4558, 4684, 4925, 5249, 5600 };

	// 存放国标一级汉字不同读音的起始区位码对应读音
	private static char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
			'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x', 'y', 'z' };

	public static char convert(String ch) {
		if(!isChinese(ch)) {
			char[] charArray = ch.toCharArray();
			if(charArray.length > 0) {
				
				return charArray[0];
			} else {
				return 0;
			}
		}
		byte[] bytes = new byte[2];
		try {
			bytes = ch.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		char result = '-';
		int secPosValue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= GB_SP_DIFF;
		}
		secPosValue = bytes[0] * 100 + bytes[1];
		for (i = 0; i < 23; i++) {
			if (secPosValue >= secPosValueList[i]
					&& secPosValue < secPosValueList[i + 1]) {
				result = firstLetter[i];
				break;
			}
		}
		return result;
	}
	
	private static boolean isChinese(char c) {
	    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
	        return true;
	    }
	    return false;
	}
	
	public static boolean isChinese(String strName) {
	    char[] ch = strName.toCharArray();
	    for (int i = 0; i < ch.length; i++) {
	        char c = ch[i];
	        if (isChinese(c)) {
	            return true;
	        }
	    }
	    return false;
	}
}