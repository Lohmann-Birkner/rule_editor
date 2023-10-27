package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

public class DatRulesAction implements java.io.Serializable{

	public int m_id = 0;
	public String m_workText = "";
	public String m_displayText = "";


        public DatRulesAction(){
            
        }
        
	public DatRulesAction(int id, String workText, String displayText) {
		m_id = id;
		m_workText = workText;
		m_displayText = displayText;
	}

	public String toString(){
		return m_displayText;
	}

}
