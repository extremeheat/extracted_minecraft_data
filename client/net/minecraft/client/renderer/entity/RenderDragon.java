package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonDeath;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonEyes;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderDragon extends RenderLiving<EntityDragon> {
   private static final ResourceLocation field_110843_g = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
   private static final ResourceLocation field_110842_f = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation field_110844_k = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   protected ModelDragon field_77084_b;

   public RenderDragon(RenderManager var1) {
      super(var1, new ModelDragon(0.0F), 0.5F);
      this.field_77084_b = (ModelDragon)this.field_77045_g;
      this.func_177094_a(new LayerEnderDragonEyes(this));
      this.func_177094_a(new LayerEnderDragonDeath());
   }

   protected void func_77043_a(EntityDragon var1, float var2, float var3, float var4) {
      float var5 = (float)var1.func_70974_a(7, var4)[0];
      float var6 = (float)(var1.func_70974_a(5, var4)[1] - var1.func_70974_a(10, var4)[1]);
      GlStateManager.func_179114_b(-var5, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.0F, 1.0F);
      if (var1.field_70725_aQ > 0) {
         float var7 = ((float)var1.field_70725_aQ + var4 - 1.0F) / 20.0F * 1.6F;
         var7 = MathHelper.func_76129_c(var7);
         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         GlStateManager.func_179114_b(var7 * this.func_77037_a(var1), 0.0F, 0.0F, 1.0F);
      }

   }

   protected void func_77036_a(EntityDragon var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (var1.field_70995_bG > 0) {
         float var8 = (float)var1.field_70995_bG / 200.0F;
         GlStateManager.func_179143_c(515);
         GlStateManager.func_179141_d();
         GlStateManager.func_179092_a(516, var8);
         this.func_110776_a(field_110842_f);
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         GlStateManager.func_179092_a(516, 0.1F);
         GlStateManager.func_179143_c(514);
      }

      this.func_180548_c(var1);
      this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      if (var1.field_70737_aN > 0) {
         GlStateManager.func_179143_c(514);
         GlStateManager.func_179090_x();
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         GlStateManager.func_179131_c(1.0F, 0.0F, 0.0F, 0.5F);
         this.field_77045_g.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         GlStateManager.func_179143_c(515);
      }

   }

   public void func_76986_a(EntityDragon var1, double var2, double var4, double var6, float var8, float var9) {
      BossStatus.func_82824_a(var1, false);
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
      if (var1.field_70992_bH != null) {
         this.func_180574_a(var1, var2, var4, var6, var9);
      }

   }

   protected void func_180574_a(EntityDragon var1, double var2, double var4, double var6, float var8) {
      float var9 = (float)var1.field_70992_bH.field_70261_a + var8;
      float var10 = MathHelper.func_76126_a(var9 * 0.2F) / 2.0F + 0.5F;
      var10 = (var10 * var10 + var10) * 0.2F;
      float var11 = (float)(var1.field_70992_bH.field_70165_t - var1.field_70165_t - (var1.field_70169_q - var1.field_70165_t) * (double)(1.0F - var8));
      float var12 = (float)((double)var10 + var1.field_70992_bH.field_70163_u - 1.0D - var1.field_70163_u - (var1.field_70167_r - var1.field_70163_u) * (double)(1.0F - var8));
      float var13 = (float)(var1.field_70992_bH.field_70161_v - var1.field_70161_v - (var1.field_70166_s - var1.field_70161_v) * (double)(1.0F - var8));
      float var14 = MathHelper.func_76129_c(var11 * var11 + var13 * var13);
      float var15 = MathHelper.func_76129_c(var11 * var11 + var12 * var12 + var13 * var13);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4 + 2.0F, (float)var6);
      GlStateManager.func_179114_b((float)(-Math.atan2((double)var13, (double)var11)) * 180.0F / 3.1415927F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b((float)(-Math.atan2((double)var14, (double)var12)) * 180.0F / 3.1415927F - 90.0F, 1.0F, 0.0F, 0.0F);
      Tessellator var16 = Tessellator.func_178181_a();
      WorldRenderer var17 = var16.func_178180_c();
      RenderHelper.func_74518_a();
      GlStateManager.func_179129_p();
      this.func_110776_a(field_110843_g);
      GlStateManager.func_179103_j(7425);
      float var18 = 0.0F - ((float)var1.field_70173_aa + var8) * 0.01F;
      float var19 = MathHelper.func_76129_c(var11 * var11 + var12 * var12 + var13 * var13) / 32.0F - ((float)var1.field_70173_aa + var8) * 0.01F;
      var17.func_181668_a(5, DefaultVertexFormats.field_181709_i);
      boolean var20 = true;

      for(int var21 = 0; var21 <= 8; ++var21) {
         float var22 = MathHelper.func_76126_a((float)(var21 % 8) * 3.1415927F * 2.0F / 8.0F) * 0.75F;
         float var23 = MathHelper.func_76134_b((float)(var21 % 8) * 3.1415927F * 2.0F / 8.0F) * 0.75F;
         float var24 = (float)(var21 % 8) * 1.0F / 8.0F;
         var17.func_181662_b((double)(var22 * 0.2F), (double)(var23 * 0.2F), 0.0D).func_181673_a((double)var24, (double)var19).func_181669_b(0, 0, 0, 255).func_181675_d();
         var17.func_181662_b((double)var22, (double)var23, (double)var15).func_181673_a((double)var24, (double)var18).func_181669_b(255, 255, 255, 255).func_181675_d();
      }

      var16.func_78381_a();
      GlStateManager.func_179089_o();
      GlStateManager.func_179103_j(7424);
      RenderHelper.func_74519_b();
      GlStateManager.func_179121_F();
   }

   protected ResourceLocation func_110775_a(EntityDragon var1) {
      return field_110844_k;
   }
}
