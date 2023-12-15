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
package de.lb.ruleprocessor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Transfers result of rule validation for each rule term, which has a valid
 * mark
 *
 * @author gerschmann
 */
public class TransferRuleResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String m_mark;
    private final String m_term;
    private final boolean m_result;
    private ArrayList<TransferRuleResult> m_children = null;
    private final String m_reference;

    public TransferRuleResult(String mark, String term, boolean res) {
        this(mark, term, res, null);
    }

    public TransferRuleResult(String mark, String term, boolean res, String references) {
        m_mark = mark;
        m_term = term.replaceAll("%", "*");
        m_result = res;

        m_reference = references == null ? "" : references;

    }

    public void addChild(TransferRuleResult child) {
        if (m_children == null) {
            m_children = new ArrayList<>();
        }
        m_children.add(child);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("mark: ").append(m_mark).append("; term: ").append(m_term).append("; res: ").append(m_result);
        if (m_reference != null) {
            buffer.append("; references: ").append(m_reference);
        }
        toStringChildren(1, buffer);
        return buffer.toString();
    }

    private void toStringChildren(int level, StringBuffer buffer) {
        if (m_children == null) {
            return;
        }
        for (TransferRuleResult oneChild : m_children) {
            buffer.append("\n");
            for (int i = 0; i < level; i++) {
                buffer.append("\t");
            }
            buffer.append("mark: ").append(oneChild.m_mark).append("; term: ").append(oneChild.m_term).append("; res: ").append(oneChild.m_result);
            if (m_reference != null && m_reference.length() > 0) {
                buffer.append("; references: ").append(oneChild.m_reference);
            }
            oneChild.toStringChildren(level + 1, buffer);
        }
    }

    public String getMark() {
        return m_mark;
    }

    public String getTerm() {
        return m_term;
    }

    public boolean isResult() {
        return m_result;
    }

    public List<TransferRuleResult> getChildren() {
        return m_children;
    }

    public String getReferences() {
        return m_reference;
    }

}
