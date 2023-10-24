/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.ruleGrouper;

/**
 *
 * @author gerschmann
 */
public class MethodCriterionEntry extends CriterionEntry{
    
    public MethodCriterionEntry(int groupIndex, String workText, String displayText, String descrText, 
		int type, int index, int depType, int dependant)
	{
		super(groupIndex, groupIndex, workText, displayText, descrText, CriterionEntry.CRIT_RULE_METHOD, type, index, false, 0);
                m_method_dependency_index = dependant;
                m_method_dependency_type = depType;
	}
    
}
