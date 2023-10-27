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
public class CRGUERuleOPS extends CRGUERuleICD
{
	public String zkOld = null;
	public String zkNew = null;

	public boolean hasOldZk = false;
	public boolean hasNewZk = false;



	public CRGUERuleOPS()
	{
	}

	public void loadValues(String[] values){
		try{
			m_vil = values.length;
			if (m_vil>0){
				codeOld = values[0];
			}
			if (m_vil>2){
				codeNew = values[2];
			}
			if (m_vil>1){
				zkOld = values[1];
				hasOldZk = zkOld.equals("J");
			}
			if (m_vil>3){
				zkNew = values[3];
				hasNewZk = zkNew.equals("J");
			}
			if (m_vil>4){
				ueOld = values[4];
				isOldUE = ueOld.equals("A");
			}
			if (m_vil>5){
				ueNew = values[5];
				isNewUE = ueOld.equals("A");
			}
		}catch (Exception ex){
			ExcException.createException(ex);
		}
	}

	public boolean isChangeValue(){
		boolean isCh = super.isChangeValue();
		if (!isCh){
			if (zkOld!=null && zkNew!=null){
				isCh = !zkOld.equals(zkNew);
			}else if (zkOld==null && zkNew!=null){
				isCh = true;
			}else if (zkOld!=null && zkNew==null){
				isCh = true;
			}
			if (isCh){
				System.out.println("");
			}
		}
		return isCh;
	}

	public String toString(){
		return "OPS " + this.codeOld + " -> " + this.codeNew;
	}

}
