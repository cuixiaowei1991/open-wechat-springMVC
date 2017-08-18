package com.cn.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 工具类
 * @author songzhili
 * 2016年6月29日下午1:19:19
 */
public class CommonHelper {
	public static Random random = new Random();
   
	public static boolean isEmpty(String source){
		if(source == null || source.length() == 0 || "null".equals(source)){
			return true;
		}
		return false;
	}
	
	public static boolean isNullOrEmpty(Object source){
		
		if(source == null || source.toString().trim().length() == 0
				|| "null".equals(source)){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int obtainSpaceDays(String startTime, String endTime) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Long countOne = 0l;
		try {
			Date dateOne = format.parse(startTime);
			Date dateTwo = format.parse(endTime);
			long timeOne = dateOne.getTime();
			long timeTwo = dateTwo.getTime();
			long count = (timeTwo - timeOne) / (24 * 3600 * 1000);
			countOne = Math.abs(count);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return countOne.intValue();
	}

	/**
	 * 获取现在时间
	 * @return返回长时间格式 yyyy-MM-dd HH:mm:ss
	 */

	public static Date getNowDateShort() {
		Date currentTime = new Date();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		Date dateString1 = null;
		try {
			dateString1 = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateString1;
	}

	/**
	 * 将时间转为指定的格式
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatTime(Object date,String dateFormat)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String date_e = null;
		try {
			date_e = sdf.format(date);
			return date_e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 返回当前日期的23时59分59秒的日期格式
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 返回当前日期的0时0分0秒的日期格式
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * String转Date
	 * @param dateString 传入的时间类型字符串
	 * @param dateFormat 需要转义的时间类型格式 例如：yyyy-MM-dd
	 * @return
	 */
	public static Date getStringToDate(String dateString,String dateFormat)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static boolean isNumeric(String str) {
		if (isEmpty(str)) {
			return false;
		}
		for (int i = str.length(); --i >= 0; ) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57) {
				return false;
			}
		}
		return true;
	}
	

	/**
	 * 获取指定位数的随机数(纯数字)
	 * @param length 随机数的位数
	 * @return String
	 */
	public static String getRandomNum(int length) {
		if (length <= 0) {
			length = 1;
		}
		StringBuilder res = new StringBuilder();
		Random random = new Random();
		int i = 0;
		while (i < length) {
			res.append(random.nextInt(10));
			i++;
		}
		return res.toString();
	}

	/**
	 * getRStr 取得指定位数的随机字符串
	 *
	 * @param myString
	 *            String[] 指定字符串组
	 * @param length
	 *            int 生成随机数的位数
	 * @return String 返回随机字符串
	 */
	public static String getRStr(String[] myString, int length) {
		StringBuffer mystrbuf = new StringBuffer();
		for (int i = 0; i < length; i = i + 1) {
			int r = random.nextInt(myString.length);
			mystrbuf.append(myString[r]);
		}
		return mystrbuf.toString();
	}


	/**
	 * 自动生成Allpay分配给代理商接入key1
	 */
	@Transactional(rollbackForClassName = "Exception")
	public static String createkey() {
		String[] myString=new String[]{"a","b","c","d","e","f","g","h","i","j","k",
				"l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
				"0","1","2","3","4","5","6","7","8","9"};
		return getRStr(myString, 32);

	}

	/**
	 * 将空对象转化成空字符串""
	 * @param object
	 * @return
	 */
	public static String nullToString(Object object){
		String result=object==null?"":object.toString();
		return result;
	}

	public static String nullOrEmptyToString(Object obj){

		if(!CommonHelper.isNullOrEmpty(obj)){
			return obj.toString();
		}
		return "";
	}

	public static String map2json(Map<String,Object> source)

	{
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(source);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}



		/**
         *
         * @param request
         * @return
         * @throws IOException
         */
	public static String obtainRequestBody(HttpServletRequest request) throws IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		StringWriter writer = new StringWriter();
		char[] chars = new char[256];
		int count = 0;
		while ((count = reader.read(chars)) > 0) {
			writer.write(chars, 0, count);
		}
		String toRe = writer.toString();
		writer.flush();
		writer.close();
		return toRe;
	}
}
