//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA). All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3. The NOSA has been approved by the Open Source
// Initiative. See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.android;

import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ClinitRequired;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Fields;
import gov.nasa.jpf.vm.MJIEnv;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Object transformer from Java objects to JPF objects
 * 
 * @author Ivan Mushketik
 * 
 *         Modified for Android on March 2013. We added callbacks to make sure
 *         static fields in a object is initiated.
 * @author Heila van der Merwe
 */
public class AndroidObjectConverter {
  public static boolean finished = true;

  /**
   * Create JPF object from Java object
   * 
   * @param env
   *          - MJI environment
   * @param javaObject
   *          - java object that is used to created JPF object from
   * @return reference to new JPF object
   */
  public static int JPFObjectFromJavaObject(MJIEnv env, Object javaObject) {
    //get the java class object 
    Class<?> javaClass = javaObject.getClass();
    
    //get the name of the class
    String typeName = javaClass.getName();
   
    ClassInfo ci = null;
    try {
      ci = ClassInfo.getInitializedClassInfo(typeName, env.getThreadInfo());
    } catch (ClinitRequired e) {
      env.repeatInvocation();
      finished = false;
      return MJIEnv.NULL;
    }

    finished = true;
    int newObjRef = env.newObject(ci);
    ElementInfo newObjEI = env.getModifiableElementInfo(newObjRef);

    while (ci != null) {
      for (FieldInfo fi : ci.getDeclaredInstanceFields()) {
        if (!fi.isReference()) {
          setJPFPrimitive(newObjEI, fi, javaObject);
        } else {
          try {
            Field arrField = getField(fi.getName(), javaClass);
            arrField.setAccessible(true);
            Object fieldJavaObj = arrField.get(javaObject);

            int fieldJPFObjRef;
            if (fieldJavaObj == null) {
              fieldJPFObjRef = MJIEnv.NULL;
            } else if (isArrayField(fi)) {
              fieldJPFObjRef = getJPFArrayRef(env, fieldJavaObj);
            } else {
              fieldJPFObjRef = JPFObjectFromJavaObject(env, fieldJavaObj);
            }

            newObjEI.setReferenceField(fi, fieldJPFObjRef);

          } catch (NoSuchFieldException nsfx) {
            throw new JPFException("JPF object creation failed, no such field: " + fi.getFullName(), nsfx);
          } catch (IllegalAccessException iax) {
            throw new JPFException("JPF object creation failed, illegal access: " + fi.getFullName(), iax);
          }
        }
      }

      ci = ci.getSuperClass();
    }

    return newObjRef;

  }

  private static void setJPFPrimitive(ElementInfo newObjEI, FieldInfo fi, Object javaObject) {
    try {

      String jpfTypeName = fi.getType();
      Class javaClass = javaObject.getClass();
      Field javaField = getField(fi.getName(), javaClass);
      javaField.setAccessible(true);

      if (jpfTypeName.equals("char")) {
        newObjEI.setCharField(fi, javaField.getChar(javaObject));
      } else if (jpfTypeName.equals("byte")) {
        newObjEI.setByteField(fi, javaField.getByte(javaObject));
      } else if (jpfTypeName.equals("short")) {
        newObjEI.setShortField(fi, javaField.getShort(javaObject));
      } else if (jpfTypeName.equals("int")) {
        newObjEI.setIntField(fi, javaField.getInt(javaObject));
      } else if (jpfTypeName.equals("long")) {
        newObjEI.setLongField(fi, javaField.getLong(javaObject));
      } else if (jpfTypeName.equals("float")) {
        newObjEI.setFloatField(fi, javaField.getFloat(javaObject));
      } else if (jpfTypeName.equals("double")) {
        newObjEI.setDoubleField(fi, javaField.getDouble(javaObject));
      }
    } catch (Exception ex) {
      throw new JPFException(ex);
    }
  }

  private static Field getField(String fieldName, Class javaClass) throws NoSuchFieldException {
    while (true) {
      try {
        Field field = javaClass.getDeclaredField(fieldName);
        return field;
      } catch (NoSuchFieldException ex) {
        // Try to search this field in a super class
        javaClass = javaClass.getSuperclass();

        // No more super class. Wrong field
        if (javaClass == null) {
          throw ex;
        }
      }
    }

  }

  public static int getJPFArrayRef(MJIEnv env, Object javaArr) throws NoSuchFieldException,
      IllegalAccessException {

    Class arrayElementClass = javaArr.getClass().getComponentType();

    int javaArrLength = Array.getLength(javaArr);
    int arrRef;

    if (arrayElementClass == Character.TYPE) {
      arrRef = env.newCharArray(javaArrLength);
      ElementInfo charArrRef = env.getElementInfo(arrRef);
      char[] charArr = charArrRef.asCharArray();

      for (int i = 0; i < javaArrLength; i++) {
        charArr[i] = Array.getChar(javaArr, i);
      }
    } else if (arrayElementClass == Byte.TYPE) {
      arrRef = env.newByteArray(javaArrLength);
      ElementInfo byteArrRef = env.getElementInfo(arrRef);
      byte[] byteArr = byteArrRef.asByteArray();

      for (int i = 0; i < javaArrLength; i++) {
        byteArr[i] = Array.getByte(javaArr, i);
      }
    } else if (arrayElementClass == Short.TYPE) {
      arrRef = env.newShortArray(javaArrLength);
      ElementInfo shortArrRef = env.getElementInfo(arrRef);
      short[] shortArr = shortArrRef.asShortArray();

      for (int i = 0; i < javaArrLength; i++) {
        shortArr[i] = Array.getShort(javaArr, i);
      }
    } else if (arrayElementClass == Integer.TYPE) {
      arrRef = env.newIntArray(javaArrLength);
      ElementInfo intArrRef = env.getElementInfo(arrRef);
      int[] intArr = intArrRef.asIntArray();

      for (int i = 0; i < javaArrLength; i++) {
        intArr[i] = Array.getInt(javaArr, i);
      }
    } else if (arrayElementClass == Long.TYPE) {
      arrRef = env.newLongArray(javaArrLength);
      ElementInfo longArrRef = env.getElementInfo(arrRef);
      long[] longArr = longArrRef.asLongArray();

      for (int i = 0; i < javaArrLength; i++) {
        longArr[i] = Array.getLong(javaArr, i);
      }
    } else if (arrayElementClass == Float.TYPE) {
      arrRef = env.newFloatArray(javaArrLength);
      ElementInfo floatArrRef = env.getElementInfo(arrRef);
      float[] floatArr = floatArrRef.asFloatArray();

      for (int i = 0; i < javaArrLength; i++) {
        floatArr[i] = Array.getFloat(javaArr, i);
      }
    } else if (arrayElementClass == Double.TYPE) {
      arrRef = env.newDoubleArray(javaArrLength);
      ElementInfo floatArrRef = env.getElementInfo(arrRef);
      double[] doubleArr = floatArrRef.asDoubleArray();

      for (int i = 0; i < javaArrLength; i++) {
        doubleArr[i] = Array.getDouble(javaArr, i);
      }
    } else {
      ClassInfo ci = null;
      try {
        ci = ClassInfo.getInitializedClassInfo(arrayElementClass.getCanonicalName(), env.getThreadInfo());
      } catch (ClinitRequired e) {
        env.repeatInvocation();
        finished = false;
        return MJIEnv.NULL;
      }
      finished = true;
      arrRef = env.newObjectArray(arrayElementClass.getCanonicalName(), javaArrLength);
      ElementInfo arrayEI = env.getElementInfo(arrRef);

      Fields fields = arrayEI.getFields();

      for (int i = 0; i < javaArrLength; i++) {
        int newArrElRef;
        Object javaArrEl = Array.get(javaArr, i);

        if (javaArrEl != null) {
          if (javaArrEl.getClass().isArray()) {
            newArrElRef = getJPFArrayRef(env, javaArrEl);
          } else {
            newArrElRef = JPFObjectFromJavaObject(env, javaArrEl);
          }
        } else {
          newArrElRef = MJIEnv.NULL;
        }

        fields.setReferenceValue(i, newArrElRef);
      }
    }
    return arrRef;
  }

  // Do we need this??
  public static Object javaObjectFromJPFObject(ElementInfo ei) {
    try {
      String typeName = ei.getType();
      Class clazz = ClassLoader.getSystemClassLoader().loadClass(typeName);

      Object javaObject = clazz.newInstance();
      ClassInfo ci = ei.getClassInfo();
      while (ci != null) {

        for (FieldInfo fi : ci.getDeclaredInstanceFields()) {
          if (!fi.isReference()) {
            setJavaPrimitive(javaObject, ei, fi);
          }
        }

        ci = ci.getSuperClass();
      }

      return javaObject;
    } catch (Exception ex) {
      throw new JPFException(ex);
    }
  }

  private static void setJavaPrimitive(Object javaObject, ElementInfo ei, FieldInfo fi)
      throws NoSuchFieldException, IllegalAccessException {
    String primitiveType = fi.getName();
    String fieldName = fi.getName();

    Class javaClass = javaObject.getClass();
    Field javaField = javaClass.getDeclaredField(fieldName);
    javaField.setAccessible(true);

    if (primitiveType.equals("char")) {
      javaField.setChar(javaObject, ei.getCharField(fi));
    } else if (primitiveType.equals("byte")) {
      javaField.setByte(javaObject, ei.getByteField(fi));
    } else if (primitiveType.equals("short")) {
      javaField.setShort(javaObject, ei.getShortField(fi));
    } else if (primitiveType.equals("int")) {
      javaField.setInt(javaObject, ei.getIntField(fi));
    } else if (primitiveType.equals("long")) {
      javaField.setLong(javaObject, ei.getLongField(fi));
    } else if (primitiveType.equals("float")) {
      javaField.setFloat(javaObject, ei.getFloatField(fi));
    } else if (primitiveType.equals("double")) {
      javaField.setDouble(javaObject, ei.getDoubleField(fi));
    } else {
      throw new JPFException("Can't convert " + primitiveType + " to "
          + javaField.getClass().getCanonicalName());
    }
  }

  private static boolean isArrayField(FieldInfo fi) {
    return fi.getType().lastIndexOf('[') >= 0;
  }

}
