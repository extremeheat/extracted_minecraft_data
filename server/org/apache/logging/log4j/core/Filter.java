package org.apache.logging.log4j.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.EnglishEnums;

public interface Filter extends LifeCycle {
   String ELEMENT_TYPE = "filter";

   Filter.Result getOnMismatch();

   Filter.Result getOnMatch();

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13);

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14);

   Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5);

   Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5);

   Filter.Result filter(LogEvent var1);

   public static enum Result {
      ACCEPT,
      NEUTRAL,
      DENY;

      private Result() {
      }

      public static Filter.Result toResult(String var0) {
         return toResult(var0, (Filter.Result)null);
      }

      public static Filter.Result toResult(String var0, Filter.Result var1) {
         return (Filter.Result)EnglishEnums.valueOf(Filter.Result.class, var0, var1);
      }
   }
}
