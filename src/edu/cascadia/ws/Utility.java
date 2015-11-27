package edu.cascadia.ws;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Utility {
	/**
	 * Null check Method
	 * 
	 * @param txt
	 * @return
	 */
	public static boolean isNotNull(String txt) {
		// System.out.println("Inside isNotNull");
		return txt != null && txt.trim().length() > 0 ? true : false;
	}

	/**
	 * Method to construct JSON
	 * 
	 * @param tag
	 * @param status
	 * @return
	 */
	public static String constructJSON(String tag, boolean status) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString();
	}

	/**
	 * Method to construct JSON with Error Msg
	 * 
	 * @param tag
	 * @param status
	 * @param err_msg
	 * @return
	 */
	public static String constructJSON(String tag, boolean status,String err_msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tag", tag);
			obj.put("status", new Boolean(status));
			obj.put("error_msg", err_msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		return obj.toString(); 
	}
	
	public static String escapeCharString(String text) {
		// only escape char single quote
		if (text.contains("\'")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (ch == '\'')  {
					sb.append("\\" + ch);
				} else {
					sb.append(ch);
				}
			}
			
			return sb.toString();
		}
		
		return text;
	}
}
