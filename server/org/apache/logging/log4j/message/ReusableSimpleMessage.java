package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public class ReusableSimpleMessage implements ReusableMessage, CharSequence {
   private static final long serialVersionUID = -9199974506498249809L;
   private static Object[] EMPTY_PARAMS = new Object[0];
   private CharSequence charSequence;

   public ReusableSimpleMessage() {
      super();
   }

   public void set(String var1) {
      this.charSequence = var1;
   }

   public void set(CharSequence var1) {
      this.charSequence = var1;
   }

   public String getFormattedMessage() {
      return String.valueOf(this.charSequence);
   }

   public String getFormat() {
      return this.getFormattedMessage();
   }

   public Object[] getParameters() {
      return EMPTY_PARAMS;
   }

   public Throwable getThrowable() {
      return null;
   }

   public void formatTo(StringBuilder var1) {
      var1.append(this.charSequence);
   }

   public Object[] swapParameters(Object[] var1) {
      return var1;
   }

   public short getParameterCount() {
      return 0;
   }

   public Message memento() {
      return new SimpleMessage(this.charSequence);
   }

   public int length() {
      return this.charSequence == null ? 0 : this.charSequence.length();
   }

   public char charAt(int var1) {
      return this.charSequence.charAt(var1);
   }

   public CharSequence subSequence(int var1, int var2) {
      return this.charSequence.subSequence(var1, var2);
   }
}
