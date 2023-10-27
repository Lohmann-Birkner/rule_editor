/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.checkpoint.server.data.caseRules.ruleElement;

/**
 *
 * @author gerschmann
 */

import org.w3c.dom.*;

public class XMLElement implements Element{
    
    private final org.w3c.dom.Element  mElement;
    
    public XMLElement(org.w3c.dom.Element pElement){
        mElement = pElement;
    }

    
    @Override
    public String getAttribute(String pAttrName) {
        return mElement.getAttribute(pAttrName);
    }

    @Override
    public NodeList getElementsByTagName(String pElementName) {
        return null;
    }

    @Override
    public void setAttribute(String pAttrName, String pAttrValue) {
        mElement.setAttribute(pAttrName, pAttrValue);
    }

    @Override
    public void removeChild(Node node) {

    }

    @Override
    public void appendChild(Element riskArea) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
