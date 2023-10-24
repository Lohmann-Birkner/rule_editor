/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.server.data.caseRules;

/**
 *
 * @author gerschmann
 */
public interface DatRuleTerm {
    public String getCriteriumName();
    public String getOperation();
    public String getCriteriumValue();
    public String getConditionOperation();
    public String getConditionCritValue();
    public boolean isValid();
}
