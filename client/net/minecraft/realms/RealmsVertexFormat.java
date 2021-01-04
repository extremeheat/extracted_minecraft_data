package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat var1) {
      super();
      this.v = var1;
   }

   public RealmsVertexFormat from(VertexFormat var1) {
      this.v = var1;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public void clear() {
      this.v.clear();
   }

   public int getUvOffset(int var1) {
      return this.v.getUvOffset(var1);
   }

   public int getElementCount() {
      return this.v.getElementCount();
   }

   public boolean hasColor() {
      return this.v.hasColor();
   }

   public boolean hasUv(int var1) {
      return this.v.hasUv(var1);
   }

   public RealmsVertexFormatElement getElement(int var1) {
      return new RealmsVertexFormatElement(this.v.getElement(var1));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement var1) {
      return this.from(this.v.addElement(var1.getVertexFormatElement()));
   }

   public int getColorOffset() {
      return this.v.getColorOffset();
   }

   public List<RealmsVertexFormatElement> getElements() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.v.getElements().iterator();

      while(var2.hasNext()) {
         VertexFormatElement var3 = (VertexFormatElement)var2.next();
         var1.add(new RealmsVertexFormatElement(var3));
      }

      return var1;
   }

   public boolean hasNormal() {
      return this.v.hasNormal();
   }

   public int getVertexSize() {
      return this.v.getVertexSize();
   }

   public int getOffset(int var1) {
      return this.v.getOffset(var1);
   }

   public int getNormalOffset() {
      return this.v.getNormalOffset();
   }

   public int getIntegerSize() {
      return this.v.getIntegerSize();
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
