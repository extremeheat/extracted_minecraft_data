package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtIncompatible
public final class Defaults {
   private static final Map<Class<?>, Object> DEFAULTS;

   private Defaults() {
      super();
   }

   private static <T> void put(Map<Class<?>, Object> var0, Class<T> var1, T var2) {
      var0.put(var1, var2);
   }

   @Nullable
   public static <T> T defaultValue(Class<T> var0) {
      Object var1 = DEFAULTS.get(Preconditions.checkNotNull(var0));
      return var1;
   }

   static {
      HashMap var0 = new HashMap();
      put(var0, Boolean.TYPE, false);
      put(var0, Character.TYPE, '\u0000');
      put(var0, Byte.TYPE, (byte)0);
      put(var0, Short.TYPE, Short.valueOf((short)0));
      put(var0, Integer.TYPE, 0);
      put(var0, Long.TYPE, 0L);
      put(var0, Float.TYPE, 0.0F);
      put(var0, Double.TYPE, 0.0D);
      DEFAULTS = Collections.unmodifiableMap(var0);
   }
}
