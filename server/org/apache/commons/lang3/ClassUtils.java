package org.apache.commons.lang3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.lang3.mutable.MutableObject;

public class ClassUtils {
   public static final char PACKAGE_SEPARATOR_CHAR = '.';
   public static final String PACKAGE_SEPARATOR = String.valueOf('.');
   public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
   public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
   private static final Map<String, Class<?>> namePrimitiveMap = new HashMap();
   private static final Map<Class<?>, Class<?>> primitiveWrapperMap;
   private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap;
   private static final Map<String, String> abbreviationMap;
   private static final Map<String, String> reverseAbbreviationMap;

   public ClassUtils() {
      super();
   }

   public static String getShortClassName(Object var0, String var1) {
      return var0 == null ? var1 : getShortClassName(var0.getClass());
   }

   public static String getShortClassName(Class<?> var0) {
      return var0 == null ? "" : getShortClassName(var0.getName());
   }

   public static String getShortClassName(String var0) {
      if (StringUtils.isEmpty(var0)) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         if (var0.startsWith("[")) {
            while(var0.charAt(0) == '[') {
               var0 = var0.substring(1);
               var1.append("[]");
            }

            if (var0.charAt(0) == 'L' && var0.charAt(var0.length() - 1) == ';') {
               var0 = var0.substring(1, var0.length() - 1);
            }

            if (reverseAbbreviationMap.containsKey(var0)) {
               var0 = (String)reverseAbbreviationMap.get(var0);
            }
         }

         int var2 = var0.lastIndexOf(46);
         int var3 = var0.indexOf(36, var2 == -1 ? 0 : var2 + 1);
         String var4 = var0.substring(var2 + 1);
         if (var3 != -1) {
            var4 = var4.replace('$', '.');
         }

         return var4 + var1;
      }
   }

   public static String getSimpleName(Class<?> var0) {
      return var0 == null ? "" : var0.getSimpleName();
   }

   public static String getSimpleName(Object var0, String var1) {
      return var0 == null ? var1 : getSimpleName(var0.getClass());
   }

   public static String getPackageName(Object var0, String var1) {
      return var0 == null ? var1 : getPackageName(var0.getClass());
   }

   public static String getPackageName(Class<?> var0) {
      return var0 == null ? "" : getPackageName(var0.getName());
   }

   public static String getPackageName(String var0) {
      if (StringUtils.isEmpty(var0)) {
         return "";
      } else {
         while(var0.charAt(0) == '[') {
            var0 = var0.substring(1);
         }

         if (var0.charAt(0) == 'L' && var0.charAt(var0.length() - 1) == ';') {
            var0 = var0.substring(1);
         }

         int var1 = var0.lastIndexOf(46);
         return var1 == -1 ? "" : var0.substring(0, var1);
      }
   }

   public static String getAbbreviatedName(Class<?> var0, int var1) {
      return var0 == null ? "" : getAbbreviatedName(var0.getName(), var1);
   }

   public static String getAbbreviatedName(String var0, int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("len must be > 0");
      } else if (var0 == null) {
         return "";
      } else {
         int var2 = var1;
         int var3 = StringUtils.countMatches(var0, '.');
         String[] var4 = new String[var3 + 1];
         int var5 = var0.length() - 1;

         for(int var6 = var3; var6 >= 0; --var6) {
            int var7 = var0.lastIndexOf(46, var5);
            String var8 = var0.substring(var7 + 1, var5 + 1);
            var2 -= var8.length();
            if (var6 > 0) {
               --var2;
            }

            if (var6 == var3) {
               var4[var6] = var8;
            } else if (var2 > 0) {
               var4[var6] = var8;
            } else {
               var4[var6] = var8.substring(0, 1);
            }

            var5 = var7 - 1;
         }

         return StringUtils.join((Object[])var4, '.');
      }
   }

   public static List<Class<?>> getAllSuperclasses(Class<?> var0) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList();

         for(Class var2 = var0.getSuperclass(); var2 != null; var2 = var2.getSuperclass()) {
            var1.add(var2);
         }

         return var1;
      }
   }

   public static List<Class<?>> getAllInterfaces(Class<?> var0) {
      if (var0 == null) {
         return null;
      } else {
         LinkedHashSet var1 = new LinkedHashSet();
         getAllInterfaces(var0, var1);
         return new ArrayList(var1);
      }
   }

   private static void getAllInterfaces(Class<?> var0, HashSet<Class<?>> var1) {
      while(var0 != null) {
         Class[] var2 = var0.getInterfaces();
         Class[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class var6 = var3[var5];
            if (var1.add(var6)) {
               getAllInterfaces(var6, var1);
            }
         }

         var0 = var0.getSuperclass();
      }

   }

   public static List<Class<?>> convertClassNamesToClasses(List<String> var0) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList(var0.size());
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();

            try {
               var1.add(Class.forName(var3));
            } catch (Exception var5) {
               var1.add((Object)null);
            }
         }

         return var1;
      }
   }

   public static List<String> convertClassesToClassNames(List<Class<?>> var0) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList(var0.size());
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            Class var3 = (Class)var2.next();
            if (var3 == null) {
               var1.add((Object)null);
            } else {
               var1.add(var3.getName());
            }
         }

         return var1;
      }
   }

   public static boolean isAssignable(Class<?>[] var0, Class<?>... var1) {
      return isAssignable(var0, var1, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
   }

   public static boolean isAssignable(Class<?>[] var0, Class<?>[] var1, boolean var2) {
      if (!ArrayUtils.isSameLength((Object[])var0, (Object[])var1)) {
         return false;
      } else {
         if (var0 == null) {
            var0 = ArrayUtils.EMPTY_CLASS_ARRAY;
         }

         if (var1 == null) {
            var1 = ArrayUtils.EMPTY_CLASS_ARRAY;
         }

         for(int var3 = 0; var3 < var0.length; ++var3) {
            if (!isAssignable(var0[var3], var1[var3], var2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isPrimitiveOrWrapper(Class<?> var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.isPrimitive() || isPrimitiveWrapper(var0);
      }
   }

   public static boolean isPrimitiveWrapper(Class<?> var0) {
      return wrapperPrimitiveMap.containsKey(var0);
   }

   public static boolean isAssignable(Class<?> var0, Class<?> var1) {
      return isAssignable(var0, var1, SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_5));
   }

   public static boolean isAssignable(Class<?> var0, Class<?> var1, boolean var2) {
      if (var1 == null) {
         return false;
      } else if (var0 == null) {
         return !var1.isPrimitive();
      } else {
         if (var2) {
            if (var0.isPrimitive() && !var1.isPrimitive()) {
               var0 = primitiveToWrapper(var0);
               if (var0 == null) {
                  return false;
               }
            }

            if (var1.isPrimitive() && !var0.isPrimitive()) {
               var0 = wrapperToPrimitive(var0);
               if (var0 == null) {
                  return false;
               }
            }
         }

         if (var0.equals(var1)) {
            return true;
         } else if (var0.isPrimitive()) {
            if (!var1.isPrimitive()) {
               return false;
            } else if (Integer.TYPE.equals(var0)) {
               return Long.TYPE.equals(var1) || Float.TYPE.equals(var1) || Double.TYPE.equals(var1);
            } else if (Long.TYPE.equals(var0)) {
               return Float.TYPE.equals(var1) || Double.TYPE.equals(var1);
            } else if (Boolean.TYPE.equals(var0)) {
               return false;
            } else if (Double.TYPE.equals(var0)) {
               return false;
            } else if (Float.TYPE.equals(var0)) {
               return Double.TYPE.equals(var1);
            } else if (Character.TYPE.equals(var0)) {
               return Integer.TYPE.equals(var1) || Long.TYPE.equals(var1) || Float.TYPE.equals(var1) || Double.TYPE.equals(var1);
            } else if (Short.TYPE.equals(var0)) {
               return Integer.TYPE.equals(var1) || Long.TYPE.equals(var1) || Float.TYPE.equals(var1) || Double.TYPE.equals(var1);
            } else if (!Byte.TYPE.equals(var0)) {
               return false;
            } else {
               return Short.TYPE.equals(var1) || Integer.TYPE.equals(var1) || Long.TYPE.equals(var1) || Float.TYPE.equals(var1) || Double.TYPE.equals(var1);
            }
         } else {
            return var1.isAssignableFrom(var0);
         }
      }
   }

   public static Class<?> primitiveToWrapper(Class<?> var0) {
      Class var1 = var0;
      if (var0 != null && var0.isPrimitive()) {
         var1 = (Class)primitiveWrapperMap.get(var0);
      }

      return var1;
   }

   public static Class<?>[] primitivesToWrappers(Class<?>... var0) {
      if (var0 == null) {
         return null;
      } else if (var0.length == 0) {
         return var0;
      } else {
         Class[] var1 = new Class[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = primitiveToWrapper(var0[var2]);
         }

         return var1;
      }
   }

   public static Class<?> wrapperToPrimitive(Class<?> var0) {
      return (Class)wrapperPrimitiveMap.get(var0);
   }

   public static Class<?>[] wrappersToPrimitives(Class<?>... var0) {
      if (var0 == null) {
         return null;
      } else if (var0.length == 0) {
         return var0;
      } else {
         Class[] var1 = new Class[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = wrapperToPrimitive(var0[var2]);
         }

         return var1;
      }
   }

   public static boolean isInnerClass(Class<?> var0) {
      return var0 != null && var0.getEnclosingClass() != null;
   }

   public static Class<?> getClass(ClassLoader var0, String var1, boolean var2) throws ClassNotFoundException {
      try {
         Class var3;
         if (namePrimitiveMap.containsKey(var1)) {
            var3 = (Class)namePrimitiveMap.get(var1);
         } else {
            var3 = Class.forName(toCanonicalName(var1), var2, var0);
         }

         return var3;
      } catch (ClassNotFoundException var7) {
         int var4 = var1.lastIndexOf(46);
         if (var4 != -1) {
            try {
               return getClass(var0, var1.substring(0, var4) + '$' + var1.substring(var4 + 1), var2);
            } catch (ClassNotFoundException var6) {
            }
         }

         throw var7;
      }
   }

   public static Class<?> getClass(ClassLoader var0, String var1) throws ClassNotFoundException {
      return getClass(var0, var1, true);
   }

   public static Class<?> getClass(String var0) throws ClassNotFoundException {
      return getClass(var0, true);
   }

   public static Class<?> getClass(String var0, boolean var1) throws ClassNotFoundException {
      ClassLoader var2 = Thread.currentThread().getContextClassLoader();
      ClassLoader var3 = var2 == null ? ClassUtils.class.getClassLoader() : var2;
      return getClass(var3, var0, var1);
   }

   public static Method getPublicMethod(Class<?> var0, String var1, Class<?>... var2) throws SecurityException, NoSuchMethodException {
      Method var3 = var0.getMethod(var1, var2);
      if (Modifier.isPublic(var3.getDeclaringClass().getModifiers())) {
         return var3;
      } else {
         ArrayList var4 = new ArrayList();
         var4.addAll(getAllInterfaces(var0));
         var4.addAll(getAllSuperclasses(var0));
         Iterator var5 = var4.iterator();

         while(true) {
            Class var6;
            do {
               if (!var5.hasNext()) {
                  throw new NoSuchMethodException("Can't find a public method for " + var1 + " " + ArrayUtils.toString(var2));
               }

               var6 = (Class)var5.next();
            } while(!Modifier.isPublic(var6.getModifiers()));

            Method var7;
            try {
               var7 = var6.getMethod(var1, var2);
            } catch (NoSuchMethodException var9) {
               continue;
            }

            if (Modifier.isPublic(var7.getDeclaringClass().getModifiers())) {
               return var7;
            }
         }
      }
   }

   private static String toCanonicalName(String var0) {
      var0 = StringUtils.deleteWhitespace(var0);
      if (var0 == null) {
         throw new NullPointerException("className must not be null.");
      } else {
         if (var0.endsWith("[]")) {
            StringBuilder var1 = new StringBuilder();

            while(var0.endsWith("[]")) {
               var0 = var0.substring(0, var0.length() - 2);
               var1.append("[");
            }

            String var2 = (String)abbreviationMap.get(var0);
            if (var2 != null) {
               var1.append(var2);
            } else {
               var1.append("L").append(var0).append(";");
            }

            var0 = var1.toString();
         }

         return var0;
      }
   }

   public static Class<?>[] toClass(Object... var0) {
      if (var0 == null) {
         return null;
      } else if (var0.length == 0) {
         return ArrayUtils.EMPTY_CLASS_ARRAY;
      } else {
         Class[] var1 = new Class[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = var0[var2] == null ? null : var0[var2].getClass();
         }

         return var1;
      }
   }

   public static String getShortCanonicalName(Object var0, String var1) {
      return var0 == null ? var1 : getShortCanonicalName(var0.getClass().getName());
   }

   public static String getShortCanonicalName(Class<?> var0) {
      return var0 == null ? "" : getShortCanonicalName(var0.getName());
   }

   public static String getShortCanonicalName(String var0) {
      return getShortClassName(getCanonicalName(var0));
   }

   public static String getPackageCanonicalName(Object var0, String var1) {
      return var0 == null ? var1 : getPackageCanonicalName(var0.getClass().getName());
   }

   public static String getPackageCanonicalName(Class<?> var0) {
      return var0 == null ? "" : getPackageCanonicalName(var0.getName());
   }

   public static String getPackageCanonicalName(String var0) {
      return getPackageName(getCanonicalName(var0));
   }

   private static String getCanonicalName(String var0) {
      var0 = StringUtils.deleteWhitespace(var0);
      if (var0 == null) {
         return null;
      } else {
         int var1;
         for(var1 = 0; var0.startsWith("["); var0 = var0.substring(1)) {
            ++var1;
         }

         if (var1 < 1) {
            return var0;
         } else {
            if (var0.startsWith("L")) {
               var0 = var0.substring(1, var0.endsWith(";") ? var0.length() - 1 : var0.length());
            } else if (var0.length() > 0) {
               var0 = (String)reverseAbbreviationMap.get(var0.substring(0, 1));
            }

            StringBuilder var2 = new StringBuilder(var0);

            for(int var3 = 0; var3 < var1; ++var3) {
               var2.append("[]");
            }

            return var2.toString();
         }
      }
   }

   public static Iterable<Class<?>> hierarchy(Class<?> var0) {
      return hierarchy(var0, ClassUtils.Interfaces.EXCLUDE);
   }

   public static Iterable<Class<?>> hierarchy(final Class<?> var0, ClassUtils.Interfaces var1) {
      final Iterable var2 = new Iterable<Class<?>>() {
         public Iterator<Class<?>> iterator() {
            final MutableObject var1 = new MutableObject(var0);
            return new Iterator<Class<?>>() {
               public boolean hasNext() {
                  return var1.getValue() != null;
               }

               public Class<?> next() {
                  Class var1x = (Class)var1.getValue();
                  var1.setValue(var1x.getSuperclass());
                  return var1x;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
      return var1 != ClassUtils.Interfaces.INCLUDE ? var2 : new Iterable<Class<?>>() {
         public Iterator<Class<?>> iterator() {
            final HashSet var1 = new HashSet();
            final Iterator var2x = var2.iterator();
            return new Iterator<Class<?>>() {
               Iterator<Class<?>> interfaces = Collections.emptySet().iterator();

               public boolean hasNext() {
                  return this.interfaces.hasNext() || var2x.hasNext();
               }

               public Class<?> next() {
                  Class var1x;
                  if (this.interfaces.hasNext()) {
                     var1x = (Class)this.interfaces.next();
                     var1.add(var1x);
                     return var1x;
                  } else {
                     var1x = (Class)var2x.next();
                     LinkedHashSet var2xx = new LinkedHashSet();
                     this.walkInterfaces(var2xx, var1x);
                     this.interfaces = var2xx.iterator();
                     return var1x;
                  }
               }

               private void walkInterfaces(Set<Class<?>> var1x, Class<?> var2xx) {
                  Class[] var3 = var2xx.getInterfaces();
                  int var4 = var3.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     Class var6 = var3[var5];
                     if (!var1.contains(var6)) {
                        var1x.add(var6);
                     }

                     this.walkInterfaces(var1x, var6);
                  }

               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   static {
      namePrimitiveMap.put("boolean", Boolean.TYPE);
      namePrimitiveMap.put("byte", Byte.TYPE);
      namePrimitiveMap.put("char", Character.TYPE);
      namePrimitiveMap.put("short", Short.TYPE);
      namePrimitiveMap.put("int", Integer.TYPE);
      namePrimitiveMap.put("long", Long.TYPE);
      namePrimitiveMap.put("double", Double.TYPE);
      namePrimitiveMap.put("float", Float.TYPE);
      namePrimitiveMap.put("void", Void.TYPE);
      primitiveWrapperMap = new HashMap();
      primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
      primitiveWrapperMap.put(Byte.TYPE, Byte.class);
      primitiveWrapperMap.put(Character.TYPE, Character.class);
      primitiveWrapperMap.put(Short.TYPE, Short.class);
      primitiveWrapperMap.put(Integer.TYPE, Integer.class);
      primitiveWrapperMap.put(Long.TYPE, Long.class);
      primitiveWrapperMap.put(Double.TYPE, Double.class);
      primitiveWrapperMap.put(Float.TYPE, Float.class);
      primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
      wrapperPrimitiveMap = new HashMap();
      Iterator var0 = primitiveWrapperMap.entrySet().iterator();

      while(var0.hasNext()) {
         Entry var1 = (Entry)var0.next();
         Class var2 = (Class)var1.getKey();
         Class var3 = (Class)var1.getValue();
         if (!var2.equals(var3)) {
            wrapperPrimitiveMap.put(var3, var2);
         }
      }

      HashMap var4 = new HashMap();
      var4.put("int", "I");
      var4.put("boolean", "Z");
      var4.put("float", "F");
      var4.put("long", "J");
      var4.put("short", "S");
      var4.put("byte", "B");
      var4.put("double", "D");
      var4.put("char", "C");
      HashMap var5 = new HashMap();
      Iterator var6 = var4.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         var5.put(var7.getValue(), var7.getKey());
      }

      abbreviationMap = Collections.unmodifiableMap(var4);
      reverseAbbreviationMap = Collections.unmodifiableMap(var5);
   }

   public static enum Interfaces {
      INCLUDE,
      EXCLUDE;

      private Interfaces() {
      }
   }
}
