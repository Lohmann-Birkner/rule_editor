/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import java.io.Serializable;

/**
 *
 * @author gerschmann
 */
public class CRGRisk extends Object implements Serializable{
    private String m_riskName;
    private String m_riskWastePercentValue;
    private String m_riskAuditPercentValue;
    private String m_riskDefaultWasteValue;
    private String m_riskComment;
    
    public CRGRisk(){
        
    }

    CRGRisk(String riskName) {
        m_riskName = riskName;
    }

    public String getRiskName() {
        return m_riskName == null?"":m_riskName;
    }

    public String getRiskWastePercentValue() {
        return m_riskWastePercentValue == null?"":m_riskWastePercentValue;
    }

    public String getRiskDefaultWasteValue() {
        return m_riskDefaultWasteValue == null?"":m_riskDefaultWasteValue;
    }

    public void setRiskName(String riskName) {
        this.m_riskName = riskName;
    }

    public void setRiskWastePercentValue(String riskPercentValue) {
        this.m_riskWastePercentValue = riskPercentValue;
    }

    public void setRiskDefaultWasteValue(String riskDefaultWaistValue) {
        this.m_riskDefaultWasteValue = riskDefaultWaistValue;
    }

    public String getRiskComment() {
        return m_riskComment == null?"":m_riskComment;
    }

    public void setRiskComment(String riskComment) {
        this.m_riskComment = riskComment;
    }

    public String getRiskAuditPercentValue() {
        return m_riskAuditPercentValue;
    }

    public void setRiskAuditPercentValue(String riskAuditPercentValue) {
        this.m_riskAuditPercentValue = riskAuditPercentValue;
    }
    
}
