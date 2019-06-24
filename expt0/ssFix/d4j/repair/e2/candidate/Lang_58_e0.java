/*
 * @(#)Numbers.java	2009-8-8
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package org.javaclub.jorm.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Operations for number.
 * 
 * @author <a href="mailto:gerald.chen@qq.com">Gerald Chen</a>
 * @version $Id: Numbers.java 65 2011-06-27 03:20:47Z gerald.chen.hz@gmail.com $
 */
public abstract class Numbers {

	private final static String BOOLSTR_TRUE = String.valueOf(true);
	
	private static final int INT_FALSE = 0;
	
	private static final int INT_TRUE = 1;
	
	private static Random random = new Random();
	
	public static int random(int max) {
		AssertUtil.isTrue(max > 0, "the parameter [max]'s value must be a positive Integer.");
		return (int) (Math.random() * max);
	}
	
	public static long random(long max) {
		AssertUtil.isTrue(max > 0, "the parameter [max]'s value must be a positive Integer.");
		return (long) (Math.random() * max);
	}
	
	public static double random(double max) {
		AssertUtil.isTrue(max > 0, "the parameter [max]'s value must be a positive Double.");
		return Math.random() * max;
	}
	
	/**
	 * Returns a pseudorandom, uniformly distributed int value between min
	 * (inclusive) and the max (inclusive)
	 */
	public static int random(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}
	
	public static long random(long min, long max) {
		return random(max - min) + min;
	}

	/**
	 * Generates specified count of no repeated random Integer
	 * 
	 * @param min first Integer of the range
	 * @param max last Integer of the range
	 * @param count the total number of the Integer generated in a specified
	 * @return
	 */
	public static int[] random(int min, int max, int count) {
		if (min > max) {
			throw new IllegalArgumentException(
					"the parameter min must be less than max.");
		}
		int n = max - min + 1;
		if (count > n) {
			throw new IllegalArgumentException(
					"the parameter count must be no more than " + n + ".");
		}
		int[] span = new int[n];
		for (int i = 0, j = min; i < n; i++, j++) {
			span[i] = j;
		}

		// for the random Integers
		int[] target = new int[count];
		for (int i = 0; i < target.length; i++) {
			int r = (int) (Math.random() * n);
			target[i] = span[r];
			span[r] = span[n - 1];
			n--;
		}
		return target;
	}
	
	/**
	 * convert string to integer
	 * 
	 * @param p_sValue string value
	 * @param p_sDesc description of the value
	 * @param p_nDefault default integer value
	 * @return Returns the default integer if the value is empty; otherwise, returns the integer converted
	 * @throws RuntimeException if encounter errors when converting data
	 */
	public final static int toInt(String _sValue, int _nDefault) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			return _nDefault;
		}

		try {
			return Integer.parseInt(_sValue);
		} catch (NumberFormatException ex) {
			return _nDefault;
		}
	}

	/**
	 * convert string to integer
	 * 
	 * @param _sValue string value
	 * @param _sDesc description of the value
	 * @return Returns the integer value
	 * @throws RuntimeException if find the value is empty, or encounter errors when converting data
	 */
	public final static int toInt(String _sValue, String _sDesc) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			throw new RuntimeException(_sDesc + " (int string) required.");
		}

		try {
			return Integer.parseInt(_sValue);
		} catch (Exception ex) {
			throw new RuntimeException("Value [" + _sValue + "] of " + _sDesc
					+ " is not a valid integer!");
		}
	}

	/**
	 * convert string to long integer
	 * 
	 * @param _sValue string value
	 * @param _sDesc description of the value
	 * @param _lDefault default long integer value
	 * @return Returns the default long integer if the value is empty; otherwise, returns the long integer converted
	 * @throws RuntimeException if encounter errors when converting data
	 */
	public final static long toLong(String _sValue, long _lDefault) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			return _lDefault;
		}

		try {
			return Long.parseLong(_sValue);
		} catch (NumberFormatException e) {
			return _lDefault;
		}
	}

	/**
	 * convert string to long integer
	 * 
	 * @param _sValue string value
	 * @param _sDesc description of the value
	 * @return Returns the long integer value converted
	 * @throws RuntimeException if find the value is empty, or encounter errors when converting data
	 */
	public final static long toLong(String _sValue, String _sDesc) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			throw new RuntimeException(_sDesc + " (long string) required.");
		}

		try {
			return Long.parseLong(_sValue);
		} catch (Exception ex) {
			throw new RuntimeException("value [" + _sValue + "] of " + _sDesc
					+ " is not a valid long integer!");
		}
	}

	/**
	 * convert string to double
	 * 
	 * @param _sValue string value
	 * @param _sDesc description of the value
	 * @param _default default double value
	 * @return Returns the default double if the value is empty; otherwise, returns the double converted
	 * @throws RuntimeException if encounter errors when converting data
	 */
	public final static double toDouble(String _sValue, double _default) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			return _default;
		}

		try {
			return Double.parseDouble(_sValue);
		} catch (NumberFormatException e) {
			return _default;
		}

	}

	/**
	 * convert string to double
	 * 
	 * @param _sValue string value
	 * @param _sDesc description of the value
	 * @return Returns the double value converted
	 * @throws RuntimeException if find the value is empty, or encounter errors when converting data
	 */
	public final static double toDouble(String _sValue, String _sDesc) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			throw new RuntimeException(_sDesc + " (double string) required.");
		}

		try {
			return Double.parseDouble(_sValue);
		} catch (Exception ex) {
			throw new RuntimeException("value [" + _sValue + "] of " + _sDesc
					+ " is not a valid double!");
		}
	}

	/**
	 * string to boolean
	 * 
	 * @param _sValue string value
	 * @return Returns the boolean value converted
	 * @throws RuntimeException if find the value is empty, or encounter errors when converting data
	 */
	public final static boolean toBool(String _sValue, String _sDesc) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			throw new RuntimeException(_sDesc + " (bool string) required.");
		}

		// else
		return BOOLSTR_TRUE.equalsIgnoreCase(_sValue);
	}

	/**
	 * string to boolean
	 * 
	 * @param _sValue string value
	 * @param _bDefault default value
	 * @return Returns the default boolean value if the value is empty, otherwise, returns boolean value converted
	 */
	public final static boolean toBool(String _sValue, boolean _bDefault) {
		if (_sValue == null || _sValue.length() == 0
				|| (_sValue = _sValue.trim()).length() == 0) {
			return _bDefault;
		}

		// else
		return BOOLSTR_TRUE.equalsIgnoreCase(_sValue);
	}
	
	/**
	 * convert integer string to boolean
	 * 
	 * @param _sValue integer value in string format
	 * @param _sDesc description of the value
	 * @param _bDefault default value
	 * @return Returns the default boolean value if the string value is empty; else, returns the boolean value converted.
	 */
	public final static boolean intToBool(String _sValue, String _sDesc) {
		int nValue = toInt(_sValue, _sDesc);
		return (nValue != INT_FALSE);
	}
	
	/**
	 * convert integer string to boolean
	 * 
	 * @param _sValue integer value in string format
	 * @param _sDesc description of the value
	 * @param _bDefault default value
	 * @return Returns the default boolean value if the string value is empty; else, returns the boolean value converted.
	 */
	public final static boolean intToBool(String _sValue, String _sDesc,
			boolean _bDefault) {
		int nValue = toInt(_sValue, (_bDefault ? INT_TRUE : INT_FALSE));
		return (nValue != INT_FALSE);
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Checks whether the <code>String</code> contains only digit characters.
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> and empty String will return <code>false</code>.
	 * </p>
	 * 
	 * @param str the <code>String</code> to check
	 * @return <code>true</code> if str contains only unicode numeric
	 */
	public static boolean isDigits(String str) {
		if (!Strings.hasLength(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks whether the String a valid Java number.
	 * </p>
	 * 
	 * <p>
	 * Valid numbers include hexadecimal marked with the <code>0x</code> qualifier, scientific notation and numbers marked with a type qualifier
	 * (e.g. 123L).
	 * </p>
	 * 
	 * <p>
	 * <code>Null</code> and empty String will return <code>false</code>.
	 * </p>
	 * 
	 * @param str the <code>String</code> to check
	 * @return <code>true</code> if the string is a correctly formatted number
	 */
	public static boolean isNumber(String str) {
		if (!Strings.hasLength(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;
		// deal with any possible sign up front
		int start = (chars[0] == '-') ? 1 : 0;
		if (sz > start + 1) {
			if (chars[start] == '0' && chars[start + 1] == 'x') {
				int i = start + 2;
				if (i == sz) {
					return false; // str == "0x"
				}
				// checking hex (it can't be anything else)
				for (; i < chars.length; i++) {
					if ((chars[i] < '0' || chars[i] > '9')
							&& (chars[i] < 'a' || chars[i] > 'f')
							&& (chars[i] < 'A' || chars[i] > 'F')) {
						return false;
					}
				}
				return true;
			}
		}
		sz--; // don't want to loop to the last char, check it afterwords
		// for type qualifiers
		int i = start;
		// loop to the next to last char or to the last char if we need another digit to
		// make a valid number (e.g. chars[0..5] = "1234E")
		while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				foundDigit = true;
				allowSigns = false;

			} else if (chars[i] == '.') {
				if (hasDecPoint || hasExp) {
					// two decimal points or dec in exponent
					return false;
				}
				hasDecPoint = true;
			} else if (chars[i] == 'e' || chars[i] == 'E') {
				// we've already taken care of hex.
				if (hasExp) {
					// two E's
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if (chars[i] == '+' || chars[i] == '-') {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false; // we need a digit after the E
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if (chars[i] >= '0' && chars[i] <= '9') {
				// no type qualifier, OK
				return true;
			}
			if (chars[i] == 'e' || chars[i] == 'E') {
				// can't have an E at the last byte
				return false;
			}
			if (!allowSigns
					&& (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
				return foundDigit;
			}
			if (chars[i] == 'l' || chars[i] == 'L') {
				// not allowing L with an exponent
				return foundDigit && !hasExp;
			}
			// last character is illegal
			return false;
		}
		// allowSigns is true iff the val ends in 'E'
		// found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
		return !allowSigns && foundDigit;
	}

	/**
	 * <p>
	 * Turns a string value into a java.lang.Number.
	 * </p>
	 * 
	 * <p>
	 * First, the value is examined for a type qualifier on the end (<code>'f','F','d','D','l','L'</code>). If it is found, it starts
	 * trying to create successively larger types from the type specified until one is found that can represent the value.
	 * </p>
	 * 
	 * <p>
	 * If a type specifier is not found, it will check for a decimal point and then try successively larger types from
	 * <code>Integer</code> to <code>BigInteger</code> and from <code>Float</code> to <code>BigDecimal</code>.
	 * </p>
	 * 
	 * <p>
	 * If the string starts with <code>0x</code> or <code>-0x</code>, it will be interpreted as a hexadecimal integer. Values with leading <code>0</code>'s
	 * will not be interpreted as octal.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * <p>
	 * This method does not trim the input string, i.e., strings with leading or trailing spaces will generate NumberFormatExceptions.
	 * </p>
	 * 
	 * @param str String containing a number, may be null
	 * @return Number created from the string
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Number createNumber(String str) throws NumberFormatException {
		if (str == null) {
			return null;
		}
		if (!Strings.hasText(str)) {
			throw new NumberFormatException("A blank string is not a valid number");
		}
		if (str.startsWith("--")) {
			// this is protection for poorness in java.lang.BigDecimal.
			// it accepts this as a legal value, but it does not appear
			// to be in specification of class. OS X Java parses it to
			// a wrong value.
			return null;
		}
		if (str.startsWith("0x") || str.startsWith("-0x")) {
			return createInteger(str);
		}
		char lastChar = str.charAt(str.length() - 1);
		String mant;
		String dec;
		String exp;
		int decPos = str.indexOf('.');
		int expPos = str.indexOf('e') + str.indexOf('E') + 1;

		if (decPos > -1) {

			if (expPos > -1) {
				if (expPos < decPos) {
					throw new NumberFormatException(str + " is not a valid number.");
				}
				dec = str.substring(decPos + 1, expPos);
			} else {
				dec = str.substring(decPos + 1);
			}
			mant = str.substring(0, decPos);
		} else {
			if (expPos > -1) {
				mant = str.substring(0, expPos);
			} else {
				mant = str;
			}
			dec = null;
		}
		if (!Character.isDigit(lastChar)) {
			if (expPos > -1 && expPos < str.length() - 1) {
				exp = str.substring(expPos + 1, str.length() - 1);
			} else {
				exp = null;
			}
			// Requesting a specific type..
			String numeric = str.substring(0, str.length() - 1);
			boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
			switch (lastChar) {
				case 'l':
				case 'L':
					if (dec == null
							&& exp == null
							&& (numeric.charAt(0) == '-'
									&& isDigits(numeric.substring(1)) || isDigits(numeric))) {
						try {
							return createLong(numeric);
						} catch (NumberFormatException nfe) {
							// Too big for a long
						}
						return createBigInteger(numeric);

					}
					throw new NumberFormatException(str + " is not a valid number.");
				case 'f':
				case 'F':
					try {
						Float f = Numbers.createFloat(numeric);
						if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros))) {
							// If it's too big for a float or the float value = 0 and the string
							// has non-zeros in it, then float does not have the precision we want
							return f;
						}

					} catch (NumberFormatException nfe) {
						// ignore the bad number
					}
					// Fall through
				case 'd':
				case 'D':
					try {
						Double d = Numbers.createDouble(numeric);
						if (!(d.isInfinite() || (d.floatValue() == 0.0D && !allZeros))) {
							return d;
						}
					} catch (NumberFormatException nfe) {
						// ignore the bad number
					}
					try {
						return createBigDecimal(numeric);
					} catch (NumberFormatException e) {
						// ignore the bad number
					}
					// Fall through
				default:
					throw new NumberFormatException(str + " is not a valid number.");

			}
		} else {
			// User doesn't have a preference on the return type, so let's start
			// small and go from there...
			if (expPos > -1 && expPos < str.length() - 1) {
				exp = str.substring(expPos + 1, str.length());
			} else {
				exp = null;
			}
			if (dec == null && exp == null) {
				// Must be an int,long,bigint
				try {
					return createInteger(str);
				} catch (NumberFormatException nfe) {
					// ignore the bad number
				}
				try {
					return createLong(str);
				} catch (NumberFormatException nfe) {
					// ignore the bad number
				}
				return createBigInteger(str);

			} else {
				// Must be a float,double,BigDec
				boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
				try {
					Float f = createFloat(str);
					if (!(f.isInfinite() || (f.floatValue() == 0.0F && !allZeros))) {
						return f;
					}
				} catch (NumberFormatException nfe) {
					// ignore the bad number
				}
				try {
					Double d = createDouble(str);
					if (!(d.isInfinite() || (d.doubleValue() == 0.0D && !allZeros))) {
						return d;
					}
				} catch (NumberFormatException nfe) {
					// ignore the bad number
				}

				return createBigDecimal(str);

			}
		}
	}

	/**
	 * <p>
	 * Utility method for {@link #createNumber(java.lang.String)}.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>true</code> if s is <code>null</code>.
	 * </p>
	 * 
	 * @param str the String to check
	 * @return if it is all zeros or <code>null</code>
	 */
	private static boolean isAllZeros(String str) {
		if (str == null) {
			return true;
		}
		for (int i = str.length() - 1; i >= 0; i--) {
			if (str.charAt(i) != '0') {
				return false;
			}
		}
		return str.length() > 0;
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>Float</code>.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Float</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Float createFloat(String str) {
		if (str == null) {
			return null;
		}
		return Float.valueOf(str);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>Double</code>.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Double</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Double createDouble(String str) {
		if (str == null) {
			return null;
		}
		return Double.valueOf(str);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>Integer</code>, handling hex and octal notations.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Integer</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Integer createInteger(String str) {
		if (str == null) {
			return null;
		}
		// decode() handles 0xAABD and 0777 (hex and octal) as well.
		return Integer.decode(str);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>Long</code>.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>Long</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static Long createLong(String str) {
		if (str == null) {
			return null;
		}
		return Long.valueOf(str);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>BigInteger</code>.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>BigInteger</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static BigInteger createBigInteger(String str) {
		if (str == null) {
			return null;
		}
		return new BigInteger(str);
	}

	/**
	 * <p>
	 * Convert a <code>String</code> to a <code>BigDecimal</code>.
	 * </p>
	 * 
	 * <p>
	 * Returns <code>null</code> if the string is <code>null</code>.
	 * </p>
	 * 
	 * @param str a <code>String</code> to convert, may be null
	 * @return converted <code>BigDecimal</code>
	 * @throws NumberFormatException if the value cannot be converted
	 */
	public static BigDecimal createBigDecimal(String str) {
		if (str == null) {
			return null;
		}
		// handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
		if (!Strings.hasText(str)) {
			throw new NumberFormatException("A blank string is not a valid number");
		}
		return new BigDecimal(str);
	}
	
	public static Integer add(Integer a, Integer b) {
        return new Integer(a.intValue() + b.intValue());
    }

    public static Integer sub(Integer a, Integer b) {
        return new Integer(a.intValue() - b.intValue());
    }

    public static Long add(Long a, Long b) {
        return new Long(a.longValue() + b.longValue());
    }

    public static Long sub(Long a, Long b) {
        return new Long(a.longValue() - b.longValue());
    }

    public static Short add(Short a, Short b) {
        return new Short((short) (a.shortValue() + b.shortValue()));
    }

    public static Short minus(Short a, Short b) {
        return new Short((short) (a.shortValue() - b.shortValue()));
    }

    public static String format(Number number, String pattern) {
        DecimalFormat formatter = new DecimalFormat(pattern);
        return formatter.format(number);
    }
    
    /**
     * 格式化double型数据，默认保留4位小数
     *
     * @param d
     * @return
     */
    public static String formatDouble(double d) {
    	return new DecimalFormat("##########.####").format(d * 10000d / 10000d);
    }
    
    /**
     * 格式化double型数据为百分比型数据
     *
     * @param d
     * @param minFraction 最少小数保留位数
     * @param maxFraction 最多小数保留位数
     * @return
     */
    public static String formatPercent(double d, int minFraction, int maxFraction) {
		NumberFormat numberFormat = NumberFormat.getPercentInstance();
		// 最少保留小数位数
		if(minFraction > 0) {
			numberFormat.setMinimumFractionDigits(minFraction);
		} else {
			numberFormat.setMinimumFractionDigits(2);
		}
		
		// 最多保留小数位数
		if(maxFraction > 0) {
			numberFormat.setMaximumFractionDigits(maxFraction);
		} else {
			numberFormat.setMaximumFractionDigits(2);
		}
		return numberFormat.format(d * 10000d / 10000d);
	}
    
    /**
     * 将基类整型数据数组转换为整型对象数组.
     *
     * @param array 基类整型数据数组
     * @return 整型对象数组
     */
    public static Integer[] toIntegerArray(int[] array) {
    	if(null == array) {
    		return null;
    	}
    	Integer[] iarray = new Integer[array.length];
    	
    	for (int i = 0; i < array.length; i++) {
			iarray[i] = new Integer(array[i]);
		}
    	return iarray;
    }
    
    /**
     * 将基类长整型数据数组转换为长整型对象数组.
     * 
     * @param array 基类长整型数据数组
     * @return 长整型对象数组
     */
    public static Long[] toLongArray(long[] array) {
    	if(null == array) {
    		return null;
    	}
    	Long[] iarray = new Long[array.length];
    	
    	for (int i = 0; i < array.length; i++) {
			iarray[i] = new Long(array[i]);
		}
    	return iarray;
    }

}
