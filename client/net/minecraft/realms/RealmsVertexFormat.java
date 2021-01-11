package net.minecraft.realms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

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
      this.v.func_177339_a();
   }

   public int getUvOffset(int var1) {
      return this.v.func_177344_b(var1);
   }

   public int getElementCount() {
      return this.v.func_177345_h();
   }

   public boolean hasColor() {
      return this.v.func_177346_d();
   }

   public boolean hasUv(int var1) {
      return this.v.func_177347_a(var1);
   }

   public RealmsVertexFormatElement getElement(int var1) {
      return new RealmsVertexFormatElement(this.v.func_177348_c(var1));
   }

   public RealmsVertexFormat addElement(RealmsVertexFormatElement var1) {
      return this.from(this.v.func_181721_a(var1.getVertexFormatElement()));
   }

   public int getColorOffset() {
      return this.v.func_177340_e();
   }

   public List<RealmsVertexFormatElement> getElements() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.v.func_177343_g().iterator();

      while(var2.hasNext()) {
         VertexFormatElement var3 = (VertexFormatElement)var2.next();
         var1.add(new RealmsVertexFormatElement(var3));
      }

      return var1;
   }

   public boolean hasNormal() {
      return this.v.func_177350_b();
   }

   public int getVertexSize() {
      return this.v.func_177338_f();
   }

   public int getOffset(int var1) {
      return this.v.func_181720_d(var1);
   }

   public int getNormalOffset() {
      return this.v.func_177342_c();
   }

   public int getIntegerSize() {
      return this.v.func_181719_f();
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
