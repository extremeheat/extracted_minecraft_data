package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

public class Tezzelator {
   public static Tessellator t = Tessellator.field_78398_a;
   public static final Tezzelator instance = new Tezzelator();

   public Tezzelator() {
      super();
   }

   public int end() {
      return t.func_78381_a();
   }

   public void vertex(double var1, double var3, double var5) {
      t.func_78377_a(var1, var3, var5);
   }

   public void color(float var1, float var2, float var3, float var4) {
      t.func_78369_a(var1, var2, var3, var4);
   }

   public void color(int var1, int var2, int var3) {
      t.func_78376_a(var1, var2, var3);
   }

   public void tex2(int var1) {
      t.func_78380_c(var1);
   }

   public void normal(float var1, float var2, float var3) {
      t.func_78375_b(var1, var2, var3);
   }

   public void noColor() {
      t.func_78383_c();
   }

   public void color(int var1) {
      t.func_78378_d(var1);
   }

   public void color(float var1, float var2, float var3) {
      t.func_78386_a(var1, var2, var3);
   }

   public TesselatorVertexState sortQuads(float var1, float var2, float var3) {
      return t.func_147564_a(var1, var2, var3);
   }

   public void restoreState(TesselatorVertexState var1) {
      t.func_147565_a(var1);
   }

   public void begin(int var1) {
      t.func_78371_b(var1);
   }

   public void begin() {
      t.func_78382_b();
   }

   public void vertexUV(double var1, double var3, double var5, double var7, double var9) {
      t.func_78374_a(var1, var3, var5, var7, var9);
   }

   public void color(int var1, int var2) {
      t.func_78384_a(var1, var2);
   }

   public void offset(double var1, double var3, double var5) {
      t.func_78373_b(var1, var3, var5);
   }

   public void color(int var1, int var2, int var3, int var4) {
      t.func_78370_a(var1, var2, var3, var4);
   }

   public void addOffset(float var1, float var2, float var3) {
      t.func_78372_c(var1, var2, var3);
   }

   public void tex(double var1, double var3) {
      t.func_78385_a(var1, var3);
   }

   public void color(byte var1, byte var2, byte var3) {
      t.func_154352_a(var1, var2, var3);
   }
}
