package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

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
      return this.v.func_177374_g();
   }

   public int getIndex() {
      return this.v.func_177369_e();
   }

   public int getByteSize() {
      return this.v.func_177368_f();
   }

   public int getCount() {
      return this.v.func_177370_d();
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
