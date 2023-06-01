package com.yhskgo.blogpress.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlogpressUtil {
	
	private static Logger logger = LoggerFactory.getLogger(BlogpressUtil.class);
	
	private static String elasticDateFormat = "MM-dd-yyyy 'T'HH:mm:ss";
	private static String displayDateFormat = "MMMM dd yyyy h:mm:ss a";
	private static final String ALPHA_NUMERIC_STRING = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
	private static int randomNoLength = 10;
	
	/**
	 * This method will format the given date in given date format
	 * @param inputDate
	 * @param dtFormat
	 * @return
	 */
	private static String getFormattedDate(Date inputDate, DateFormat dtFormat) {
		if(inputDate != null) {
			return dtFormat.format(inputDate);
		} else {
			return "";
		}
	}
	
	/**
	 * This method will return DateFormat object from date pattern.
	 * @param dateFormatPattern
	 * @return
	 */
	public static DateFormat getDateFormatObj(String dateFormatPattern) {
		DateFormat dtFormat = new SimpleDateFormat(dateFormatPattern);
		return dtFormat;
	}
	
	/**
	 * This method will return formatted Date for elastic search
	 * @param inputDate
	 * @return
	 */
	public static String getFormattedDateForElasticSearch(Date inputDate) {
		return getFormattedDate(inputDate, getDateFormatObj(elasticDateFormat));
	}
	
	/**
	 * This method will return formatted Date for display on web page
	 * @param inputDate
	 * @return
	 */
	public static String getFormattedDateForDisplayOnPage(Date inputDate) {
		return getFormattedDate(inputDate, getDateFormatObj(displayDateFormat));
	}
	
	/**
	 * generate random number with long value of date object
	 */
	public static String RandomNumber(Date currentDate) {
		int count = randomNoLength;
		StringBuilder builder = new StringBuilder();
		while (count-- !=0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		Date date = currentDate;
		if(date!=null) {
			currentDate = new Date();
		}
		return builder.append(currentDate.getTime()).toString();
	}
	
	public static int parseInteger(String intStr) {
		int returnValue =0;
		try {
			returnValue = Integer.parseInt(intStr);
		} catch (NumberFormatException e) {
			// TODO: handle exception
			logger.error("error occurred while parsing integer "+intStr, e.getMessage(), e);
			
		}
		return returnValue;
	}
	

}
