package io.netty.handler.codec.xml;

public class XmlDTD {
   private final String text;

   public XmlDTD(String var1) {
      super();
      this.text = var1;
   }

   public String text() {
      return this.text;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlDTD var2 = (XmlDTD)var1;
         if (this.text != null) {
            if (!this.text.equals(var2.text)) {
               return false;
            }
         } else if (var2.text != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.text != null ? this.text.hashCode() : 0;
   }

   public String toString() {
      return "XmlDTD{text='" + this.text + '\'' + '}';
   }
}
