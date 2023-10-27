package de.lb.ruleprocessor.checkpoint.utils;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 2.0
 */
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.*;

public class UtlDateTimeConverter
{
	private static UtlDateTimeConverter m_converter = null;
	static final long ONE_HOUR = 60 * 60 * 1000L;

	private SimpleDateFormat m_dateFormatEnglDet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat m_dateFormatEngl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat m_dateFormatEnglDetLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private SimpleDateFormat m_dateFormatGerm = new SimpleDateFormat("dd.MM.yyyy");
	private SimpleDateFormat m_dateTimeFormatGerm = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private SimpleDateFormat m_dateFormatExpTime = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat m_dateFormatExp = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat m_timeFormatExp = new SimpleDateFormat("HHmm");
	private SimpleDateFormat m_dateFormatDetailTime = new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat m_dateUpload = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

//	private Calendar m_calendar = new GregorianCalendar();
	private java.sql.Date m_maxValue = null;

	public UtlDateTimeConverter()
	{
	}

	public static UtlDateTimeConverter converter(){
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_converter sollte die Methode synchronized sein. */
		if (m_converter==null)
			m_converter = new UtlDateTimeConverter();
		return m_converter;
	}

	public  int getYear(java.util.Date dt){
		if (dt!=null){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			return m_calendar.get(Calendar.YEAR);
		}else
			return 0;
	}

	public  int getCurrentYear(){
		java.util.Date dt = new Date(System.currentTimeMillis());
		if (dt!=null){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			return m_calendar.get(Calendar.YEAR);
		}else
			return 0;
	}

	public  java.util.Date getStartDateForYear(int year){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		m_calendar.set(Calendar.MONTH, Calendar.JANUARY);
		m_calendar.set(Calendar.DAY_OF_MONTH, 1);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTime();
	}

	public  long getStartDatetimeForYear(int year){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		m_calendar.set(Calendar.MONTH, Calendar.JANUARY);
		m_calendar.set(Calendar.DAY_OF_MONTH, 1);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTimeInMillis();
	}

	public  java.util.Date getStartDateForQuarter(int year, int quarter){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		if (quarter==4)
			m_calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		else if (quarter==3)
			m_calendar.set(Calendar.MONTH, Calendar.JULY);
		else if (quarter==2)
			m_calendar.set(Calendar.MONTH, Calendar.APRIL);
		else
			m_calendar.set(Calendar.MONTH, Calendar.JANUARY);
		m_calendar.set(Calendar.DAY_OF_MONTH, 1);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTime();
	}

	public  long getStartDatetimeForQuarter(int year, int quarter){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		if (quarter==4)
			m_calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		else if (quarter==3)
			m_calendar.set(Calendar.MONTH, Calendar.JULY);
		else if (quarter==2)
			m_calendar.set(Calendar.MONTH, Calendar.APRIL);
		else
			m_calendar.set(Calendar.MONTH, Calendar.JANUARY);
		m_calendar.set(Calendar.DAY_OF_MONTH, 1);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTimeInMillis();
	}

	public  java.util.Date getEndDateForYear(int year){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		m_calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		m_calendar.set(Calendar.HOUR_OF_DAY, 23);
		m_calendar.set(Calendar.MINUTE, 59);
		m_calendar.set(Calendar.SECOND, 59);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTime();
	}

	public  long getEndDatetimeForYear(int year){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		m_calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		m_calendar.set(Calendar.HOUR_OF_DAY, 23);
		m_calendar.set(Calendar.MINUTE, 59);
		m_calendar.set(Calendar.SECOND, 59);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTimeInMillis();
	}

	public  java.util.Date getEndDateForQuarter(int year, int quarter){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		if(quarter == 4) {
			m_calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		} else if(quarter == 3) {
			m_calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			m_calendar.set(Calendar.DAY_OF_MONTH, 30);
		} else if(quarter == 2) {
			m_calendar.set(Calendar.MONTH, Calendar.JUNE);
			m_calendar.set(Calendar.DAY_OF_MONTH, 30);
		} else {
			m_calendar.set(Calendar.MONTH, Calendar.MARCH);
			m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		}
		m_calendar.set(Calendar.HOUR_OF_DAY, 23);
		m_calendar.set(Calendar.MINUTE, 59);
		m_calendar.set(Calendar.SECOND, 59);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTime();
	}

	public  long getEndDatetimeForQuarter(int year, int quarter){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.set(Calendar.YEAR, year);
		if(quarter == 4) {
			m_calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		} else if(quarter == 3) {
			m_calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			m_calendar.set(Calendar.DAY_OF_MONTH, 30);
		} else if(quarter == 2) {
			m_calendar.set(Calendar.MONTH, Calendar.JUNE);
			m_calendar.set(Calendar.DAY_OF_MONTH, 30);
		} else {
			m_calendar.set(Calendar.MONTH, Calendar.MARCH);
			m_calendar.set(Calendar.DAY_OF_MONTH, 31);
		}
		m_calendar.set(Calendar.HOUR_OF_DAY, 23);
		m_calendar.set(Calendar.MINUTE, 59);
		m_calendar.set(Calendar.SECOND, 59);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTimeInMillis();
	}

	public  Calendar getCurrentCalendar(){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTimeInMillis(System.currentTimeMillis());
		return m_calendar;
	}

	public Calendar getCalendar(java.util.Date dt){
		Calendar m_calendar = new GregorianCalendar();
		if (dt!=null)
		m_calendar.setTime(dt);
		return m_calendar;
	}
	public  String formatToGermanDate(java.util.Date dt){
		return formatToGermanDate(dt, false);
	}

	public synchronized String formatToGermanDate(java.util.Date dt, boolean withTime){
		if (dt==null)
			return "";
		if (withTime)
			return m_dateTimeFormatGerm.format(dt);
		else
			return m_dateFormatGerm.format(dt);
	}
        
        public static synchronized Date getDateFromStringWithFormat(String dtString, String formatString){
            try{
                SimpleDateFormat format = new SimpleDateFormat(formatString );

                return format.parse(dtString);
            }catch(Exception e){
                return new Date();
            }
            
        }

	public java.util.Date setTime(java.util.Date destDate, java.util.Date sourceDate){
		if (destDate==null)
			destDate = new Date(System.currentTimeMillis());
		if (sourceDate!=null){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(sourceDate);
			int hh = m_calendar.get(Calendar.HOUR_OF_DAY);
			int mm = m_calendar.get(Calendar.MINUTE);
			m_calendar.setTime(destDate);
			m_calendar.set(Calendar.HOUR_OF_DAY, hh);
			m_calendar.set(Calendar.MINUTE, mm);
			m_calendar.set(Calendar.SECOND, 0);
			m_calendar.set(Calendar.MILLISECOND, 0);
			return m_calendar.getTime();
		}
		return destDate;
	}

	public synchronized String patternDateToStringEnglDetail(){
		return m_dateFormatEnglDet.toPattern();
	}

	public synchronized String convertDateToStringEnglDetail(java.util.Date dt, boolean resetTime){
		if (dt==null)
			return "";
		if (resetTime){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			m_calendar.set(Calendar.HOUR_OF_DAY, 0);
			m_calendar.set(Calendar.MINUTE, 0);
			m_calendar.set(Calendar.SECOND, 0);
			m_calendar.set(Calendar.MILLISECOND, 0);
			return m_dateFormatEnglDet.format(m_calendar.getTime());
		}else
			return m_dateFormatEnglDet.format(dt);
	}

	public synchronized String convertDateToStringEngl(java.util.Date dt){
		if (dt==null)
			return "";
		return m_dateFormatEngl.format(dt);
	}

	/* Umbau 18.10.2012 durch LUR
	 * Ersetzung des Aufrufes der Methode convertDateToStringEnglDetail(java.util.Date dt, boolean resetTime) 
	 * durch den eigentlichen Funktionsrumpf zur Vermeidung von Deadlocks in diesem Bereich
	 */
	public synchronized String convertDateToStringEnglDetail(java.util.Date dt, boolean resetTime, boolean withMax){
		if (dt==null)
			return "";
		if (withMax){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			m_calendar.set(Calendar.HOUR_OF_DAY, 23);
			m_calendar.set(Calendar.MINUTE, 59);
			m_calendar.set(Calendar.SECOND, 59);
			m_calendar.set(Calendar.MILLISECOND, 0);
			return m_dateFormatEnglDet.format(m_calendar.getTime());
		}else {
			//return convertDateToStringEnglDetail(dt, resetTime);
			if (dt==null) {
				return "";
			}
			if (resetTime){
				Calendar m_calendar = new GregorianCalendar();
				m_calendar.setTime(dt);
				m_calendar.set(Calendar.HOUR_OF_DAY, 0);
				m_calendar.set(Calendar.MINUTE, 0);
				m_calendar.set(Calendar.SECOND, 0);
				m_calendar.set(Calendar.MILLISECOND, 0);
				return m_dateFormatEnglDet.format(m_calendar.getTime());
			}else {
				return m_dateFormatEnglDet.format(dt);
			}
		}
	}

	public synchronized String patternDateToStringEnglDetailLong(){
		return m_dateFormatEnglDetLong.toPattern();
	}

	public synchronized String convertDateToStringEnglDetailLong(java.util.Date dt, boolean resetTime){
		if (dt==null)
			return "";
		if (resetTime){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			m_calendar.set(Calendar.HOUR_OF_DAY, 0);
			m_calendar.set(Calendar.MINUTE, 0);
			m_calendar.set(Calendar.SECOND, 0);
			m_calendar.set(Calendar.MILLISECOND, 0);
			return m_dateFormatEnglDetLong.format(m_calendar.getTime());
		}else
			return m_dateFormatEnglDetLong.format(dt);
	}

	/* Umbau 18.10.2012 durch LUR
	 * Ersetzung des Aufrufes der Methode convertDateToStringEnglDetail(java.util.Date dt, boolean resetTime) 
	 * durch den eigentlichen Funktionsrumpf zur Vermeidung von Deadlocks in diesem Bereich
	 */
	public synchronized String convertDateToStringEnglDetailLong(java.util.Date dt, boolean resetTime, boolean withMax){
		if (dt==null)
			return "";
		if (withMax){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.setTime(dt);
			m_calendar.set(Calendar.HOUR_OF_DAY, 23);
			m_calendar.set(Calendar.MINUTE, 59);
			m_calendar.set(Calendar.SECOND, 59);
			m_calendar.set(Calendar.MILLISECOND, 000);
			return m_dateFormatEnglDetLong.format(m_calendar.getTime());
		}else {
			//return convertDateToStringEnglDetail(dt, resetTime);
			if (dt==null) {
				return "";
			}
			if (resetTime){
				Calendar m_calendar = new GregorianCalendar();
				m_calendar.setTime(dt);
				m_calendar.set(Calendar.HOUR_OF_DAY, 0);
				m_calendar.set(Calendar.MINUTE, 0);
				m_calendar.set(Calendar.SECOND, 0);
				m_calendar.set(Calendar.MILLISECOND, 0);
				return m_dateFormatEnglDet.format(m_calendar.getTime());
			}else {
				return m_dateFormatEnglDet.format(dt);
			}
		}
	}

	public synchronized String convertDateToExportString(java.util.Date dt, boolean withTime){
		if (dt==null)
			return "";
		if (withTime)
			return this.m_dateFormatExpTime.format(dt);
		else
			return this.m_dateFormatExp.format(dt);
	}

	public synchronized String convertDateToUpload(java.util.Date dt){
		if (dt==null)
			return "";
		return this.m_dateUpload.format(dt);
	}

	public synchronized Date convertStringToUpload(String dt){
		try{
			if(dt == null)
				return null;
			return this.m_dateUpload.parse(dt);
		}catch (Exception ex){
			return null;
		}
	}


	public synchronized String convertDateToDetailedString(java.util.Date dt){
		if (dt==null)
			return "";
		return this.m_dateFormatDetailTime.format(dt);
	}

	public synchronized String convertTimeToExportString(java.util.Date dt){
		if (dt==null)
			return "";
		else
			return this.m_timeFormatExp.format(dt);
	}

	public  int getDateDifferenzDays(java.util.Date startDate, java.util.Date endDate){
		if (endDate==null)
			endDate = new java.util.Date(System.currentTimeMillis());
		if (endDate==null)
			endDate = new java.util.Date(System.currentTimeMillis());
		long diff = endDate.getTime() - startDate.getTime();
		if (diff>0)
			return (int)(diff / 1000 / 60 / 60 / 24);
		else
			return 0;
	}

	public synchronized Date convertStringToDate(String strDate){
		Date date=null;
		try {
			date = m_dateFormatExp.parse(strDate);
		} catch(ParseException ex) {
		}
		return date;
	}

	public  static java.sql.Date convertStringToDate(String strDate, String format){
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		Date datum = null;
		try {
			datum = dateformat.parse(strDate);
		} catch(ParseException ex) {
			datum = new Date();
		}
		java.sql.Date sqlDate = new java.sql.Date(datum.getTime());
		return sqlDate;
	}

	public  Date resetTime(Date dt){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTime(dt);
		m_calendar.set(Calendar.HOUR, 0);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 1000);
		return m_calendar.getTime();
	}

	public  long getDateInMillisWithoutTime(java.sql.Date dt){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTime(dt);
		m_calendar.set(Calendar.HOUR, 0);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 1000);
		return m_calendar.getTimeInMillis();
	}

	public  long daysBetween(Date d1, Date d2){
		return daysBetween(d2.getTime(), d1.getTime());
	}

	public  long daysBetween(long d1, long d2){
		return ( (d2 - d1 + ONE_HOUR) /
			(ONE_HOUR * 24));
	}

	public  int getWeekDay(java.util.Date dt){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTime(dt);
		return m_calendar.get(Calendar.DAY_OF_WEEK);
	}

	public  java.sql.Date getMaximumDate(){
		if (m_maxValue==null){
			Calendar m_calendar = new GregorianCalendar();
			m_calendar.set(Calendar.YEAR, 9999);
			m_calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			m_calendar.set(Calendar.DAY_OF_MONTH, 31);
			m_calendar.set(Calendar.HOUR_OF_DAY, 0);
			m_calendar.set(Calendar.MINUTE, 0);
			m_calendar.set(Calendar.SECOND, 0);
			m_maxValue = new java.sql.Date(m_calendar.getTimeInMillis());
		}
		return m_maxValue;
	}

	public  java.sql.Date setSystemTimeStamp2Date(java.sql.Date date)
	{
		java.sql.Date changedDate = null;
		GregorianCalendar calSystem = new GregorianCalendar();
		GregorianCalendar calReminderDate = new GregorianCalendar();

		if(date != null) {
			calSystem.setTimeInMillis(System.currentTimeMillis());
			calReminderDate.setTime(date);
			calReminderDate.set(GregorianCalendar.HOUR_OF_DAY, calSystem.get(GregorianCalendar.HOUR_OF_DAY));
			calReminderDate.set(GregorianCalendar.MINUTE, calSystem.get(GregorianCalendar.MINUTE));
			calReminderDate.set(GregorianCalendar.SECOND, calSystem.get(GregorianCalendar.SECOND));
			calReminderDate.set(GregorianCalendar.MILLISECOND, calSystem.get(GregorianCalendar.MILLISECOND));

			changedDate = new java.sql.Date(calReminderDate.getTimeInMillis());
		}
		if(changedDate == null){
			changedDate = date;
		}
		return changedDate;
	}


/*	public int calculateYearInteval(java.util.Date beginDate, java.util.Date endDate)
	{
		int years = 0;
		m_calendar1.setTime(endDate);
		m_calendar2.setTime(beginDate);
		if(m_calendar1.get(m_calendar1.MONTH) < m_calendar2.get(m_calendar2.MONTH)) {
			years = m_calendar1.get(m_calendar1.YEAR) - m_calendar2.get(m_calendar2.YEAR);
		} else if((m_calendar1.get(m_calendar1.MONTH) == m_calendar2.get(m_calendar2.MONTH)) &&
			((m_calendar1.get(m_calendar1.DATE) <= m_calendar2.get(m_calendar2.DATE)))) {
			years = m_calendar1.get(m_calendar1.YEAR) - m_calendar2.get(m_calendar2.YEAR);
		} else {
			years = m_calendar1.get(m_calendar1.YEAR) - m_calendar2.get(m_calendar2.YEAR) - 1;
		}
		return years;
	}*/

	public int calculateDaysInteval(java.util.Date beginDate, java.util.Date endDate)
	{
            Calendar cal1 = new GregorianCalendar();
              cal1.setTime(beginDate);
              cal1.set(Calendar.HOUR_OF_DAY, 0);
              cal1.set(Calendar.MINUTE, 0);
              Calendar cal2 = new GregorianCalendar();
              cal2.setTime(endDate);
              cal2.set(Calendar.HOUR_OF_DAY, 0);
              cal2.set(Calendar.MINUTE, 0);
              long numDays = Math.round((double)(cal2.getTime().getTime() -
                                                 cal1.getTime().getTime()) /
                                        (double)86400000);
               return (int)numDays;
 	}

	  public boolean checkDiffLess24Hours(java.util.Date endDate1, java.util.Date beginDate2)
	  {
		  boolean isLess24 = false;
		  if(endDate1 != null && beginDate2 != null) {
			  long diff = beginDate2.getTime()-endDate1.getTime();
			  if(diff <= (ONE_HOUR * 24)) {
				  isLess24 = true;
			  }
		  }
		  return isLess24;
	  }

	public java.sql.Date getNextMidnightDate(long dt)
	{
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTimeInMillis(dt);
		m_calendar.set(Calendar.HOUR, 0);
		m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		m_calendar.set(Calendar.MINUTE, 0);
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		m_calendar.add(Calendar.DAY_OF_YEAR, 1);

		return new java.sql.Date(m_calendar.getTimeInMillis());
	}

	public java.sql.Date getPreviewDateLastTime(long dt)
	{
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTimeInMillis(dt);
		m_calendar.set(Calendar.HOUR, 23);
		m_calendar.set(Calendar.HOUR_OF_DAY, 23);
		m_calendar.set(Calendar.MINUTE, 59);
		m_calendar.set(Calendar.SECOND, 59);
		m_calendar.set(Calendar.MILLISECOND, 0);
		m_calendar.add(Calendar.DAY_OF_YEAR, -1);

		return new java.sql.Date(m_calendar.getTimeInMillis());
	}
        
        public static boolean isOnSameDay(Date date1, Date date2) 
        {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            return fmt.format(date1).equals(fmt.format(date2));
        }

        /**
         * aus sql.Date wird java.util.Date ohne Zeit zurückgegeben
         * @param date
         * @return 
         */
        public Date getDateWithoutTime(java.sql.Date date){
            if(date != null){
                return resetTime(new Date(date.getTime()));
            }
            return null;
        }
        
        /**
         * aus sql.Date witrd java.util.Date nur Tageszeit gegeben
         * @param date
         * @return 
         */
        public Date getTime(java.sql.Date date){
            if(date != null){
		Calendar m_calendar = new GregorianCalendar();
		m_calendar.setTime(new Date(date.getTime()));
		m_calendar.set(Calendar.YEAR, 0);
		m_calendar.set(Calendar.DAY_OF_YEAR, 0);
                return m_calendar.getTime();
            }
            return null;
        }
        
        
    
}
