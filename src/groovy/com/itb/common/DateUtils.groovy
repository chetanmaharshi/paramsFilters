package com.itb.common

import java.text.DateFormat

/**
 * Author: Sunny Thandassery
 * Date: Dec 10, 2009
 * Copyright (C) 2009 BluSynergy LLC. All rights reserved.
 */

/**
 * A utility class to blank out the time portion of a Date
 * (As found on something called the Internet)
 */
public class DateUtils {

    public static months = ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"]

    // FYI - Same quirks in GDK as JDK wrt Date:
    // Date.month is 0 based (i.e., month = 1 => Feb). Also Date.year + 1900 is actual year

	/**
	 * Parses a date from a string without throwing any exceptions.
     * If the day of the month is not provided, then it is set to the last day of the month 
	 * @param val The string value of the date in any one of these formats: MM/dd/yyyy, yyyy-MM-dd, M/yy
	 * @return The date or null in case of empty/invalid input
	 */
	public static Date parseDate(String val) {
		Date dt = null
		try {
            val?.trim()
            
			if(val && val.count('-')==2)
                dt = Date.parse("yy-M-d", val)
			if(val && val.count('/')==2)
                dt = Date.parse("M/d/yy", val)
			else if(val && val.count('/')==1) { // eg. expiry date
                dt = Date.parse("M/yy", val)
                if(dt)
                    dt = setLastDayOfMonth(dt)
            }
            else {
                dt = new Date(val)
            }
		}
		catch(e) {}
		return dt
	}

    /**
     * Gets the current date with the time portion zeroed out
     */
    public static Date getToday() {
        return setMidnight(new Date())
    }

    /**
     * Gets the date for tomorrow with the time portion zeroed out
     */
    public static Date getTomorrow() {
        return (getToday() + 1) as Date
    }

    /**
     * Blanks out the time portion and returns a new Date object. The @theDate parameter is unaffected
     */
    public static Date setMidnight(Date theDate) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(theDate)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }
  public static Date setHour_23Minute_59(Date theDate) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(theDate)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }
 

    /**
     * Blanks out the time portion, changes to the last day of the month and returns a new Date object.
     * The @theDate parameter is unaffected
     */
    public static Date setLastDayOfMonth(Date theDate) {
        if(!theDate)
            return null

        GregorianCalendar ret = new GregorianCalendar()
        ret.time = theDate
        ret.set(Calendar.HOUR_OF_DAY, 0)
        ret.set(Calendar.MINUTE, 0)
        ret.set(Calendar.SECOND, 0)
        ret.set(Calendar.MILLISECOND, 0)
        ret.set(Calendar.DAY_OF_MONTH, ret.getActualMaximum(Calendar.DAY_OF_MONTH))

/*
        Date ret = new Date(theDate.getTime())
        int month = ret.month
        while(ret.month == month)
            ret.date++
        ret.date--
*/
        return ret.getTime()
    }


    public static Calendar setLastDayOfMonth(Calendar calendar) {
        if(calendar) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        return calendar
    }

    /**
     * Returns a Date object initialized to the 1st day of the following month (with time blanked out)
     */
    public static Date getFirstOfFollowingMonth() {
        Date dt = DateUtils.today
        dt.date = 1
        dt.month++ // confirmed that this will roll the year if reqd.
        return dt
    }

    /**
     * Returns a Date object initialized to the 1st day of the current month (with time blanked out)
     */
    public static Date getFirstOfThisMonth() {
        Date dt = DateUtils.today
        dt.date = 1
        return dt
    }

    /**
     * Returns a Date object initialized to the 1st day of the following month (with time blanked out)
     */
    public static Date getLastOfFollowingMonth() {
        Calendar cal = Calendar.getInstance()
        Date dt = DateUtils.today
        dt.month++
        cal.setTime(dt)
        //Set the last day of month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return cal.getTime()
    }

    /**
     * Returns DateFormat instance for specified Organization.
     */
    public static DateFormat getDateFormatter(Locale locale) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale)
        if(!df)
            df = DateFormat.getDateInstance(DateFormat.SHORT)
        return df
    }

    /**
     * Return a Calendar instance which point on last day of current month.
     */
    public static Calendar getLastDayOfCurrentMonth() {
        Calendar cal = Calendar.getInstance()
        cal.setTime(new Date())
        //Set the last day of current month to operate with hole month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return cal
    }

    public static Date getFirstOfPreviousMonth() {
        Date dt = DateUtils.today
        dt.date = 1
        dt.month--
        return setHour_23Minute_59(dt)
    }

	public static Date getFirstOfNextMonth() {
		Date dt = DateUtils.today
		dt.date = 1
		dt.month++
		return setMidnight(dt)
	}

    public static Date getLastOfPreviousMonth() {
        Calendar cal=Calendar.getInstance();
        Date dt = DateUtils.today
        dt.month--
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        dt.date = cal.get(Calendar.DATE);
        return setHour_23Minute_59(dt)
    }
    public static Date getFirstof12MonthsAgo(){
        Date date=DateUtils.getToday()-365
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        return new Date((year-1900), month, day,0,0,0)
    }

    public static Date getFirstOfFirstMonth(){
        Date dt = DateUtils.today
		dt.date = 1
		dt.month = 0
		return setMidnight(dt)
    }
             /*  will get first date before 12 month From according to current month last date  */
    public static Calendar getFirstDay12MonthsAgo(){
        Date date=DateUtils.getToday()-365
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        cal.add(Calendar.MONTH, +1)
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        return cal
    }

    public static List iterate12Month(Date date) {
		Date dt = DateUtils.today
		dt.date = 1
        def monthList = []
		monthList.add(dt.month++)
        return monthList

	}

    public static Calendar getFirstDay12MonthsAgoForDate(Date date){
        date=date-365
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        cal.add(Calendar.MONTH, +1)
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        return cal
    }

     public static Calendar getFirstDay6MonthsAgoForDate(Date date){
        date=date-182
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        cal.add(Calendar.MONTH, +1)
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        return cal
    }


    public static Calendar getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        //Set the last day of current month to operate with hole month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return cal
    }
    public static Calendar getFirstDayOfMonths(Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
//        cal.add(Calendar.MONTH, +1)
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        return cal
    }
    public static Date getLastOfPreviousMonthSetMidNight() {      // Like  31-Dec-2013 00:00:00
        Calendar cal=Calendar.getInstance();
        Date dt = DateUtils.today
        dt.month--
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        dt.date = cal.get(Calendar.DATE);
        return setMidnight(dt)
    }

    public static Date addMinutes(Date dt, Integer minutes) {
        dt.minutes += minutes
        return dt
    }
    public static Date addSeconds(Date dt, Integer seconds){
        dt.seconds += seconds
        return dt
    }

    public static String getMonthNameFromNumber(int month){
        if(month){
            return months[month]
        }
        return null
    }
}