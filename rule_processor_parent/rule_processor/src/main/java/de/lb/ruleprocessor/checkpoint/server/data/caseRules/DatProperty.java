/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

/**
 * Eigenschaften für einen Method
 * @author gerschmann
 */
public class DatProperty {
    
    String m_workText;//interner Name für die Eigenschaft
    String m_displayText;// externer Name der Eigenschaft
    
    public DatProperty(String dispText, String workTxt)
    {
        m_workText = workTxt;
        m_displayText = dispText;
    }
    
    public String toString()
    {
        return m_displayText;
    }
    
    public String getWorkText()
    {
        return m_workText;
    }

}
