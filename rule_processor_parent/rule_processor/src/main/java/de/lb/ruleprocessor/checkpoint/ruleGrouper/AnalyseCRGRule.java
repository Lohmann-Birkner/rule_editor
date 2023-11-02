/*
 * Copyright (c) 2018 Lohmann & Birkner.
 * NOTICE:  All information contained herein is, and remains
 * the property of Lohmann & Birkner and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Lohmann & Birkner
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Lohmann & Birkner.
 * http://www.lohmann-birkner.de/de/index.php
 *
 * Contributors:
 *    2018  gerschmann - initial API and implementation and/or initial documentation
 */
package de.lb.ruleprocessor.checkpoint.ruleGrouper;


import java.util.Date;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 *
 * @author gerschmann
 */
public class AnalyseCRGRule extends CRGRule {

    private static final long serialVersionUID = 1L;

    private final transient CRGRuleManagerIF m_manager;
    private Map<String, String> mappingId2TableName = null;

    public AnalyseCRGRule(CRGRuleManagerIF mgr, Element ele, String identifier, int analysisGrade) {
        this(mgr, ele, identifier,  analysisGrade, null);
    }

    public AnalyseCRGRule(CRGRuleManagerIF mgr, Element ele, String identifier, int analysisGrade, Map<String, String> pMappingId2TableName) {
        super();
        m_manager = mgr;
        m_ruleIdentififier = identifier;
        setFullRuleAnalysis(analysisGrade);
        setIsCheckpoint(false);
        setCheckTables(analysisGrade != RULE_ANALYSE_NO_TABLES);
        mappingId2TableName = pMappingId2TableName;
//        synchronized (ele) {
        init(ele, null);
//        }
    }

    @Override
    protected String[] getStringArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        if (m_manager == null) {
            return super.getStringArrayFromRuleTables(tableName, ruleIdent, year);
        }
        return m_manager.getStringArrayFromRuleTables(tableName, ruleIdent, year);
    }

    @Override
    protected void setStringArrayTable(CRGRuleElement ruleEle, String val) throws Exception {
        super.setStringArrayTable(ruleEle, val);
        checkTableName(ruleEle, val);
    }

    @Override
    protected int[] getIntArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        if (m_manager == null) {
            return super.getIntArrayFromRuleTables(tableName, ruleIdent, year);
        }
        return m_manager.getIntArrayFromRuleTables(tableName, ruleIdent, year);

    }

    @Override
    protected void setIntegerArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception {
        super.setIntegerArrayTable(ruleEle, val, opType);
        checkTableName(ruleEle, val);
    }

    @Override
    protected double[] getDoubleArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        if (m_manager == null) {
            return super.getDoubleArrayFromRuleTables(tableName, ruleIdent, year);
        }
        return m_manager.getDoubleArrayFromRuleTables(tableName, ruleIdent, year);
    }

    @Override
    protected void setDoubleArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception {
        super.setDoubleArrayTable(ruleEle, val, opType);
        checkTableName(ruleEle, val);
    }

    @Override
    protected Date[] getDateArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        if (m_manager == null) {
            return super.getDateArrayFromRuleTables(tableName, ruleIdent, year);
        }
        return m_manager.getDateArrayFromRuleTables(tableName, ruleIdent, year);
    }

    @Override
    protected void setDateArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception {
        super.setDateArrayTable(ruleEle, val, opType);
        checkTableName(ruleEle, val);
    }

    @Override
    protected long[] getLongArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        if (m_manager == null) {
            return super.getLongArrayFromRuleTables(tableName, ruleIdent, year);
        }
        return m_manager.getLongArrayFromRuleTables(tableName, ruleIdent, year);
    }

    private void checkTableName(CRGRuleElement ruleEle, String tableId) {
        if (mappingId2TableName != null) {
            String tName = mappingId2TableName.get(tableId);
            ruleEle.m_tableName = tName == null ? tableId : tName;
        }
    }
}
