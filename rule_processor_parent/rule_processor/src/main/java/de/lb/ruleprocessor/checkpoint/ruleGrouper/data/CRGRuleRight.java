package de.lb.ruleprocessor.checkpoint.ruleGrouper.data;


import de.lb.ruleprocessor.checkpoint.server.db.OSObject;

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
public class CRGRuleRight extends OSObject
{
	public static final String TABLE_NAME = "crg_rulerights";

	public long crg_rulepools_id;
	public long role_id;
	public boolean crgrr_isgroup;
	public String crgrr_key;
	public String crgrr_value;

	private int m_editState = -1;

	public CRGRuleRight()
	{
		super(TABLE_NAME, false);
	}

	public boolean isEnabled(){
		if (m_editState<0){
			if (crgrr_value!=null && crgrr_value.equals("false"))
				m_editState = 0;
			else
				m_editState = 1;
		}
		return m_editState==1;
	}

}
