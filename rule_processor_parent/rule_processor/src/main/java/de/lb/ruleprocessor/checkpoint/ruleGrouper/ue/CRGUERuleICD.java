package de.lb.ruleprocessor.checkpoint.ruleGrouper.ue;

import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;

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
public class CRGUERuleICD extends CRGUECode
{

	public CRGUERuleICD()
	{
	}


	public void loadValues(String[] values){
		try{
			m_vil = values.length;
			if (m_vil>0){
				codeOld = values[0];
			}
			if (m_vil>1){
				codeNew = values[1];
			}
			if (m_vil>2){
				ueOld = values[2];
				isOldUE = ueOld.equals("A");
			}
			if (m_vil>3){
				ueNew = values[3];
				isNewUE = ueOld.equals("A");
			}
		}catch (Exception ex){
			ExcException.createException(ex);
		}
	}

	public boolean isChangeValue(){
		if (codeOld!=null){
			if (codeNew!=null){
				if (codeOld.equals(codeNew)){
					return false;
				}else
					return true;
			}else
				return true;
		}else{
				return false;
			}
	}

	public String toString(){
		return "ICD " + this.codeOld + " -> " + this.codeNew;
	}

}
