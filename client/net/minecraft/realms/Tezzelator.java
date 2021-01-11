package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator {
   public static Tessellator t = Tessellator.func_178181_a();
   public static final Tezzelator instance = new Tezzelator();

   public Tezzelator() {
      super();
   }

   public void end() {
      t.func_78381_a();
   }

   public Tezzelator vertex(double var1, double var3, double var5) {
      t.func_178180_c().func_181662_b(var1, var3, var5);
      return this;
   }

   public void color(float var1, float var2, float var3, float var4) {
      t.func_178180_c().func_181666_a(var1, var2, var3, var4);
   }

   public void tex2(short var1, short var2) {
      t.func_178180_c().func_181671_a(var1, var2);
   }

   public void normal(float var1, float var2, float var3) {
      t.func_178180_c().func_181663_c(var1, var2, var3);
   }

   public void begin(int var1, RealmsVertexFormat var2) {
      t.func_178180_c().func_181668_a(var1, var2.getVertexFormat());
   }

   public void endVertex() {
      t.func_178180_c().func_181675_d();
   }

   public void offset(double var1, double var3, double var5) {
      t.func_178180_c().func_178969_c(var1, var3, var5);
   }

   public RealmsBufferBuilder color(int var1, int var2, int var3, int var4) {
      return new RealmsBufferBuilder(t.func_178180_c().func_181669_b(var1, var2, var3, var4));
   }

   public Tezzelator tex(double var1, double var3) {
      t.func_178180_c().func_181673_a(var1, var3);
      return this;
   }
}
