package org.apache.logging.log4j.message;

import java.util.Arrays;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class ParameterizedMessage implements Message, StringBuilderFormattable {
   private static final int DEFAULT_STRING_BUILDER_SIZE = 255;
   public static final String RECURSION_PREFIX = "[...";
   public static final String RECURSION_SUFFIX = "...]";
   public static final String ERROR_PREFIX = "[!!!";
   public static final String ERROR_SEPARATOR = "=>";
   public static final String ERROR_MSG_SEPARATOR = ":";
   public static final String ERROR_SUFFIX = "!!!]";
   private static final long serialVersionUID = -665975803997290697L;
   private static final int HASHVAL = 31;
   private static ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal();
   private String messagePattern;
   private transient Object[] argArray;
   private String formattedMessage;
   private transient Throwable throwable;
   private int[] indices;
   private int usedCount;

   /** @deprecated */
   @Deprecated
   public ParameterizedMessage(String var1, String[] var2, Throwable var3) {
      super();
      this.argArray = var2;
      this.throwable = var3;
      this.init(var1);
   }

   public ParameterizedMessage(String var1, Object[] var2, Throwable var3) {
      super();
      this.argArray = var2;
      this.throwable = var3;
      this.init(var1);
   }

   public ParameterizedMessage(String var1, Object... var2) {
      super();
      this.argArray = var2;
      this.init(var1);
   }

   public ParameterizedMessage(String var1, Object var2) {
      this(var1, var2);
   }

   public ParameterizedMessage(String var1, Object var2, Object var3) {
      this(var1, var2, var3);
   }

   private void init(String var1) {
      this.messagePattern = var1;
      int var2 = Math.max(1, var1 == null ? 0 : var1.length() >> 1);
      this.indices = new int[var2];
      int var3 = ParameterFormatter.countArgumentPlaceholders2(var1, this.indices);
      this.initThrowable(this.argArray, var3);
      this.usedCount = Math.min(var3, this.argArray == null ? 0 : this.argArray.length);
   }

   private void initThrowable(Object[] var1, int var2) {
      if (var1 != null) {
         int var3 = var1.length;
         if (var2 < var3 && this.throwable == null && var1[var3 - 1] instanceof Throwable) {
            this.throwable = (Throwable)var1[var3 - 1];
         }
      }

   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return this.argArray;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public String getFormattedMessage() {
      if (this.formattedMessage == null) {
         StringBuilder var1 = getThreadLocalStringBuilder();
         this.formatTo(var1);
         this.formattedMessage = var1.toString();
      }

      return this.formattedMessage;
   }

   private static StringBuilder getThreadLocalStringBuilder() {
      StringBuilder var0 = (StringBuilder)threadLocalStringBuilder.get();
      if (var0 == null) {
         var0 = new StringBuilder(255);
         threadLocalStringBuilder.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   public void formatTo(StringBuilder var1) {
      if (this.formattedMessage != null) {
         var1.append(this.formattedMessage);
      } else if (this.indices[0] < 0) {
         ParameterFormatter.formatMessage(var1, this.messagePattern, this.argArray, this.usedCount);
      } else {
         ParameterFormatter.formatMessage2(var1, this.messagePattern, this.argArray, this.usedCount, this.indices);
      }

   }

   public static String format(String var0, Object[] var1) {
      return ParameterFormatter.format(var0, var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ParameterizedMessage var2 = (ParameterizedMessage)var1;
         if (this.messagePattern != null) {
            if (this.messagePattern.equals(var2.messagePattern)) {
               return Arrays.equals(this.argArray, var2.argArray);
            }
         } else if (var2.messagePattern == null) {
            return Arrays.equals(this.argArray, var2.argArray);
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
      var1 = 31 * var1 + (this.argArray != null ? Arrays.hashCode(this.argArray) : 0);
      return var1;
   }

   public static int countArgumentPlaceholders(String var0) {
      return ParameterFormatter.countArgumentPlaceholders(var0);
   }

   public static String deepToString(Object var0) {
      return ParameterFormatter.deepToString(var0);
   }

   public static String identityToString(Object var0) {
      return ParameterFormatter.identityToString(var0);
   }

   public String toString() {
      return "ParameterizedMessage[messagePattern=" + this.messagePattern + ", stringArgs=" + Arrays.toString(this.argArray) + ", throwable=" + this.throwable + ']';
   }
}
