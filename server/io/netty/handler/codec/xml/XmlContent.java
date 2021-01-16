package io.netty.handler.codec.xml;

public abstract class XmlContent {
   private final String data;

   protected XmlContent(String var1) {
      super();
      this.data = var1;
   }

   public String data() {
      return this.data;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlContent var2 = (XmlContent)var1;
         if (this.data != null) {
            if (!this.data.equals(var2.data)) {
               return false;
            }
         } else if (var2.data != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.data != null ? this.data.hashCode() : 0;
   }

   public String toString() {
      return "XmlContent{data='" + this.data + '\'' + '}';
   }
}
