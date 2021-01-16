package io.netty.handler.codec.xml;

public class XmlAttribute {
   private final String type;
   private final String name;
   private final String prefix;
   private final String namespace;
   private final String value;

   public XmlAttribute(String var1, String var2, String var3, String var4, String var5) {
      super();
      this.type = var1;
      this.name = var2;
      this.prefix = var3;
      this.namespace = var4;
      this.value = var5;
   }

   public String type() {
      return this.type;
   }

   public String name() {
      return this.name;
   }

   public String prefix() {
      return this.prefix;
   }

   public String namespace() {
      return this.namespace;
   }

   public String value() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlAttribute var2 = (XmlAttribute)var1;
         if (!this.name.equals(var2.name)) {
            return false;
         } else {
            label60: {
               if (this.namespace != null) {
                  if (this.namespace.equals(var2.namespace)) {
                     break label60;
                  }
               } else if (var2.namespace == null) {
                  break label60;
               }

               return false;
            }

            if (this.prefix != null) {
               if (!this.prefix.equals(var2.prefix)) {
                  return false;
               }
            } else if (var2.prefix != null) {
               return false;
            }

            if (this.type != null) {
               if (!this.type.equals(var2.type)) {
                  return false;
               }
            } else if (var2.type != null) {
               return false;
            }

            if (this.value != null) {
               if (!this.value.equals(var2.value)) {
                  return false;
               }
            } else if (var2.value != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.type != null ? this.type.hashCode() : 0;
      var1 = 31 * var1 + this.name.hashCode();
      var1 = 31 * var1 + (this.prefix != null ? this.prefix.hashCode() : 0);
      var1 = 31 * var1 + (this.namespace != null ? this.namespace.hashCode() : 0);
      var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlAttribute{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", prefix='" + this.prefix + '\'' + ", namespace='" + this.namespace + '\'' + ", value='" + this.value + '\'' + '}';
   }
}
