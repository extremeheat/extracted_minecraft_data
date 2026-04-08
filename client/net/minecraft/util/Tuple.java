package net.minecraft.util;

public class Tuple<A, B> {
   private A a;
   private B b;

   public Tuple(final A a, final B b) {
      super();
      this.a = a;
      this.b = b;
   }

   public A getA() {
      return this.a;
   }

   public void setA(final A a) {
      this.a = a;
   }

   public B getB() {
      return this.b;
   }

   public void setB(final B b) {
      this.b = b;
   }
}
