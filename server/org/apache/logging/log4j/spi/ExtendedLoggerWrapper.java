package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

public class ExtendedLoggerWrapper extends AbstractLogger {
   private static final long serialVersionUID = 1L;
   protected final ExtendedLogger logger;

   public ExtendedLoggerWrapper(ExtendedLogger var1, String var2, MessageFactory var3) {
      super(var2, var3);
      this.logger = var1;
   }

   public Level getLevel() {
      return this.logger.getLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3) {
      return this.logger.isEnabled(var1, var2, var3);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object... var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.logger.isEnabled(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4) {
      return this.logger.isEnabled(var1, var2, var3, var4);
   }

   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      this.logger.logMessage(var1, var2, var3, var4, var5);
   }
}
