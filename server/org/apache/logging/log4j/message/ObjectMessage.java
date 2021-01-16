package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;

public class ObjectMessage implements Message, StringBuilderFormattable {
   private static final long serialVersionUID = -5903272448334166185L;
   private transient Object obj;
   private transient String objectString;

   public ObjectMessage(Object var1) {
      super();
      this.obj = var1 == null ? "null" : var1;
   }

   public String getFormattedMessage() {
      if (this.objectString == null) {
         this.objectString = String.valueOf(this.obj);
      }

      return this.objectString;
   }

   public void formatTo(StringBuilder var1) {
      if (this.objectString != null) {
         var1.append(this.objectString);
      } else {
         StringBuilders.appendValue(var1, this.obj);
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ObjectMessage var2 = (ObjectMessage)var1;
         return this.obj == null ? var2.obj == null : this.equalObjectsOrStrings(this.obj, var2.obj);
      } else {
         return false;
      }
   }

   private boolean equalObjectsOrStrings(Object var1, Object var2) {
      return var1.equals(var2) || String.valueOf(var1).equals(String.valueOf(var2));
   }

   public int hashCode() {
      return this.obj != null ? this.obj.hashCode() : 0;
   }

   public String toString() {
      return this.getFormattedMessage();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.obj instanceof Serializable) {
         var1.writeObject(this.obj);
      } else {
         var1.writeObject(String.valueOf(this.obj));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.obj = var1.readObject();
   }

   public Throwable getThrowable() {
      return this.obj instanceof Throwable ? (Throwable)this.obj : null;
   }
}
