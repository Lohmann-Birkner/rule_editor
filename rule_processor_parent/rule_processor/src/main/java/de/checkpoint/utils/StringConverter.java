package de.checkpoint.utils;


import java.lang.*;
import java.text.*;
import java.math.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 2.0
 */
public class StringConverter
{

    private static final Logger LOG = Logger.getLogger(StringConverter.class.getName());
    
	public static StringConverter m_conv = null;

	public StringConverter()
	{
	}

	public static StringConverter stringConverter(){
		if (m_conv==null){
			m_conv = new StringConverter();
		}
		return m_conv;
	}

	public static String intToString(int zahl,int minLength,int maxLength){
		String convert = "";
		try {
			convert = String.valueOf(zahl);
		} catch(Exception ex) {
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Rückgabe von getMessage() wird gar nicht verarbeitet?! */
		  ex.getMessage();
		}
		int lg=convert.length();
		if(lg>maxLength){
			convert=convert.substring(0,maxLength);
		}
		if(lg<minLength){
			for (int i=lg; convert.length()>=maxLength; i++){
				convert = "0" + convert;
			}
		}
		return convert;
	}

	public static String intToString(int zahl,int maxLength){
		String convert = "";
		try {
			convert = String.valueOf(zahl);
		} catch(Exception ex) {
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Rückgabe von getMessage() wird gar nicht verarbeitet?! */
			ex.getMessage();
		}
		if(convert.length()>maxLength){
			convert=convert.substring(0,maxLength);
		}
		return convert;
	}

	public static String shortString(float zahl, int maxLength){
		String convert = "";
		try {
			//zahl=Math.round(zahl*100)/100;
			convert=String.valueOf(zahl);
			BigDecimal myDec = new BigDecimal(zahl);
			myDec = myDec.setScale( 2, BigDecimal.ROUND_HALF_UP );
			convert = myDec.toString();
		} catch(Exception ex) {
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Rückgabe von getMessage() wird gar nicht verarbeitet?! */
			ex.getMessage();
		}
		if(convert.length()>maxLength){
			convert=convert.substring(0,maxLength);
		}
		convert=convert.replace('.',',');
		if(convert.endsWith(",")){
			convert=convert.substring(0,maxLength-1);
		}
		return convert;
	}

	public static String shortString(String wert,int minLength,int maxLength){
		if(wert==null)
			return "";
		int lg = wert.length();
		if (lg<minLength){
			for (int i=lg; wert.length()>=maxLength; i++){
				wert = "0" + wert;
			}
		}else if (lg>maxLength){
			return wert.substring(0,maxLength);
		}
		return wert;
	}

	public static String shortString(String wert,int maxLength){
		String convert=wert.trim();
		if(convert.length()>maxLength){
			convert=convert.substring(0,maxLength);
		}
		return convert;
	}

	public static String getDatabaseString(String text){
		String tt="";
		try{
			char c;
			for(int i = 0, n = text.length(); i < n; i++) {
				c = text.charAt(i);
				switch (c){
					case '\'':{
						tt = tt + "''";break;
					}
					default:
						tt = tt + c;
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
		return tt;
	}


	public StringBuffer getMailClientText(String in)
	{
		try {
			StringBuffer bf = new StringBuffer();
			String tt = "";
			char c;
			for (int i=0, n=in.length(); i<n; i++){
				c = in.charAt(i);
				switch(c) {
					case '\n':
						tt += "%0A"; break;
					case '\r':
						tt += "%0D"; break;
					case ' ':
						tt += "%20"; break;
					case ',':
						tt += "%2C"; break;
					case '?':
						tt += "%3F"; break;
					case '.':
						tt += "%2E"; break;
					case '!':
						tt += "%21"; break;
					case ':':
						tt += "%3A"; break;
					case ';':
						tt += "%3B"; break;
					case '<':
						tt += "%3C"; break;
					case '>':
						tt += "%3E"; break;
					default:
						tt += c;
				}
			}
			//tt = UtlDecrypter.toHex("%", tt.getBytes("Cp850"));
			//tt = tt.replace("%0D", "%0A");
			bf.append(tt);
			bf.append("%0A");
			return bf;
		} catch(Exception ex) {
			LOG.log(Level.SEVERE, "", ex);
			return new StringBuffer("");
		}

	}



}
