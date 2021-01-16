package org.apache.logging.log4j.message;

import java.util.Arrays;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public class ReusableParameterizedMessage implements ReusableMessage {
   private static final int MIN_BUILDER_SIZE = 512;
   private static final int MAX_PARMS = 10;
   private static final long serialVersionUID = 7800075879295123856L;
   private transient ThreadLocal<StringBuilder> buffer;
   private String messagePattern;
   private int argCount;
   private int usedCount;
   private final int[] indices = new int[256];
   private transient Object[] varargs;
   private transient Object[] params = new Object[10];
   private transient Throwable throwable;
   transient boolean reserved = false;

   public ReusableParameterizedMessage() {
      super();
   }

   private Object[] getTrimmedParams() {
      return this.varargs == null ? Arrays.copyOf(this.params, this.argCount) : this.varargs;
   }

   private Object[] getParams() {
      return this.varargs == null ? this.params : this.varargs;
   }

   public Object[] swapParameters(Object[] var1) {
      Object[] var2;
      if (this.varargs == null) {
         var2 = this.params;
         if (var1.length >= 10) {
            this.params = var1;
         } else if (this.argCount <= var1.length) {
            System.arraycopy(this.params, 0, var1, 0, this.argCount);
            var2 = var1;
         } else {
            this.params = new Object[10];
         }
      } else {
         if (this.argCount <= var1.length) {
            var2 = var1;
         } else {
            var2 = new Object[this.argCount];
         }

         System.arraycopy(this.varargs, 0, var2, 0, this.argCount);
      }

      return var2;
   }

   public short getParameterCount() {
      return (short)this.argCount;
   }

   public Message memento() {
      return new ParameterizedMessage(this.messagePattern, this.getTrimmedParams());
   }

   private void init(String var1, int var2, Object[] var3) {
      this.varargs = null;
      this.messagePattern = var1;
      this.argCount = var2;
      int var4 = count(var1, this.indices);
      this.initThrowable(var3, var2, var4);
      this.usedCount = Math.min(var4, var2);
   }

   private static int count(String var0, int[] var1) {
      try {
         return ParameterFormatter.countArgumentPlaceholders2(var0, var1);
      } catch (Exception var3) {
         return ParameterFormatter.countArgumentPlaceholders(var0);
      }
   }

   private void initThrowable(Object[] var1, int var2, int var3) {
      if (var3 < var2 && var1[var2 - 1] instanceof Throwable) {
         this.throwable = (Throwable)var1[var2 - 1];
      } else {
         this.throwable = null;
      }

   }

   ReusableParameterizedMessage set(String var1, Object... var2) {
      this.init(var1, var2 == null ? 0 : var2.length, var2);
      this.varargs = var2;
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2) {
      this.params[0] = var2;
      this.init(var1, 1, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.init(var1, 2, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.init(var1, 3, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.init(var1, 4, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.init(var1, 5, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.params[5] = var7;
      this.init(var1, 6, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.params[5] = var7;
      this.params[6] = var8;
      this.init(var1, 7, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.params[5] = var7;
      this.params[6] = var8;
      this.params[7] = var9;
      this.init(var1, 8, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.params[5] = var7;
      this.params[6] = var8;
      this.params[7] = var9;
      this.params[8] = var10;
      this.init(var1, 9, this.params);
      return this;
   }

   ReusableParameterizedMessage set(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.params[0] = var2;
      this.params[1] = var3;
      this.params[2] = var4;
      this.params[3] = var5;
      this.params[4] = var6;
      this.params[5] = var7;
      this.params[6] = var8;
      this.params[7] = var9;
      this.params[8] = var10;
      this.params[9] = var11;
      this.init(var1, 10, this.params);
      return this;
   }

   public String getFormat() {
      return this.messagePattern;
   }

   public Object[] getParameters() {
      return this.getTrimmedParams();
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public String getFormattedMessage() {
      StringBuilder var1 = this.getBuffer();
      this.formatTo(var1);
      return var1.toString();
   }

   private StringBuilder getBuffer() {
      if (this.buffer == null) {
         this.buffer = new ThreadLocal();
      }

      StringBuilder var1 = (StringBuilder)this.buffer.get();
      if (var1 == null) {
         int var2 = this.messagePattern == null ? 0 : this.messagePattern.length();
         var1 = new StringBuilder(Math.min(512, var2 * 2));
         this.buffer.set(var1);
      }

      var1.setLength(0);
      return var1;
   }

   public void formatTo(StringBuilder var1) {
      if (this.indices[0] < 0) {
         ParameterFormatter.formatMessage(var1, this.messagePattern, this.getParams(), this.argCount);
      } else {
         ParameterFormatter.formatMessage2(var1, this.messagePattern, this.getParams(), this.usedCount, this.indices);
      }

   }

   ReusableParameterizedMessage reserve() {
      this.reserved = true;
      return this;
   }

   public String toString() {
      return "ReusableParameterizedMessage[messagePattern=" + this.getFormat() + ", stringArgs=" + Arrays.toString(this.getParameters()) + ", throwable=" + this.getThrowable() + ']';
   }
}
