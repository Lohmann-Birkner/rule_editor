package de.checkpoint.ruleGrouper;



import java.math.BigDecimal;
import java.util.ArrayList;

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
public class CheckValue
{
	private final int MIN_ARRAY_SIZE = 100;
//	private double actualDouble = 0d;
	private BigDecimal actualLong = BigDecimal.ZERO;
//	private long actualLongTime =0;
	private String actualString = "";
	private int actualType = -1;
	private int lastType = -1;
//	private double lastDouble = 0d;
	private BigDecimal lastLong = BigDecimal.ZERO;
//	private long lastLongTime =0;
	private String lastString = "";
	private boolean[] stateArray = new boolean[MIN_ARRAY_SIZE];
	private boolean isArray = false, isCompare = false, isCompute = false;
	private ArrayList<BigDecimal> lastLongArray = null;
//	private long[] lastLongTimeArray = null;
	private ArrayList<BigDecimal> actualLongArray = null;
//	private long[] actualLongTimeArray = null;
//	private double[] lastDoubleArray = null;
//	private double[] actualDoubleArray = null;
	private String[] actualStringArray = null;
	private String[] lastStringArray = null;
	private int lastCompareState = 0;
//	private double doubleValue = 0d;
	private BigDecimal longValue = BigDecimal.ZERO;
//	private long longtimeValue = 0;
	private String stringValue = "";
	private ArrayList<BigDecimal> longArray = null;
//	private long[] longtimeArray = null;
//	private double[] doubleArray = new double[0];
	private String[] stringArray = new String[0];
	private double zusatz;
	private boolean lastState = false;
	public int[] dependIndex = null;
	public int lastDepended = -1;
	public int lastDependIndex = -1;
	public int lastLevelOfDepended = -1;
	public boolean[] groupDependResults = new boolean[0];
	public boolean[][] groupDependsIndex = new boolean[0][0];
	public boolean isDepend = false, isIndexDepend = false;
	public int lastGroupIndex = -1;

	public int minCountForDependCompareOperation = 1; //für die zusammenhängenden Array - Kriterien muss aud 2 gesetzt werden (mindeste Anzahl der Treffer bei MehrereIn Operationen

	protected static final BigDecimal B_DEFAULT_INT_VALUE = new BigDecimal(CRGInputOutputBasic.DEFAULT_INT_VALUE);
	protected static final BigDecimal B_DEFAULT_DOUBLE_VALUE = new BigDecimal(CRGInputOutputBasic.DEFAULT_DOUBLE_VALUE);
	protected static final BigDecimal B_DEFAULT_LONG_VALUE = new BigDecimal(CRGInputOutputBasic.DEFAULT_LONG_VALUE);

	public CheckValue()
	{}

	public void setAllStateArray()
	{
		for(int j = 0; j < stateArray.length; j++) {
			stateArray[j] = false;
		}
	}

	public void setLastCompareState(int actVal)
	{
		lastCompareState = actVal;
	}

	public int getLastCompareState()
	{
		return lastCompareState;
	}

	public void setIsCompare(boolean actVal)
	{
		isCompare = actVal;
	}

	public boolean getIsCompare()
	{
		return isCompare;
	}

	public void setIsArray(boolean actVal)
	{
		isArray = actVal;
	}

	public boolean getIsArray()
	{
		return isArray;
	}

	public void setIsCompute(boolean actVal)
	{
		isCompute = actVal;
	}

	public boolean getIsCompute()
	{
		return isCompute;
	}

	public void setDoubleValue(double actVal)
	{
		longValue = new BigDecimal(actVal);
	}

	public void setDoubleValue(BigDecimal actVal)
	{
		longValue = actVal;
	}

	public void setStringValue(String actVal)
	{
		stringValue = actVal;
	}

	public void setLongValue(BigDecimal actVal)
	{
		longValue = actVal;
	}

	public void setLongValue(long actVal)
	{
		longValue = new BigDecimal(actVal);
	}

	public void setLongTimeValue(BigDecimal actVal)
	{
//		longtimeValue = actVal;
		setLongValue(actVal);
	}



	public void setDoubleArray(ArrayList<BigDecimal> actVal)
	{
		setLongArray(actVal);
	}

	public void setDoubleArray(float[] actVal)
	{
		setLongArray(actVal);
	}

	public void setDoubleArray(double[] actVal)
	{
		longArray = new ArrayList<BigDecimal>();
		for(int i = 0; i < actVal.length; i++) {
			longArray.add(new BigDecimal(actVal[i]));

		}
	}

	public void setStringArray(String[] actVal)
	{
		if(actVal == null) {
			actVal = new String[0];
		}
		stringArray = actVal;
	}

	public void setLongArray(long[] actVal)
	{
		longArray = new ArrayList<BigDecimal>();
		for(int i = 0; i < actVal.length; i++) {
			longArray.add(new BigDecimal(actVal[i]));

		}
	}

	public void setLongArray(ArrayList<BigDecimal> actVal)
	{
		longArray = actVal;
	}

	public void setLongArray(float[] actVal)
	{
		longArray = new ArrayList<BigDecimal>();
		for(int i = 0; i < actVal.length; i++) {
			longArray.add(new BigDecimal(actVal[i]));

		}
	}

	public void setLongTimeArray(long[] actVal)
	{
		/*		longtimeArray = new long[actVal.length];
		  System.arraycopy(actVal, 0, longtimeArray, 0, actVal.length);
		 */
		setLongArray(actVal);
	}

	public void setCheckValues(CheckValue chk)
	{
		setActualType(chk.getActualType());
		switch(getActualType()) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				setDoubleValue(chk.getLastDouble());
				setActualDouble(chk.getLastDouble());
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				setDoubleArray(chk.getLastDoubleArray());
				setActualDoubleArray(chk.getLastDoubleArray());
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				// es steht: <= usw. oder + usw.
				this.setActualString(chk.getLastString());
				this.setStringValue(chk.getLastString());
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				//frage wie oben. und array erwartet
				setStringArray(chk.getLastStringArray());
				setActualStringArray(chk.getLastStringArray());
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				setLongValue(chk.getLastLong());
				setActualLong(chk.getActualLong());
				break;
			}
			/*			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				setLongTimeValue(chk.getLastLongTime());
				setActualLongTime(chk.getLastLongTime());
				break;
			   }*/
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				setLongArray(chk.getLastLongArray());
				setActualLongArray(chk.getLastLongArray());
				break;
			}
			/*			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:	{
				setLongTimeArray(chk.getLastLongTimeArray());
				setActualLongTimeArray(chk.getLastLongTimeArray());
				break;
			   }*/

		}
	}

	public void setActualDouble(BigDecimal actVal)
	{
		actualLong = actVal;
	}

	public BigDecimal getActualDouble()
	{
		return actualLong;
	}

	public void setActualString(String actVal)
	{
		actualString = actVal;
	}

	public String getActualString()
	{
		return actualString;
	}

	public void setActualLong(BigDecimal actVal)
	{
		actualLong = actVal;
	}

	public BigDecimal getActualLong()
	{
		return actualLong;
	}

	/*	public void setActualLongTime(long actVal){
	  actualLongTime = actVal;
	 }
	 */
	/*	public long getActualLongTime(){
	  return actualLongTime;
	 }
	 */
	public void setLastDouble(double actVal)
	{
		lastLong =  new BigDecimal(actVal);
	}

	public BigDecimal getLastDouble()
	{
		return lastLong;
	}

	public void setLastString(String actVal)
	{
		lastString = actVal;
	}

	public String getLastString()
	{
		return actualString;
	}

	public void setLastLong(BigDecimal actVal)
	{
		lastLong = actVal;
	}

	public BigDecimal getLastLong()
	{
		return lastLong;
	}

	/*	public void setLastLongTime(long actVal){
	  lastLongTime = actVal;
	 }

	 public long getLastLongTime(){
	  return lastLongTime;
	 }
	 */
	public void setActualDoubleArray(ArrayList<BigDecimal> actVal)
	{
		actualLongArray = actVal;
	}

	public ArrayList<BigDecimal> getActualDoubleArray()
	{
		return actualLongArray;
	}

	public void setActualStringArray(String[] actVal)
	{
		actualStringArray = actVal;
	}

	public String[] getActualStringArray()
	{
		return actualStringArray;
	}

	public void setActualLongArray(ArrayList<BigDecimal> actVal)
	{
		actualLongArray = actVal;
	}

	public void setActualLongArray(float[] actVal)
	{
		actualLongArray = new ArrayList<BigDecimal>();
		if(actVal != null){
			for(int i = 0; i < actVal.length; i++) {
				actualLongArray.add(new BigDecimal(actVal[i]));
			}
		}
	}

	public ArrayList<BigDecimal> getActualLongArray()
	{
		return actualLongArray;
	}

	/*	public void setActualLongTimeArray(long[] actVal){
	  actualLongTimeArray = new long[actVal.length];
	  System.arraycopy(actVal, 0, actualLongTimeArray, 0, actVal.length);
	 }

	 public long[] getActualLongTimeArray(){
	  return actualLongTimeArray;
	 }
	 */
	public void setLastDoubleArray(ArrayList<BigDecimal> actVal)
	{
		lastLongArray = actVal;
	}

	public ArrayList<BigDecimal> getLastDoubleArray()
	{
		return lastLongArray;
	}

	public void setLastStringArray(String[] actVal)
	{
		lastStringArray = actVal;
	}

	public String[] getLastStringArray()
	{
		return actualStringArray;
	}

/*	public void setLastLongArray(long[] actVal)
	{
		lastLongArray = new float[actVal.length];
		/*		for(int i=0;i<actVal.length;i++){
		   lastLongArray[i] = actVal[i];
		  }*/
//		System.arraycopy(actVal, 0, lastLongArray, 0, actVal.length);
//	}

	public ArrayList<BigDecimal> getLastLongArray()
	{
		return lastLongArray;
	}

	/*
	 public void setLastLongTimeArray(long[] actVal){
	  lastLongTimeArray = new long[actVal.length];
	  System.arraycopy(actVal, 0, lastLongTimeArray, 0, actVal.length);
	 }

	 public long[] getLastLongTimeArray(){
	  return lastLongTimeArray;
	 }
	 */
	public void setActualType(int actVal)
	{
		lastType = actualType;
		actualType = actVal;
	}

	public int getActualType()
	{
		return actualType;
	}

	public void setStateArray(boolean[] actArray)
	{
		stateArray = actArray;
	}

	public boolean[] getStateArray()
	{
		return stateArray;
	}

	public void setZusatz(double z)
	{
		zusatz = z;
	}

	public void setLastState(boolean lastState)
	{
		this.lastState = lastState;
	}

	public double getZusatz()
	{
		return zusatz;
	}

	public boolean getLastState()
	{
		return lastState;
	}

	public void setLastStateWithNot(boolean isNot)
	{
		lastState = isNot?(!lastState):lastState;
	}

	/**
	 *
	 */
	public boolean getCheckValue(int state, boolean lastState, int elementType) throws Exception
	{
		// es wird immer vorher der Ausgangs- WERT/ARRAY gesetzt, nicht actual oder last
		// bevor diese funktion aufgerufen wird
		// Werte: longValue, doubleValue, stringValue
		// Array: longArray, doubleArray, stringArray
		//-----------
		// es ist nicht auszuschließen, dass die Arrays unterschiedliche Länge haben, aber NUR
		// dann, wenn es keine Abhängigkeiten gibt. Bei Abhängigkeiten wird der Index der Arrays
		// verglichen, wenn dann die Arrays unterschiedliche Länge haben, macht das keinen Sinn
		//-----------
		boolean isNullArrayLast = true, isNullArrayActual = true, isNullArray = true;
		int lengthLast = -1, lengthActual = -1, lengthArray = -1;
		boolean returnVal = false;
		switch(actualType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER:
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_DATE:
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				isNullArrayLast = lastLongArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastLongArray.size();
				}
				isNullArrayActual = actualLongArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualLongArray.size();
				}
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				isNullArrayLast = lastStringArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastStringArray.length;
				}
				isNullArrayActual = actualStringArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualStringArray.length;
				}
				break;
			}
/*			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				isNullArrayLast = lastLongArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastLongArray.size();
				}
				isNullArrayActual = actualLongArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualLongArray.size();
				}
				break;
			}*/
			/*			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				isNullArrayLast = lastLongTimeArray == null? true:false;
				if(!isNullArrayLast)
				 lengthLast = lastLongTimeArray.length;
				isNullArrayActual = actualLongTimeArray == null? true:false;
				if(!isNullArrayActual)
				 lengthActual = actualLongTimeArray.length;
				break;
			   }*/
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				isArray = true;
				isNullArrayLast = lastLongArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastLongArray.size();
				}
				isNullArrayActual = actualLongArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualLongArray.size();
				}
				isNullArray = false;
				lengthArray = longArray.size();
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				isArray = true;
				isNullArrayLast = lastStringArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastStringArray.length;
				}
				isNullArrayActual = actualStringArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualStringArray.length;
				}
				isNullArray = false;
				lengthArray = stringArray.length;
				break;
			}
/*			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				isArray = true;
				isNullArrayLast = lastLongArray == null ? true : false;
				if(!isNullArrayLast) {
					lengthLast = lastLongArray.size();
				}
				isNullArrayActual = actualLongArray == null ? true : false;
				if(!isNullArrayActual) {
					lengthActual = actualLongArray.size();
				}
				isNullArray = false;
				lengthArray = longArray.size();
				break;
			}*/
			/*			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				isArray = true;
				isNullArrayLast = lastLongTimeArray == null? true:false;
				if(!isNullArrayLast)
				 lengthLast = lastLongTimeArray.length;
				isNullArrayActual = actualLongTimeArray == null? true:false;
				if(!isNullArrayActual)
				 lengthActual = actualLongTimeArray.length;
				isNullArray = false;
				lengthArray = longtimeArray.length;
				break;
			   }*/
		}
		if(isCompare) { //vergleich von Elementen
			if(elementType == CRGRuleGrouperStatics.TYPE_VALUE || elementType == CRGRuleGrouperStatics.TYPE_ELEMENT) {
				switch(actualType) {
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DATE:
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
						if(lastType == CRGRuleGrouperStatics.DATATYPE_DATE) {
							actualLong = longValue;
						} else {
							actualLong = longValue;
						}
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_STRING: {
						actualString = stringValue;
						break;
					}
/*					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DATE: {
						if(lastType == CRGRuleGrouperStatics.DATATYPE_INTEGER
							|| lastType == CRGRuleGrouperStatics.DATATYPE_DOUBLE) {
							actualLong = longValue;
							actualType = CRGRuleGrouperStatics.DATATYPE_DOUBLE;
						} else {
							actualLong = longValue;
						}
						break;
					}*/
					/*					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
						  if(lastType==CRGRuleGrouperStatics.DATATYPE_DATE)
						   actualLong = longtimeValue;
						  else
						   actualLongTime = longtimeValue;
						  break;
						 }*/
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						actualLongArray = longArray;
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						actualStringArray = stringArray;
						break;
					}
/*					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						actualLongArray = longArray;
						break;
					}*/
					/*					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
						  if(lastType==CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE)
						   actualLongArray = longtimeArray;
						  else
						   actualLongTimeArray = longtimeArray;
						  break;
						 }*/
				}
			}
			boolean fullState = false;
			if(isNullArray) {
				// isNullArray ist true, wenn ein einzelner Wert gesetzt ist, geprüft werden soll.
				// nur bei nicht Array - Datentypen
				if(!isNullArrayLast) {
					returnVal = false;
					for(int j = 0; j < lengthLast; j++) {
						switch(actualType) {
							case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_DATE:
							case CRGRuleGrouperStatics.DATATYPE_INTEGER:
							case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
								lastLong = lastLongArray.get(j);
								break;
							}
							case CRGRuleGrouperStatics.DATATYPE_STRING: {
								lastString = lastStringArray[j];
								break;
							}
/*							case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_DATE: {
								lastLong = lastLongArray.get(j);
								break;
							}*/
							/*							case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
									lastLongTime = lastLongTimeArray[j];
									break;
								   }*/
						}
						returnVal = setCheckType(actualType, state);
						if(this.lastGroupIndex > 0) {
							setGroupDependResultsValue(j, returnVal);
							fullState = fullState || returnVal;
						}
						if(returnVal) {
							setStateArrayValue(j, true);
							lastState = true;
							if(isCompare && isDepend && dependIndex.length > j) {
								dependIndex[j] = lastDependIndex;
							}
						} else {
							setStateArrayValue(j, false);
						}
					}
					if(this.lastGroupIndex > 0) {
						if(getGroupResults(lastGroupIndex) == null) {
							setGroupResults(lastGroupIndex);
						}

						lastState = checkGroupIndexValues(lastGroupIndex,
									state, fullState);
					}
				} else {
					lastState = setCheckType(actualType, state);
				}
			} else {
				// isNullArray ist false, wenn ein Array gesetzt ist, geprüft werden soll.
				// nur bei Array - Datentypen
				if(!isNullArrayLast) {
					int laenge = setArrayLength(lengthLast, lengthActual);
					returnVal = false;
					for(int j = 0; j < laenge; j++) {
						switch(actualType) {
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
								// der einzelne Wert wird nicht benötigt, kann genutzt werden
								lastLong = lastLongArray.get(j);
								actualLong = actualLongArray.get(j);
								break;
							}
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
								lastString = lastStringArray[j];
								actualString = actualStringArray[j];
								break;
							}
/*							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
								lastLong = lastLongArray.get(j);
								actualLong = actualLongArray.get(j);
								break;
							}*/
							/*							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
									lastLongTime = lastLongTimeArray[j];
									actualLongTime = actualLongTimeArray[j];
									break;
								   }*/
						}
						returnVal = setCheckType(actualType, state);
						if(this.lastGroupIndex > 0) {
							setGroupDependResultsValue(j, returnVal);
							fullState = fullState || returnVal;
						}
						if(returnVal) {
							setStateArrayValue(j, true);
							lastState = true;
						} else {
							setStateArrayValue(j, false);
						}
					}
					if(this.lastGroupIndex > 0) {
						if(getGroupResults(lastGroupIndex) == null) {
							setGroupResults(lastGroupIndex);
						}

						lastState = checkGroupIndexValues(lastGroupIndex,
									state, fullState);
					}
				} else {
					returnVal = false;
					int len = lengthActual;
					if(lengthActual == -1 && lengthArray > -1) {
						len = lengthArray;
					}
					for(int j = 0; j < len; j++) {
						switch(actualType) {
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
								actualLong = actualLongArray.get(j);
								break;
							}
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
								actualString = actualStringArray[j];
								break;
							}
/*							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
								actualLong = actualLongArray.get(j);
								break;
							}*/
							/*							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
									actualLongTime = actualLongTimeArray[j];
									break;
								   }*/
						}
						returnVal = setCheckType(actualType, state);
						if(this.lastGroupIndex > 0) {
							setGroupDependResultsValue(j, returnVal);
							fullState = fullState || returnVal;
						}
						if(returnVal) {
							setStateArrayValue(j, true);
							lastState = true;
						} else {
							setStateArrayValue(j, false);
						}
					}
					if(this.lastGroupIndex > 0) {
						if(getGroupResults(lastGroupIndex) == null) {
							setGroupResults(lastGroupIndex);
						}

						lastState = checkGroupIndexValues(lastGroupIndex,
									state, fullState);
					}
				}
			}
			lastCompareState = state;
		} else if(isCompute) { //Berechnung von Elementen
			switch(actualType) {

				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_DATE:
				case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							if(lastType == CRGRuleGrouperStatics.DATATYPE_DATE) {
								actualLong = getRuleComputeValueLong(actualLong, longValue, state, actualType);
							} else {
								actualLong = getRuleComputeValueLong(actualLong, longValue, state, actualType);
							}
						} else {
							for(int j = 0; j < lengthActual; j++) {
								actualLongArray.set(j, getRuleComputeValueLong(actualLongArray.get(j), longValue, state, actualType));
							}
						}
					} else {
						if(isNullArrayLast) {
							actualLong = getRuleComputeValueLong(lastLong, longValue, state, actualType);
						} else {
                                                    int len = actualLongArray.size();
							for(int j = 0; j < lengthLast; j++) {
                                                            if(j < len){
								actualLongArray.set(j, getRuleComputeValueLong(lastLongArray.get(j), longValue, state, actualType));
                                                            }else{
								actualLongArray.add( getRuleComputeValueLong(lastLongArray.get(j), longValue, state, actualType));
                                                                
                                                            }
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_STRING: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							actualString = CheckpointRuleGrouper.getRuleComputeValueString(actualString, stringValue,
										   state);
						} else {
							for(int j = 0; j < lengthActual; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(
									actualStringArray[j], stringValue, state);
							}
						}
					} else {
						if(isNullArrayLast) {
							actualString = CheckpointRuleGrouper.getRuleComputeValueString(lastString, stringValue,
										   state);
						} else {
							for(int j = 0; j < lengthLast; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(lastStringArray[
									j], stringValue, state);
							}
						}
					}
					break;
				}
/*				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_DATE: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							actualLong = getRuleComputeValueLong(actualLong, longValue, state);
						} else {
							for(int j = 0; j < lengthActual; j++) {
								actualLongArray.set(j, getRuleComputeValueLong(actualLongArray.get(j), longValue, state));
							}
						}
					} else {
						if(isNullArrayLast) {
							actualLong = getRuleComputeValueLong(lastLong, longValue, state);
						} else {
							for(int j = 0; j < lengthLast; j++) {
								actualLongArray.set(j,  getRuleComputeValueLong(lastLongArray.get(j), longValue, state));
							}
						}
					}
					break;
				}*/
				/*				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
					 if(elementType == CRGRuleGrouperStatics.TYPE_VALUE){
					  if(isNullArrayActual){
				 actualLongTime = CheckpointRuleGrouper.getRuleComputeValueLong(actualLongTime, longtimeValue, state);
					  }else{
					   for(int j=0;j<lengthActual;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(actualLongTimeArray[j], longtimeValue, state);
					   }
					  }
					 }else{
					  if(isNullArrayLast){
				 actualLongTime = CheckpointRuleGrouper.getRuleComputeValueLong(lastLongTime, longtimeValue, state);
					  }else{
					   for(int j=0;j<lengthLast;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(lastLongTimeArray[j], longtimeValue, state);
					   }
					  }
					 }
					 break;
					}*/
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							actualLongArray = new ArrayList<BigDecimal>(); //nur um die Größe zu zuweisen
							for(int j = 0; j < lengthArray; j++) {
								actualLongArray.add(j, getRuleComputeValueLong(actualLong, longArray.get(j), state, actualType));
							}
						} else {
							int laenge = setArrayLength(lengthActual, lengthArray);
							for(int j = 0; j < laenge; j++) {
								actualLongArray.set(j, getRuleComputeValueLong(actualLongArray.get(j), longArray.get(j), state, actualType));
							}
						}
					} else {
						if(isNullArrayActual) {
							actualLongArray = new ArrayList<BigDecimal>(); //nur um die Größe zu zuweisen
						}
						if(isNullArrayLast) {
                                                    int len = actualLongArray.size();
							for(int j = 0; j < lengthArray; j++) {
                                                            if(j < len){
								actualLongArray.set(j, getRuleComputeValueLong(lastLong, longArray.get(j), state, actualType));
                                                            }else{
                                                                actualLongArray.add(getRuleComputeValueLong(lastLong, longArray.get(j), state, actualType));
                                                            }
							}
						} else {
							int laenge = setArrayLength(lengthLast, lengthArray);
                                                        int len = actualLongArray.size();
							for(int j = 0; j < laenge; j++) {
                                                            if(j < len) {
								actualLongArray.set(j, getRuleComputeValueLong(lastLongArray.get(j), longArray.get(j), state, actualType));
                                                            } else{
 								actualLongArray.add( getRuleComputeValueLong(lastLongArray.get(j), longArray.get(j), state, actualType));
                                                           
                                                            }
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							actualStringArray = new String[stringArray.length]; //nur um die Größe zu zuweisen
							for(int j = 0; j < lengthArray; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(actualString,
									stringArray[j], state);
							}
						} else {
							int laenge = setArrayLength(lengthActual, lengthArray);
							for(int j = 0; j < laenge; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(
									actualStringArray[j], stringArray[j], state);
							}
						}
					} else {
						if(isNullArrayActual) {
							actualStringArray = new String[stringArray.length]; //nur um die Größe zu zuweisen
						}
						if(isNullArrayLast) {
							for(int j = 0; j < lengthArray; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(lastString,
									stringArray[j], state);
							}
						} else {
							int laenge = setArrayLength(lengthLast, lengthArray);
							for(int j = 0; j < laenge; j++) {
								actualStringArray[j] = CheckpointRuleGrouper.getRuleComputeValueString(lastStringArray[
									j], stringArray[j], state);
							}
						}
					}
					break;
				}
/*				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					;
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
					if(elementType == CRGRuleGrouperStatics.TYPE_VALUE) {
						if(isNullArrayActual) {
							actualLongArray = new float[longArray.length]; //nur um die Größe zu zuweisen
							for(int j = 0; j < lengthArray; j++) {
								actualLongArray[j] = getRuleComputeValueLong(actualLong, longArray[j], state);
							}
						} else {
							int laenge = setArrayLength(lengthActual, lengthArray);
							for(int j = 0; j < laenge; j++) {
								actualLongArray[j] = getRuleComputeValueLong(actualLongArray[j], longArray[j], state);
							}
						}
					} else {
						if(isNullArrayActual) {
							actualLongArray = new float[longArray.length]; //nur um die Größe zu zuweisen
						}
						if(isNullArrayLast) {
							for(int j = 0; j < lengthArray; j++) {
								actualLongArray[j] = getRuleComputeValueLong(lastLong, longArray[j], state);
							}
						} else {
							int laenge = setArrayLength(lengthLast, lengthArray);
							for(int j = 0; j < laenge; j++) {
								actualLongArray[j] = getRuleComputeValueLong(lastLongArray[j], longArray[j], state);
							}
						}
					}
					break;
				}
				/*				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
					 if(elementType == CRGRuleGrouperStatics.TYPE_VALUE){
					  if(isNullArrayActual){
					   actualLongTimeArray = new long[longtimeArray.length]; //nur um die Größe zu zuweisen
					   for(int j=0;j<lengthArray;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(actualLongTime, longtimeArray[j], state);
					   }
					  }else{
					   int laenge = setArrayLength(lengthActual, lengthArray);
					   for(int j=0;j<laenge;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(actualLongTimeArray[j], longtimeArray[j], state);
					   }
					  }
					 }else{
					  if(isNullArrayActual){
					   actualLongTimeArray = new long[longtimeArray.length]; //nur um die Größe zu zuweisen
					  }
					  if(isNullArrayLast){
					   for(int j=0;j<lengthArray;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(lastLongTime, longtimeArray[j], state);
					   }
					  }else{
					   int laenge = setArrayLength(lengthLast, lengthArray);
					   for(int j=0;j<laenge;j++){
				 actualLongTimeArray[j] = CheckpointRuleGrouper.getRuleComputeValueLong(actualLongTimeArray[j], longtimeArray[j], state);
					   }
					  }
					 }
					 break;
					}*/
			}
			if(lastCompareState > 0) {
				// in diesem Abschnitt muss natürlich mit dem lastCompareState getestet werden!!!
				if(isNullArray) {
					// isNullArray ist true, wenn ein einzelner Wert gesetzt ist, geprüft werden soll.
					// nur bei nicht Array - Datentypen
					if(!isNullArrayLast) {
						returnVal = false;
						for(int j = 0; j < lengthLast; j++) {
							switch(actualType) {
								case CRGRuleGrouperStatics.DATATYPE_INTEGER:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
								case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
									lastLong = lastLongArray.get(j);
									break;
								}
								case CRGRuleGrouperStatics.DATATYPE_STRING: {
									lastString = lastStringArray[j];
									break;
								}
/*								case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
								case CRGRuleGrouperStatics.DATATYPE_DATE: {
									lastLong = lastLongArray[j];
									break;
								}*/
								/*								case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
										 lastLongTime = lastLongTimeArray[j];
										 break;
										}*/
							}
							returnVal = setCheckType(actualType, lastCompareState);
							if(returnVal) {
								setStateArrayValue(j, true);
								lastState = true;
							} else {
								setStateArrayValue(j, false);
							}
						}
					} else {
						lastState = setCheckType(actualType, lastCompareState);
					}
				} else {
					// isNullArray ist false, wenn ein Array gesetzt ist, geprüft werden soll.
					// nur bei Array - Datentypen
					if(!isNullArrayLast) {
						int laenge = setArrayLength(lengthLast, lengthActual);
						returnVal = false;
						for(int j = 0; j < laenge; j++) {
							switch(actualType) {
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
									// der einzelne Wert wird nicht benötigt, kann genutzt werden
									lastLong = lastLongArray.get(j);
									actualLong = actualLongArray.get(j);
									break;
								}
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
									lastString = lastStringArray[j];
									actualString = actualStringArray[j];
									break;
								}
/*								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
									;
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
									lastLong = lastLongArray[j];
									actualLong = actualLongArray[j];
									break;
								}*/
								/*								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
										 lastLongTime = lastLongTimeArray[j];
										 actualLongTime = actualLongTimeArray[j];
										 break;
										}*/
							}
							returnVal = setCheckType(actualType, lastCompareState);
							if(returnVal) {
								setStateArrayValue(j, true);
								lastState = true;
							} else {
								setStateArrayValue(j, false);
							}
						}
					} else {
						returnVal = false;
						for(int j = 0; j < lengthActual; j++) {
							switch(actualType) {
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
									actualLong = actualLongArray.get(j);
									break;
								}
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
									actualString = actualStringArray[j];
									break;
								}
/*								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
									;
								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
									actualLong = actualLongArray[j];
									break;
								}*/
								/*								case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
										 actualLongTime = actualLongTimeArray[j];
										 break;
										}*/
							}
							returnVal = setCheckType(actualType, lastCompareState);
							if(returnVal) {
								setStateArrayValue(j, true);
								returnVal = true;
							} else {
								setStateArrayValue(j, false);
							}
						}
					}
				}
			} else {
				switch(actualType) {
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DATE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						lastLong = actualLong;
						lastLongArray = actualLongArray;
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_STRING:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						lastString = actualString;
						lastStringArray = actualStringArray;
						break;
					}
/*					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DATE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						lastLongArray = actualLongArray;
						lastLong = actualLong;
						break;
					}*/
					/*					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
						 case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
						  lastLongTimeArray = actualLongTimeArray;
						  lastLongTime = actualLongTime;
						  break;
						 }*/
				}
			}
		} else { //nur ermitteln des Elementen-Wertes
			switch(actualType) {
				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_DATE:
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
					actualLong = longValue;
					lastLong = actualLong;
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_STRING: {
					actualString = stringValue;
					lastString = actualString;
					break;
				}
/*				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
					;
				case CRGRuleGrouperStatics.DATATYPE_DATE: {
					actualLong = longValue;
					lastLong = actualLong;
					break;
				}*/
				/*				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
					 actualLongTime = longtimeValue;
					 lastLongTime = actualLongTime;
					 break;
					}*/
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					;
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
					actualLongArray = longArray;
					lastLongArray = actualLongArray;
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
					actualStringArray = stringArray;
					lastStringArray = actualStringArray;
					break;
				}
/*				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					;
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
					actualLongArray = longArray;
					lastLongArray = actualLongArray;
					break;
				}*/
				/*				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
					 actualLongTimeArray = longtimeArray;
					 lastLongTimeArray = actualLongTimeArray;
					 break;
					}*/
			}
		}
		return lastState;
	}

	public boolean setCheckType(int Type, int state) throws Exception
	{
		boolean isState = false;
		if((Type == CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME ||
			Type == CRGRuleGrouperStatics.DATATYPE_DAY_TIME) &&
			(lastType == CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE ||
			lastType == CRGRuleGrouperStatics.DATATYPE_DATE)) {
			Type = CRGRuleGrouperStatics.DATATYPE_DATE;
		}
		switch(Type) {
/*			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				if(lastLong.equals(this.B_DEFAULT_DOUBLE_VALUE) ||lastLong.equals(this.B_DEFAULT_INT_VALUE) ||lastLong.equals(this.B_DEFAULT_LONG_VALUE)) {
					isState = false;
				} else {
					isState = checkRuleLongValue(lastLong, actualLong, state);
				}
				break;
			}*/
			case CRGRuleGrouperStatics.DATATYPE_STRING:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				if((lastString == null) || (actualString == null) || actualString.length() == 0) {
					return isState;
				}
				isState = CheckpointRuleGrouper.checkRuleStringValue(lastString, actualString, state);
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_INTEGER:
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
			case CRGRuleGrouperStatics.DATATYPE_DATE:
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				if(lastLong.equals(this.B_DEFAULT_DOUBLE_VALUE) ||lastLong.equals(this.B_DEFAULT_INT_VALUE) ||lastLong.equals(this.B_DEFAULT_LONG_VALUE)) {
					isState = false;
				} else {
					isState = checkRuleLongValue(lastLong, actualLong, state);
				}
				break;
			}
			/*			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
			   case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
			 if((lastLongTime==CRGInputOutput.DEFAULT_LONG_VALUE)||(actualLongTime==CRGInputOutput.DEFAULT_LONG_VALUE))
				 isState = false;
				else
				 isState = CheckpointRuleGrouper.checkRuleLongValue(lastLongTime, actualLongTime, state);
				break;
			   }*/
		}
		return isState;
	}

	public int setArrayLength(int length1, int length2) throws Exception
	{
		//länge von actual und last unterschiedlich nicht sehr wahrscheinlich
		//möglichkeit noch suchen
		if(length1 == length2) {
			return length1;
		}
		int laenge = 0, laengeCut = 0;
		if(length1 < length2) {
			laenge = length1;
			laengeCut = length2 - laenge;
		} else {
			laenge = length2;
			laengeCut = length1 - laenge;
		}
		for(int j = 0; j < laengeCut; j++) {
			setStateArrayValue(j + laenge, false);
		}
		return laenge;
	}

	private void setStateArrayValue(int ind, boolean value)
	{
		stateArray = setArrayValue(ind, value, stateArray);
	}

	public void setGroupDependResultsValue(int ind, boolean value)
	{

		groupDependResults = setArrayValue(ind, value, groupDependResults);
	}

	private boolean[] setArrayValue(int ind, boolean value, boolean[] array)
	{
		try {
			array[ind] = value;
		} catch(IndexOutOfBoundsException e) {
			int i;
			boolean[] tmp = new boolean[ind + MIN_ARRAY_SIZE];
			for(i = 0; i < array.length; i++) {
				tmp[i] = array[i];
			}
			for(i = i; i < tmp.length; i++) {
				tmp[i] = false;
			}
			array = tmp;
			array = setArrayValue(ind, value, array);
		}
		return array;
	}

	public int setRuleElementUnformatedValue(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		return 0;
	}

	/**
	 * liefert null wenn für die Gruppe groupDependIndex
	 * noch kein Array angelegt wurde
	 *
	 * @param i int
	 * @return boolean
	 */
	public boolean[] getGroupResults(int groupDependIndex)
	{
		try {
			return groupDependsIndex[groupDependIndex];
		} catch(ArrayIndexOutOfBoundsException ex) {
			return null;
		}
	}

	/**
	 * setzt den Array aus groupDependResluts auf die groupDependIndex Stelle in
	 * den Array groupDependsIndex
	 *
	 * @param i int
	 */
	public void setGroupResults(int groupDependIndex)
	{
		if(groupDependsIndex.length < groupDependIndex) {
			int i;
			boolean[][] tmp = new boolean[groupDependIndex + 1][0];
			for(i = 0; i < groupDependsIndex.length; i++) {
				tmp[i] = groupDependsIndex[i];
			}
			for(i = i; i < tmp.length; i++) {
				tmp[i] = new boolean[0];
			}
			groupDependsIndex = tmp;

		}
		for(int i = 0; i < groupDependResults.length; i++) {
			groupDependsIndex[groupDependIndex] = setArrayValue(i, groupDependResults[i],
												  groupDependsIndex[groupDependIndex]);
		}

	}

	/**
	 * checkGroupIndexValues
	 *
	 * @param groupDependIndex int
	 * @param state int
	 * @return boolean
	 */
	public boolean checkGroupIndexValues(int groupDependIndex, int state, boolean retState)
	{
		boolean[] array = getGroupResults(groupDependIndex);
		return checkGroupIndexValues(state, retState, array);
	}

	public boolean checkGroupIndexValues(int state, boolean retState, boolean[] array)
	{
		boolean ret = false;
		if(array == null || groupDependResults == null || array.length == 0 || groupDependResults.length == 0) {
			return retState;
		}
		if(state == CRGRuleGrouperStatics.OP_AND) {
			for(int i = 0; i < groupDependResults.length; i++) {
				ret = groupDependResults[i] && array.length > i && array[i];
				if(ret) {
					return true;
				}
			}
			return false;

		} else if(state == CRGRuleGrouperStatics.OP_OR) {
			for(int i = 0; i < groupDependResults.length; i++) {
				ret = groupDependResults[i] || (array.length > i && array[i]);
				if(ret) {
					return true;
				}
			}
			return false;
		}
		return retState;
	}

	public boolean checkDependValues(int state, boolean retState, int[] oldDeps)
	{
		boolean ret = false;
		if(oldDeps == null
			|| dependIndex == null
			|| dependIndex.length != oldDeps.length) {
			return retState;
		}
		if(state == CRGRuleGrouperStatics.OP_AND) {
			int count = 0;
			for(int i = 0; i < oldDeps.length; i++) {
				if(oldDeps[i] == dependIndex[i] && dependIndex[i] != 0) {
					count++;
					if(count >= this.minCountForDependCompareOperation)
						return true;
				}
			}
			return false;

		} else if(state == CRGRuleGrouperStatics.OP_OR) {
			for(int i = 0; i < oldDeps.length; i++) {
				if(oldDeps[i] != 0 || dependIndex[i] != 0) {
					return true;
				}
			}
			return false;
		}
		return retState;
	}

	/**
	 * resetGroupIndexResult
	 *
	 * @param groupDependIndex int
	 */
	public void resetGroupIndexResult(int groupIndex)
	{
		if(groupIndex >= 0 && groupIndex < groupDependsIndex.length) {
			groupDependsIndex[groupIndex] = new boolean[0];
		}
	}

	public void resetDepends()
	{
		dependIndex = null;
		isDepend = false;
		lastDepended = -1;
		lastLevelOfDepended = -1;
		setDependCountForSingle();
	}

	protected static BigDecimal getRuleComputeValueLong(BigDecimal value, BigDecimal critValue, int opType, int actType) throws Exception
	{
		// hier abhängig von actType, myDec = myDec.setScale( 2, BigDecimal.ROUND_HALF_UP ); setzen
		switch(actType){
		case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
		case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:{
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Rückgabewert wird gar nicht verarbeitet.
		   * Korrektur: value = value.setScale(3, BigDecimal.ROUND_DOWN); */
			value.setScale(3, BigDecimal.ROUND_DOWN);
			critValue.setScale(3, BigDecimal.ROUND_DOWN);
			break;
		}
		default:
			value.setScale(0, BigDecimal.ROUND_DOWN);
			critValue.setScale(0, BigDecimal.ROUND_DOWN);
		};
		switch(opType) {
			case CRGRuleGrouperStatics.OP_PLUS: {
				return value.add( critValue);
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return value.subtract( critValue);
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return value.multiply( critValue);
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				if(critValue.equals(BigDecimal.ZERO))
					return BigDecimal.ZERO;
				try{
                                    if(actType >= CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE
                                            && actType <= CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME
                                            || actType == CRGRuleGrouperStatics.DATATYPE_DATE){
                                        long v = value.longValue();
                                        long c = critValue.longValue();
                                        return new BigDecimal(v/c);
                                    }else{
                                        double v = value.doubleValue();
                                        double c = critValue.doubleValue();
                                        return new BigDecimal(v/c);
                                    }
//					return value.divide(critValue);
				}catch(Exception e){
					return BigDecimal.ZERO;
				}
			}
			case CRGRuleGrouperStatics.OP_NO_OPERATION: {
				return critValue;
			}
			default:
				return BigDecimal.ZERO;
		}
	}

	protected static boolean checkRuleLongValue(BigDecimal value, BigDecimal critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
				return critValue.equals(value);
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				return !critValue.equals(value);
			}
			case CRGRuleGrouperStatics.OP_GT: {
				return value.compareTo( critValue)>0;
			}
			case CRGRuleGrouperStatics.OP_GT_EQUAL: {
				return value.compareTo( critValue) >= 0;
			}
			case CRGRuleGrouperStatics.OP_LT: {
				return value.compareTo( critValue) < 0;
			}
			case CRGRuleGrouperStatics.OP_LT_EQUAL: {
				return value.compareTo( critValue) <= 0;
			}
			case CRGRuleGrouperStatics.OP_PLUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return true;
			}
			default:
				return false;
		}
	}

	protected void setDependCountForMany()
	{
		minCountForDependCompareOperation = 2;
	}

	protected void setDependCountForSingle()
	{
		minCountForDependCompareOperation = 1;
	}

}
