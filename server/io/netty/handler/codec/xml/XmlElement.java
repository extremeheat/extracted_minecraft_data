package io.netty.handler.codec.xml;

import java.util.LinkedList;
import java.util.List;

public abstract class XmlElement {
   private final String name;
   private final String namespace;
   private final String prefix;
   private final List<XmlNamespace> namespaces = new LinkedList();

   protected XmlElement(String var1, String var2, String var3) {
      super();
      this.name = var1;
      this.namespace = var2;
      this.prefix = var3;
   }

   public String name() {
      return this.name;
   }

   public String namespace() {
      return this.namespace;
   }

   public String prefix() {
      return this.prefix;
   }

   public List<XmlNamespace> namespaces() {
      return this.namespaces;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         XmlElement var2 = (XmlElement)var1;
         if (!this.name.equals(var2.name)) {
            return false;
         } else {
            label48: {
               if (this.namespace != null) {
                  if (this.namespace.equals(var2.namespace)) {
                     break label48;
                  }
               } else if (var2.namespace == null) {
                  break label48;
               }

               return false;
            }

            if (this.namespaces != null) {
               if (!this.namespaces.equals(var2.namespaces)) {
                  return false;
               }
            } else if (var2.namespaces != null) {
               return false;
            }

            if (this.prefix != null) {
               if (!this.prefix.equals(var2.prefix)) {
                  return false;
               }
            } else if (var2.prefix != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.name.hashCode();
      var1 = 31 * var1 + (this.namespace != null ? this.namespace.hashCode() : 0);
      var1 = 31 * var1 + (this.prefix != null ? this.prefix.hashCode() : 0);
      var1 = 31 * var1 + (this.namespaces != null ? this.namespaces.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return ", name='" + this.name + '\'' + ", namespace='" + this.namespace + '\'' + ", prefix='" + this.prefix + '\'' + ", namespaces=" + this.namespaces;
   }
}
