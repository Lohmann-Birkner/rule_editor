package de.checkpoint.server.exceptions;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;






public class ExcException extends RemoteException
{
    
    public static final boolean FROM_SERVER = true;
    public static final boolean FROM_CLIENT = false;
	private boolean m_isWarning;
	public final static long waitTime = 60000;

	private String m_msgString = null;
	private String m_explanation = null;
    private static final Logger LOG = Logger.getLogger(ExcException.class.getName());



	public ExcException(String msg, Throwable details, boolean isWarning)
	{
		super(msg, details);
		m_msgString = msg;
		m_isWarning = isWarning;
		m_explanation = "";
	}

	public String getUserMessage()
	{
		return m_msgString;
	}

	public ExcException(String msg, boolean isWarning)
	{
		super(msg);
		m_msgString = msg;
		m_isWarning = isWarning;
		m_explanation = "";
	}

	public void setMessage(String tt)
	{

	}

	public static void createLogException(Throwable ex)
	{
		LOG.log(Level.SEVERE, "", ex);
	}

	public static void createException(Throwable ex)
	{
		LOG.log(Level.SEVERE, "", ex);
	}

	private static synchronized void logException(Throwable ex, String msgString)
	{

		LOG.log(Level.SEVERE, msgString, ex);
	}

	public static void createException(Throwable ex, String error)
	{
		LOG.log(Level.SEVERE, error, ex);
	}


	public static boolean isWarning(Throwable ex)
	{
		if (ex instanceof ExcException && ((ExcException) ex).isWarning())
			return true;
		if (ex instanceof RemoteException && ((RemoteException) ex).detail != null)
			return isWarning(((RemoteException) ex).detail);
		else
			return false;
	}

	public boolean isWarning()
	{
		return m_isWarning;
	}

	public String getErrorMessage()
	{
		StringBuffer msg = new StringBuffer(m_msgString);
		Throwable nestedEx = this.detail;
		while(nestedEx != null) {
			if(nestedEx instanceof ExcException) {
				if(msg.length() > 0)
					msg.append("\n");
				msg.append(((ExcException)nestedEx).m_msgString);
			}
			nestedEx = nestedEx instanceof RemoteException ? ((RemoteException)nestedEx).detail : null;
		}
		if(msg.length() == 0) {
			msg.append(super.getMessage());
			if((msg.length() == 0) && (detail != null))
				return detail.getMessage();
		}
		return msg.toString();
	}

	public static String getErrorMessage(Throwable ex)
	{
		if(ex instanceof ExcException)
			return((ExcException)ex).getErrorMessage();
		else {
			if(ex != null) {
				StringBuffer errorText = new StringBuffer(ex.getMessage() == null ? "" : ex.getMessage());
				if((ex instanceof RemoteException)
					&& (((RemoteException)ex).detail != null)) {
					String detailText = getErrorMessage(((RemoteException)ex).detail);
					if(detailText != null && detailText.length() > 0) {
						if(((RemoteException)ex).detail instanceof ExcException) {
							errorText = new StringBuffer(detailText);
						} else {
							if(errorText.toString().length() > 0)
								errorText.append("\n");
							errorText.append(detailText);
						}
					}
				}
				if (errorText.length() > 0)
					return errorText.toString();
				else
					return ex.toString();
			}else
				return "";
		}
	}


	public String getExplanation()
	{
		return getExplanation(this);
	}

	private static String getExplanation(RemoteException re)
	{
		String explanation;
		if(re instanceof ExcException)
			explanation = ((ExcException)re).m_explanation == null ? "" : ((ExcException)re).m_explanation;
		else
			explanation = "";
		if(re.detail instanceof RemoteException) {
			String moreExplanation = getExplanation((RemoteException)re.detail);
			if(moreExplanation != null) {
				if(explanation.length() > 0)
					explanation += "\n" + moreExplanation;
				else
					explanation = moreExplanation;
			}
		}
		return explanation;
	}
}
