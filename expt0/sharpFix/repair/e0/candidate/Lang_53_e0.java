/**
 * This file is part of OCPsoft SocialPM: Agile Project Management Tools (SocialPM)
 *
 * Copyright (c)2011 Lincoln Baxter, III <lincoln@ocpsoft.com> (OCPsoft)
 * Copyright (c)2011 OCPsoft.com (http://ocpsoft.com)
 * 
 * If you are developing and distributing open source applications under
 * the GNU General Public License (GPL), then you are free to re-distribute SocialPM
 * under the terms of the GPL, as follows:
 *
 * SocialPM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SocialPM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SocialPM.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For individuals or entities who wish to use SocialPM privately, or
 * internally, the following terms do not apply:
 * 
 * For OEMs, ISVs, and VARs who wish to distribute SocialPM with their
 * products, or host their product online, OCPsoft provides flexible
 * OEM commercial licenses.
 * 
 * Optionally, Customers may choose a Commercial License. For additional
 * details, contact an OCPsoft representative (sales@ocpsoft.com)
 */
package com.ocpsoft.socialpm.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

public class Dates
{
   public static boolean isSameDay(final Date one, final Date two)
   {
      return DateUtils.isSameDay(one, two);
   }

   public static Date now()
   {
      return new Date(System.currentTimeMillis());
   }

   public static Date addDays(final Date date, final int amount)
   {
      return DateUtils.addDays(date, amount);
   }

   public static long calculateNumberOfDaysBetween(final Date one, final Date two)
   {
      long milisOne = one.getTime();
      long milisTwo = two.getTime();
      long diff = milisTwo - milisOne;

      // Calculate difference in days
      long diffDays = diff / (24 * 60 * 60 * 1000);

      return diffDays;
   }

   /**
    * Perform an inclusive date range comparison to a specific field precision
    * 
    * @param field see <i>java.util.Calendar</i> Millisecond, Second, Minute, Hour, Day, Week, etc...
    */
   public static boolean isInPrecisionRange(Date start, Date end, Date date, final int field)
   {
      start = DateUtils.truncate(start, field);
      end = DateUtils.truncate(end, field);
      date = DateUtils.truncate(date, field);
      if ((date.compareTo(start) >= 0) && (date.compareTo(end) <= 0))
      {
         return true;
      }
      return false;
   }

   public static boolean isInRange(final Date start, final Date end, final Date date)
   {
      if ((date.compareTo(start) >= 0) && (date.compareTo(end) <= 0))
      {
         return true;
      }
      return false;
   }

   public static boolean isDateInPast(final Date pastDate)
   {
      Date today = new Date();
      return !isSameDay(today, pastDate) && (today.compareTo(pastDate) > 0);
   }

   public static boolean isDateInFuture(final Date futureDate)
   {
      Date today = new Date();
      return !isSameDay(today, futureDate) && (today.compareTo(futureDate) < 0);
   }

   public static boolean anyInRange(final Date start, final Date end, final Date... dates)
   {
      for (Date d : dates)
      {
         if (isInRange(start, end, d))
         {
            return true;
         }
      }
      return false;
   }

   public static boolean anyInRange(final Date start, final Date end, final List<Date> dates)
   {
      for (Date d : dates)
      {
         if (isInRange(start, end, d))
         {
            return true;
         }
      }
      return false;
   }

   public static boolean allInRange(final Date start, final Date end, final Date... dates)
   {
      for (Date d : dates)
      {
         if (!isInRange(start, end, d))
         {
            return false;
         }
      }
      return true;
   }

   /**
    * <p>
    * A suite of utilities surrounding the use of the {@link java.util.Calendar} and {@link java.util.Date} object.
    * </p>
    * 
    * <p>
    * DateUtils contains a lot of common methods considering manipulations of Dates or Calendars. Some methods require
    * some extra explanation. The truncate and round methods could be considered the Math.floor(), Math.ceil() or
    * Math.round versions for dates This way date-fields will be ignored in bottom-up order. As a complement to these
    * methods we've introduced some fragment-methods. With these methods the Date-fields will be ignored in top-down
    * order. Since a date without a year is not a valid date, you have to decide in what kind of date-field you want
    * your result, for instance milliseconds or days.
    * </p>
    * 
    * 
    * 
    * @author <a href="mailto:sergek@lokitech.com">Serge Knystautas</a>
    * @author Stephen Colebourne
    * @author Janek Bogucki
    * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
    * @author Phil Steitz
    * @author Robert Scholte
    * @since 2.0
    * @version $Id: DateUtils.java 634096 2008-03-06 00:58:11Z niallp $
    */
   @SuppressWarnings({ "unused", "rawtypes" })
   private static class DateUtils
   {
      /**
       * The UTC time zone (often referred to as GMT).
       */
      public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("GMT");
      /**
       * Number of milliseconds in a standard second.
       * 
       * @since 2.1
       */
      public static final long MILLIS_PER_SECOND = 1000;
      /**
       * Number of milliseconds in a standard minute.
       * 
       * @since 2.1
       */
      public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
      /**
       * Number of milliseconds in a standard hour.
       * 
       * @since 2.1
       */
      public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
      /**
       * Number of milliseconds in a standard day.
       * 
       * @since 2.1
       */
      public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

      /**
       * This is half a month, so this represents whether a date is in the top or bottom half of the month.
       */
      public final static int SEMI_MONTH = 1001;

      private static final int[][] fields = {
         { Calendar.MILLISECOND },
         { Calendar.SECOND },
         { Calendar.MINUTE },
         { Calendar.HOUR_OF_DAY, Calendar.HOUR },
         { Calendar.DATE, Calendar.DAY_OF_MONTH, Calendar.AM_PM
            /* Calendar.DAY_OF_YEAR, Calendar.DAY_OF_WEEK, Calendar.DAY_OF_WEEK_IN_MONTH */
         },
         { Calendar.MONTH, DateUtils.SEMI_MONTH },
         { Calendar.YEAR },
         { Calendar.ERA } };

      /**
       * A week range, starting on Sunday.
       */
      public final static int RANGE_WEEK_SUNDAY = 1;

      /**
       * A week range, starting on Monday.
       */
      public final static int RANGE_WEEK_MONDAY = 2;

      /**
       * A week range, starting on the day focused.
       */
      public final static int RANGE_WEEK_RELATIVE = 3;

      /**
       * A week range, centered around the day focused.
       */
      public final static int RANGE_WEEK_CENTER = 4;

      /**
       * A month range, the week starting on Sunday.
       */
      public final static int RANGE_MONTH_SUNDAY = 5;

      /**
       * A month range, the week starting on Monday.
       */
      public final static int RANGE_MONTH_MONDAY = 6;

      /**
       * <p>
       * <code>DateUtils</code> instances should NOT be constructed in standard programming. Instead, the class should
       * be used as <code>DateUtils.parse(str);</code>.
       * </p>
       * 
       * <p>
       * This constructor is public to permit tools that require a JavaBean instance to operate.
       * </p>
       */
      public DateUtils()
      {
         super();
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Checks if two date objects are on the same day ignoring time.
       * </p>
       * 
       * <p>
       * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return
       * false.
       * </p>
       * 
       * @param date1 the first date, not altered, not null
       * @param date2 the second date, not altered, not null
       * @return true if they represent the same day
       * @throws IllegalArgumentException if either date is <code>null</code>
       * @since 2.1
       */
      public static boolean isSameDay(final Date date1, final Date date2)
      {
         if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar cal1 = Calendar.getInstance();
         cal1.setTime(date1);
         Calendar cal2 = Calendar.getInstance();
         cal2.setTime(date2);
         return isSameDay(cal1, cal2);
      }

      /**
       * <p>
       * Checks if two calendar objects are on the same day ignoring time.
       * </p>
       * 
       * <p>
       * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return
       * false.
       * </p>
       * 
       * @param cal1 the first calendar, not altered, not null
       * @param cal2 the second calendar, not altered, not null
       * @return true if they represent the same day
       * @throws IllegalArgumentException if either calendar is <code>null</code>
       * @since 2.1
       */
      public static boolean isSameDay(final Calendar cal1, final Calendar cal2)
      {
         if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
         }
         return ((cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)) &&
                  (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1.get(Calendar.DAY_OF_YEAR) == cal2
                  .get(Calendar.DAY_OF_YEAR)));
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Checks if two date objects represent the same instant in time.
       * </p>
       * 
       * <p>
       * This method compares the long millisecond time of the two objects.
       * </p>
       * 
       * @param date1 the first date, not altered, not null
       * @param date2 the second date, not altered, not null
       * @return true if they represent the same millisecond instant
       * @throws IllegalArgumentException if either date is <code>null</code>
       * @since 2.1
       */
      public static boolean isSameInstant(final Date date1, final Date date2)
      {
         if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
         }
         return date1.getTime() == date2.getTime();
      }

      /**
       * <p>
       * Checks if two calendar objects represent the same instant in time.
       * </p>
       * 
       * <p>
       * This method compares the long millisecond time of the two objects.
       * </p>
       * 
       * @param cal1 the first calendar, not altered, not null
       * @param cal2 the second calendar, not altered, not null
       * @return true if they represent the same millisecond instant
       * @throws IllegalArgumentException if either date is <code>null</code>
       * @since 2.1
       */
      public static boolean isSameInstant(final Calendar cal1, final Calendar cal2)
      {
         if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
         }
         return cal1.getTime().getTime() == cal2.getTime().getTime();
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Checks if two calendar objects represent the same local time.
       * </p>
       * 
       * <p>
       * This method compares the values of the fields of the two objects. In addition, both calendars must be the same
       * of the same type.
       * </p>
       * 
       * @param cal1 the first calendar, not altered, not null
       * @param cal2 the second calendar, not altered, not null
       * @return true if they represent the same millisecond instant
       * @throws IllegalArgumentException if either date is <code>null</code>
       * @since 2.1
       */
      public static boolean isSameLocalTime(final Calendar cal1, final Calendar cal2)
      {
         if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
         }
         return ((cal1.get(Calendar.MILLISECOND) == cal2.get(Calendar.MILLISECOND)) &&
                  (cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND)) &&
                  (cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)) &&
                  (cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR)) &&
                  (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) &&
                  (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) &&
                  (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)) && (cal1.getClass() == cal2.getClass()));
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Parses a string representing a date by trying a variety of different parsers.
       * </p>
       * 
       * <p>
       * The parse will try each parse pattern in turn. A parse is only deemed sucessful if it parses the whole of the
       * input string. If no parse patterns match, a ParseException is thrown.
       * </p>
       * 
       * @param str the date to parse, not null
       * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
       * @return the parsed date
       * @throws IllegalArgumentException if the date string or pattern array is null
       * @throws ParseException if none of the date patterns were suitable
       */
      public static Date parseDate(final String str, final String[] parsePatterns) throws ParseException
      {
         if ((str == null) || (parsePatterns == null)) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
         }

         SimpleDateFormat parser = null;
         ParsePosition pos = new ParsePosition(0);
         for (int i = 0; i < parsePatterns.length; i++) {
            if (i == 0) {
               parser = new SimpleDateFormat(parsePatterns[0]);
            }
            else {
               parser.applyPattern(parsePatterns[i]);
            }
            pos.setIndex(0);
            Date date = parser.parse(str, pos);
            if ((date != null) && (pos.getIndex() == str.length())) {
               return date;
            }
         }
         throw new ParseException("Unable to parse the date: " + str, -1);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of years to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addYears(final Date date, final int amount)
      {
         return add(date, Calendar.YEAR, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of months to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addMonths(final Date date, final int amount)
      {
         return add(date, Calendar.MONTH, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of weeks to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addWeeks(final Date date, final int amount)
      {
         return add(date, Calendar.WEEK_OF_YEAR, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of days to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addDays(final Date date, final int amount)
      {
         return add(date, Calendar.DAY_OF_MONTH, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of hours to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addHours(final Date date, final int amount)
      {
         return add(date, Calendar.HOUR_OF_DAY, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of minutes to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addMinutes(final Date date, final int amount)
      {
         return add(date, Calendar.MINUTE, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of seconds to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addSeconds(final Date date, final int amount)
      {
         return add(date, Calendar.SECOND, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds a number of milliseconds to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       */
      public static Date addMilliseconds(final Date date, final int amount)
      {
         return add(date, Calendar.MILLISECOND, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Adds to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param calendarField the calendar field to add to
       * @param amount the amount to add, may be negative
       * @return the new date object with the amount added
       * @throws IllegalArgumentException if the date is null
       * @deprecated Will become privately scoped in 3.0
       */
      @Deprecated
      public static Date add(final Date date, final int calendarField, final int amount)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar c = Calendar.getInstance();
         c.setTime(date);
         c.add(calendarField, amount);
         return c.getTime();
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the years field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setYears(final Date date, final int amount)
      {
         return set(date, Calendar.YEAR, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the months field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setMonths(final Date date, final int amount)
      {
         return set(date, Calendar.MONTH, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the day of month field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setDays(final Date date, final int amount)
      {
         return set(date, Calendar.DAY_OF_MONTH, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the hours field to a date returning a new object. Hours range from 0-23. The original date object is
       * unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setHours(final Date date, final int amount)
      {
         return set(date, Calendar.HOUR_OF_DAY, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the minute field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setMinutes(final Date date, final int amount)
      {
         return set(date, Calendar.MINUTE, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the seconds field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setSeconds(final Date date, final int amount)
      {
         return set(date, Calendar.SECOND, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the miliseconds field to a date returning a new object. The original date object is unchanged.
       * 
       * @param date the date, not null
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      public static Date setMilliseconds(final Date date, final int amount)
      {
         return set(date, Calendar.MILLISECOND, amount);
      }

      // -----------------------------------------------------------------------
      /**
       * Sets the specified field to a date returning a new object. This does not use a lenient calendar. The original
       * date object is unchanged.
       * 
       * @param date the date, not null
       * @param calendarField the calendar field to set the amount to
       * @param amount the amount to set
       * @return a new Date object set with the specified value
       * @throws IllegalArgumentException if the date is null
       * @since 2.4
       */
      private static Date set(final Date date, final int calendarField, final int amount)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         // getInstance() returns a new object, so this method is thread safe.
         Calendar c = Calendar.getInstance();
         c.setLenient(false);
         c.setTime(date);
         c.set(calendarField, amount);
         return c.getTime();
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Round this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with HOUR, it would return
       * 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 April 2002 0:00:00.000.
       * </p>
       * 
       * <p>
       * For a date in a timezone that handles the change to daylight saving time, rounding to Calendar.HOUR_OF_DAY will
       * behave as follows. Suppose daylight saving time begins at 02:00 on March 30. Rounding a date that crosses this
       * time would produce the following values:
       * <ul>
       * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
       * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Date round(final Date date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar gval = Calendar.getInstance();
         gval.setTime(date);
         modify(gval, field, true);
         return gval.getTime();
      }

      /**
       * <p>
       * Round this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with HOUR, it would return
       * 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 April 2002 0:00:00.000.
       * </p>
       * 
       * <p>
       * For a date in a timezone that handles the change to daylight saving time, rounding to Calendar.HOUR_OF_DAY will
       * behave as follows. Suppose daylight saving time begins at 02:00 on March 30. Rounding a date that crosses this
       * time would produce the following values:
       * <ul>
       * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
       * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date (a different object)
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Calendar round(final Calendar date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar rounded = (Calendar) date.clone();
         modify(rounded, field, true);
         return rounded;
      }

      /**
       * <p>
       * Round this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if this was passed with HOUR, it would return
       * 28 Mar 2002 14:00:00.000. If this was passed with MONTH, it would return 1 April 2002 0:00:00.000.
       * </p>
       * 
       * <p>
       * For a date in a timezone that handles the change to daylight saving time, rounding to Calendar.HOUR_OF_DAY will
       * behave as follows. Suppose daylight saving time begins at 02:00 on March 30. Rounding a date that crosses this
       * time would produce the following values:
       * <ul>
       * <li>March 30, 2003 01:10 rounds to March 30, 2003 01:00</li>
       * <li>March 30, 2003 01:40 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:10 rounds to March 30, 2003 03:00</li>
       * <li>March 30, 2003 02:40 rounds to March 30, 2003 04:00</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, either Date or Calendar
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ClassCastException if the object type is not a <code>Date</code> or <code>Calendar</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Date round(final Object date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         if (date instanceof Date) {
            return round((Date) date, field);
         }
         else if (date instanceof Calendar) {
            return round((Calendar) date, field).getTime();
         }
         else {
            throw new ClassCastException("Could not round " + date);
         }
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Truncate this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it would return 28
       * Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar 2002 0:00:00.000.
       * </p>
       * 
       * @param date the date to work with
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Date truncate(final Date date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar gval = Calendar.getInstance();
         gval.setTime(date);
         modify(gval, field, false);
         return gval.getTime();
      }

      /**
       * <p>
       * Truncate this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it would return 28
       * Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar 2002 0:00:00.000.
       * </p>
       * 
       * @param date the date to work with
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date (a different object)
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Calendar truncate(final Calendar date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar truncated = (Calendar) date.clone();
         modify(truncated, field, false);
         return truncated;
      }

      /**
       * <p>
       * Truncate this date, leaving the field specified as the most significant field.
       * </p>
       * 
       * <p>
       * For example, if you had the datetime of 28 Mar 2002 13:45:01.231, if you passed with HOUR, it would return 28
       * Mar 2002 13:00:00.000. If this was passed with MONTH, it would return 1 Mar 2002 0:00:00.000.
       * </p>
       * 
       * @param date the date to work with, either <code>Date</code> or <code>Calendar</code>
       * @param field the field from <code>Calendar</code> or <code>SEMI_MONTH</code>
       * @return the rounded date
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ClassCastException if the object type is not a <code>Date</code> or <code>Calendar</code>
       * @throws ArithmeticException if the year is over 280 million
       */
      public static Date truncate(final Object date, final int field)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         if (date instanceof Date) {
            return truncate((Date) date, field);
         }
         else if (date instanceof Calendar) {
            return truncate((Calendar) date, field).getTime();
         }
         else {
            throw new ClassCastException("Could not truncate " + date);
         }
      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * Internal calculation method.
       * </p>
       * 
       * @param val the calendar
       * @param field the field constant
       * @param round true to round, false to truncate
       * @throws ArithmeticException if the year is over 280 million
       */
      private static void modify(final Calendar val, final int field, final boolean round)
      {
         if (val.get(Calendar.YEAR) > 280000000) {
            throw new ArithmeticException("Calendar value too large for accurate calculations");
         }

         if (field == Calendar.MILLISECOND) {
            return;
         }

         // ----------------- Fix for LANG-59 ---------------------- START ---------------
         // see http://issues.apache.org/jira/browse/LANG-59
         //
         // Manually truncate milliseconds, seconds and minutes, rather than using
         // Calendar methods.

         Date date = val.getTime();
         long time = date.getTime();
         boolean done = false;

         // truncate milliseconds
         int millisecs = val.get(Calendar.MILLISECOND);
         if (!round || (millisecs < 500)) {
            time = time - millisecs;
         }
         if (field == Calendar.SECOND) {
            done = true;
         }

         // truncate seconds
         int seconds = val.get(Calendar.SECOND);
         if (!done && (!round || (seconds < 30))) {
            time = time - (seconds * 1000L);
         }
         if (field == Calendar.MINUTE) {
            done = true;
         }

         // truncate minutes
         int minutes = val.get(Calendar.MINUTE);
         if (!done && (!round || (minutes < 30))) {
            time = time - (minutes * 60000L);
         }

         // reset time
         if (date.getTime() != time) {
            date.setTime(time);
            val.setTime(date);
         }
         // ----------------- Fix for LANG-59 ----------------------- END ----------------

         boolean roundUp = false;
         for (int[] field2 : fields) {
            for (int element : field2) {
               if (element == field) {
                  // This is our field... we stop looping
                  if (round && roundUp) {
                     if (field == DateUtils.SEMI_MONTH) {
                        // This is a special case that's hard to generalize
                        // If the date is 1, we round up to 16, otherwise
                        // we subtract 15 days and add 1 month
                        if (val.get(Calendar.DATE) == 1) {
                           val.add(Calendar.DATE, 15);
                        }
                        else {
                           val.add(Calendar.DATE, -15);
                           val.add(Calendar.MONTH, 1);
                        }
                     }
                     else {
                        // We need at add one to this field since the
                        // last number causes us to round up
                        val.add(field2[0], 1);
                     }
                  }
                  return;
               }
            }
            // We have various fields that are not easy roundings
            int offset = 0;
            boolean offsetSet = false;
            // These are special types of fields that require different rounding rules
            switch (field)
            {
            case DateUtils.SEMI_MONTH:
               if (field2[0] == Calendar.DATE) {
                  // If we're going to drop the DATE field's value,
                  // we want to do this our own way.
                  // We need to subtrace 1 since the date has a minimum of 1
                  offset = val.get(Calendar.DATE) - 1;
                  // If we're above 15 days adjustment, that means we're in the
                  // bottom half of the month and should stay accordingly.
                  if (offset >= 15) {
                     offset -= 15;
                  }
                  // Record whether we're in the top or bottom half of that range
                  roundUp = offset > 7;
                  offsetSet = true;
               }
               break;
            case Calendar.AM_PM:
               if (field2[0] == Calendar.HOUR_OF_DAY) {
                  // If we're going to drop the HOUR field's value,
                  // we want to do this our own way.
                  offset = val.get(Calendar.HOUR_OF_DAY);
                  if (offset >= 12) {
                     offset -= 12;
                  }
                  roundUp = offset > 6;
                  offsetSet = true;
               }
               break;
            }
            if (!offsetSet) {
               int min = val.getActualMinimum(field2[0]);
               int max = val.getActualMaximum(field2[0]);
               // Calculate the offset from the minimum allowed value
               offset = val.get(field2[0]) - min;
               // Set roundUp if this is more than half way between the minimum and maximum
               roundUp = offset > ((max - min) / 2);
            }
            // We need to remove this field
            if (offset != 0) {
               val.set(field2[0], val.get(field2[0]) - offset);
            }
         }
         throw new IllegalArgumentException("The field " + field + " is not supported");

      }

      // -----------------------------------------------------------------------
      /**
       * <p>
       * This constructs an <code>Iterator</code> over each day in a date range defined by a focus date and range style.
       * </p>
       * 
       * <p>
       * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will return an
       * <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with Saturday, August 3, 2002, returning
       * a Calendar instance for each intermediate day.
       * </p>
       * 
       * <p>
       * This method provides an iterator that returns Calendar objects. The days are progressed using
       * {@link Calendar#add(int, int)}.
       * </p>
       * 
       * @param focus the date to work with, not null
       * @param rangeStyle the style constant to use. Must be one of {@link DateUtils#RANGE_MONTH_SUNDAY},
       *           {@link DateUtils#RANGE_MONTH_MONDAY}, {@link DateUtils#RANGE_WEEK_SUNDAY},
       *           {@link DateUtils#RANGE_WEEK_MONDAY}, {@link DateUtils#RANGE_WEEK_RELATIVE},
       *           {@link DateUtils#RANGE_WEEK_CENTER}
       * @return the date iterator, which always returns Calendar instances
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws IllegalArgumentException if the rangeStyle is invalid
       */
      public static Iterator iterator(final Date focus, final int rangeStyle)
      {
         if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar gval = Calendar.getInstance();
         gval.setTime(focus);
         return iterator(gval, rangeStyle);
      }

      /**
       * <p>
       * This constructs an <code>Iterator</code> over each day in a date range defined by a focus date and range style.
       * </p>
       * 
       * <p>
       * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will return an
       * <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with Saturday, August 3, 2002, returning
       * a Calendar instance for each intermediate day.
       * </p>
       * 
       * <p>
       * This method provides an iterator that returns Calendar objects. The days are progressed using
       * {@link Calendar#add(int, int)}.
       * </p>
       * 
       * @param focus the date to work with
       * @param rangeStyle the style constant to use. Must be one of {@link DateUtils#RANGE_MONTH_SUNDAY},
       *           {@link DateUtils#RANGE_MONTH_MONDAY}, {@link DateUtils#RANGE_WEEK_SUNDAY},
       *           {@link DateUtils#RANGE_WEEK_MONDAY}, {@link DateUtils#RANGE_WEEK_RELATIVE},
       *           {@link DateUtils#RANGE_WEEK_CENTER}
       * @return the date iterator
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws IllegalArgumentException if the rangeStyle is invalid
       */
      public static Iterator iterator(final Calendar focus, final int rangeStyle)
      {
         if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar start = null;
         Calendar end = null;
         int startCutoff = Calendar.SUNDAY;
         int endCutoff = Calendar.SATURDAY;
         switch (rangeStyle)
         {
         case RANGE_MONTH_SUNDAY:
         case RANGE_MONTH_MONDAY:
            // Set start to the first of the month
            start = truncate(focus, Calendar.MONTH);
            // Set end to the last of the month
            end = (Calendar) start.clone();
            end.add(Calendar.MONTH, 1);
            end.add(Calendar.DATE, -1);
            // Loop start back to the previous sunday or monday
            if (rangeStyle == RANGE_MONTH_MONDAY) {
               startCutoff = Calendar.MONDAY;
               endCutoff = Calendar.SUNDAY;
            }
            break;
         case RANGE_WEEK_SUNDAY:
         case RANGE_WEEK_MONDAY:
         case RANGE_WEEK_RELATIVE:
         case RANGE_WEEK_CENTER:
            // Set start and end to the current date
            start = truncate(focus, Calendar.DATE);
            end = truncate(focus, Calendar.DATE);
            switch (rangeStyle)
            {
            case RANGE_WEEK_SUNDAY:
               // already set by default
               break;
            case RANGE_WEEK_MONDAY:
               startCutoff = Calendar.MONDAY;
               endCutoff = Calendar.SUNDAY;
               break;
            case RANGE_WEEK_RELATIVE:
               startCutoff = focus.get(Calendar.DAY_OF_WEEK);
               endCutoff = startCutoff - 1;
               break;
            case RANGE_WEEK_CENTER:
               startCutoff = focus.get(Calendar.DAY_OF_WEEK) - 3;
               endCutoff = focus.get(Calendar.DAY_OF_WEEK) + 3;
               break;
            }
            break;
         default:
            throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
         }
         if (startCutoff < Calendar.SUNDAY) {
            startCutoff += 7;
         }
         if (startCutoff > Calendar.SATURDAY) {
            startCutoff -= 7;
         }
         if (endCutoff < Calendar.SUNDAY) {
            endCutoff += 7;
         }
         if (endCutoff > Calendar.SATURDAY) {
            endCutoff -= 7;
         }
         while (start.get(Calendar.DAY_OF_WEEK) != startCutoff) {
            start.add(Calendar.DATE, -1);
         }
         while (end.get(Calendar.DAY_OF_WEEK) != endCutoff) {
            end.add(Calendar.DATE, 1);
         }
         return new DateIterator(start, end);
      }

      /**
       * <p>
       * This constructs an <code>Iterator</code> over each day in a date range defined by a focus date and range style.
       * </p>
       * 
       * <p>
       * For instance, passing Thursday, July 4, 2002 and a <code>RANGE_MONTH_SUNDAY</code> will return an
       * <code>Iterator</code> that starts with Sunday, June 30, 2002 and ends with Saturday, August 3, 2002, returning
       * a Calendar instance for each intermediate day.
       * </p>
       * 
       * @param focus the date to work with, either <code>Date</code> or <code>Calendar</code>
       * @param rangeStyle the style constant to use. Must be one of the range styles listed for the
       *           {@link #iterator(Calendar, int)} method.
       * @return the date iterator
       * @throws IllegalArgumentException if the date is <code>null</code>
       * @throws ClassCastException if the object type is not a <code>Date</code> or <code>Calendar</code>
       */
      public static Iterator iterator(final Object focus, final int rangeStyle)
      {
         if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         if (focus instanceof Date) {
            return iterator((Date) focus, rangeStyle);
         }
         else if (focus instanceof Calendar) {
            return iterator((Calendar) focus, rangeStyle);
         }
         else {
            throw new ClassCastException("Could not iterate based on " + focus);
         }
      }

      /**
       * <p>
       * Returns the number of milliseconds within the fragment. All datefields greater than the fragment will be
       * ignored.
       * </p>
       * 
       * <p>
       * Asking the milliseconds of any date will only return the number of milliseconds of the current second
       * (resulting in a number between 0 and 999). This method will retrieve the number of milliseconds for any
       * fragment. For example, if you want to calculate the number of milliseconds past today, your fragment is
       * Calendar.DATE or Calendar.DAY_OF_YEAR. The result will be all milliseconds of the past hour(s), minutes(s) and
       * second(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a SECOND field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10538 (10*1000 + 538)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in milliseconds)</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @return number of milliseconds within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInMilliseconds(final Date date, final int fragment)
      {
         return getFragment(date, fragment, Calendar.MILLISECOND);
      }

      /**
       * <p>
       * Returns the number of seconds within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the seconds of any date will only return the number of seconds of the current minute (resulting in a
       * number between 0 and 59). This method will retrieve the number of seconds for any fragment. For example, if you
       * want to calculate the number of seconds past today, your fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The
       * result will be all seconds of the past hour(s) and minutes(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a SECOND field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent to deprecated
       * date.getSeconds())</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent to deprecated
       * date.getSeconds())</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 26110 (7*3600 + 15*60 + 10)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in seconds)</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @return number of seconds within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInSeconds(final Date date, final int fragment)
      {
         return getFragment(date, fragment, Calendar.SECOND);
      }

      /**
       * <p>
       * Returns the number of minutes within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the minutes of any date will only return the number of minutes of the current hour (resulting in a
       * number between 0 and 59). This method will retrieve the number of minutes for any fragment. For example, if you
       * want to calculate the number of minutes past this month, your fragment is Calendar.MONTH. The result will be
       * all minutes of the past day(s) and hour(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a MINUTE field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15 (equivalent to deprecated
       * date.getMinutes())</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15 (equivalent to deprecated
       * date.getMinutes())</li>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 15</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 435 (7*60 + 15)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in minutes)</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @return number of minutes within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInMinutes(final Date date, final int fragment)
      {
         return getFragment(date, fragment, Calendar.MINUTE);
      }

      /**
       * <p>
       * Returns the number of hours within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the hours of any date will only return the number of hours of the current day (resulting in a number
       * between 0 and 23). This method will retrieve the number of hours for any fragment. For example, if you want to
       * calculate the number of hours past this month, your fragment is Calendar.MONTH. The result will be all hours of
       * the past day(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a HOUR field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7 (equivalent to deprecated
       * date.getHours())</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7 (equivalent to deprecated
       * date.getHours())</li>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 7</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 127 (5*24 + 7)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in hours)</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @return number of hours within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInHours(final Date date, final int fragment)
      {
         return getFragment(date, fragment, Calendar.HOUR_OF_DAY);
      }

      /**
       * <p>
       * Returns the number of days within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the days of any date will only return the number of days of the current month (resulting in a number
       * between 1 and 31). This method will retrieve the number of days for any fragment. For example, if you want to
       * calculate the number of days past this year, your fragment is Calendar.YEAR. The result will be all days of the
       * past month(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a DAY field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to deprecated date.getDay())</li>
       * <li>February 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to deprecated date.getDay())</li>
       * <li>January 28, 2008 with Calendar.YEAR as fragment will return 28</li>
       * <li>February 28, 2008 with Calendar.YEAR as fragment will return 59</li>
       * <li>January 28, 2008 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be split in
       * days)</li>
       * </ul>
       * </p>
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @return number of days within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInDays(final Date date, final int fragment)
      {
         return getFragment(date, fragment, Calendar.DAY_OF_YEAR);
      }

      /**
       * <p>
       * Returns the number of milliseconds within the fragment. All datefields greater than the fragment will be
       * ignored.
       * </p>
       * 
       * <p>
       * Asking the milliseconds of any date will only return the number of milliseconds of the current second
       * (resulting in a number between 0 and 999). This method will retrieve the number of milliseconds for any
       * fragment. For example, if you want to calculate the number of seconds past today, your fragment is
       * Calendar.DATE or Calendar.DAY_OF_YEAR. The result will be all seconds of the past hour(s), minutes(s) and
       * second(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a MILLISECOND field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538 (equivalent to
       * calendar.get(Calendar.MILLISECOND))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.SECOND as fragment will return 538 (equivalent to
       * calendar.get(Calendar.MILLISECOND))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10538 (10*1000 + 538)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in milliseconds)</li>
       * </ul>
       * </p>
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @return number of milliseconds within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInMilliseconds(final Calendar calendar, final int fragment)
      {
         return getFragment(calendar, fragment, Calendar.MILLISECOND);
      }

      /**
       * <p>
       * Returns the number of seconds within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the seconds of any date will only return the number of seconds of the current minute (resulting in a
       * number between 0 and 59). This method will retrieve the number of seconds for any fragment. For example, if you
       * want to calculate the number of seconds past today, your fragment is Calendar.DATE or Calendar.DAY_OF_YEAR. The
       * result will be all seconds of the past hour(s) and minutes(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a SECOND field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent to
       * calendar.get(Calendar.SECOND))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MINUTE as fragment will return 10 (equivalent to
       * calendar.get(Calendar.SECOND))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 26110 (7*3600 + 15*60 + 10)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in seconds)</li>
       * </ul>
       * </p>
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @return number of seconds within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInSeconds(final Calendar calendar, final int fragment)
      {
         return getFragment(calendar, fragment, Calendar.SECOND);
      }

      /**
       * <p>
       * Returns the number of minutes within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the minutes of any date will only return the number of minutes of the current hour (resulting in a
       * number between 0 and 59). This method will retrieve the number of minutes for any fragment. For example, if you
       * want to calculate the number of minutes past this month, your fragment is Calendar.MONTH. The result will be
       * all minutes of the past day(s) and hour(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a MINUTE field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15 (equivalent to
       * calendar.get(Calendar.MINUTES))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.HOUR_OF_DAY as fragment will return 15 (equivalent to
       * calendar.get(Calendar.MINUTES))</li>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 15</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 435 (7*60 + 15)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in minutes)</li>
       * </ul>
       * </p>
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @return number of minutes within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInMinutes(final Calendar calendar, final int fragment)
      {
         return getFragment(calendar, fragment, Calendar.MINUTE);
      }

      /**
       * <p>
       * Returns the number of hours within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the hours of any date will only return the number of hours of the current day (resulting in a number
       * between 0 and 23). This method will retrieve the number of hours for any fragment. For example, if you want to
       * calculate the number of hours past this month, your fragment is Calendar.MONTH. The result will be all hours of
       * the past day(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a HOUR field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 1, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7 (equivalent to
       * calendar.get(Calendar.HOUR_OF_DAY))</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.DAY_OF_YEAR as fragment will return 7 (equivalent to
       * calendar.get(Calendar.HOUR_OF_DAY))</li>
       * <li>January 1, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 7</li>
       * <li>January 6, 2008 7:15:10.538 with Calendar.MONTH as fragment will return 127 (5*24 + 7)</li>
       * <li>January 16, 2008 7:15:10.538 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be
       * split in hours)</li>
       * </ul>
       * </p>
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @return number of hours within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInHours(final Calendar calendar, final int fragment)
      {
         return getFragment(calendar, fragment, Calendar.HOUR_OF_DAY);
      }

      /**
       * <p>
       * Returns the number of days within the fragment. All datefields greater than the fragment will be ignored.
       * </p>
       * 
       * <p>
       * Asking the days of any date will only return the number of days of the current month (resulting in a number
       * between 1 and 31). This method will retrieve the number of days for any fragment. For example, if you want to
       * calculate the number of days past this year, your fragment is Calendar.YEAR. The result will be all days of the
       * past month(s).
       * </p>
       * 
       * <p>
       * Valid fragments are: Calendar.YEAR, Calendar.MONTH, both Calendar.DAY_OF_YEAR and Calendar.DATE,
       * Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND and Calendar.MILLISECOND A fragment less than or equal
       * to a DAY field will return 0.
       * </p>
       * 
       * <p>
       * <ul>
       * <li>January 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to
       * calendar.get(Calendar.DAY_OF_MONTH))</li>
       * <li>February 28, 2008 with Calendar.MONTH as fragment will return 28 (equivalent to
       * calendar.get(Calendar.DAY_OF_MONTH))</li>
       * <li>January 28, 2008 with Calendar.YEAR as fragment will return 28 (equivalent to
       * calendar.get(Calendar.DAY_OF_YEAR))</li>
       * <li>February 28, 2008 with Calendar.YEAR as fragment will return 59 (equivalent to
       * calendar.get(Calendar.DAY_OF_YEAR))</li>
       * <li>January 28, 2008 with Calendar.MILLISECOND as fragment will return 0 (a millisecond cannot be split in
       * days)</li>
       * </ul>
       * </p>
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @return number of days within the fragment of date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      public static long getFragmentInDays(final Calendar calendar, final int fragment)
      {
         return getFragment(calendar, fragment, Calendar.DAY_OF_YEAR);
      }

      /**
       * Date-version for fragment-calculation in any unit
       * 
       * @param date the date to work with, not null
       * @param fragment the Calendar field part of date to calculate
       * @param unit Calendar field defining the unit
       * @return number of units within the fragment of the date
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      private static long getFragment(final Date date, final int fragment, final int unit)
      {
         if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         return getFragment(calendar, fragment, unit);
      }

      /**
       * Calendar-version for fragment-calculation in any unit
       * 
       * @param calendar the calendar to work with, not null
       * @param fragment the Calendar field part of calendar to calculate
       * @param unit Calendar field defining the unit
       * @return number of units within the fragment of the calendar
       * @throws IllegalArgumentException if the date is <code>null</code> or fragment is not supported
       * @since 2.4
       */
      private static long getFragment(final Calendar calendar, final int fragment, final int unit)
      {
         if (calendar == null) {
            throw new IllegalArgumentException("The date must not be null");
         }
         long millisPerUnit = getMillisPerUnit(unit);
         long result = 0;

         // Fragments bigger than a day require a breakdown to days
         switch (fragment)
         {
         case Calendar.YEAR:
            result += (calendar.get(Calendar.DAY_OF_YEAR) * MILLIS_PER_DAY) / millisPerUnit;
            break;
         case Calendar.MONTH:
            result += (calendar.get(Calendar.DAY_OF_MONTH) * MILLIS_PER_DAY) / millisPerUnit;
            break;
         }

         switch (fragment)
         {
         // Number of days already calculated for these cases
         case Calendar.YEAR:
         case Calendar.MONTH:

            // The rest of the valid cases
         case Calendar.DAY_OF_YEAR:
         case Calendar.DATE:
            result += (calendar.get(Calendar.HOUR_OF_DAY) * MILLIS_PER_HOUR) / millisPerUnit;
         case Calendar.HOUR_OF_DAY:
            result += (calendar.get(Calendar.MINUTE) * MILLIS_PER_MINUTE) / millisPerUnit;
         case Calendar.MINUTE:
            result += (calendar.get(Calendar.SECOND) * MILLIS_PER_SECOND) / millisPerUnit;
         case Calendar.SECOND:
            result += (calendar.get(Calendar.MILLISECOND) * 1) / millisPerUnit;
            break;
         case Calendar.MILLISECOND:
            break;// never useful
         default:
            throw new IllegalArgumentException("The fragment " + fragment + " is not supported");
         }
         return result;
      }

      /**
       * Returns the number of millis of a datefield, if this is a constant value
       * 
       * @param unit A Calendar field which is a valid unit for a fragment
       * @return number of millis
       * @throws IllegalArgumentException if date can't be represented in millisenconds
       * @since 2.4
       */
      private static long getMillisPerUnit(final int unit)
      {
         long result = Long.MAX_VALUE;
         switch (unit)
         {
         case Calendar.DAY_OF_YEAR:
         case Calendar.DATE:
            result = MILLIS_PER_DAY;
            break;
         case Calendar.HOUR_OF_DAY:
            result = MILLIS_PER_HOUR;
            break;
         case Calendar.MINUTE:
            result = MILLIS_PER_MINUTE;
            break;
         case Calendar.SECOND:
            result = MILLIS_PER_SECOND;
            break;
         case Calendar.MILLISECOND:
            result = 1;
            break;
         default:
            throw new IllegalArgumentException("The unit " + unit + " cannot be represented is milleseconds");
         }
         return result;
      }

      /**
       * <p>
       * Date iterator.
       * </p>
       */
      static class DateIterator implements Iterator
      {
         private final Calendar endFinal;
         private final Calendar spot;

         /**
          * Constructs a DateIterator that ranges from one date to another.
          * 
          * @param startFinal start date (inclusive)
          * @param endFinal end date (not inclusive)
          */
         DateIterator(final Calendar startFinal, final Calendar endFinal)
         {
            super();
            this.endFinal = endFinal;
            spot = startFinal;
            spot.add(Calendar.DATE, -1);
         }

         /**
          * Has the iterator not reached the end date yet?
          * 
          * @return <code>true</code> if the iterator has yet to reach the end date
          */
         @Override
         public boolean hasNext()
         {
            return spot.before(endFinal);
         }

         /**
          * Return the next calendar in the iteration
          * 
          * @return Object calendar for the next date
          */
         @Override
         public Object next()
         {
            if (spot.equals(endFinal)) {
               throw new NoSuchElementException();
            }
            spot.add(Calendar.DATE, 1);
            return spot.clone();
         }

         /**
          * Always throws UnsupportedOperationException.
          * 
          * @throws UnsupportedOperationException
          * @see java.util.Iterator#remove()
          */
         @Override
         public void remove()
         {
            throw new UnsupportedOperationException();
         }
      }

      // -------------------------------------------------------------------------
      // Deprecated int constants
      // TODO: Remove in 3.0

      /**
       * Number of milliseconds in a standard second.
       * 
       * @deprecated Use MILLIS_PER_SECOND. This will be removed in Commons Lang 3.0.
       */
      @Deprecated
      public static final int MILLIS_IN_SECOND = 1000;
      /**
       * Number of milliseconds in a standard minute.
       * 
       * @deprecated Use MILLIS_PER_MINUTE. This will be removed in Commons Lang 3.0.
       */
      @Deprecated
      public static final int MILLIS_IN_MINUTE = 60 * 1000;
      /**
       * Number of milliseconds in a standard hour.
       * 
       * @deprecated Use MILLIS_PER_HOUR. This will be removed in Commons Lang 3.0.
       */
      @Deprecated
      public static final int MILLIS_IN_HOUR = 60 * 60 * 1000;
      /**
       * Number of milliseconds in a standard day.
       * 
       * @deprecated Use MILLIS_PER_DAY. This will be removed in Commons Lang 3.0.
       */
      @Deprecated
      public static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

   }

}
