package de.lb.ruleprocessor.checkpoint.ruleGrouper.data;

import java.math.BigDecimal;

public class CRGAppServerCommunication implements java.io.Serializable
{
	public  static  final String TABLE_NAME = "CRG_APPSERVER_COMMUNICATION";
	public  static  final String CRG_APPSERVER_IDENT = "CRG_APPSERVER_IDENT";
	public  static  final String CRGPL_IDENTIFIER = "CRGPL_IDENTIFIER";
	public  static  final String CRGPL_YEAR = "CRGPL_YEAR";
	public  static  final String CRG_CAN_SAVE = "CRG_CAN_SAVE";
	public  static  final String CRG_DO_REFRESH = "CRG_DO_REFRESH";

	public String crg_appserver_ident;
	public String crgpl_identifier;
	public BigDecimal crgpl_year;

	public CRGAppServerCommunication()
	{
	}

	public int getYear()
	{
		try{
			return crgpl_year.intValue();
		}catch(Exception e){
			return 0;
		}
	}
}
