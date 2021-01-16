package io.netty.handler.codec.xml;

public class XmlProcessingInstruction {
   private final String data;
   private final String target;

   public XmlProcessingInstruction(String var1, String var2) {
      super();
      this.data = var1;
      this.target = var2;
   }

   public String data() {
      return this.data;
   }

   public String target() {
      return this.target;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlProcessingInstruction var2 = (XmlProcessingInstruction)var1;
         if (this.data != null) {
            if (!this.data.equals(var2.data)) {
               return false;
            }
         } else if (var2.data != null) {
            return false;
         }

         if (this.target != null) {
            if (this.target.equals(var2.target)) {
               return true;
            }
         } else if (var2.target == null) {
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.data != null ? this.data.hashCode() : 0;
      var1 = 31 * var1 + (this.target != null ? this.target.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlProcessingInstruction{data='" + this.data + '\'' + ", target='" + this.target + '\'' + '}';
   }
}
