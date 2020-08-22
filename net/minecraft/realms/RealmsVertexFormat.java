package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.List;

public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat var1) {
      this.v = var1;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public List getElements() {
      ArrayList var1 = Lists.newArrayList();
      UnmodifiableIterator var2 = this.v.getElements().iterator();

      while(var2.hasNext()) {
         VertexFormatElement var3 = (VertexFormatElement)var2.next();
         var1.add(new RealmsVertexFormatElement(var3));
      }

      return var1;
   }

   public boolean equals(Object var1) {
      return this.v.equals(var1);
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public String toString() {
      return this.v.toString();
   }
}
