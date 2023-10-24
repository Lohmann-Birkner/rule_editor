package de.checkpoint.ruleGrouper.ue;


import de.checkpoint.server.exceptions.ExcException;

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
public abstract class CRGUECode extends CRGUERule
{

	public String codeOld = null;
	public String codeNew = null;
	public String ueOld = null;
	public String ueNew = null;

	public boolean isOldUE = false;
	public boolean isNewUE = false;

	protected int m_vil = 0;

	public abstract void loadValues(String[] values);

	public CRGUECode()
	{
	}

	public void loadValues(String line){
		try{
			String[] vals = line.split(";");
			loadValues(vals);
		}catch (Exception ex){
			ExcException.createException(ex);
		}
	}


}
