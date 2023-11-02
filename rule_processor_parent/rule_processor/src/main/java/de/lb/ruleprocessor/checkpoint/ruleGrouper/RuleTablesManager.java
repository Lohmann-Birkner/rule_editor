/*
 * Copyright (c) 2019 Lohmann & Birkner.
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
 *    2019  gerschmann - initial API and implementation and/or initial documentation
 */
package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerschmann
 */
public class RuleTablesManager {

    private static final Logger LOG = Logger.getLogger(RuleTablesManager.class.getName());

    private static final Map<String, String[]> mTableIdent2Table = new HashMap<>(); // for all tables
    private static final Map<String, int[]> mIntIdent2Table = new HashMap<>();
    private static final Map<String, double[]> mDoubleIdent2Table = new HashMap<>();
    private static final Map<String, long[]> mLongIdent2Table = new HashMap<>();
    private static final Map<String, Date[]> mDateIdent2Table = new HashMap<>();

    public void resetAllTables() {
        mTableIdent2Table.clear();
        mIntIdent2Table.clear();
        mDoubleIdent2Table.clear();
        mLongIdent2Table.clear();
        mDateIdent2Table.clear();

    }

    public synchronized void addRuleTable(String key, String cpxContent) {
        String[] content = mTableIdent2Table.get(key);
        if (content != null) {
// table is already saved                    
            return;
        } else {

            if (cpxContent == null || cpxContent.isEmpty()) {
                mTableIdent2Table.put(key, new String[0]);
            } else {
                String[] tabArr = cpxContent.split(",");
                Arrays.sort(tabArr);
                mTableIdent2Table.put(key, tabArr);
            }
        }

    }

    public String[] getStringArrayFromRuleTables(String key) {
//        try {
        String[] content = mTableIdent2Table.get(key);
        if (content == null) {
// we got already tables from DB when we got CpxRules. If it is not there, than it was not saved when rules were saved
            return null;
        } else {
            return content;
        }
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "error on reading rule table " + tableName + " for pool " + poolIdent + " and year " + String.valueOf(year), ex);
//            return null;
//        }
    }

    public int[] getIntArrayFromRuleTables(String key) throws Exception {
        int[] content = mIntIdent2Table.get(key);
        if (content == null) {
            String[] strContent = getStringArrayFromRuleTables(key);
            if (strContent == null) {
                content = new int[0];
                mIntIdent2Table.put(key, content);
                LOG.log(Level.INFO, "for table {0} no content found", key);
                return content;
            }
            content = new int[strContent.length];
            for (int i = 0; i < strContent.length; i++) {
                //bei falscher Regeleingabe in der Tabelle Strings statt integer
                if (strContent[i].matches("\\d*")) {
                    content[i] = Integer.parseInt(strContent[i]);
                } else {
                    content[i] = -Integer.MAX_VALUE;
                }
            }
            mIntIdent2Table.put(key, content);
        }
        return content;

    }

    public double[] getDoubleArrayFromRuleTables(String key) throws Exception {
        double[] content = mDoubleIdent2Table.get(key);
        if (content == null) {
            String[] strContent = getStringArrayFromRuleTables(key);
            if (strContent == null) {
                content = new double[0];
                mDoubleIdent2Table.put(key, content);
                LOG.log(Level.INFO, "for table {0} no content found", key);
                return content;
            }
            content = new double[strContent.length];
            for (int i = 0; i < strContent.length; i++) {
                try {
                    content[i] = Double.parseDouble(strContent[i]);
                } catch (NumberFormatException ex) {
                    LOG.log(Level.SEVERE, "error by parsing double table value " + strContent[i], ex);
                    content[i] = -Double.MAX_VALUE;
                }

            }
            mDoubleIdent2Table.put(key, content);
        }
        return content;

    }

    public synchronized long[] getLongArrayFromRuleTables(String key) throws Exception {
        SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        SimpleDateFormat m_simpleTimeFormat = new SimpleDateFormat("HH:mm");
        long[] content = mLongIdent2Table.get(key);
        if (content == null) {
            String[] strContent = getStringArrayFromRuleTables(key);
            if (strContent == null) {
                content = new long[0];
                mLongIdent2Table.put(key, content);
                LOG.log(Level.INFO, "for table {0} no content found", key);
                return content;
            }
            content = new long[strContent.length];
            for (int i = 0; i < strContent.length; i++) {

// the long values are built from date values, that are saved in tables
                if (strContent[i].indexOf(':') >= 0) {
                    content[i] = m_simpleTimeFormat.parse(strContent[i]).getTime();
                } else {
                    content[i] = m_simpleDateFormat.parse(strContent[i]).getTime();
                }

            }
            mLongIdent2Table.put(key, content);
        }
        return content;
    }

    public synchronized Date[] getDateArrayFromRuleTables(String key) throws Exception {
        SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat m_simpleTimeFormat = new SimpleDateFormat("HH:mm");
        Date[] content = mDateIdent2Table.get(key);
        if (content == null) {
            String[] strContent = getStringArrayFromRuleTables(key);
            if (strContent == null) {
                content = new Date[0];
                mDateIdent2Table.put(key, content);
                LOG.log(Level.INFO, "for table {0} no content found", key);
                return content;
            }
            content = new Date[strContent.length];
            for (int i = 0; i < strContent.length; i++) {
                if (strContent[i].indexOf(':') >= 0) {
                    content[i] = m_simpleTimeFormat.parse(strContent[i]);
                } else {
                    content[i] = m_simpleDateFormat.parse(strContent[i]);
                }

            }
            mDateIdent2Table.put(key, content);

        }
        return content;
    }
}
