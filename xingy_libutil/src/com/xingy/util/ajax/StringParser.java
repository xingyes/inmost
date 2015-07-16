package com.xingy.util.ajax;

public class StringParser extends Parser<byte[], String>{

	@Override
	public String parse(byte[] bytes, String charset) throws Exception{
		
		String str =  new String(bytes, 0, bytes.length, charset );
		
		return str;
		
	}
}