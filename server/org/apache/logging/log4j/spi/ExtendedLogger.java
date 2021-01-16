package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

public interface ExtendedLogger extends Logger {
   boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4);

   boolean isEnabled(Level var1, Marker var2, CharSequence var3, Throwable var4);

   boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4);

   boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4);

   boolean isEnabled(Level var1, Marker var2, String var3);

   boolean isEnabled(Level var1, Marker var2, String var3, Object... var4);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13);

   void logIfEnabled(String var1, Level var2, Marker var3, Message var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, CharSequence var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, Object var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object... var5);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14);

   void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, MessageSupplier var4, Throwable var5);

   void logIfEnabled(String var1, Level var2, Marker var3, String var4, Supplier<?>... var5);

   void logIfEnabled(String var1, Level var2, Marker var3, Supplier<?> var4, Throwable var5);
}
