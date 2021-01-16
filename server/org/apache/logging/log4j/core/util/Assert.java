package org.apache.logging.log4j.core.util;

import java.util.Collection;
import java.util.Map;

public final class Assert {
   private Assert() {
      super();
   }

   public static boolean isEmpty(Object var0) {
      if (var0 == null) {
         return true;
      } else if (var0 instanceof CharSequence) {
         return ((CharSequence)var0).length() == 0;
      } else if (var0.getClass().isArray()) {
         return ((Object[])((Object[])var0)).length == 0;
      } else if (var0 instanceof Collection) {
         return ((Collection)var0).isEmpty();
      } else {
         return var0 instanceof Map ? ((Map)var0).isEmpty() : false;
      }
   }

   public static boolean isNonEmpty(Object var0) {
      return !isEmpty(var0);
   }

   public static <T> T requireNonEmpty(T var0) {
      return requireNonEmpty(var0, "");
   }

   public static <T> T requireNonEmpty(T var0, String var1) {
      if (isEmpty(var0)) {
         throw new IllegalArgumentException(var1);
      } else {
         return var0;
      }
   }

   public static int valueIsAtLeast(int var0, int var1) {
      if (var0 < var1) {
         throw new IllegalArgumentException("Value should be at least " + var1 + " but was " + var0);
      } else {
         return var0;
      }
   }
}
