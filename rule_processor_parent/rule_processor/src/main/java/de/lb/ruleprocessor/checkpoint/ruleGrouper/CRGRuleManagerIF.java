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


import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRulePool;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRuleTypes;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gerschmann
 */
public interface CRGRuleManagerIF {

    String[] getStringArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception;

    int[] getIntArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception;

    double[] getDoubleArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception;

    long[] getLongArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception;

    Date[] getDateArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception;

    public CRGRuleTypes getRulesErrorTypeByText(String m_errorTypeText)  throws Exception;

    public CRGRuleTypes getRulesErrorTypeByText(String m_errorTypeText, List ruleTypes) throws Exception;

    public String[] getTableStringValues(String tableName, String ruleIdent, int year) throws Exception;

    public int[] getTableIntValues(String tableName, String ruleIdentififier, int year) throws Exception;

    public Date[] getTableDateValues(String val, String m_ruleIdentififier) throws Exception;

    public long[] getTableLongValues(String val, String m_ruleIdentififier, int m_year) throws Exception;

    public double[] getTableDoubleValues(String tableName, String ruleIdentififier, int year)throws Exception;

    public CRGRule[] getAllRules()throws Exception;

}
