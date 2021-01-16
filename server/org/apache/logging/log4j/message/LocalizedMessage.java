package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.status.StatusLogger;

public class LocalizedMessage implements Message, LoggerNameAwareMessage {
   private static final long serialVersionUID = 3893703791567290742L;
   private String baseName;
   private transient ResourceBundle resourceBundle;
   private final Locale locale;
   private transient StatusLogger logger;
   private String loggerName;
   private String key;
   private String[] stringArgs;
   private transient Object[] argArray;
   private String formattedMessage;
   private transient Throwable throwable;

   public LocalizedMessage(String var1, Object[] var2) {
      this((ResourceBundle)null, (Locale)null, var1, var2);
   }

   public LocalizedMessage(String var1, String var2, Object[] var3) {
      this(var1, (Locale)null, var2, var3);
   }

   public LocalizedMessage(ResourceBundle var1, String var2, Object[] var3) {
      this(var1, (Locale)null, var2, var3);
   }

   public LocalizedMessage(String var1, Locale var2, String var3, Object[] var4) {
      super();
      this.logger = StatusLogger.getLogger();
      this.key = var3;
      this.argArray = var4;
      this.throwable = null;
      this.baseName = var1;
      this.resourceBundle = null;
      this.locale = var2;
   }

   public LocalizedMessage(ResourceBundle var1, Locale var2, String var3, Object[] var4) {
      super();
      this.logger = StatusLogger.getLogger();
      this.key = var3;
      this.argArray = var4;
      this.throwable = null;
      this.baseName = null;
      this.resourceBundle = var1;
      this.locale = var2;
   }

   public LocalizedMessage(Locale var1, String var2, Object[] var3) {
      this((ResourceBundle)null, var1, var2, var3);
   }

   public LocalizedMessage(String var1, Object var2) {
      this((ResourceBundle)null, (Locale)null, var1, new Object[]{var2});
   }

   public LocalizedMessage(String var1, String var2, Object var3) {
      this(var1, (Locale)null, var2, new Object[]{var3});
   }

   public LocalizedMessage(ResourceBundle var1, String var2) {
      this(var1, (Locale)null, var2, new Object[0]);
   }

   public LocalizedMessage(ResourceBundle var1, String var2, Object var3) {
      this(var1, (Locale)null, var2, new Object[]{var3});
   }

   public LocalizedMessage(String var1, Locale var2, String var3, Object var4) {
      this(var1, var2, var3, new Object[]{var4});
   }

   public LocalizedMessage(ResourceBundle var1, Locale var2, String var3, Object var4) {
      this(var1, var2, var3, new Object[]{var4});
   }

   public LocalizedMessage(Locale var1, String var2, Object var3) {
      this((ResourceBundle)null, var1, var2, new Object[]{var3});
   }

   public LocalizedMessage(String var1, Object var2, Object var3) {
      this((ResourceBundle)null, (Locale)null, var1, new Object[]{var2, var3});
   }

   public LocalizedMessage(String var1, String var2, Object var3, Object var4) {
      this(var1, (Locale)null, var2, new Object[]{var3, var4});
   }

   public LocalizedMessage(ResourceBundle var1, String var2, Object var3, Object var4) {
      this(var1, (Locale)null, var2, new Object[]{var3, var4});
   }

   public LocalizedMessage(String var1, Locale var2, String var3, Object var4, Object var5) {
      this(var1, var2, var3, new Object[]{var4, var5});
   }

   public LocalizedMessage(ResourceBundle var1, Locale var2, String var3, Object var4, Object var5) {
      this(var1, var2, var3, new Object[]{var4, var5});
   }

   public LocalizedMessage(Locale var1, String var2, Object var3, Object var4) {
      this((ResourceBundle)null, var1, var2, new Object[]{var3, var4});
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public String getFormattedMessage() {
      if (this.formattedMessage != null) {
         return this.formattedMessage;
      } else {
         ResourceBundle var1 = this.resourceBundle;
         if (var1 == null) {
            if (this.baseName != null) {
               var1 = this.getResourceBundle(this.baseName, this.locale, false);
            } else {
               var1 = this.getResourceBundle(this.loggerName, this.locale, true);
            }
         }

         String var2 = this.getFormat();
         String var3 = var1 != null && var1.containsKey(var2) ? var1.getString(var2) : var2;
         Object var4 = this.argArray == null ? this.stringArgs : this.argArray;
         FormattedMessage var5 = new FormattedMessage(var3, (Object[])var4);
         this.formattedMessage = var5.getFormattedMessage();
         this.throwable = var5.getThrowable();
         return this.formattedMessage;
      }
   }

   public String getFormat() {
      return this.key;
   }

   public Object[] getParameters() {
      return (Object[])(this.argArray != null ? this.argArray : this.stringArgs);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   protected ResourceBundle getResourceBundle(String var1, Locale var2, boolean var3) {
      ResourceBundle var4 = null;
      if (var1 == null) {
         return null;
      } else {
         try {
            if (var2 != null) {
               var4 = ResourceBundle.getBundle(var1, var2);
            } else {
               var4 = ResourceBundle.getBundle(var1);
            }
         } catch (MissingResourceException var9) {
            if (!var3) {
               this.logger.debug("Unable to locate ResourceBundle " + var1);
               return null;
            }
         }

         String var5 = var1;

         int var6;
         while(var4 == null && (var6 = var5.lastIndexOf(46)) > 0) {
            var5 = var5.substring(0, var6);

            try {
               if (var2 != null) {
                  var4 = ResourceBundle.getBundle(var5, var2);
               } else {
                  var4 = ResourceBundle.getBundle(var5);
               }
            } catch (MissingResourceException var8) {
               this.logger.debug("Unable to locate ResourceBundle " + var5);
            }
         }

         return var4;
      }
   }

   public String toString() {
      return this.getFormattedMessage();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      this.getFormattedMessage();
      var1.writeUTF(this.formattedMessage);
      var1.writeUTF(this.key);
      var1.writeUTF(this.baseName);
      var1.writeInt(this.argArray.length);
      this.stringArgs = new String[this.argArray.length];
      int var2 = 0;
      Object[] var3 = this.argArray;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         this.stringArgs[var2] = var6.toString();
         ++var2;
      }

      var1.writeObject(this.stringArgs);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.formattedMessage = var1.readUTF();
      this.key = var1.readUTF();
      this.baseName = var1.readUTF();
      var1.readInt();
      this.stringArgs = (String[])((String[])var1.readObject());
      this.logger = StatusLogger.getLogger();
      this.resourceBundle = null;
      this.argArray = null;
   }
}
