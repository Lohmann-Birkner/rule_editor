package de.checkpoint.server.data;


import de.checkpoint.server.db.OSObject;
import de.checkpoint.server.exceptions.ExcException;
import de.checkpoint.utils.UtlDateTimeConverter;
import java.util.Date;

public class DatDrgModel extends OSObject implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	public String m_model = "";
	public String m_caption = "";
	public int m_catalogNo = 0;
	public int m_grouperID = 0;
	public int m_checkresultNo = 1;
	public int m_year = 0;
	public boolean m_hasPEPP = false;
        private Date m_peppMinDate = null;
        private int m_checkedLicense = -1;
        public int m_deltaYear = 0; // ist nur für die Zielgrouper relevant

	public DatDrgModel()
	{
		super("", false);
	}

	public DatDrgModel(DatDrgModel baseModel)
	{
		super("", false);
		m_model = baseModel.m_model;
		m_caption = baseModel.m_caption;
		m_catalogNo = baseModel.m_catalogNo;
		m_grouperID = baseModel.m_grouperID;
		m_checkresultNo = baseModel.m_checkresultNo;
		m_hasPEPP = baseModel.m_hasPEPP;
	}

	public void setValues(String groupStr)
	{
		try {
			String[] vals = groupStr.split(";");
			if(vals.length > 0) {
				m_model = vals[0];
			}
			if(vals.length > 1) {
				m_caption = vals[1];
			}
			if(vals.length > 2) {
				m_catalogNo = Integer.valueOf(vals[2]).intValue();
			}
			if(vals.length > 3) {
				m_grouperID = Integer.valueOf(vals[3]).intValue();
			}
			if(vals.length > 4) {
				m_checkresultNo = Integer.valueOf(vals[4]).intValue();
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public String toLongString()
	{
		return m_model + "; " + m_caption;
	}

	public String toString()
	{
		return m_caption;
	}

	public String toShortString()
	{
		return m_caption;
	}
        
        private Date getPeppMinDate ()
        {
            if(m_peppMinDate == null){
                m_peppMinDate = UtlDateTimeConverter.getDateFromStringWithFormat("31.12.2012 23:59", "dd.MM.yyyy hh:mm");
               
            }
            return m_peppMinDate;
        }
        
        private boolean hasPeppLic()
        {
             return true;
        }
 /*      
        public  boolean groupAutoPepp(Date adm)
        {
            return hasPeppLic() && ((m_hasPEPP && m_grouperID > 0) || ((m_grouperID == 0 || (m_hasPEPP && m_grouperID > GrouperInterfaceBasic.GDRG_DEST_OFFSET))&& adm != null && adm.after(getPeppMinDate())));
        }
*/
        public  boolean isPeppModel()
        {
            return hasPeppLic();// && (m_hasPEPP || m_grouperID == 0);// ((m_hasPEPP && m_grouperID > 0) || ((m_grouperID == 0 || (m_hasPEPP && m_grouperID > GrouperInterfaceBasic.GDRG_DEST_OFFSET))&& adm != null && adm.after(getPeppMinDate())));
        }
}
