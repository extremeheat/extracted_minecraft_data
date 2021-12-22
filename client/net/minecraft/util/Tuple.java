package net.minecraft.util;

public class Tuple<A, B> {
   // $FF: renamed from: a java.lang.Object
   private A field_407;
   // $FF: renamed from: b java.lang.Object
   private B field_408;

   public Tuple(A var1, B var2) {
      super();
      this.field_407 = var1;
      this.field_408 = var2;
   }

   public A getA() {
      return this.field_407;
   }

   public void setA(A var1) {
      this.field_407 = var1;
   }

   public B getB() {
      return this.field_408;
   }

   public void setB(B var1) {
      this.field_408 = var1;
   }
}
