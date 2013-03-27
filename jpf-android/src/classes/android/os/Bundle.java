package android.os;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import android.util.SparseArray;

/**
 * A mapping from String values to various Parcelable types.
 * 
 */
public final class Bundle implements Parcelable, Cloneable {
  private static final String LOG_TAG = "Bundle";
  public static final Bundle EMPTY;

  static {
    EMPTY = new Bundle();
    EMPTY.mMap = Collections.unmodifiableMap(new HashMap<String, Object>());
  }

  // Invariant - exactly one of mMap / mParcelledData will be null
  // (except inside a call to unparcel)

  /* package */Map<String, Object> mMap = null;

  /*
   * If mParcelledData is non-null, then mMap will be null and the data are stored as a Parcel containing a
   * Bundle. When the data are unparcelled, mParcelledData willbe set to null.
   */

  /**
   * Constructs a new, empty Bundle.
   */
  public Bundle() {
    mMap = new HashMap<String, Object>();
  }

  /**
   * Constructs a Bundle whose data is stored as a Parcel. The data will be unparcelled on first contact,
   * using the assigned ClassLoader.
   * 
   * @param parcelledData
   *          a Parcel containing a Bundle
   */
  Bundle(Parcel parcelledData) {
  }

  /* package */Bundle(Parcel parcelledData, int length) {
  }

  /**
   * Constructs a new, empty Bundle that uses a specific ClassLoader for instantiating Parcelable and
   * Serializable objects.
   * 
   * @param loader
   *          An explicit ClassLoader to use when instantiating objects inside of the Bundle.
   */
  public Bundle(ClassLoader loader) {
    mMap = new HashMap<String, Object>();
  }

  /**
   * Constructs a new, empty Bundle sized to hold the given number of elements. The Bundle will grow as
   * needed.
   * 
   * @param capacity
   *          the initial capacity of the Bundle
   */
  public Bundle(int capacity) {
    mMap = new HashMap<String, Object>(capacity);
  }

  /**
   * Constructs a Bundle containing a copy of the mappings from the given Bundle.
   * 
   * @param b
   *          a Bundle to be copied.
   */
  public Bundle(Bundle b) {

    if (b.mMap != null) {
      mMap = new HashMap<String, Object>(b.mMap);
    } else {
      mMap = null;
    }

  }

  /**
   * Make a Bundle for a single key/value pair.
   * 
   * @hide
   */
  public static Bundle forPair(String key, String value) {
    // TODO: optimize this case.
    Bundle b = new Bundle(1);
    b.putString(key, value);
    return b;
  }

  /**
   * TODO: optimize this later (getting just the value part of a Bundle with a single pair) once
   * Bundle.forPair() above is implemented with a special single-value Map implementation/serialization.
   * 
   * Note: value in single-pair Bundle may be null.
   * 
   * @hide
   */
  public String getPairValue() {
    int size = mMap.size();
    if (size > 1) {
      Log.w(LOG_TAG, "getPairValue() used on Bundle with multiple pairs.");
    }
    if (size == 0) {
      return null;
    }
    Object o = mMap.values().iterator().next();
    try {
      return (String) o;
    } catch (ClassCastException e) {
      typeWarning("getPairValue()", o, "String", e);
      return null;
    }
  }

  /**
   * Changes the ClassLoader this Bundle uses when instantiating objects.
   * 
   * @param loader
   *          An explicit ClassLoader to use when instantiating objects inside of the Bundle.
   */
  public void setClassLoader(ClassLoader loader) {
  }

  /**
   * Return the ClassLoader currently associated with this Bundle.
   */
  public ClassLoader getClassLoader() {
    return null;
  }

  /** @hide */
  public boolean setAllowFds(boolean allowFds) {
    return false;
  }

  /**
   * Clones the current Bundle. The internal map is cloned, but the keys and values to which it refers are
   * copied by reference.
   */
  @Override
  public Object clone() {
    return new Bundle(this);
  }

  /**
   * If the underlying data are stored as a Parcel, unparcel them using the currently assigned class loader.
   */
  /* package */synchronized void unparcel() {

  }

  /**
   * Returns the number of mappings contained in this Bundle.
   * 
   * @return the number of mappings as an int.
   */
  public int size() {
    return mMap.size();
  }

  /**
   * Returns true if the mapping of this Bundle is empty, false otherwise.
   */
  public boolean isEmpty() {
    return mMap.isEmpty();
  }

  /**
   * Removes all elements from the mapping of this Bundle.
   */
  public void clear() {
    mMap.clear();
  }

  /**
   * Returns true if the given key is contained in the mapping of this Bundle.
   * 
   * @param key
   *          a String key
   * @return true if the key is part of the mapping, false otherwise
   */
  public boolean containsKey(String key) {
    return mMap.containsKey(key);
  }

  /**
   * Returns the entry with the given key as an object.
   * 
   * @param key
   *          a String key
   * @return an Object, or null
   */
  public Object get(String key) {
    return mMap.get(key);
  }

  /**
   * Removes any entry with the given key from the mapping of this Bundle.
   * 
   * @param key
   *          a String key
   */
  public void remove(String key) {
    mMap.remove(key);
  }

  /**
   * Inserts all mappings from the given Bundle into this Bundle.
   * 
   * @param map
   *          a Bundle
   */
  public void putAll(Bundle map) {
    mMap.putAll(map.mMap);

    // fd state is now known if and only if both bundles already knew
  }

  /**
   * Returns a Set containing the Strings used as keys in this Bundle.
   * 
   * @return a Set of String keys
   */
  public Set<String> keySet() {
    return mMap.keySet();
  }

  /**
   * Reports whether the bundle contains any parcelled file descriptors.
   */
  public boolean hasFileDescriptors() {
    return false;
  }

  /**
   * Inserts a Boolean value into the mapping of this Bundle, replacing any existing value for the given key.
   * Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a Boolean, or null
   */
  public void putBoolean(String key, boolean value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a byte value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a byte
   */
  public void putByte(String key, byte value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a char value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a char, or null
   */
  public void putChar(String key, char value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a short value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a short
   */
  public void putShort(String key, short value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an int value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an int, or null
   */
  public void putInt(String key, int value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a long value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a long
   */
  public void putLong(String key, long value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a float value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a float
   */
  public void putFloat(String key, float value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a double value into the mapping of this Bundle, replacing any existing value for the given key.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a double
   */
  public void putDouble(String key, double value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a String value into the mapping of this Bundle, replacing any existing value for the given key.
   * Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a String, or null
   */
  public void putString(String key, String value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a CharSequence value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a CharSequence, or null
   */
  public void putCharSequence(String key, CharSequence value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a Parcelable value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a Parcelable object, or null
   */
  public void putParcelable(String key, Parcelable value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an array of Parcelable values into the mapping of this Bundle, replacing any existing value for
   * the given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an array of Parcelable objects, or null
   */
  public void putParcelableArray(String key, Parcelable[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a List of Parcelable values into the mapping of this Bundle, replacing any existing value for the
   * given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an ArrayList of Parcelable objects, or null
   */
  public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a SparceArray of Parcelable values into the mapping of this Bundle, replacing any existing value
   * for the given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a SparseArray of Parcelable objects, or null
   */
  public void putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing any existing value for the
   * given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an ArrayList<Integer> object, or null
   */
  public void putIntegerArrayList(String key, ArrayList<Integer> value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing any existing value for the
   * given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an ArrayList<String> object, or null
   */
  public void putStringArrayList(String key, ArrayList<String> value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing any existing value
   * for the given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an ArrayList<CharSequence> object, or null
   */
  public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a Serializable value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a Serializable object, or null
   */
  public void putSerializable(String key, Serializable value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a boolean array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a boolean array object, or null
   */
  public void putBooleanArray(String key, boolean[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a byte array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a byte array object, or null
   */
  public void putByteArray(String key, byte[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a short array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a short array object, or null
   */
  public void putShortArray(String key, short[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a char array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a char array object, or null
   */
  public void putCharArray(String key, char[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an int array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an int array object, or null
   */
  public void putIntArray(String key, int[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a long array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a long array object, or null
   */
  public void putLongArray(String key, long[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a float array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a float array object, or null
   */
  public void putFloatArray(String key, float[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a double array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a double array object, or null
   */
  public void putDoubleArray(String key, double[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a String array value into the mapping of this Bundle, replacing any existing value for the given
   * key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a String array object, or null
   */
  public void putStringArray(String key, String[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a CharSequence array value into the mapping of this Bundle, replacing any existing value for the
   * given key. Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a CharSequence array object, or null
   */
  public void putCharSequenceArray(String key, CharSequence[] value) {
    mMap.put(key, value);
  }

  /**
   * Inserts a Bundle value into the mapping of this Bundle, replacing any existing value for the given key.
   * Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          a Bundle object, or null
   */
  public void putBundle(String key, Bundle value) {
    mMap.put(key, value);
  }

  /**
   * Inserts an IBinder value into the mapping of this Bundle, replacing any existing value for the given key.
   * Either key or value may be null.
   * 
   * @param key
   *          a String, or null
   * @param value
   *          an IBinder object, or null
   * 
   * @deprecated
   * @hide
   */
  @Deprecated
  public void putIBinder(String key, IBinder value) {
    mMap.put(key, value);
  }

  /**
   * Returns the value associated with the given key, or false if no mapping of the desired type exists for
   * the given key.
   * 
   * @param key
   *          a String
   * @return a boolean value
   */
  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  // Log a message if the value was non-null but not of the expected type
  private void typeWarning(String key, Object value, String className, Object defaultValue,
                           ClassCastException e) {
    StringBuilder sb = new StringBuilder();
    sb.append("Key ");
    sb.append(key);
    sb.append(" expected ");
    sb.append(className);
    sb.append(" but value was a ");
    sb.append(value.getClass().getName());
    sb.append(".  The default value ");
    sb.append(defaultValue);
    sb.append(" was returned.");
    Log.w(LOG_TAG, sb.toString());
    Log.w(LOG_TAG, "Attempt to cast generated internal exception:", e);
  }

  private void typeWarning(String key, Object value, String className, ClassCastException e) {
    typeWarning(key, value, className, "<null>", e);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a boolean value
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Boolean) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Boolean", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or (byte) 0 if no mapping of the desired type exists for
   * the given key.
   * 
   * @param key
   *          a String
   * @return a byte value
   */
  public byte getByte(String key) {
    return getByte(key, (byte) 0);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a byte value
   */
  public Byte getByte(String key, byte defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Byte) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Byte", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or false if no mapping of the desired type exists for
   * the given key.
   * 
   * @param key
   *          a String
   * @return a char value
   */
  public char getChar(String key) {
    return getChar(key, (char) 0);
  }

  /**
   * Returns the value associated with the given key, or (char) 0 if no mapping of the desired type exists for
   * the given key.
   * 
   * @param key
   *          a String
   * @return a char value
   */
  public char getChar(String key, char defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Character) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Character", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or (short) 0 if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a short value
   */
  public short getShort(String key) {
    return getShort(key, (short) 0);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a short value
   */
  public short getShort(String key, short defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Short) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Short", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or 0 if no mapping of the desired type exists for the
   * given key.
   * 
   * @param key
   *          a String
   * @return an int value
   */
  public int getInt(String key) {
    return getInt(key, 0);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return an int value
   */
  public int getInt(String key, int defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Integer) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Integer", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or 0L if no mapping of the desired type exists for the
   * given key.
   * 
   * @param key
   *          a String
   * @return a long value
   */
  public long getLong(String key) {
    return getLong(key, 0L);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a long value
   */
  public long getLong(String key, long defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Long) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Long", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or 0.0f if no mapping of the desired type exists for the
   * given key.
   * 
   * @param key
   *          a String
   * @return a float value
   */
  public float getFloat(String key) {
    return getFloat(key, 0.0f);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a float value
   */
  public float getFloat(String key, float defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Float) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Float", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or 0.0 if no mapping of the desired type exists for the
   * given key.
   * 
   * @param key
   *          a String
   * @return a double value
   */
  public double getDouble(String key) {
    return getDouble(key, 0.0);
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String
   * @return a double value
   */
  public double getDouble(String key, double defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (Double) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Double", defaultValue, e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a String value, or null
   */
  public String getString(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (String) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "String", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String, or null
   * @param defaultValue
   *          Value to return if key does not exist
   * @return a String value, or null
   */
  public String getString(String key, String defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (String) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "String", e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a CharSequence value, or null
   */
  public CharSequence getCharSequence(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (CharSequence) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "CharSequence", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or defaultValue if no mapping of the desired type exists
   * for the given key.
   * 
   * @param key
   *          a String, or null
   * @param defaultValue
   *          Value to return if key does not exist
   * @return a CharSequence value, or null
   */
  public CharSequence getCharSequence(String key, CharSequence defaultValue) {
    Object o = mMap.get(key);
    if (o == null) {
      return defaultValue;
    }
    try {
      return (CharSequence) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "CharSequence", e);
      return defaultValue;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a Bundle value, or null
   */
  public Bundle getBundle(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (Bundle) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Bundle", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a Parcelable value, or null
   */
  public <T extends Parcelable> T getParcelable(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (T) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Parcelable", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a Parcelable[] value, or null
   */
  public Parcelable[] getParcelableArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (Parcelable[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Parcelable[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an ArrayList<T> value, or null
   */
  public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (ArrayList<T>) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "ArrayList", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * 
   * @return a SparseArray of T values, or null
   */
  public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (SparseArray<T>) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "SparseArray", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a Serializable value, or null
   */
  public Serializable getSerializable(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (Serializable) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "Serializable", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an ArrayList<String> value, or null
   */
  public ArrayList<Integer> getIntegerArrayList(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (ArrayList<Integer>) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "ArrayList<Integer>", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an ArrayList<String> value, or null
   */
  public ArrayList<String> getStringArrayList(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (ArrayList<String>) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "ArrayList<String>", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an ArrayList<CharSequence> value, or null
   */
  public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (ArrayList<CharSequence>) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "ArrayList<CharSequence>", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a boolean[] value, or null
   */
  public boolean[] getBooleanArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (boolean[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "byte[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a byte[] value, or null
   */
  public byte[] getByteArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (byte[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "byte[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a short[] value, or null
   */
  public short[] getShortArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (short[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "short[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a char[] value, or null
   */
  public char[] getCharArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (char[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "char[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an int[] value, or null
   */
  public int[] getIntArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (int[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "int[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a long[] value, or null
   */
  public long[] getLongArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (long[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "long[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a float[] value, or null
   */
  public float[] getFloatArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (float[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "float[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a double[] value, or null
   */
  public double[] getDoubleArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (double[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "double[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a String[] value, or null
   */
  public String[] getStringArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (String[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "String[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return a CharSequence[] value, or null
   */
  public CharSequence[] getCharSequenceArray(String key) {
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (CharSequence[]) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "CharSequence[]", e);
      return null;
    }
  }

  /**
   * Returns the value associated with the given key, or null if no mapping of the desired type exists for the
   * given key or a null value is explicitly associated with the key.
   * 
   * @param key
   *          a String, or null
   * @return an IBinder value, or null
   * 
   * @deprecated
   * @hide
   */
  @Deprecated
  public IBinder getIBinder(String key) {
    unparcel();
    Object o = mMap.get(key);
    if (o == null) {
      return null;
    }
    try {
      return (IBinder) o;
    } catch (ClassCastException e) {
      typeWarning(key, o, "IBinder", e);
      return null;
    }
  }

  public static final Parcelable.Creator<Bundle> CREATOR = new Parcelable.Creator<Bundle>() {
    public Bundle createFromParcel(Parcel in) {
      return in.readBundle();
    }

    public Bundle[] newArray(int size) {
      return new Bundle[size];
    }
  };

  /**
   * Report the nature of this Parcelable's contents
   */
  public int describeContents() {
    int mask = 0;
    return mask;
  }

  /**
   * Writes the Bundle contents to a Parcel, typically in order for it to be passed through an IBinder
   * connection.
   * 
   * @param parcel
   *          The parcel to copy this bundle to.
   */
  public void writeToParcel(Parcel parcel, int flags) {
  }

  /**
   * Reads the Parcel contents into this Bundle, typically in order for it to be passed through an IBinder
   * connection.
   * 
   * @param parcel
   *          The parcel to overwrite this bundle from.
   */
  public void readFromParcel(Parcel parcel) {
  }

  @Override
  public synchronized String toString() {
    return "Bundle[" + mMap.toString() + "]";
  }
}