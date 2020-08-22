package net.minecraft.realms;

import com.mojang.blaze3d.vertex.Tesselator;

public class Tezzelator {
   public static final Tesselator t = Tesselator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public void end() {
      t.end();
   }

   public Tezzelator vertex(double var1, double var3, double var5) {
      t.getBuilder().vertex(var1, var3, var5);
      return this;
   }

   public void begin(int var1, RealmsVertexFormat var2) {
      t.getBuilder().begin(var1, var2.getVertexFormat());
   }

   public void endVertex() {
      t.getBuilder().endVertex();
   }

   public Tezzelator color(int var1, int var2, int var3, int var4) {
      t.getBuilder().color(var1, var2, var3, var4);
      return this;
   }

   public Tezzelator tex(float var1, float var2) {
      t.getBuilder().uv(var1, var2);
      return this;
   }
}
