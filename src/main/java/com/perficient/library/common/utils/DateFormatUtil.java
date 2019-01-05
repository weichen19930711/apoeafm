package com.perficient.library.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

public class DateFormatUtil {

    public static final String PATTEN = "yyyy-MM-dd";

    public String dateFormat4XLS(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        if (date != null) {
            return simpleDateFormat.format(date);
        }
        return "";
    }

    public static Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastDay(int displayMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, displayMonths);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static int getWorkingDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and dateEnd must not be null");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("endDate must greater then startDate");
        }

        int workDays = 0;
        Date increaseDate = startDate;
        Calendar increaseCalendar = Calendar.getInstance();
        while (increaseDate.compareTo(endDate) <= 0) {
            increaseCalendar.setTime(increaseDate);
            int dayOfWeek = increaseCalendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SUNDAY && dayOfWeek != Calendar.SATURDAY) {
                workDays++;
            }
            increaseDate = DateUtils.addDays(increaseDate, 1);
        }
        return workDays;
    }

    public static Date getYearFirst(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        Date currYearFirst = cal.getTime();
        return currYearFirst;
    }

    public static Date getYearLast(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = cal.getTime();
        return currYearLast;
    }

    public static int daysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        // truncate days to avoid two different days' interval is less than one day
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        endDate = DateUtils.truncate(endDate, Calendar.DATE);

        return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Set<String> getMonthsBetween(String startMonth, String endMonth, String monthFormat)
        throws ParseException {
        return getMonthsBetween(startMonth, endMonth, monthFormat, monthFormat);
    }

    public static Set<String> getMonthsBetween(String startMonth, String endMonth, String monthFormat,
        String resultMonthFormat) throws ParseException {
        Set<String> result = new TreeSet<String>();

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(DateUtils.parseDate(startMonth, monthFormat));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(DateUtils.parseDate(endMonth, monthFormat));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(DateFormatUtils.format(curr, resultMonthFormat));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    public static Set<String> getYearsBetween(Date startYear, Date endYear, String resultYearFormat)
        throws ParseException {
        Set<String> result = new TreeSet<String>();

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(startYear);
        max.setTime(endYear);

        Calendar curr = min;
        while (curr.before(max) || curr.equals(max)) {
            result.add(DateFormatUtils.format(curr, resultYearFormat));
            curr.add(Calendar.YEAR, 1);
        }

        return result;
    }

    public static Set<String> getMonthsBetween(Date startMonth, Date endMonth, String resultMonthFormat)
        throws ParseException {
        Set<String> result = new TreeSet<String>();

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(startMonth);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(endMonth);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            result.add(DateFormatUtils.format(curr, resultMonthFormat));
            curr.add(Calendar.MONTH, 1);
        }

        return result;
    }

    public static Set<String> getDaysBetween(Date startDay, Date endDay, String resultDayFormat) {
        Set<String> result = new TreeSet<String>();

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(startDay);
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DAY_OF_MONTH));

        max.setTime(endDay);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DAY_OF_MONTH));

        Calendar curr = min;
        while (curr.before(max) || curr.equals(max)) {
            result.add(DateFormatUtils.format(curr, resultDayFormat));
            curr.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }

}
