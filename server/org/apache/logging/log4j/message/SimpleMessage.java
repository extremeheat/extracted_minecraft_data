package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class SimpleMessage implements Message, StringBuilderFormattable, CharSequence {
   private static final long serialVersionUID = -8398002534962715992L;
   private String message;
   private transient CharSequence charSequence;

   public SimpleMessage() {
      this((String)null);
   }

   public SimpleMessage(String var1) {
      super();
      this.message = var1;
      this.charSequence = var1;
   }

   public SimpleMessage(CharSequence var1) {
      super();
      this.charSequence = var1;
   }

   public String getFormattedMessage() {
      if (this.message == null) {
         this.message = String.valueOf(this.charSequence);
      }

      return this.message;
   }

   public void formatTo(StringBuilder var1) {
      if (this.message != null) {
         var1.append(this.message);
      } else {
         var1.append(this.charSequence);
      }

   }

   public String getFormat() {
      return this.getFormattedMessage();
   }

   public Object[] getParameters() {
      return null;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         boolean var10000;
         label35: {
            SimpleMessage var2 = (SimpleMessage)var1;
            if (this.charSequence != null) {
               if (this.charSequence.equals(var2.charSequence)) {
                  break label35;
               }
            } else if (var2.charSequence == null) {
               break label35;
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.charSequence != null ? this.charSequence.hashCode() : 0;
   }

   public String toString() {
      return this.getFormattedMessage();
   }

   public Throwable getThrowable() {
      return null;
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

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.getFormattedMessage();
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.charSequence = this.message;
   }
}
