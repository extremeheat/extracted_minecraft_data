package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class DebugRenderer {
   public final DebugRendererPathfinding field_188286_a;
   public final DebugRenderer.IDebugRenderer field_188287_b;
   public final DebugRenderer.IDebugRenderer field_190077_c;
   public final DebugRenderer.IDebugRenderer field_190078_d;
   public final DebugRenderer.IDebugRenderer field_191325_e;
   public final DebugRenderer.IDebugRenderer field_191557_f;
   public final DebugRendererCave field_201747_g;
   public final DebugRendererStructure field_201748_h;
   public final DebugRenderer.IDebugRenderer field_201749_i;
   public final DebugRenderer.IDebugRenderer field_201750_j;
   public final DebugRenderer.IDebugRenderer field_193852_g;
   private boolean field_190079_e;
   private boolean field_190080_f;
   private boolean field_190081_g;
   private boolean field_190082_h;
   private boolean field_191326_j;
   private boolean field_191558_l;
   private boolean field_201751_r;
   private boolean field_201752_s;
   private boolean field_201753_t;
   private boolean field_201754_u;
   private boolean field_193853_n;

   public DebugRenderer(Minecraft var1) {
      super();
      this.field_188286_a = new DebugRendererPathfinding(var1);
      this.field_188287_b = new DebugRendererWater(var1);
      this.field_190077_c = new DebugRendererChunkBorder(var1);
      this.field_190078_d = new DebugRendererHeightMap(var1);
      this.field_191325_e = new DebugRendererCollisionBox(var1);
      this.field_191557_f = new DebugRendererNeighborsUpdate(var1);
      this.field_201747_g = new DebugRendererCave(var1);
      this.field_201748_h = new DebugRendererStructure(var1);
      this.field_201749_i = new DebugRendererLight(var1);
      this.field_201750_j = new DebugRendererWorldGenAttempts(var1);
      this.field_193852_g = new DebugRendererSolidFace(var1);
   }

   public boolean func_190074_a() {
      return this.field_190079_e || this.field_190080_f || this.field_190081_g || this.field_190082_h || this.field_191326_j || this.field_191558_l || this.field_201753_t || this.field_201754_u || this.field_193853_n;
   }

   public boolean func_190075_b() {
      this.field_190079_e = !this.field_190079_e;
      return this.field_190079_e;
   }

   public void func_190073_a(float var1, long var2) {
      if (this.field_190080_f) {
         this.field_188286_a.func_190060_a(var1, var2);
      }

      if (this.field_190079_e && !Minecraft.func_71410_x().func_189648_am()) {
         this.field_190077_c.func_190060_a(var1, var2);
      }

      if (this.field_190081_g) {
         this.field_188287_b.func_190060_a(var1, var2);
      }

      if (this.field_190082_h) {
         this.field_190078_d.func_190060_a(var1, var2);
      }

      if (this.field_191326_j) {
         this.field_191325_e.func_190060_a(var1, var2);
      }

      if (this.field_191558_l) {
         this.field_191557_f.func_190060_a(var1, var2);
      }

      if (this.field_201751_r) {
         this.field_201747_g.func_190060_a(var1, var2);
      }

      if (this.field_201752_s) {
         this.field_201748_h.func_190060_a(var1, var2);
      }

      if (this.field_201753_t) {
         this.field_201749_i.func_190060_a(var1, var2);
      }

      if (this.field_201754_u) {
         this.field_201750_j.func_190060_a(var1, var2);
      }

      if (this.field_193853_n) {
         this.field_193852_g.func_190060_a(var1, var2);
      }

   }

   public static void func_191556_a(String var0, int var1, int var2, int var3, float var4, int var5) {
      func_190076_a(var0, (double)var1 + 0.5D, (double)var2 + 0.5D, (double)var3 + 0.5D, var4, var5);
   }

   public static void func_190076_a(String var0, double var1, double var3, double var5, float var7, int var8) {
      Minecraft var9 = Minecraft.func_71410_x();
      if (var9.field_71439_g != null && var9.func_175598_ae() != null && var9.func_175598_ae().field_78733_k != null) {
         FontRenderer var10 = var9.field_71466_p;
         EntityPlayerSP var11 = var9.field_71439_g;
         double var12 = var11.field_70142_S + (var11.field_70165_t - var11.field_70142_S) * (double)var7;
         double var14 = var11.field_70137_T + (var11.field_70163_u - var11.field_70137_T) * (double)var7;
         double var16 = var11.field_70136_U + (var11.field_70161_v - var11.field_70136_U) * (double)var7;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)(var1 - var12), (float)(var3 - var14) + 0.07F, (float)(var5 - var16));
         GlStateManager.func_187432_a(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179152_a(0.02F, -0.02F, 0.02F);
         RenderManager var18 = var9.func_175598_ae();
         GlStateManager.func_179114_b(-var18.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b((float)(var18.field_78733_k.field_74320_O == 2 ? 1 : -1) * var18.field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179140_f();
         GlStateManager.func_179098_w();
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179152_a(-1.0F, 1.0F, 1.0F);
         var10.func_211126_b(var0, (float)(-var10.func_78256_a(var0) / 2), 0.0F, var8);
         GlStateManager.func_179145_e();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }
   }

   public interface IDebugRenderer {
      void func_190060_a(float var1, long var2);
   }
}
