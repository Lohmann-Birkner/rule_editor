package de.lb.ruleprocessor.checkpoint.installer.IAActions;

import java.net.URL;

import de.lb.ruleprocessor.checkpoint.server.appServer.AppResourceBundle;
import de.lb.ruleprocessor.checkpoint.server.appServer.AppResources;
import java.io.BufferedReader;

import java.io.InputStream;
import javax.swing.JOptionPane;
import java.io.InputStreamReader;
import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
 * @author unbekannt
 * @version 2.0
 */
public class CommonOperations
        
{

    private static final Logger LOG = Logger.getLogger(CommonOperations.class.getName());
    
	static String[] specSigns = {"\'", "\"", ",", "/", "\\", "?", "&", ":", " ", ".", "ü", "Ü", "ö", "Ö", "Ä", "ä"};
	public static String specSignsString = "\', \", , /, \\, ?, &, :, ., ü, Ü, ö, Ö, Ä, ä, Komma, Leerzeichen";
	public static String replacePattern(String in, String pat, String rep)
	{
		StringBuffer statement = new StringBuffer(in);
		int start = statement.indexOf(pat);
		while((start = statement.indexOf(pat)) >= 0) {
			int end = start + pat.length();
			statement.replace(start, end, rep);
		}
		return statement.toString();
	}

	public static boolean hasSpecSym(String str)
	{
		for(int i = 0; i < specSigns.length; i++) {
			if(str.indexOf(specSigns[i]) >= 0) {
				return true;

			}
		}
		return false;
	}

	public static boolean isKeyword(URL url, String word)
	{
		try {
			if(url != null) {
				InputStream is = url.openStream();
				BufferedReader in = new BufferedReader(
									new InputStreamReader(is));
				String str;
				while((str = in.readLine()) != null) {
					if(word.equalsIgnoreCase(str)) {
						return true;
					}
				}
				return false;
			}
		} catch(Exception ex) {
                    LOG.log(Level.SEVERE, "Error on read URL", ex);

		}
		return false;
	}

	public static String stringAsHTML(String str, int count)
	{
		int lineLen = 0;
		StringBuffer buf = new StringBuffer("<html><body><font face=\"SansSerif\" size=\"3\">");
                if(str != null){
                    String[] words = str.split(" ");

                    for(int i = 0; i < words.length; i++) {
                            if(words[i].contains("<li>")) {
                                    lineLen = 0;
                            } else {
                                    if(lineLen + words[i].length() > count) {
                                            buf.append("<br>");
                                            lineLen = 0;
                                    }
                            }
                            buf.append(words[i]);
                            buf.append(" ");
                            lineLen += words[i].length() + 1;
                    }
                }
		buf.append("</font</body></html>");
                String s = buf.toString();
                buf = null;
		return s;
	}

	public static String stringArrayAsHTML(String[] types, int count)
	{
		StringBuffer buf = new StringBuffer("<html><body><font face=\"SansSerif\" size=\"3\">");
                if(types != null){
		for(int i = 0; i < types.length; i++) {
                    if(types[i] != null){
			String[] words = types[i].split(" ");
			int lineLen = 0;
			for(int ii = 0; ii < words.length; ii++) {
				int p1 = words[ii].indexOf("<");
				int p2 = words[ii].indexOf(">");
				if(p1 >= 0 && (p2 < 0 || p2 < p1)) {
					words[ii] = words[ii].replace("<", "&lt;");
				}
				if(lineLen + words[ii].length() > count) {
					buf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					lineLen = 5;
				}
				buf.append(words[ii]);
				buf.append(" ");
				lineLen += words[ii].length() + 1;
			}
                    }
			buf.append("<br>");
		}
                }
		buf.append("</font></body></html>");
               String s = buf.toString();
                buf = null;
		return s;
	}

	public static int getNextNumberAfter(String message, String nextAfter)
	{
		String words[] = message.split(" ");
		String severity = "";
		StringBuffer retSev = new StringBuffer("0");
		int ret = 0;
		int i = 0;
		for(i = 0; i < words.length; i++) {
			if(words[i].equalsIgnoreCase(nextAfter)) {
				if(i < words.length - 1) {
					severity = words[i + 1];
				}
				break;
			}
		}
		if(severity.length() > 0) {
			char sev[] = severity.toCharArray();

			for(int j = 0; j < sev.length; j++) {
				if((sev[j] >= '0') && (sev[j] <= '9')) {
					retSev.append(sev[j]);
				} else {
					break;
				}
			}
		}
		try {
			ret = Integer.parseInt(retSev.toString());
		} catch(Exception e) {

		}
		return ret;
	}

	public CommonOperations()
	{
	}

	public static String translateToReadable(String str, int max)
	{
		return  translateToReadable(str, max, true);
	}
	
	public static String translateToReadable(String str, int max, boolean doCheck)
        {
            return translateToReadable(str, max, doCheck, false);
        }
	public static String translateToReadable(String str, int max, boolean doCheck, boolean multistring)
	{
		if(str == null) {
			return "";
		}
		str = str.replaceAll(";", ",");
		str = str.replaceAll("„", "'");
		str = str.replaceAll("“", "'");
                if(multistring){
                    str = str.replaceAll("\n\r", "<br>");
                    str = str.replaceAll("\n", "<br>");
                    str = str.replaceAll("\r\n", "<br>");
                }
		if(doCheck) {
			str = checkStringLength("Check", str, max - 1);
		}
		return str;
	}

	/**
	 * Beschränkt Länge der Texte
	 * @param resultType String
	 * @param text String
	 * @param length int
	 * @return String
	 */
	public static String checkStringLength(String resultType, String text, int length)
	{
        try {
            byte[] bytes = text.getBytes("UTF-8");
            if(bytes.length > length){
                length -= bytes.length - text.length();
            }
        } catch (UnsupportedEncodingException ex) {
           ExcException.createException(ex);
        }
		if(text.length() > length) {
			return text.substring(0, length);
		}
		return text;
	}

	/** liefert ein long Array gebaut aus einem Aufzählungsstring mit splitPattern
		* @param full String
		* @param splitPattern String
		* @return long[]
		*/
		public static long[] getLongArrayFromString(String full, String splitPattern) {
		long[] ret = null;
		if(full != null && full.length() > 0) {
			String[] fgs = full.split(splitPattern);
			ret = new long[fgs.length];
			for(int i = 0; i < fgs.length; i++) {
				try {
					ret[i] = Long.parseLong(fgs[i].trim());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			Arrays.sort(ret);
		}
		return ret;
	}

	public static String doStringFromStringArray(String[] array, String splitPattern)
	{
		return 	doStringFromStringArray( array, splitPattern, "");

	}

	public static String doStringFromStringArray(String[] array, String splitPattern, String commonPattern)
	{
		if(array == null || array.length == 0)
			return "";
		StringBuffer ret = new StringBuffer();
		int len = array.length;
		for(int i = 0; i < len; i++){
			ret.append(array[i]);
			ret.append(commonPattern);
			if(i != len - 1)
				ret.append(splitPattern);
		}
		return ret.toString();
	}

	public static  String getFixedSize(String str, int size, boolean atTheEnd)
	{
		if(str == null)
			str = "";
		if(str.length() == size)
			return str;
		if(str.length() > size)
			return str.substring(0, size);
		StringBuffer ret = new StringBuffer();
		if(atTheEnd)
			ret.append(str);
		for(int i = 0; i < size - str.length(); i++)
			ret.append(" ");
		if(!atTheEnd)
			ret.append(str);
		return ret.toString();
	}



	public static void main(String[] args)
	{
		CommonOperations commonoperations = new CommonOperations();
	}
}
