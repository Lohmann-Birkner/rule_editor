package de.lb.ruleprocessor.checkpoint.ruleGrouper.data;

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

import de.lb.ruleprocessor.checkpoint.server.db.OSObject;

public class CRGRulePool extends OSObject implements java.io.Serializable
{
	public static final String TABLE_NAME = "crg_rulepools";
	public static final String GLOBAL_POOL_NAME = "global";
	public String crgpl_identifier;
	public int crgpl_year;
	public boolean crgpl_active;
	public java.sql.Date crgpl_active_dtm;
	public java.sql.Date crgpl_deactive_dtm;
	public long crgpl_user_id;

	private CRGPoolRight m_right = null;

	public CRGRulePool()
	{
		super(TABLE_NAME, true);
	}

	public String toString(){
		String tt = String.valueOf(crgpl_year);
		String cmp = tt;
		if(crgpl_year == 0){
			tt = GLOBAL_POOL_NAME;
			cmp = "0";
		}
		if (crgpl_identifier!=null && !crgpl_identifier.equals(cmp)){
			tt = tt + " - " + crgpl_identifier;
		}
		return tt;
	}

	public void setPoolRight(CRGPoolRight right){
		m_right = right;
	}

	public CRGPoolRight getPoolRight(){
		return m_right;
	}

	public boolean hasPoolRight(){
		return m_right!=null;
	}


}
