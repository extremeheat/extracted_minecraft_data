package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@PerformanceSensitive({"allocation"})
public class ReusableObjectMessage implements ReusableMessage {
   private static final long serialVersionUID = 6922476812535519960L;
   private transient Object obj;
   private transient String objectString;

   public ReusableObjectMessage() {
      super();
   }

   public void set(Object var1) {
      this.obj = var1;
   }

   public String getFormattedMessage() {
      return String.valueOf(this.obj);
   }

   public void formatTo(StringBuilder var1) {
      if (this.obj != null && !(this.obj instanceof String)) {
         if (this.obj instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable)this.obj).formatTo(var1);
         } else if (this.obj instanceof CharSequence) {
            var1.append((CharSequence)this.obj);
         } else if (this.obj instanceof Integer) {
            var1.append((Integer)this.obj);
         } else if (this.obj instanceof Long) {
            var1.append((Long)this.obj);
         } else if (this.obj instanceof Double) {
            var1.append((Double)this.obj);
         } else if (this.obj instanceof Boolean) {
            var1.append((Boolean)this.obj);
         } else if (this.obj instanceof Character) {
            var1.append((Character)this.obj);
         } else if (this.obj instanceof Short) {
            var1.append((Short)this.obj);
         } else if (this.obj instanceof Float) {
            var1.append((Float)this.obj);
         } else {
            var1.append(this.obj);
         }
      } else {
         var1.append((String)this.obj);
      }

   }

   public String getFormat() {
      return this.getFormattedMessage();
   }

   public Object getParameter() {
      return this.obj;
   }

   public Object[] getParameters() {
      return new Object[]{this.obj};
   }

   public String toString() {
      return this.getFormattedMessage();
   }

   public Throwable getThrowable() {
      return this.obj instanceof Throwable ? (Throwable)this.obj : null;
   }

   public Object[] swapParameters(Object[] var1) {
      return var1;
   }

   public short getParameterCount() {
      return 0;
   }

   public Message memento() {
      return new ObjectMessage(this.obj);
   }
}
