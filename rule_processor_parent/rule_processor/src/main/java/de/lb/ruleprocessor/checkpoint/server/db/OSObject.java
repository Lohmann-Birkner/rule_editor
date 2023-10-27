package de.lb.ruleprocessor.checkpoint.server.db;


import de.lb.ruleprocessor.checkpoint.server.appServer.AppResources;
import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import de.lb.ruleprocessor.checkpoint.utils.UtlDateTimeConverter;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

public abstract class OSObject extends Object implements Serializable, Comparable, OSObjectWrapperIF
{
	private static Format _formatDt = new SimpleDateFormat("dd.MM.yyyy");

	private static SimpleDateFormat m_dateFormat = null;
	private static SimpleDateFormat m_dateFormat2 = null;
	private static SimpleDateFormat m_dateFormatLocale = null;
	private static SimpleDateFormat m_dateFormat4 = null;
	private static SimpleDateFormat m_dateFormat5 = null;
	private static SimpleDateFormat m_dateFormat6 = null;
	private static SimpleDateFormat m_timeFormat = null;

	public static final String ATT_ID = "id";
	public static final String ATT_CREATION_DATE = "creation_date";
	public static final String ATT_CREATION_USER = "creation_user";
	public static final String ATT_MODIFICATION_DATE = "modification_date";
	public static final String ATT_MODIFICATION_USER = "modification_user";

	public long id = -1;
	public java.sql.Date creation_date = null;
	public String creation_user = "";
	public java.sql.Date modification_date = null;
	public String modification_user = "";

	protected Vector m_references = new Vector(0);
	protected String m_tableName = "";
	protected String[] m_columns = null;
	protected boolean m_isDetailed = true;

	protected boolean m_changed = false;
	protected Field[] m_fields = null;
	protected boolean m_isDistinct = false;

	protected String var_suff = "m_";

	protected Object[][] m_refsToFullCaseCheckList = null;

	public OSObject(String tableName, boolean isDetailed)
	{
		super();
		m_tableName = tableName;
		m_isDetailed = isDetailed;
		assignReferences();
	}

	private static SimpleDateFormat getSimpleDateFormat()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormat sollte die Methode synchronized sein. */
		if(m_dateFormat == null)
			m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return m_dateFormat;
	}

	private static SimpleDateFormat getSimpleDateFormat2()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormat2 sollte die Methode synchronized sein. */
		if(m_dateFormat2 == null)
			m_dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
		return m_dateFormat2;
	}

	private static SimpleDateFormat getSimpleDateFormat4()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormat4 sollte die Methode synchronized sein. */
		if(m_dateFormat4 == null)
			m_dateFormat4 = new SimpleDateFormat("dd.MM.yyyy");
		return m_dateFormat4;
	}

	private static SimpleDateFormat getSimpleDateFormat5()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormat5 sollte die Methode synchronized sein. */
		if(m_dateFormat5 == null)
			m_dateFormat5 = new SimpleDateFormat(AppResources.DATEFORMAT_MINUTE);
		return m_dateFormat5;
	}

	private static SimpleDateFormat getSimpleDateFormat6()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormat6 sollte die Methode synchronized sein. */
		if(m_dateFormat6 == null)
			m_dateFormat6 = new SimpleDateFormat("yyyyMMdd");
		return m_dateFormat6;
	}

	private static SimpleDateFormat getSimpleDateFormatLocale()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_dateFormatLocale sollte die Methode synchronized sein. */
		if(m_dateFormatLocale == null)
			m_dateFormatLocale = new SimpleDateFormat(AppResources.DATEFORMAT_DATE,
								Locale.getDefault());
		return m_dateFormatLocale;
	}

	private static SimpleDateFormat getSimpleTimeFormat()
		{
      /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_timeFormat sollte die Methode synchronized sein. */
			if(m_timeFormat == null)
				m_timeFormat = new SimpleDateFormat("HH:mm");
			return m_timeFormat;
		}

	public String getTableName()
	{
		return m_tableName;
	}

	public void setTableName(String aName)
	{
		m_tableName = aName;
	}
        
 

	public void setColumnColl2Null()
	{
		m_columns = null;
	}

	public int setResultSet(ResultSet rs, int index, Field[] fields)
	{
		try {
			if(fields == null)
				fields = this.getObjectFields();
			for(int i = 0; i < fields.length; i++) {
				Field aField = fields[i];
				if(aField != null) {
					String nm = aField.getName();
					int len = nm.length();
					if(aField.getModifiers() == 1 && !(len >= 2 && nm.substring(0, 2).equals(var_suff))) {
						Class fieldClass = aField.getType();
						if(fieldClass.equals(long.class))
							aField.setLong(this, rs.getLong(index++));
						else if(fieldClass.equals(java.sql.Date.class))
							aField.set(this, rs.getDate(index++));
						else if(fieldClass.equals(String.class))
							aField.set(this, rs.getString(index++));
						else if(fieldClass.equals(int.class))
							aField.setInt(this, rs.getInt(index++));
						else if(fieldClass.equals(float.class))
							aField.setFloat(this, rs.getFloat(index++));
						else if(fieldClass.equals(double.class))
							aField.setDouble(this, rs.getDouble(index++));
						else if(fieldClass.equals(boolean.class))
							aField.setBoolean(this, rs.getBoolean(index++));
						else if(fieldClass.equals(byte.class))
							aField.setByte(this, rs.getByte(index++));
						else if(fieldClass.equals(char.class))
							aField.setChar(this, rs.getString(index++).charAt(0));
						else if(fieldClass.equals(InputStream.class))
							aField.set(this, rs.getBinaryStream(index++));
						else
							aField.set(this, rs.getObject(index++));
						checkField(fields[i]);
					}
				}
			}
			return index;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			return -1;
		}
	}

	public int setResultSet(Object[] rs, int index, Field[] fields) throws Exception
	{
		try {
			if(fields == null)
				fields = this.getObjectFields();
			for(int i = 0; i < fields.length; i++) {
				Field aField = fields[i];
				Object obj = rs[index];
				if(aField != null) {
					String nm = aField.getName();
					int    len = nm.length();
					if(aField.getModifiers() == 1 && !(len >= 2 && nm.substring(0, 2).equals(var_suff))) {
						if(obj != null) {
							try {
								Class fieldClass = aField.getType();
								if(fieldClass.equals(long.class)) {
									if(obj instanceof Integer)
										aField.setLong(this,
											((Integer)obj).longValue());
									else if(obj instanceof Long)
										aField.setLong(this,
											((Long)obj).longValue());
								} else if(fieldClass.equals(java.sql.Date.class)) {
									aField.set(this, (java.sql.Date)obj);
								} else if(fieldClass.equals(String.class))
									aField.set(this, (String)obj);
								else if(fieldClass.equals(int.class))
									aField.setInt(this,
										((Integer)obj).intValue());
								else if(fieldClass.equals(float.class)) {
									if(obj instanceof Float)
										aField.setFloat(this, ((Float)obj).floatValue());
									else
										aField.setFloat(this, ((Double)obj).floatValue());
								} else if(fieldClass.equals(double.class))
									aField.setDouble(this,
										((Double)obj).doubleValue());
								else if(fieldClass.equals(boolean.class)) {
									if(obj instanceof Boolean)
										aField.setBoolean(this,	((Boolean)obj).booleanValue());
									else {
										int ii = ((Integer)obj).intValue();
										aField.setBoolean(this, ii != -1 && ii != 0);
									}
								} else if(fieldClass.equals(byte.class))
									aField.setByte(this,
										((Byte)obj).byteValue());
								else if(fieldClass.equals(char.class))
									aField.setChar(this,
										((String)obj).charAt(0));
								else if(fieldClass.equals(InputStream.class))
									aField.set(this, (InputStream)obj);
								else
									aField.set(this, obj);
							} catch(Exception ex) {
								ExcException.createException(ex,
									this.getClass().getName()
									+ " mit ID " + this.id + " Index " + index
									+ "Name " + aField.getName() + " : " +
									obj.toString());
							}
						}
						checkField(aField);
						index++;
					}
				}
			}
			return index;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id + " Index " + index);
			throw ex;
		}
	}

	protected void checkField(Field field)
	{
		try {
			if(field.get(this) == null) {
				if(field.getType().equals(long.class))
					field.setLong(this, -1);
				else if(field.getType().equals(java.sql.Date.class))
					; // field.set(this, getCurrentDate());
				else if(field.getType().equals(String.class))
					field.set(this, "");
				else if(field.getType().equals(int.class))
					field.setInt(this, -1);
				else if(field.getType().equals(float.class))
					field.setFloat(this, 0.0f);
				else if(field.getType().equals(double.class))
					field.setDouble(this, 0.0d);
				else if(field.getType().equals(boolean.class))
					field.setBoolean(this, false);
					/*else if (field.getType().equals(byte.class))
					 field.setByte(this, new byte[0]);*/
				else if(field.getType().equals(char.class))
					field.setChar(this, ' ');
				else
					field.set(this, "");
			}
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id
				+ " Feldtyp " + field.getType());
		}
	}


	public Vector getReferences()
	{
		return m_references;
	}

	public Field[] getObjectFields()
	{
		if(m_fields == null)
			m_fields = (Field[])getAllDeclaredFields(this.getClass());
		return m_fields;
	}

	public Field[] getAllDeclaredFields(Class c)
	{
		try {
			int count = 0;
			Field[] subFields = new Field[0];
			Class sc = c.getSuperclass();
			if(sc != null && !sc.equals(Object.class)) {
				subFields = getAllDeclaredFields(sc);
			} else
				return c.getFields();
			Field[] fields = c.getFields();
			Vector allFields = new Vector(0);
			for(int i = 0; i < subFields.length; i++) {
				Field f = subFields[i];
				String nm = f.getName();
				int len = nm.length();
				if(!allFields.contains(f)
					&& f.getModifiers() == 1
					&& !(len >= 2 && nm.substring(0, 2).equals(var_suff))
					&& checkdetailedState(f.getName()))
					allFields.add(f);
			}
			for(int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				String nm = f.getName();
				int len = nm.length();
				if(!allFields.contains(f)
					&& f.getModifiers() == 1
					&& !(len >= 2 && nm.substring(0, 2).equals(var_suff))
					&& checkdetailedState(f.getName()))
					allFields.add(f);
			}
			allFields.trimToSize();
			Object[] objs = allFields.toArray();
			Field[] resFields = new Field[objs.length];
			for(int i = 0; i < objs.length; i++)
				resFields[i] = (Field)objs[i];
			return resFields;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			return new Field[0];
		}
	}

	private boolean checkdetailedState(String name)
	{
		if(this.m_isDetailed)
			return true;
		else {
			if(name.equals(this.ATT_CREATION_DATE)
				|| name.equals(this.ATT_CREATION_USER)
				|| name.equals(this.ATT_MODIFICATION_DATE)
				|| name.equals(this.ATT_MODIFICATION_USER))
				return false;
			else
				return true;
		}
	}



	public String getDeleteStatement(boolean reload)
	{
		String m_DeleteStatement = null;
		try {
			m_DeleteStatement = "DELETE FROM " + this.getTableName() +
								" WHERE id=" + this.id;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
		}
		return m_DeleteStatement;
	}

	private String fieldValue(Field aField, boolean isOracle)
	{
		if(aField != null) {
			String nm = aField.getName();
			int    len = nm.length();
			if(aField.getModifiers() == 1 && !(len >= 2 && nm.substring(0, 2).equals(var_suff))) {
				try {
					Class fieldClass = aField.getType();
					if(fieldClass.equals(int.class))
						return String.valueOf(aField.getInt(this));
					else if(fieldClass.equals(String.class)) {
						String aString = (String)(aField.get(this));
						if(aString == null)
							return "NULL";
						else{
							if(isCLOB(aField.getName())) {
								return "?";
							} else {
								int i = aString.indexOf("'");
								if(i >= 0) {
									aString = aString.replace("'", "´");
								}
								return "'" + aString + "'";
							}
						}
					} else if(fieldClass.equals(boolean.class)) {
						if(aField.getBoolean(this))
							return "1";
						else
							return "0";
					} else if(fieldClass.equals(java.sql.Date.class)) {
						java.sql.Date aDate = (java.sql.Date)aField.get(this);
						if(aDate == null) {
							return "NULL";
						} 
					} else if(fieldClass.equals(long.class)) {
						return String.valueOf(aField.getLong(this));
					} else if(fieldClass.equals(float.class)) {
						return String.valueOf(aField.getFloat(this));
					} else if(fieldClass.equals(double.class)) {
						return String.valueOf(aField.getDouble(this));
					} else if(fieldClass.equals(char.class)) {
						return "'" + String.valueOf(aField.getChar(this)) + "'";
					} else if(fieldClass.equals(byte.class)) {
						return String.valueOf(aField.getByte(this));
                                        } else if(fieldClass.equals(byte[].class)) {
						if( isBLOB( aField.getName())) {
							return "?";
						}
                                        }
				} catch(Exception ex) {
					ExcException.createException(ex,
						this.getClass().getName()
						+ " mit ID " + this.id
						+ "Name " + aField.getName() +
						" : " + this.toString());
				}
			}
		}
		return null;
	}

	public String getUpdateStatement(boolean reload, String userName, boolean isOracle)
	{
		StringBuffer query = null;
		try {
			modification_date = getCurrentDate();
			boolean setCreationDate = false;
			boolean setCreationUser = false;
			if(creation_date == null){
				creation_date = modification_date;
				setCreationDate = true;
			}

			modification_user = userName;
			if(creation_user == null){
				creation_user = modification_user;
				setCreationUser = true;
			}
			Field fields[] = this.getObjectFields();
			String fn;
			int i, max = fields.length - 1;

			query = new StringBuffer("UPDATE " + this.getTableName() + " SET ");
			for(i = 0; i <= max; i++) {
				fn = fields[i].getName();
				if(fn.equals(ATT_ID)) {
					continue;
                                }
				if (fn.equals(ATT_CREATION_DATE) && !setCreationDate) {
					continue;
                                }
				if (fn.equals(ATT_CREATION_USER) && !setCreationUser) {
					continue;
                                }
				if (fn.equals("type") || fn.equals("number")) {
					fn += "_";
                                }
				query.append(fn);
				query.append('=');
				query.append(fieldValue(fields[i], isOracle));
				if(i < max) {
					query.append(", ");
                                }
			}
			query.append(" WHERE ");
			query.append(ATT_ID);
			query.append('=');
			query.append(id);
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			query = null;
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil query != null nicht abgefragt wird.  */
		return query.toString();
	}

	public String getInsertStatement(boolean reload, String userName, boolean isOracle)
	{
		StringBuffer query = null;
		StringBuffer values = null;
		try {
			modification_date = getCurrentDate();
			modification_user = userName;
			creation_date = getCurrentDate();
			creation_user = userName;
			Field fields[] = this.getObjectFields();
			String fn;
			int i, max = fields.length - 1;
			query = new StringBuffer("INSERT INTO " + this.getTableName() + "(");
			values = new StringBuffer(") VALUES (");
			for(i = 0; i <= max; i++) {
				fn = fields[i].getName();
				if (fn.equals("type") || fn.equals("number"))
					fn += "_";
				query.append(fn);
				values.append(fieldValue(fields[i], isOracle));
				if(i < max) {
					query.append(',');
					values.append(',');
				}
			}
			query.append(values);
			query.append(')');
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			query = null;
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil query != null nicht abgefragt wird.  */
		return query.toString();
	}

	private java.sql.Date getCurrentDate()
	{
		try {
			java.sql.Date dt = new java.sql.Date(GregorianCalendar.getInstance().
							   getTimeInMillis());
			return dt;
		} catch(Exception ex) {
			ExcException.createException(ex);
			return new java.sql.Date(System.currentTimeMillis());
		}
	}

	public java.sql.Date getModificationDate()
	{
		return this.modification_date;
	}

	public final String getCreationUser()
	{
		return this.creation_user == null ? "" : creation_user;
	}

	public final String getModificationUser()
	{
		return this.modification_user == null ? "" : modification_user;
	}

	public java.sql.Date getCreationDate()
	{
		return this.creation_date;
	}

	public long getID()
	{
		return this.id;
	}

	public int compareTo(Object o)
	{
		if(o == null) // null greater than everything
			return 1;
		else {
			Collator col = Collator.getInstance(Locale.getDefault());
			return col.compare(this.toString(), o.toString());
		}
	}

/*	protected RmcCaseTypes findCaseType(int type, long identID)
	{
		try {
			RmcCaseTypes[] types = RmcCaseAdminMgr_rm.getCaseTypes(type);
			if(types.length > 0 && identID <= 0)
				return types[0];
			else {
				for(int i = 0; i < types.length; i++) {
					if(types[i].getIdent() == identID)
						return types[i];
				}
			}
			return new RmcCaseTypes(1, -1, "Fehler bei Suche der Eigenschaft");
		} catch(Exception ex) {
			ExcException.createException(ex);
			return new RmcCaseTypes(1, -1, "Fehler bei Suche der Eigenschaft");
		}
	}

	protected RmcCaseTypes findCaseType(int type, String name)
	{
		try {
			RmcCaseTypes[] types = RmcCaseAdminMgr_rm.getCaseTypes(type);
			if(types.length > 0 && name.length() <= 0)
				return types[0];
			else {
				for(int i = 0; i < types.length; i++) {
					if(types[i].getText().equals(name))
						return types[i];
				}
			}
			return new RmcCaseTypes(1, -1, "Fehler bei Suche der Eigenschaft");
		} catch(Exception ex) {
			ExcException.createException(ex);
			return new RmcCaseTypes(1, -1, "Fehler bei Suche der Eigenschaft");
		}
	}
*/
	public OSObject setCopyValues(OSObject newObject){
		try {
			Field[] fields = this.getObjectFields();
			for(int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(field != null) {
					String nm = field.getName();
					int    len = nm.length();
					if(fields[i].getModifiers() == 1 &&
						!(len >= 2 && nm.substring(0, 2).equals(var_suff))) {
						if(nm.equals(this.ATT_ID))
							field.setLong(newObject, -1);
						else{
							Object obj = field.get(this);
							if (obj instanceof Vector){
								obj = ((Vector)obj).clone();
							}
							else if(obj instanceof long[]){
								obj = ((long[])obj).clone();
							}
							field.set(newObject, obj);
						}
					}
				}
			}
			m_fields = null;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			m_fields = null;
		}
		return newObject;
	}

	public OSObject copyObject()
	{
		OSObject newObject = null;
		try {
			Class cl = Class.forName(this.getClass().getName());
			newObject = (OSObject)cl.newInstance();
			setCopyValues(newObject);
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return newObject;
	}

	public boolean equalsObject(OSObject obj)
	{
		try {
			if (obj==null)
				return false;
			Object o1, o2;
			Field f;
			String nm;
			Field[] fields = this.getObjectFields();
			for(int i = 0; i < fields.length; i++) {
				f = fields[i];
				if(f != null) {
					nm = f.getName();
					int len = nm.length();
					if(f.getModifiers() == 1 &&	!(len >= 2 && nm.substring(0, 2).equals(var_suff))) {
						if(checkColumnName(nm)) {
							try {
								o1 = f.get(obj);
								o2 = f.get(this);
								if((o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2))) {
									; // alles ist gut
								} else {
									m_fields = null;
									return false;
								}
							} catch(Exception ex) {
								ExcException.createException(ex,
									this.getClass().getName() + " mit ID " +
									this.id);
								m_fields = null;
								return false;
							}
						}
					}
				}
			}
			m_fields = null;
		} catch(Exception ex) {
			ExcException.createException(ex,
				this.getClass().getName() + " mit ID " +
				this.id);
			m_fields = null;
		}
		return true;
	}

	protected boolean checkColumnName(String nm)
	{
		if(!nm.equals(this.ATT_ID)
			&& !nm.equals(this.ATT_CREATION_DATE)
			&& !nm.equals(this.ATT_CREATION_USER)
			&& !nm.equals(this.ATT_MODIFICATION_DATE)
			&& !nm.equals(this.ATT_MODIFICATION_USER))
			return true;
		else
			return false;

	}

	public String toShortString()
	{
		return super.toString();
	}

	public String toLongString()
	{
		return super.toString();
	}

	public void setChanged(boolean hasChanged)
	{
		this.m_changed = hasChanged;
	}

	public boolean hasChanged()
	{
		return m_changed;
	}

	public OSObject getObject()
	{
		return this;
	}

	public String getSelectString()
	{
		if(m_isDistinct)
			return "SELECT DISTINCT ";
		else
			return "SELECT ";
	}

	public void resetFields()
	{
		m_fields = null;
		m_columns = null;
	}

	public static String getSimpleDateFormat(java.util.Date dt, int type)
	{
		if (dt==null)
			return "";
		if(type == 1)
			return getSimpleDateFormat().format(dt);
		else if(type == 2)
			return getSimpleDateFormat2().format(dt);
		else if(type == 4)
			return getSimpleDateFormat4().format(dt);
		else if(type == 5)
			return getSimpleTimeFormat().format(dt);
		else if (type==6)
			return getSimpleDateFormat5().format(dt);
		else if (type==7)
			return getSimpleDateFormat6().format(dt);
		else
			return getSimpleDateFormatLocale().format(dt);
	}

	public synchronized static java.sql.Date convertStrToDate(String txt)
	{

		java.sql.Date date = null;
		if(txt == null)return null;
		try {
			 synchronized(txt){
				Format _formatDt = new SimpleDateFormat("dd.MM.yyyy");
				java.util.Date dt = (java.util.Date)_formatDt.parseObject(txt);
				date = new java.sql.Date(dt.getTime());
				}
		} catch(ParseException pe) {
			date = null;
		}
		return date;
	}

	public static String convertDateToStr(java.util.Date date)
	{
		try {
			synchronized(date){
				Format _formatDt = new SimpleDateFormat("dd.MM.yyyy");
				return _formatDt.format(date);
			}
		} catch(Exception e) {
			return null;
		}
	}

	public boolean isDetailed(){
		return this.m_isDetailed;
	}

	private boolean isStorno(){
		return false;
	}

	protected Object[][] getRefsToFullCaseCheckList()
	{
            if(m_refsToFullCaseCheckList == null){
                assignReferences();
            }
            return m_refsToFullCaseCheckList;
                
	}

	protected void assignReferences()
	{
            m_refsToFullCaseCheckList = new Object[0][0];
        }

	public boolean hasBLOB() {
		return false;
	}

	public boolean isBLOB( String name) {
		return false;
	}

	public void insertBLOB( PreparedStatement ps) throws SQLException {
	}

	public boolean hasCLOB() {
		return false;
	}

	public boolean isCLOB( String name) {
		return false;
	}

	public void insertCLOB( PreparedStatement ps) throws SQLException {
	}

    protected void setFieldFromString(OSObject obj, Field aField, String str) throws Exception
    {
        Class<?> fieldClass = aField.getType();

            if(fieldClass.equals(int.class)) {
                    int i = (str != null) ? Integer.valueOf(str) : 0;
                    aField.setInt(obj, i);
            } else if(fieldClass.equals(long.class)) {
                    long l = (str != null) ? Long.valueOf(str) : 0;
                    aField.setLong(obj, l);
            } else if(fieldClass.equals(double.class)) {
                    double d = (str != null) ? Double.valueOf(str) : 0;
                    aField.setDouble(obj, d);
            } else if(fieldClass.equals(String.class)) {
                    aField.set(obj, str);
            } else if(fieldClass.equals(char.class)) {
                    char c = (str != null && str.length() >= 1) ? str.charAt(0) : 0;
                    aField.setChar(obj, c);
            } else if(fieldClass.equals(Date.class)){
                Date d =  str.length() < 8?null:UtlDateTimeConverter.convertStringToDate(str, "yyyyMMdd");
                aField.set(obj, d);
            }else throw new Exception("unknown Field " + aField.toString() + " " + str);


    }


}
