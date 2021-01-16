package org.apache.logging.log4j.core.config.plugins.convert;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DateTypeConverter {
   private static final Map<Class<? extends Date>, MethodHandle> CONSTRUCTORS = new ConcurrentHashMap();

   public static <D extends Date> D fromMillis(long var0, Class<D> var2) {
      try {
         return ((MethodHandle)CONSTRUCTORS.get(var2)).invoke(var0);
      } catch (Throwable var4) {
         return null;
      }
   }

   private DateTypeConverter() {
      super();
   }

   static {
      Lookup var0 = MethodHandles.publicLookup();
      Iterator var1 = Arrays.asList(Date.class, java.sql.Date.class, Time.class, Timestamp.class).iterator();

      while(var1.hasNext()) {
         Class var2 = (Class)var1.next();

         try {
            CONSTRUCTORS.put(var2, var0.findConstructor(var2, MethodType.methodType(Void.TYPE, Long.TYPE)));
         } catch (IllegalAccessException | NoSuchMethodException var4) {
         }
      }

   }
}
