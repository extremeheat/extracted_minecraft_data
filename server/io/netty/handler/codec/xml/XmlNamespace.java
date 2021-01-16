package io.netty.handler.codec.xml;

public class XmlNamespace {
   private final String prefix;
   private final String uri;

   public XmlNamespace(String var1, String var2) {
      super();
      this.prefix = var1;
      this.uri = var2;
   }

   public String prefix() {
      return this.prefix;
   }

   public String uri() {
      return this.uri;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlNamespace var2 = (XmlNamespace)var1;
         if (this.prefix != null) {
            if (!this.prefix.equals(var2.prefix)) {
               return false;
            }
         } else if (var2.prefix != null) {
            return false;
         }

         if (this.uri != null) {
            if (this.uri.equals(var2.uri)) {
               return true;
            }
         } else if (var2.uri == null) {
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.prefix != null ? this.prefix.hashCode() : 0;
      var1 = 31 * var1 + (this.uri != null ? this.uri.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlNamespace{prefix='" + this.prefix + '\'' + ", uri='" + this.uri + '\'' + '}';
   }
}
