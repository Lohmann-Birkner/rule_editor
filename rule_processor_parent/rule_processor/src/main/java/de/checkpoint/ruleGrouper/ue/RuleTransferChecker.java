/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.ruleGrouper.ue;


import de.checkpoint.ruleGrouper.CRGRule;
import de.checkpoint.server.data.caseRules.DatRuleElement;
import de.checkpoint.server.data.caseRules.DatRuleOp;
import de.checkpoint.server.data.caseRules.DatRuleRoot;
import de.checkpoint.server.data.caseRules.DatRuleVal;
import de.checkpoint.server.exceptions.ExcException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author gerschmann
 */
public class RuleTransferChecker {
    
    private List<CRGUERuleICD> m_lstICD = new ArrayList<>();
    private List<CRGUERuleOPS> m_lstOPS = new ArrayList<>();
    private Map <String, List<String>> m_tables  = new HashMap<>();

    public  RuleTransferChecker(List<CRGUERuleICD> lstICD, List<CRGUERuleOPS> lstOPS, Map <String, List<String>> tables){
        m_lstICD = lstICD;
        m_lstOPS = lstOPS;
        m_tables = tables;
    }

    	public void checkRuleUE(CRGRule rule){
		DatRuleElement ele = rule.getRootElement();
		if (ele!=null){
			checkRuleElementUE(ele.getElements(), rule);
		}
	}

        	protected void checkRuleElementUE(Vector elements, CRGRule rule){
		for (int i=0, n=elements.size(); i<n; i++){
			DatRuleRoot child = (DatRuleRoot) elements.get(i);
			if (child instanceof DatRuleElement){
				checkRuleElementUE(((DatRuleElement)child).getElements(), rule);
			}
			else if (child instanceof DatRuleOp){

			}else if (child instanceof DatRuleVal){
				DatRuleVal val = (DatRuleVal)child;
				if (val.m_crit!=null){
					checkRuleElementUEICD(val, rule);
				}
			}
		}
	}

	protected void checkRuleElementUEICD(DatRuleVal val, CRGRule rule){
		if (val.m_crit.indexOf("iagnose")>0){
			if (val.m_val!=null){
				if (val.m_op!=null && val.m_op.indexOf("@")>=0){
					checkRuleTableDiagnose(val.m_val, val, m_lstICD, rule);
				}else
					checkRuleElementDiagnose(val.m_val, val, m_lstICD, rule, false);
			}
		}
		if (val.m_crit.indexOf("rozedur")>0){
			if (val.m_val!=null){
				if (val.m_op!=null && val.m_op.indexOf("@")>=0){
					checkRuleTableProcedure(val.m_val, val, m_lstOPS, rule);
				}else
					checkRuleElementProcedure(val.m_val, val, m_lstOPS, rule, false);
			}
		}
	}

	private void checkRuleTableDiagnose(String tableName, DatRuleVal val, List<CRGUERuleICD> lstICD, CRGRule rule){
		try{
			List<String> tblValues = m_tables.get(tableName.replaceAll("'", ""));
			String codeVal;
			int n = tblValues!=null ? tblValues.size() : 0;
			for (int i=0; i<n; i++){
				codeVal = tblValues.get(i).toString();
				checkRuleElementDiagnose(codeVal, val, lstICD, rule, true);
			}
		}catch (Exception ex){
			ExcException.createException(ex);
		}
	}

	private void checkRuleElementDiagnose(String code, DatRuleVal val,  List<CRGUERuleICD> lstICD, CRGRule rule, boolean isTable){
		CRGUERuleICD icdue;
		code = code.replaceAll("%", "");
		code = code.replaceAll("'", "");
		for (int i=0, n=lstICD.size(); i<n; i++){
			icdue = (CRGUERuleICD) lstICD.get(i);
			if (icdue.codeOld.toUpperCase(). indexOf(code.toUpperCase())>=0){
				rule.addUEMessages(new CRGRuleUEMessage(val, icdue, code, isTable));
			}
			if (code.toUpperCase().indexOf(icdue.codeOld.toUpperCase())>=0){
				rule.addUEMessages(new CRGRuleUEMessage(val, icdue, code, isTable));
			}
		}
	}

	private void checkRuleTableProcedure(String tableName, DatRuleVal val,  List<CRGUERuleOPS> lstOPS, CRGRule rule){
		try{
			List<String> tblValues = m_tables.get(tableName);
			String codeVal;
			int n = tblValues!=null ? tblValues.size() : 0;
			for (int i=0; i<n; i++){
				codeVal = tblValues.get(i).toString();
				checkRuleElementProcedure(codeVal, val, lstOPS, rule, true);
			}
		}catch (Exception ex){
			ExcException.createException(ex);
		}
	}

	private void checkRuleElementProcedure(String code, DatRuleVal val,  List<CRGUERuleOPS> lstOPS, CRGRule rule, boolean isTable){
		CRGUERuleOPS opsue;
		code = code.replaceAll("%", "");
		code = code.replaceAll("'", "");
		for (int i=0, n=lstOPS.size(); i<n; i++){
			opsue = (CRGUERuleOPS) lstOPS.get(i);
			if (opsue.codeOld.toUpperCase().indexOf(code.toUpperCase())>=0){
				rule.addUEMessages(new CRGRuleUEMessage(val, opsue, code, isTable));
			}
		}
	}

    
}
