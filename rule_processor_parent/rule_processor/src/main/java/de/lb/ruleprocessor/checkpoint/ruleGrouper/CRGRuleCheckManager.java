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


import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRuleTypes;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements Method getStringArrayFromRuleTables which the class CpxCRGRule
 * uses by applying of table methods(in table, not in table, multiple in table
 * and so on)
 *
 * @author gerschmann
 */
public class CRGRuleCheckManager implements CRGRuleManagerIF {

    /**
     * saves the contents of rule tables
     */
    private static final RuleTablesManager mRuleTablesManager = new RuleTablesManager();

    /**
     * CpxCRGRule uses this method when there is any table operation in the rule
     * syntax. It gets the table content for rule according to the pool- and
     * year identification, saved in this rule For rule check are pool- und year
     * name not required
     *
     * @param pTableName table name
     * @param pPoolIdent rule pool ident
     * @param pYear rule pool year
     * @return content of the table as an array of strings or null when the
     * table is not found
     * @throws Exception error
     */
    @Override
    public String[] getStringArrayFromRuleTables(String pTableName, String pPoolIdent, int pYear) throws Exception {

        return mRuleTablesManager.getStringArrayFromRuleTables(pTableName);
    }

    public void resetTables() {
        mRuleTablesManager.resetAllTables();
    }

   public  void fillRuleTables(Map<String, String> pRuleTables) {
        resetTables();
        if (pRuleTables == null || pRuleTables.isEmpty()) {
            return;
        }
        Set<String> tabNames = pRuleTables.keySet();
        tabNames.forEach((key) -> {
            mRuleTablesManager.addRuleTable(key, pRuleTables.get(key));
        });
    }

    @Override
    public int[] getIntArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        return mRuleTablesManager.getIntArrayFromRuleTables(tableName);
    }

    @Override
    public double[] getDoubleArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        return mRuleTablesManager.getDoubleArrayFromRuleTables(tableName);

    }

    @Override
    public long[] getLongArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        return mRuleTablesManager.getLongArrayFromRuleTables(tableName);
    }

    @Override
    public Date[] getDateArrayFromRuleTables(String tableName, String ruleIdent, int year) throws Exception {
        return mRuleTablesManager.getDateArrayFromRuleTables(tableName);
    }

    @Override
    public CRGRuleTypes getRulesErrorTypeByText(String m_errorTypeText) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CRGRuleTypes getRulesErrorTypeByText(String m_errorTypeText, List ruleTypes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] getTableStringValues(String tableName, String ruleIdent, int year) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getTableIntValues(String tableName, String ruleIdentififier, int year) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date[] getTableDateValues(String val, String m_ruleIdentififier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long[] getTableLongValues(String val, String m_ruleIdentififier, int m_year) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getTableDoubleValues(String tableName, String ruleIdentififier, int year) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CRGRule[] getAllRules() throws Exception {
        return new CRGRule[0];
    }


}
