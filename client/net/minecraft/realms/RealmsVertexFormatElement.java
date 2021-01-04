package net.minecraft.realms;

import com.mojang.blaze3d.vertex.VertexFormatElement;

public class RealmsVertexFormatElement {
   private final VertexFormatElement v;

   public RealmsVertexFormatElement(VertexFormatElement var1) {
      super();
      this.v = var1;
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.v;
   }

   public boolean isPosition() {
      return this.v.isPosition();
   }

   public int getIndex() {
      return this.v.getIndex();
   }

   public int getByteSize() {
      return this.v.getByteSize();
   }

   public int getCount() {
      return this.v.getCount();
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public boolean equals(Object var1) {
      return this.v.equals(var1);
   }

   public String toString() {
      return this.v.toString();
   }
}
