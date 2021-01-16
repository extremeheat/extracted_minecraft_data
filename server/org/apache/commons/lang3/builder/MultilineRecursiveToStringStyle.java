package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SystemUtils;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle {
   private static final long serialVersionUID = 1L;
   private int indent = 2;
   private int spaces = 2;

   public MultilineRecursiveToStringStyle() {
      super();
      this.resetIndent();
   }

   private void resetIndent() {
      this.setArrayStart("{" + SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces));
      this.setArraySeparator("," + SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces));
      this.setArrayEnd(SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces - this.indent) + "}");
      this.setContentStart("[" + SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces));
      this.setFieldSeparator("," + SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces));
      this.setContentEnd(SystemUtils.LINE_SEPARATOR + this.spacer(this.spaces - this.indent) + "]");
   }

   private StringBuilder spacer(int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < var1; ++var3) {
         var2.append(" ");
      }

      return var2;
   }

   public void appendDetail(StringBuffer var1, String var2, Object var3) {
      if (!ClassUtils.isPrimitiveWrapper(var3.getClass()) && !String.class.equals(var3.getClass()) && this.accept(var3.getClass())) {
         this.spaces += this.indent;
         this.resetIndent();
         var1.append(ReflectionToStringBuilder.toString(var3, this));
         this.spaces -= this.indent;
         this.resetIndent();
      } else {
         super.appendDetail(var1, var2, var3);
      }

   }

   protected void appendDetail(StringBuffer var1, String var2, Object[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (Object[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void reflectionAppendArrayDetail(StringBuffer var1, String var2, Object var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, long[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (long[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, int[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (int[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, short[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (short[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, byte[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (byte[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, char[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (char[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, double[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (double[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, float[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (float[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer var1, String var2, boolean[] var3) {
      this.spaces += this.indent;
      this.resetIndent();
      super.appendDetail(var1, var2, (boolean[])var3);
      this.spaces -= this.indent;
      this.resetIndent();
   }
}
