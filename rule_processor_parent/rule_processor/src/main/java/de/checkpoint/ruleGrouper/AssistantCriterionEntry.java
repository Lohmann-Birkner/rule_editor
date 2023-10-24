/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.ruleGrouper;

/**
 *
 * @author gerschmann
 */
public class AssistantCriterionEntry extends CriterionEntry{
 // diese Kriterien erscheinen nicht in der Regelbaum, sind mit einem Method verbunden
    
    public AssistantCriterionEntry(String workText, String displayText,
		int type, int index)
	{
		super(0, 0, workText, displayText, "", CriterionEntry.CRIT_RULE_ASSISTANT_TO_METHOD, type, index, false, 0); 
	}
}
