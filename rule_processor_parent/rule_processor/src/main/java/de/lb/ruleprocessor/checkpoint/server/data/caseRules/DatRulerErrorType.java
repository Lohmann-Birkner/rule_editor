package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

public class DatRulerErrorType implements java.io.Serializable
{
	public static final String SHORT_TEXT = "shortText";
	public static final String DISPLAY_TEXT = "displayText";
	public static final String TYPE = "type";
	public static final String USER_ID = "userid";
	public static final String ROLE_ID = "roleid";

	public int id = -1;
	public String shortText = "";
	public String displayText = "";
	public int type;
	public long userid;
//    public long roleid;
	public int m_orgID = 0;
	public boolean isForAll = false;

	private long[] m_roles = null;

	public DatRulerErrorType()
	{
	}

	public int getIdent()
	{
		return(int)id;
	}

	public String getText()
	{
		return shortText;
	}

	public String toString()
	{
		return displayText;
	}

	public void setRoleIds(long[] array)
	{
		if(isForAll)
			return;
		m_roles = array;
		for(int i = 0; i < m_roles.length; i++)
			if(m_roles[i] == 0) {
				isForAll = true;
				break;
			}
	}

	public String getRolesAsString()
	{

		String tt = "";
		if(m_roles != null) {
			for(int i = 0; i < m_roles.length; i++) {
				if(m_roles[i] >= 0)
					tt += String.valueOf(m_roles[i]) + ",";
			}
			if(tt.endsWith(","))
				tt = tt.substring(0, tt.length() - 1);
		}
		return tt;
	}

	public boolean hasRole(long role)
	{
		try{
			for(int i = 0; i < m_roles.length; i++) {
				if(m_roles[i] == role)
					return true;
			}
		}catch(NullPointerException e){};
		return false;
	}

	public void addRoleId(long roleID)
	{
		if(hasRole(roleID))
			return;
		if(m_roles == null)
			m_roles = new long[1];
		else
			m_roles = new long[m_roles.length + 1];
		m_roles[m_roles.length - 1] = roleID;
	}

	public void removeRole(long roleID)
	{
		if(!hasRole(roleID))
			return;
		long[] roles = m_roles;
		m_roles = new long[m_roles.length - 1];
		int j = 0;
		for(int i = 0; i < roles.length; i++){
			if(roles[i] != roleID){
				m_roles[j] = roles[i];
				j++;
			}
		}
	}


	public boolean hasRoles()
	{
		try{
			return m_roles.length > 0;

		}catch(Exception e){};
		return false;
	}

	public boolean equals(DatRulerErrorType ert)
	{
		return (this.displayText.equalsIgnoreCase(ert.displayText));
	}

}
