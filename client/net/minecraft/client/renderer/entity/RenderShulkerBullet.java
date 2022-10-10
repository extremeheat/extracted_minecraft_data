package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelShulkerBullet;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderShulkerBullet extends Render<EntityShulkerBullet> {
   private static final ResourceLocation field_188348_a = new ResourceLocation("textures/entity/shulker/spark.png");
   private final ModelShulkerBullet field_188349_b = new ModelShulkerBullet();

   public RenderShulkerBullet(RenderManager var1) {
      super(var1);
   }

   private float func_188347_a(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void func_76986_a(EntityShulkerBullet var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      float var10 = this.func_188347_a(var1.field_70126_B, var1.field_70177_z, var9);
      float var11 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
      float var12 = (float)var1.field_70173_aa + var9;
      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.func_179114_b(MathHelper.func_76126_a(var12 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(MathHelper.func_76134_b(var12 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(MathHelper.func_76126_a(var12 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
      float var13 = 0.03125F;
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      this.func_180548_c(var1);
      this.field_188349_b.func_78088_a(var1, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.func_179147_l();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.func_179152_a(1.5F, 1.5F, 1.5F);
      this.field_188349_b.func_78088_a(var1, 0.0F, 0.0F, 0.0F, var10, var11, 0.03125F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityShulkerBullet var1) {
      return field_188348_a;
   }
}
