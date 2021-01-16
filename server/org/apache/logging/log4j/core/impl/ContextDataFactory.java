package org.apache.logging.log4j.core.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

public class ContextDataFactory {
   private static final Lookup LOOKUP = MethodHandles.lookup();
   private static final String CLASS_NAME = PropertiesUtil.getProperties().getStringProperty("log4j2.ContextData");
   private static final Class<? extends StringMap> CACHED_CLASS;
   private static final MethodHandle DEFAULT_CONSTRUCTOR;
   private static final MethodHandle INITIAL_CAPACITY_CONSTRUCTOR;
   private static final StringMap EMPTY_STRING_MAP;

   public ContextDataFactory() {
      super();
   }

   private static Class<? extends StringMap> createCachedClass(String var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return LoaderUtil.loadClass(var0).asSubclass(StringMap.class);
         } catch (Exception var2) {
            return null;
         }
      }
   }

   private static MethodHandle createDefaultConstructor(Class<? extends StringMap> var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return LOOKUP.findConstructor(var0, MethodType.methodType(Void.TYPE));
         } catch (IllegalAccessException | NoSuchMethodException var2) {
            return null;
         }
      }
   }

   private static MethodHandle createInitialCapacityConstructor(Class<? extends StringMap> var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return LOOKUP.findConstructor(var0, MethodType.methodType(Void.TYPE, Integer.TYPE));
         } catch (IllegalAccessException | NoSuchMethodException var2) {
            return null;
         }
      }
   }

   public static StringMap createContextData() {
      if (DEFAULT_CONSTRUCTOR == null) {
         return new SortedArrayStringMap();
      } else {
         try {
            return DEFAULT_CONSTRUCTOR.invoke();
         } catch (Throwable var1) {
            return new SortedArrayStringMap();
         }
      }
   }

   public static StringMap createContextData(int var0) {
      if (INITIAL_CAPACITY_CONSTRUCTOR == null) {
         return new SortedArrayStringMap(var0);
      } else {
         try {
            return INITIAL_CAPACITY_CONSTRUCTOR.invoke(var0);
         } catch (Throwable var2) {
            return new SortedArrayStringMap(var0);
         }
      }
   }

   public static StringMap emptyFrozenContextData() {
      return EMPTY_STRING_MAP;
   }

   static {
      CACHED_CLASS = createCachedClass(CLASS_NAME);
      DEFAULT_CONSTRUCTOR = createDefaultConstructor(CACHED_CLASS);
      INITIAL_CAPACITY_CONSTRUCTOR = createInitialCapacityConstructor(CACHED_CLASS);
      EMPTY_STRING_MAP = createContextData(1);
      EMPTY_STRING_MAP.freeze();
   }
}
