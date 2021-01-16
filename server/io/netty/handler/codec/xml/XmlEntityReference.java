package io.netty.handler.codec.xml;

public class XmlEntityReference {
   private final String name;
   private final String text;

   public XmlEntityReference(String var1, String var2) {
      super();
      this.name = var1;
      this.text = var2;
   }

   public String name() {
      return this.name;
   }

   public String text() {
      return this.text;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlEntityReference var2 = (XmlEntityReference)var1;
         if (this.name != null) {
            if (!this.name.equals(var2.name)) {
               return false;
            }
         } else if (var2.name != null) {
            return false;
         }

         if (this.text != null) {
            if (this.text.equals(var2.text)) {
               return true;
            }
         } else if (var2.text == null) {
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.name != null ? this.name.hashCode() : 0;
      var1 = 31 * var1 + (this.text != null ? this.text.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlEntityReference{name='" + this.name + '\'' + ", text='" + this.text + '\'' + '}';
   }
}
