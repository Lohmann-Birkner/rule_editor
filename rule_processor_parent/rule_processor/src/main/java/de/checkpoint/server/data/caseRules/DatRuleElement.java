package de.checkpoint.server.data.caseRules;

import java.util.*;

public class DatRuleElement extends DatRuleRoot
{

	public Vector m_elements = new Vector();
	public String m_not = "false";

	public boolean m_isNot = false;


	public DatRuleElement(DatRuleElement parent)
	{
		super(parent);
	}

	public DatRuleElement(DatRuleElement parent, String mark)
	{
		super(parent);
                m_mark = mark;
	}

	public void setIsNot(boolean isNot)
	{
		m_isNot = isNot;
		m_not = m_isNot?"true":"false";
	}

	public void addElement(Object obj)
	{
		m_elements.add(obj);
	}

	public Vector getElements()
	{
		return m_elements;
	}

	public DatRuleElement copyObject(DatRuleElement parent)
	{
		DatRuleElement newRuleEle = new DatRuleElement(parent);
		newRuleEle.m_isNot = m_isNot;
		newRuleEle.m_not = m_not;
		for(int i = 0, n = m_elements.size(); i < n; i++) {
			DatRuleRoot child = (DatRuleRoot)m_elements.get(i);
			if(child instanceof DatRuleElement) {
				newRuleEle.addElement(((DatRuleElement)child).copyObject(newRuleEle));
			} else if(child instanceof DatRuleOp) {
				DatRuleOp newOp = new DatRuleOp(((DatRuleOp)child).m_op, newRuleEle);
				newRuleEle.addElement(newOp);
			} else if(child instanceof DatRuleVal) {
				DatRuleVal oldVal = (DatRuleVal)child;
				DatRuleVal newVal = new DatRuleVal(newRuleEle);
                                newVal.copyValues(oldVal);
				newRuleEle.addElement(newVal);
			}
		}
		return newRuleEle;
	}

	// proves whether this element has nested elements
	public boolean hasNestedElements()
	{
		for(int i = 0, n = m_elements.size(); i < n; i++) {
			DatRuleRoot child = (DatRuleRoot)m_elements.get(i);
			if(child instanceof DatRuleElement) {
				return true;
			}
		}
		return false;
	}

	private Vector getRuleTerms(DatRuleElement ele, Vector v)
	{
		Vector eles = ele.getElements();
		if(eles != null) {
			for(int i = 0, n = eles.size(); i < n; i++) {
				Object obj = eles.get(i);
				if(obj instanceof DatRuleElement) {
					v.add(obj);
					getRuleTerms((DatRuleElement)obj, v);
				} else if(obj instanceof DatRuleVal) {
					v.add(obj);
				}
			}
		}
		return v;
	}
        
        public void getAllRuleMembers(DatRuleElement ele, ArrayList<DatRuleRoot> members, HashMap<String, DatRuleRoot> marks){
		Vector eles = ele.getElements();
		if(eles != null) {
			for(int i = 0, n = eles.size(); i < n; i++) {
				DatRuleRoot obj = (DatRuleRoot)eles.get(i);
                                members.add(obj);
                                if(obj.m_mark != null && !obj.m_mark .isEmpty()){
                                    marks.put(obj.m_mark, obj);
                                }
				if(obj instanceof DatRuleElement) {
					getAllRuleMembers((DatRuleElement)obj, members, marks);
				}
			}
		}
            
        }
        public void rearrangeMarks(ArrayList<DatRuleRoot> members){

                for(int i = 0; i < members.size(); i++){
                    members.get(i).m_mark = String.valueOf(i + 1);
                }
            
        }

	public Vector getTerms()
	{
		Vector v = new Vector();
		return getRuleTerms(this, v);
	}

	public boolean hasCritTypes(int flag)
	{
		boolean ret = false;
		for(int i = 0, n = m_elements.size(); i < n; i++) {
			DatRuleRoot child = (DatRuleRoot)m_elements.get(i);
			if(child instanceof DatRuleElement) {
				ret = ((DatRuleElement)child).hasCritTypes(flag);
				if(ret) {
					return true;
				}
			} else if(child instanceof DatRuleVal) {
				try{
					ret = ((DatRuleVal)child).hasCritTypes(flag);
					if(ret) {
						return true;
					}
				}catch(Exception e){
					return false;
				}
			}
		}
		return false;
	}

	public boolean hasNot()
	{
		return m_isNot;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer((m_mark == null || m_mark.isEmpty())?"(":("mark: " + m_mark + " ("));
		if(m_isNot)
			buffer = new StringBuffer("not(");
		for(int i = 0, n = m_elements.size(); i < n; i++) {
			DatRuleRoot child = (DatRuleRoot)m_elements.get(i);
			buffer.append(m_elements.get(i).toString());
		}
		buffer.append(")");
		return buffer.toString();
	}

        @Override
	public boolean equals(Object obj)
	{
		return (obj != null &&
			obj instanceof DatRuleElement && ((DatRuleElement)obj).toString().equals(this.toString()));
	}
}
