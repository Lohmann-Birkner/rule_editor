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
public interface Element {
    
    public String getAttribute(String pAttrName);
    
    public NodeList getElementsByTagName(String pElementName);

    public void setAttribute(String pAttrName, String pAttrValue);

    public void removeChild(Node node);

    public void appendChild(Element riskArea);


}