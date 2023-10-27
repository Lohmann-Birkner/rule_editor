package de.lb.ruleprocessor.checkpoint.ruleGrouper;

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
public class SelectCritValues
{
	Object m_value = null;
	String m_comment;
	String m_secStringValue = "";
	public SelectCritValues(Object value, String comment)
	{
		m_value = value;
		m_comment = comment;
		int pos1 = m_comment.indexOf("("), pos2 = m_comment.indexOf(" - ");
		if(pos1 > 0 && pos2 > 0 && pos2 > pos1) {
			m_secStringValue = m_comment.substring(0, pos1);
		}
	}

	public String toString()
	{
		return m_comment;
	}

	public boolean equals(Object obj)
	{
		if(obj == null) {
			return false;
		}
		if((m_value == null) || (obj instanceof SelectCritValues && ((SelectCritValues)obj).m_value == null)) {
			return false;
		}
		if(obj instanceof SelectCritValues)
			return m_value.equals(((SelectCritValues)obj).m_value);
		return false;
	}

	/**
	 * liefert value as String
	 * @return String
	 */
	public String getStringValue()
	{
		if(m_value instanceof String) {
			return(String)m_value;
		}
		if(m_value instanceof Integer) {
			return((Integer)m_value).toString();
		}
		if(m_value instanceof Double) {
			return((Double)m_value).toString();
		}
		return "";
	}

	public boolean isAValue(String str)
	{
		try {
			if(m_value instanceof String) {
				return((String)m_value).indexOf(str) == 0;
			}
			if(m_value instanceof Integer) {
				return((Integer)m_value).intValue() == Integer.parseInt(str);
			}
			if(m_value instanceof Double) {
				return((Double)m_value).doubleValue() == Double.parseDouble(str);
			}
		} catch(Exception e) {}
		return false;
	}

	public String getSecStringValue()
	{
		return m_secStringValue;
	}

	public boolean isASecondValueValue(String str)
	{
		return m_secStringValue.equals(str);
	}
}
