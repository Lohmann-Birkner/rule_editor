/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.server.data.caseRules.ruleElement;

/**
 *
 * @author gerschmann
 */
public interface Document {

    public Element createElement(String ELEMENT_RISK);

    public NodeList getElementsByTagName(String ELEMENT_RULES);
    

    public org.w3c.dom.Document getXmlDocument();
    
}
