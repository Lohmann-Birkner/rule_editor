/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.server.data.caseRules;

/**
 * Realisierung der Regelmethods
 * @author gerschmann
 */
public class DatRulesMethod  extends DatRulesOperator{

    Class[] m_parameterType = null;

	public DatRulesMethod(String displayText, String workText, Class[] cls) {
		super(displayText, workText, false);
                m_parameterType = cls;
	}


	public boolean equals(Object obj)
	{
		if((obj == null) || (!(obj instanceof DatRulesMethod)))
			return false;
		return m_name.equals(((DatRulesOperator)obj).m_name);
	}

    
}
