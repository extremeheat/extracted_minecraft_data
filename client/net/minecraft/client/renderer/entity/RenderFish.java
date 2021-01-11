package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook> {
   private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");

   public RenderFish(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityFishHook var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
      this.func_180548_c(var1);
      Tessellator var10 = Tessellator.func_178181_a();
      WorldRenderer var11 = var10.func_178180_c();
      boolean var12 = true;
      boolean var13 = true;
      float var14 = 0.0625F;
      float var15 = 0.125F;
      float var16 = 0.125F;
      float var17 = 0.1875F;
      float var18 = 1.0F;
      float var19 = 0.5F;
      float var20 = 0.5F;
      GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      var11.func_181668_a(7, DefaultVertexFormats.field_181710_j);
      var11.func_181662_b(-0.5D, -0.5D, 0.0D).func_181673_a(0.0625D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(0.5D, -0.5D, 0.0D).func_181673_a(0.125D, 0.1875D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(0.5D, 0.5D, 0.0D).func_181673_a(0.125D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(-0.5D, 0.5D, 0.0D).func_181673_a(0.0625D, 0.125D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var10.func_78381_a();
      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      if (var1.field_146042_b != null) {
         float var21 = var1.field_146042_b.func_70678_g(var9);
         float var22 = MathHelper.func_76126_a(MathHelper.func_76129_c(var21) * 3.1415927F);
         Vec3 var23 = new Vec3(-0.36D, 0.03D, 0.35D);
         var23 = var23.func_178789_a(-(var1.field_146042_b.field_70127_C + (var1.field_146042_b.field_70125_A - var1.field_146042_b.field_70127_C) * var9) * 3.1415927F / 180.0F);
         var23 = var23.func_178785_b(-(var1.field_146042_b.field_70126_B + (var1.field_146042_b.field_70177_z - var1.field_146042_b.field_70126_B) * var9) * 3.1415927F / 180.0F);
         var23 = var23.func_178785_b(var22 * 0.5F);
         var23 = var23.func_178789_a(-var22 * 0.7F);
         double var24 = var1.field_146042_b.field_70169_q + (var1.field_146042_b.field_70165_t - var1.field_146042_b.field_70169_q) * (double)var9 + var23.field_72450_a;
         double var26 = var1.field_146042_b.field_70167_r + (var1.field_146042_b.field_70163_u - var1.field_146042_b.field_70167_r) * (double)var9 + var23.field_72448_b;
         double var28 = var1.field_146042_b.field_70166_s + (var1.field_146042_b.field_70161_v - var1.field_146042_b.field_70166_s) * (double)var9 + var23.field_72449_c;
         double var30 = (double)var1.field_146042_b.func_70047_e();
         if (this.field_76990_c.field_78733_k != null && this.field_76990_c.field_78733_k.field_74320_O > 0 || var1.field_146042_b != Minecraft.func_71410_x().field_71439_g) {
            float var32 = (var1.field_146042_b.field_70760_ar + (var1.field_146042_b.field_70761_aq - var1.field_146042_b.field_70760_ar) * var9) * 3.1415927F / 180.0F;
            double var33 = (double)MathHelper.func_76126_a(var32);
            double var35 = (double)MathHelper.func_76134_b(var32);
            double var37 = 0.35D;
            double var39 = 0.8D;
            var24 = var1.field_146042_b.field_70169_q + (var1.field_146042_b.field_70165_t - var1.field_146042_b.field_70169_q) * (double)var9 - var35 * 0.35D - var33 * 0.8D;
            var26 = var1.field_146042_b.field_70167_r + var30 + (var1.field_146042_b.field_70163_u - var1.field_146042_b.field_70167_r) * (double)var9 - 0.45D;
            var28 = var1.field_146042_b.field_70166_s + (var1.field_146042_b.field_70161_v - var1.field_146042_b.field_70166_s) * (double)var9 - var33 * 0.35D + var35 * 0.8D;
            var30 = var1.field_146042_b.func_70093_af() ? -0.1875D : 0.0D;
         }

         double var47 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var9;
         double var34 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var9 + 0.25D;
         double var36 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var9;
         double var38 = (double)((float)(var24 - var47));
         double var40 = (double)((float)(var26 - var34)) + var30;
         double var42 = (double)((float)(var28 - var36));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         var11.func_181668_a(3, DefaultVertexFormats.field_181706_f);
         boolean var44 = true;

         for(int var45 = 0; var45 <= 16; ++var45) {
            float var46 = (float)var45 / 16.0F;
            var11.func_181662_b(var2 + var38 * (double)var46, var4 + var40 * (double)(var46 * var46 + var46) * 0.5D + 0.25D, var6 + var42 * (double)var46).func_181669_b(0, 0, 0, 255).func_181675_d();
         }

         var10.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         super.func_76986_a(var1, var2, var4, var6, var8, var9);
      }

   }

   protected ResourceLocation func_110775_a(EntityFishHook var1) {
      return field_110792_a;
   }
}
