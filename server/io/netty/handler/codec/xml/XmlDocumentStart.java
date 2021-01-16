package io.netty.handler.codec.xml;

public class XmlDocumentStart {
   private final String encoding;
   private final String version;
   private final boolean standalone;
   private final String encodingScheme;

   public XmlDocumentStart(String var1, String var2, boolean var3, String var4) {
      super();
      this.encoding = var1;
      this.version = var2;
      this.standalone = var3;
      this.encodingScheme = var4;
   }

   public String encoding() {
      return this.encoding;
   }

   public String version() {
      return this.version;
   }

   public boolean standalone() {
      return this.standalone;
   }

   public String encodingScheme() {
      return this.encodingScheme;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlDocumentStart var2 = (XmlDocumentStart)var1;
         if (this.standalone != var2.standalone) {
            return false;
         } else {
            label48: {
               if (this.encoding != null) {
                  if (this.encoding.equals(var2.encoding)) {
                     break label48;
                  }
               } else if (var2.encoding == null) {
                  break label48;
               }

               return false;
            }

            if (this.encodingScheme != null) {
               if (!this.encodingScheme.equals(var2.encodingScheme)) {
                  return false;
               }
            } else if (var2.encodingScheme != null) {
               return false;
            }

            if (this.version != null) {
               if (!this.version.equals(var2.version)) {
                  return false;
               }
            } else if (var2.version != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.encoding != null ? this.encoding.hashCode() : 0;
      var1 = 31 * var1 + (this.version != null ? this.version.hashCode() : 0);
      var1 = 31 * var1 + (this.standalone ? 1 : 0);
      var1 = 31 * var1 + (this.encodingScheme != null ? this.encodingScheme.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlDocumentStart{encoding='" + this.encoding + '\'' + ", version='" + this.version + '\'' + ", standalone=" + this.standalone + ", encodingScheme='" + this.encodingScheme + '\'' + '}';
   }
}
