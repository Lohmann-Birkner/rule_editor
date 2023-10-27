package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatInterval;
import java.text.*;
import java.util.*;

import java.io.Serializable;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGIntervalEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	int m_critFrom = CRGRuleGrouperStatics.INDEX_INTERVAL_NO_INTERVAL;
	int m_critTo = CRGRuleGrouperStatics.INDEX_INTERVAL_NO_INTERVAL;
	String m_fromValue = "";
	String m_toValue = "";
	long m_from = 0l;
	long m_to = 0l;
//	private static SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//	private static Calendar calendar = new GregorianCalendar();
	long m_admDate = 0;
	long m_disDate = 0;
	int m_ogvd = 0;
        int []retSortInds = null;
        String m_intervalAsString = "";

	public CRGIntervalEntry(DatInterval interval) throws Exception
	{
		m_critFrom = interval.getFromCrit().m_operationType;
		m_fromValue = interval.getIntervalFromValue();
		m_critTo = interval.getToCrit().m_operationType;
		m_toValue = interval.getIntervalToValue();
               m_intervalAsString = interval.toString() ;
	}

	private Date getFullDate(Date date)
	{
		if(date == null) {
			date = new Date();
		}
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private synchronized Date getDateFromString(String dt)
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Rückgabewert wird gar nicht verarbeitet.
     * Korrektur: dt = dt.replaceAll("'", ""); */
		dt.replaceAll("'", "");
		try {
			if(dt.length() >= 6) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                            return simpleDateFormat.parse(dt);
			}
		} catch(Exception e) {
		}
		return new Date();
	}

	public long getEndIntervalValue() throws Exception
	{
		return this.m_to;
	}

	public long getStartIntervalValue() throws Exception
	{
		// es wird timeStamp als Datum genommen, von dem Interval gerechnet wird
		return this.m_from;
	}

	private void checkWhoOlder()
	{
		if(m_from > m_to) {
			long tmp = m_from;
			m_from = m_to;
			m_to = tmp;
		}
	}

	/**
	 * überprüft endzeit des Intervals, wenn Startzeit ein Datum ist
	 * @param timeStamp Date
	 * @throws Exception
	 */
	private void checkToPlainTimeValue(Date timeStamp, Date date) throws Exception
	{
		int toRes = 0;
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		try {
			toRes = Integer.parseInt(m_toValue); // für den Fall der Anzahl der Tage, Monate, Quartale, Jahre
		} catch(Exception e) {
		  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Exception wird unterdrückt! */
		}
		switch(m_critTo) {
			case CRGRuleGrouperStatics.INDEX_INTERVAL_NOW:
				m_to = getFullDate(new Date()).getTime();
				checkWhoOlder();
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_DATE:
				;
				m_to = getDateFromString(this.m_toValue).getTime();
				checkWhoOlder();
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS:
				if(toRes == 0) { //aktuelles Datum
					calendar.setTime(timeStamp);
				} else {
					calendar.add(Calendar.DAY_OF_YEAR, toRes);
				}
				m_to = calendar.getTimeInMillis();
				checkWhoOlder();
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS:
				if(toRes == 0) { //aktuelles Datum
					calendar.setTime(timeStamp);
				} else {
					calendar.add(Calendar.MONTH, toRes);
				}
				m_to = calendar.getTimeInMillis();
				if(m_to < m_from) {
					m_to = m_from;
					m_from = getFirstInMonth(calendar.getTime());
					break;
				} else {
					m_to = getLastInMonth(calendar.getTime());
					break;
				}
			case CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS:
				if(toRes == 0) { //aktuelles Datum
					calendar.setTime(timeStamp);
				} else {
					calendar.add(Calendar.MONTH, (toRes - 1) * 3 + 2);
				}
				m_to = calendar.getTimeInMillis();
				if(m_to < m_from) {
					m_to = m_from;
					m_from = getFirstInQuarter(calendar.getTime());
					break;
				} else {
					m_to = getLastInQuarter(calendar.getTime());
					break;
				}

			case CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS:
				if(toRes == 0) { //aktuelles Datum
					calendar.setTime(timeStamp);
				} else {
					calendar.add(Calendar.YEAR, toRes);
				}
				m_to = calendar.getTimeInMillis();
				if(m_to < m_from) {
					m_to = m_from;
					m_from = getFirstInMonth(calendar.getTime());
					break;
				} else {
					m_to = getLastInMonth(calendar.getTime());
					break;
				}

			case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1:
				;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2:
				calendar.setTime(timeStamp);
				m_to = calendar.getTimeInMillis();
				checkWhoOlder();
				break;

		}

	}

	/**
	 * sets start und end Werte für to als datum, from - als Monat
	 */
	private synchronized void setToDateFromMonth(Date from)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		if(m_to < m_from) {
			m_from = m_to;
			calendar.setTime(from);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			m_to = calendar.getTimeInMillis();
		} else {
			calendar.setTime(from);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			m_from = calendar.getTimeInMillis();
		}
	}

	private synchronized long getFirstInQuarter(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		int month = (calendar.get(Calendar.MONTH) / 3) * 3;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTimeInMillis();
	}

	private synchronized long getLastInQuarter(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH) / 3 * 3 + 2;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTimeInMillis();
	}

	private synchronized long getFirstInMonth(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTimeInMillis();
	}

	private synchronized long getLastInMonth(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTimeInMillis();
	}

	private synchronized long getFirstInYear(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTimeInMillis();
	}

	private synchronized long getLastInYear(Date date)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTimeInMillis();
	}

	private void setToDateFromQuarter(Date from)
	{
		if(m_to < m_from) {
			m_from = m_to;
			m_to = getLastInQuarter(from);
		} else {
			m_from = getFirstInQuarter(from);
		}
	}

	private void setToDateFromYear(Date from)
	{
		if(m_to < m_from) {
			m_from = m_to;
			m_to = getLastInYear(from);
		} else {
			m_from = getFirstInYear(from);
		}
	}

	public synchronized void printIntervalValues()
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date(this.m_from));
		System.out.println("from " + this.m_fromValue + " "
			+ String.valueOf(calendar.get(Calendar.YEAR))
			+ String.valueOf(calendar.get(Calendar.MONTH) + 1)
			+ String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		calendar.setTime(new Date(this.m_to));
		System.out.println("to " + this.m_toValue + " "
			+ String.valueOf(calendar.get(Calendar.YEAR))
			+ String.valueOf(calendar.get(Calendar.MONTH) + 1)
			+ String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
	}

	public synchronized static void printTime(Date dt)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(dt);
		System.out.println(
			String.valueOf(calendar.get(Calendar.YEAR))
			+ String.valueOf(calendar.get(Calendar.MONTH) + 1)
			+ String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

	}

	private synchronized long getQuarterDate(int quarter, int year, boolean last)
	{
                Calendar calendar = GregorianCalendar.getInstance();
		if(quarter >= 4) {
			year += (quarter - 1) / 4;
			if(quarter != 4) {
				quarter = quarter - (quarter / 4 * 4 + 1);
			}
		} else if(quarter < 0) {
			quarter = Math.abs(quarter);
			year -= quarter / 4 + 1;
			quarter = 5 - (quarter - (quarter - 1) / 4 * 4);
		}
		calendar.set(Calendar.YEAR, year);
		if(last) {
			calendar.set(Calendar.MONTH, (quarter - 1) * 3 + 2);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			return calendar.getTimeInMillis();
		} else {
			if(quarter == 0) {
				quarter = 1;
			}
			calendar.set(Calendar.MONTH, (quarter - 1) * 3);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return calendar.getTimeInMillis();
		}
	}
        
        public boolean isCaseInterval() throws Exception
        {
            return m_critFrom == CRGRuleGrouperStatics.INDEX_INTERVAL_CURRENT_CASE
			&& m_critTo == CRGRuleGrouperStatics.INDEX_INTERVAL_CASE;
        }
/**
 * während der Ermittlung des Intervalls für aktueller Fall - Fall ermittelte Indizes
 * in der Sortierung der Fälle die in den Intervall reinpassen 
 * @return 
 */
        public int[] getRetSortInds()
        {
            return retSortInds;
        }

	public void checkIntervalValues(Date timeStamp) throws Exception
	{
            retSortInds = null;
                Calendar calendar = GregorianCalendar.getInstance();
		int fromRes = 0;
		Date dFrom = null;
		try {
			fromRes = Integer.parseInt(m_fromValue); // für den Fall der Anzahl der Tage, Monate, Quartale, Jahre
		} catch(Exception e) {
		  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Exception wird unterdrückt! */
		}
		int toRes = 0;
		try {
			toRes = Integer.parseInt(m_toValue); // für den Fall der Anzahl der Tage, Monate, Quartale, Jahre
		} catch(Exception e) {}
		if(timeStamp == null) {
			timeStamp = new Date(); // aktuelles Datum
		}
		timeStamp = getFullDate(timeStamp);
		calendar.setTime(timeStamp);
		switch(m_critFrom) {
			case CRGRuleGrouperStatics.INDEX_INTERVAL_NOW: {
				this.m_from = getFullDate(new Date()).getTime();
				calendar.setTime(getFullDate(new Date(m_from)));
				checkToPlainTimeValue(timeStamp, calendar.getTime());
				break;
			}
			case CRGRuleGrouperStatics.INDEX_INTERVAL_DATE: {
				m_from = getDateFromString(this.m_fromValue).getTime();
				checkToPlainTimeValue(timeStamp, new Date(m_from));
				break;

			}
			case CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS:
				calendar.add(Calendar.DAY_OF_YEAR, fromRes);
				m_from = calendar.getTime().getTime();
				checkToPlainTimeValue(timeStamp, calendar.getTime());
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS:
				calendar.add(Calendar.MONTH, fromRes);
				m_from = calendar.getTimeInMillis();
				dFrom = calendar.getTime();
				switch(m_critTo) {
					case CRGRuleGrouperStatics.INDEX_INTERVAL_NOW:
						m_to = getFullDate(new Date()).getTime();
						setToDateFromMonth(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DATE:
						m_to = getDateFromString(this.m_toValue).getTime();
						setToDateFromMonth(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.DAY_OF_YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						setToDateFromMonth(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.MONTH, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getLastInMonth(calendar.getTime());
							m_to = getFirstInMonth(dFrom);
							break;
						} else {
							m_to = getLastInMonth(calendar.getTime());
							m_from = getFirstInMonth(dFrom);
							break;
						}
					case CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.MONTH, toRes * 3);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInQuarter(calendar.getTime());
							m_to = getLastInMonth(dFrom);

						} else {
							m_to = getLastInQuarter(calendar.getTime());
							m_from = getFirstInMonth(dFrom);

						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInYear(calendar.getTime());
							m_to = getLastInMonth(dFrom);
							break;
						} else {
							m_to = getLastInYear(calendar.getTime());
							calendar.setTime(dFrom);
							calendar.set(Calendar.DAY_OF_MONTH, 1);
							m_from = getFirstInMonth(dFrom);
							break;
						}
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1:
						;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2:
						m_to = timeStamp.getTime();
						setToDateFromMonth(dFrom);
						break;
				}
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS:
				int month = calendar.get(Calendar.MONTH);
				int timeStampYear = calendar.get(Calendar.YEAR);
				int stampQuarter = month / 3 + 1; // 1, 2, 3, 4
				int fromQuarter = stampQuarter + fromRes;
				calendar.add(Calendar.MONTH, fromRes * 3);
				dFrom = calendar.getTime();
				m_from = dFrom.getTime();
				switch(m_critTo) {
					case CRGRuleGrouperStatics.INDEX_INTERVAL_NOW:
						m_to = getFullDate(new Date()).getTime();
						setToDateFromQuarter(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DATE:
						m_to = getDateFromString(this.m_toValue).getTime();
						setToDateFromQuarter(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.DAY_OF_YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						setToDateFromQuarter(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.MONTH, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInMonth(calendar.getTime());
							m_to = getLastInQuarter(dFrom);
						} else {
							m_to = getLastInMonth(calendar.getTime());
							m_from = getFirstInQuarter(dFrom);
						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS:
						int toQuarter = stampQuarter;
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							toQuarter = fromQuarter + toRes;
						}
						calendar.add(Calendar.MONTH, toRes * 3);
						m_to = calendar.getTimeInMillis();
						if(m_from > m_to) {
							int tmpQuarter = toQuarter;
							toQuarter = fromQuarter;
							fromQuarter = tmpQuarter;
						}
						m_to = getQuarterDate(toQuarter, timeStampYear, true);
						m_from = getQuarterDate(fromQuarter, timeStampYear, false);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInYear(calendar.getTime());
							m_to = getLastInQuarter(dFrom);
						} else {
							m_to = getLastInYear(calendar.getTime());
							m_from = getFirstInQuarter(dFrom);
						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1:
						;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2:
						m_to = timeStamp.getTime();
						setToDateFromQuarter(dFrom);
						break;
				}
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS:
				;
				calendar.add(Calendar.YEAR, fromRes);
				dFrom = calendar.getTime();
				m_from = dFrom.getTime();
				switch(m_critTo) {
					case CRGRuleGrouperStatics.INDEX_INTERVAL_NOW:
						m_to = getFullDate(new Date()).getTime();
						setToDateFromYear(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DATE:
						m_to = getDateFromString(this.m_toValue).getTime();
						setToDateFromYear(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.DAY_OF_YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						setToDateFromYear(dFrom);
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.MONTH, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInMonth(calendar.getTime());
							m_to = getLastInYear(dFrom);
						} else {
							m_to = getLastInMonth(calendar.getTime());
							m_from = getFirstInYear(dFrom);
						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS:
						if(toRes == 0) { //aktuelles Datum
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.MONTH, toRes * 3);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInQuarter(calendar.getTime());
							m_to = getLastInYear(dFrom);
						} else {
							m_to = getLastInQuarter(calendar.getTime());
							m_from = getFirstInYear(dFrom);
						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS:
						if(toRes == 0) { //aktuelle jahr
							calendar.setTime(timeStamp);
						} else {
							calendar.add(Calendar.YEAR, toRes);
						}
						m_to = calendar.getTimeInMillis();
						if(m_to < m_from) {
							m_from = getFirstInYear(calendar.getTime());
							m_to = getLastInYear(dFrom);
						} else {
							m_to = getLastInYear(calendar.getTime());
							m_from = getFirstInYear(dFrom);
						}
						break;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1:
						;
					case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2:
						m_to = timeStamp.getTime();
						setToDateFromYear(dFrom);
						break;

				}
				break;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1:
				;
			case CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2: {
				m_from = timeStamp.getTime();
				checkToPlainTimeValue(timeStamp, new Date(m_from));
				break;
			}

		}
	}


	public boolean hasTimeStamp1()
	{
		return(m_critFrom == CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1
			|| m_critTo == CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1);
	}

	public boolean hasTimeStamp2()
	{
		return(m_critFrom == CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2
			|| m_critTo == CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2);
	}
        
        @Override
        public String toString(){
            return m_intervalAsString;
        }

}
