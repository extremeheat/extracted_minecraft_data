package net.minecraft.util;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(A var1, B var2) {
      super();
      this.a = var1;
      this.b = var2;
   }

   public A getA() {
      return this.a;
   }

   public B getB() {
      return this.b;
   }
}
