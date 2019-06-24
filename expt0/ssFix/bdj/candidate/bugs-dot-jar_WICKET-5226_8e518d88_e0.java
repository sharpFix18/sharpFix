/*
 * Copyright 2010 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.toolkit.util.enumeration;

import com.alibaba.toolkit.util.collection.ArrayHashMap;
import com.alibaba.toolkit.util.collection.ListMap;
import com.alibaba.toolkit.util.typeconvert.ConvertChain;
import com.alibaba.toolkit.util.typeconvert.Converter;
import com.alibaba.toolkit.util.typeconvert.Convertible;

import java.io.InvalidClassException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.text.MessageFormat;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * ���Ͱ�ȫ��ö������.
 *
 * @version $Id: Enum.java,v 1.1 2003/07/03 07:26:20 baobao Exp $
 * @author Michael Zhou
 */
public abstract class Enum implements IntegralNumber, Comparable, Serializable,
                                                     Convertible {
    private static final long serialVersionUID = -3420208858441821772L;
    private static final Map  entries = new WeakHashMap();
    private final String      name;
    private final Object      value;

    /**
     * ����һ��ö����.  ��ö����������һ���Զ�������ֵ.  ���ֵȡ����<code>Enum</code>������, һ���ǵ�����.
     * ���<code>Enum</code>��ʵ����<code>Flags</code>�ӿ�, �����ֵ�Ǳ�����(����).
     *
     * @param name ö����������
     */
    protected Enum(String name) {
        this(name, null, false);
    }

    /**
     * ����һ��ö����, ������ָ����ֵ.
     *
     * @param name  ö����������
     * @param value ö������ֵ, ���ֵ����Ϊ<code>null</code>
     */
    protected Enum(String name, Object value) {
        this(name, value, true);
    }

    /**
     * ����һ��ö����.
     *
     * @param name      ö����������
     * @param value     ö������ֵ
     * @param withValue �����<code>true</code>, ���ö����������ָ����ֵ, �����ö������������һ���Զ�������ֵ
     */
    private Enum(String name, Object value, boolean withValue) {
        if ((name == null) || ((name = name.trim()).length() == 0)) {
            throw new IllegalArgumentException(EnumConstants.ENUM_NAME_IS_EMPTY);
        }

        if (withValue && (value == null)) {
            throw new NullPointerException(EnumConstants.ENUM_VALUE_IS_NULL);
        }

        this.name = name;

        Class    enumClass = getClass();
        EnumType enumType = getEnumType(enumClass);
        boolean  flagMode = this instanceof Flags;

        if (withValue) {
            this.value = enumType.setValue(value, flagMode);
        } else {
            this.value = enumType.getNextValue(flagMode);
        }

        if (enumType.nameMap.containsKey(name)) {
            throw new IllegalArgumentException(MessageFormat.format(
                                                       EnumConstants.DUPLICATED_ENUM_NAME,
                                                       new Object[] {
                name,
                enumClass.getName()
            }));
        }

        enumType.nameMap.put(name, this);

        // ��enum����valueMap, ����ж��enum��ֵ��ͬ, ��ȡ��һ��
        if (!enumType.valueMap.containsKey(this.value)) {
            enumType.valueMap.put(this.value, this);
        }

        // �����flagģʽ, �򽫵�ǰenum����ȫ��
        if (flagMode) {
            if (enumType.fullSet == null) {
                try {
                    enumType.fullSet = createFlagSet(enumClass);
                } catch (UnsupportedOperationException e) {
                }
            }

            if (enumType.fullSet != null) {
                enumType.fullSet.set((Flags) this);
            }
        }
    }

    /**
     * ȡ��<code>Enum</code>ֵ������.
     *
     * @param enumClass  ö������
     *
     * @return <code>Enum</code>ֵ������
     */
    public static Class getUnderlyingClass(Class enumClass) {
        return getEnumType(enumClass).getUnderlyingClass();
    }

    /**
     * �ж�ָ�����Ƶ�ö�����Ƿ񱻶���.
     *
     * @param enumClass  ö������
     * @param name       ö����������
     *
     * @return �������, �򷵻�<code>true</code>
     */
    public static boolean isNameDefined(Class enumClass, String name) {
        return getEnumType(enumClass).nameMap.containsKey(name);
    }

    /**
     * �ж�ָ��ֵ��ö�����Ƿ񱻶���.
     *
     * @param enumClass  ö������
     * @param value      ö������ֵ
     *
     * @return �������, �򷵻�<code>true</code>
     */
    public static boolean isValueDefined(Class enumClass, Object value) {
        return getEnumType(enumClass).valueMap.containsKey(value);
    }

    /**
     * ȡ��ָ�����Ƶ�ö����.
     *
     * @param enumClass  ö������
     * @param name       ö����������
     *
     * @return ö����, ���������, �򷵻�<code>null</code>
     */
    public static Enum getEnumByName(Class enumClass, String name) {
        return (Enum) getEnumType(enumClass).nameMap.get(name);
    }

    /**
     * ȡ��ָ��ֵ��ö����.
     *
     * @param enumClass  ö������
     * @param value      ö������ֵ
     *
     * @return ö����, ���������, �򷵻�<code>null</code>
     */
    public static Enum getEnumByValue(Class enumClass, Object value) {
        return (Enum) getEnumType(enumClass).valueMap.get(value);
    }

    /**
     * ȡ��ָ�����͵�����ö������<code>Map</code>, ��<code>Map</code>�������.
     *
     * @param enumClass ö������
     *
     * @return ָ�����͵�����ö������<code>Map</code>
     */
    public static Map getEnumMap(Class enumClass) {
        return Collections.unmodifiableMap(getEnumType(enumClass).nameMap);
    }

    /**
     * ȡ��ָ�����͵�����ö������<code>Iterator</code>.
     *
     * @param enumClass ö������
     *
     * @return ָ�����͵�����ö������<code>Iterator</code>
     */
    public static Iterator iterator(Class enumClass) {
        return getEnumMap(enumClass).values().iterator();
    }

    /**
     * ������ָ��ö�����Ӧ�Ŀ�λ��.
     *
     * @param enumClass  ö����
     *
     * @return ��λ��
     */
    public static FlagSet createFlagSet(Class enumClass) {
        if (!(Flags.class.isAssignableFrom(enumClass))) {
            throw new UnsupportedOperationException(MessageFormat.format(
                                                            EnumConstants.ENUM_IS_NOT_A_FLAG,
                                                            new Object[] {
                enumClass.getName()
            }));
        }

        EnumType enumType = getEnumType(enumClass);

        if (enumType.flagSetClassExists && (enumType.flagSetClass == null)) {
            enumType.flagSetClass = findStaticInnerClass(enumClass,
                                                         EnumConstants.FLAG_SET_INNER_CLASS_NAME,
                                                         FlagSet.class);

            if (enumType.flagSetClass == null) {
                enumType.flagSetClassExists = false;
            }
        }

        if (enumType.flagSetClassExists && (enumType.flagSetClass != null)) {
            try {
                return (FlagSet) enumType.flagSetClass.newInstance();
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e) {
            }
        }

        throw new UnsupportedOperationException(MessageFormat.format(
                                                        EnumConstants.CREATE_FLAG_SET_IS_UNSUPPORTED,
                                                        new Object[] {
            enumClass.getName()
        }));
    }

    /**
     * ����ȫ��.
     *
     * @param enumClass  ö������
     *
     * @return ��ǰö�����͵�ȫ��
     */
    public static FlagSet createFullSet(Class enumClass) {
        FlagSet  flagSet  = createFlagSet(enumClass);
        EnumType enumType = getEnumType(enumClass);

        if ((flagSet != null) && (enumType.fullSet != null)) {
            flagSet.set(enumType.fullSet);
        }

        return flagSet;
    }

    /**
     * ȡ��ָ�����<code>ClassLoader</code>��Ӧ��entry��.
     *
     * @param enumClass  <code>Enum</code>��
     *
     * @return entry��
     */
    private static Map getEnumEntryMap(Class enumClass) {
        ClassLoader classLoader = enumClass.getClassLoader();
        Map         entryMap = null;
        synchronized (entries) {
            entryMap = (Map) entries.get(classLoader);

            if (entryMap == null) {
                entryMap = new Hashtable();
                entries.put(classLoader, entryMap);
            }
        }

        return entryMap;
    }

    /**
     * ȡ��<code>Enum</code>���<code>EnumType</code>
     *
     * @param enumClass <code>Enum</code>��
     *
     * @return <code>Enum</code>���Ӧ��<code>EnumType</code>����
     */
    private static EnumType getEnumType(Class enumClass) {
        if (enumClass == null) {
            throw new NullPointerException(EnumConstants.ENUM_CLASS_IS_NULL);
        }

        if (!Enum.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException(MessageFormat.format(EnumConstants.CLASS_IS_NOT_ENUM,
                                                                    new Object[] {
                enumClass.getName()
            }));
        }

        Map      entryMap = getEnumEntryMap(enumClass);
        EnumType enumType = (EnumType) entryMap.get(enumClass.getName());

        if (enumType == null) {
            Method createEnumTypeMethod = findStaticMethod(enumClass,
                                                           EnumConstants.CREATE_ENUM_TYPE_METHOD_NAME,
                                                           new Class[0]);

            if (createEnumTypeMethod != null) {
                try {
                    enumType = (EnumType) createEnumTypeMethod.invoke(null, new Object[0]);
                } catch (IllegalAccessException e) {
                } catch (IllegalArgumentException e) {
                } catch (InvocationTargetException e) {
                } catch (ClassCastException e) {
                }
            }

            if (enumType != null) {
                entryMap.put(enumClass.getName(), enumType);
            }
        }

        if (enumType == null) {
            throw new UnsupportedOperationException(MessageFormat.format(
                                                            EnumConstants.FAILED_CREATING_ENUM_TYPE,
                                                            new Object[] {
                enumClass.getName()
            }));
        }

        return enumType;
    }

    /**
     * ���ҷ���.
     *
     * @param enumClass   ö������
     * @param methodName  ������
     * @param paramTypes  �������ͱ�
     *
     * @return ��������, ��<code>null</code>��ʾδ�ҵ�
     */
    private static Method findStaticMethod(Class enumClass, String methodName, Class[] paramTypes) {
        Method method = null;

        for (Class clazz = enumClass; !clazz.equals(Enum.class); clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (NoSuchMethodException e) {
            }
        }

        if ((method != null) && Modifier.isStatic(method.getModifiers())) {
            return method;
        }

        return null;
    }

    /**
     * �����ڲ���.
     *
     * @param enumClass       ö������
     * @param innerClassName  ������
     * @param superClass      ����
     *
     * @return �ڲ������, ��<code>null</code>��ʾδ�ҵ�
     */
    private static Class findStaticInnerClass(Class enumClass, String innerClassName,
                                              Class superClass) {
        innerClassName = enumClass.getName() + "$" + innerClassName;

        Class innerClass = null;

        for (Class clazz = enumClass; !clazz.equals(Enum.class); clazz = clazz.getSuperclass()) {
            Class[] classes = clazz.getDeclaredClasses();

            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().equals(innerClassName)
                        && superClass.isAssignableFrom(classes[i])) {
                    innerClass = classes[i];
                    break;
                }
            }
        }

        if ((innerClass != null) && Modifier.isStatic(innerClass.getModifiers())) {
            return innerClass;
        }

        return null;
    }

    /**
     * ȡ��ö����������.
     *
     * @return ö����������
     */
    public String getName() {
        return name;
    }

    /**
     * ȡ��ö������ֵ.
     *
     * @return ö������ֵ
     */
    public Object getValue() {
        return value;
    }

    /**
     * ʵ��<code>Number</code>��, ȡ��<code>byte</code>ֵ.
     *
     * @return <code>byte</code>ֵ
     */
    public byte byteValue() {
        return (byte) intValue();
    }

    /**
     * ʵ��<code>Number</code>��, ȡ��<code>short</code>ֵ.
     *
     * @return <code>short</code>ֵ
     */
    public short shortValue() {
        return (short) intValue();
    }

    /**
     * ʵ��<code>Convertible</code>�ӿ�,
     * ȡ�ý���ǰ<code>Enum</code>ת����ָ��<code>targetType</code>��<code>Converter</code>. ת���Ĺ�������:
     *
     * <ul>
     * <li>
     * ���<code>targetType</code>���ַ���, �򷵻�ö����������.
     * </li>
     * <li>
     * ����ö������ֵ���ݵ�ת������.
     * </li>
     * </ul>
     *
     *
     * @param targetType Ŀ������
     *
     * @return ����ǰ<code>Enum</code>ת����ָ��<code>targetType</code>��<code>Converter</code>
     */
    public Converter getConverter(Class targetType) {
        return new Converter() {
            public Object convert(Object value, ConvertChain chain) {
                Enum  enumObj       = (Enum) value;
                Class targetType = chain.getTargetType();

                if (String.class.equals(targetType)) {
                    return enumObj.toString();
                }

                return chain.convert(enumObj.getValue());
            }
        };
    }

    /**
     * ȡ�ú͵�ǰö������ֵ��ͬ��λ��.
     *
     * @return �µ�λ��
     */
    public FlagSet createFlagSet() {
        FlagSet flagSet = createFlagSet(getClass());

        if (flagSet != null) {
            return (FlagSet) flagSet.set((Flags) this);
        }

        return null;
    }

    /**
     * ���óɲ��ɱ��λ��.
     *
     * @return λ������
     */
    public Flags setImmutable() {
        return createFlagSet().setImmutable();
    }

    /**
     * �Ե�ǰλ��ִ���߼������.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags and(Flags flags) {
        return createFlagSet().and(flags);
    }

    /**
     * �Ե�ǰλ��ִ���߼��ǲ���.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags andNot(Flags flags) {
        return createFlagSet().andNot(flags);
    }

    /**
     * �Ե�ǰλ��ִ���߼������.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags or(Flags flags) {
        return createFlagSet().or(flags);
    }

    /**
     * �Ե�ǰλ��ִ���߼�������.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags xor(Flags flags) {
        return createFlagSet().xor(flags);
    }

    /**
     * �����ǰλ����ȫ��λ.
     *
     * @return ��ǰλ��
     */
    public Flags clear() {
        return createFlagSet().clear();
    }

    /**
     * �����ǰλ����ָ��λ, ��Ч��<code>andNot</code>����.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags clear(Flags flags) {
        return createFlagSet().clear(flags);
    }

    /**
     * ���õ�ǰλ����ָ��λ, ��Ч��<code>or</code>����.
     *
     * @param flags  ��־λ
     *
     * @return ��ǰλ��
     */
    public Flags set(Flags flags) {
        return createFlagSet().set(flags);
    }

    /**
     * ���Ե�ǰλ����ָ��λ, ��Ч��<code>and(flags) != 0</code>.
     *
     * @param flags  ��־λ
     *
     * @return ���ָ��λ����λ, �򷵻�<code>true</code>
     */
    public boolean test(Flags flags) {
        return createFlagSet().test(flags);
    }

    /**
     * ���Ե�ǰλ����ָ��λ, ��Ч��<code>and(flags) == flags</code>.
     *
     * @param flags  ��־λ
     *
     * @return ���ָ��λ����λ, �򷵻�<code>true</code>
     */
    public boolean testAll(Flags flags) {
        return createFlagSet().test(flags);
    }

    /**
     * ����һ��ö�����Ƚϴ�С, ���ǰ�ö������ֵ�Ƚ�.
     *
     * @param otherEnum Ҫ�Ƚϵ�ö����
     *
     * @return �������<code>0</code>, ��ʾֵ���, ����<code>0</code>��ʾ��ǰ��ö������ֵ��<code>otherEnum</code>��,
     *         С��<code>0</code>��ʾ��ǰ��ö������ֵ��<code>otherEnum</code>С
     */
    public int compareTo(Object otherEnum) {
        if (!getClass().equals(otherEnum.getClass())) {
            throw new IllegalArgumentException(MessageFormat.format(
                                                       EnumConstants.COMPARE_TYPE_MISMATCH,
                                                       new Object[] {
                getClass().getName(),
                otherEnum.getClass().getName()
            }));
        }

        return ((Comparable) value).compareTo(((Enum) otherEnum).value);
    }

    /**
     * �Ƚ�����ö�����Ƿ����, ��: ������ͬ, ����ֵ��ͬ(�����ֿ��Բ�ͬ).
     *
     * @param obj  Ҫ�ȽϵĶ���
     *
     * @return ������, �򷵻�<code>true</code>
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        return value.equals(((Enum) obj).value);
    }

    /**
     * ȡ��ö������hashֵ.  �������ö������ͬ, �����ǵ�hashֵһ����ͬ.
     *
     * @return hashֵ
     */
    public int hashCode() {
        return getClass().hashCode() ^ value.hashCode();
    }

    /**
     * ��ö����ת�����ַ���, Ҳ����ö����������.
     *
     * @return ö����������
     */
    public String toString() {
        return name;
    }

    /**
     * ��"�����л�"���̵���, ȷ������ö������singleton.
     *
     * @return ö������singleton
     *
     * @throws ObjectStreamException ��������л�����
     */
    protected Object readResolve() throws ObjectStreamException {
        Enum enumObj = Enum.getEnumByName(getClass(), getName());

        if ((enumObj == null) || !enumObj.value.equals(value)) {
            throw new InvalidClassException(getClass().getName());
        }

        return enumObj;
    }

    /**
     * ����һ��ö�����͵Ķ�����Ϣ.
     */
    protected abstract static class EnumType {
        private Object value;
        final ListMap  nameMap            = new ArrayHashMap();
        final ListMap  valueMap           = new ArrayHashMap();
        boolean        flagSetClassExists = true;
        Class          flagSetClass;
        FlagSet        fullSet;

        /**
         * ����ָ��ֵΪ��ǰֵ.
         *
         * @param value    ��ǰֵ
         * @param flagMode �Ƿ�Ϊλģʽ
         *
         * @return ��ǰֵ
         */
        final Object setValue(Object value, boolean flagMode) {
            this.value = value;

            if (flagMode && !isPowerOfTwo(value)) {
                throw new IllegalArgumentException(MessageFormat.format(
                                                           EnumConstants.VALUE_IS_NOT_POWER_OF_TWO,
                                                           new Object[] {
                    value
                }));
            }

            return value;
        }

        /**
         * ȡ����һ��ֵ.
         *
         * @param flagMode �Ƿ�Ϊλģʽ
         *
         * @return ��ǰֵ
         */
        final Object getNextValue(boolean flagMode) {
            value = getNextValue(value, flagMode);

            if (flagMode && isZero(value)) {
                throw new UnsupportedOperationException(EnumConstants.VALUE_OUT_OF_RANGE);
            }

            return value;
        }

        /**
         * ȡ��<code>Enum</code>ֵ������.
         *
         * @return <code>Enum</code>ֵ������
         */
        protected abstract Class getUnderlyingClass();

        /**
         * ȡ��ָ��ֵ����һ��ֵ.
         *
         * @param value    ָ��ֵ
         * @param flagMode �Ƿ�Ϊλģʽ
         *
         * @return ���<code>value</code>Ϊ<code>null</code>, �򷵻�Ĭ�ϵĳ�ʼֵ, ���򷵻���һ��ֵ
         */
        protected abstract Object getNextValue(Object value, boolean flagMode);

        /**
         * �ж��Ƿ�Ϊ<code>0</code>.
         *
         * @param value Ҫ�жϵ�ֵ
         *
         * @return �����, �򷵻�<code>true</code>
         */
        protected abstract boolean isZero(Object value);

        /**
         * �ж��Ƿ�Ϊ������������.
         *
         * @param value Ҫ�жϵ�ֵ
         *
         * @return �����, �򷵻�<code>true</code>
         */
        protected abstract boolean isPowerOfTwo(Object value);
    }
}
