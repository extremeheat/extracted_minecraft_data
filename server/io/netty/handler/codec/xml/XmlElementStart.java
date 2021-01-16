package io.netty.handler.codec.xml;

import java.util.LinkedList;
import java.util.List;

public class XmlElementStart extends XmlElement {
   private final List<XmlAttribute> attributes = new LinkedList();

   public XmlElementStart(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public List<XmlAttribute> attributes() {
      return this.attributes;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         if (!super.equals(var1)) {
            return false;
         } else {
            XmlElementStart var2 = (XmlElementStart)var1;
            if (this.attributes != null) {
               if (!this.attributes.equals(var2.attributes)) {
                  return false;
               }
            } else if (var2.attributes != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + (this.attributes != null ? this.attributes.hashCode() : 0);
      return var1;
   }

   public String toString() {
      return "XmlElementStart{attributes=" + this.attributes + super.toString() + "} ";
   }
}
