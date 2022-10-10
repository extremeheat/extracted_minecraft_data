package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerWitherAura;
import net.minecraft.client.renderer.entity.model.ModelWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.ResourceLocation;

public class RenderWither extends RenderLiving<EntityWither> {
   private static final ResourceLocation field_110913_a = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation field_110912_f = new ResourceLocation("textures/entity/wither/wither.png");

   public RenderWither(RenderManager var1) {
      super(var1, new ModelWither(0.0F), 1.0F);
      this.func_177094_a(new LayerWitherAura(this));
   }

   protected ResourceLocation func_110775_a(EntityWither var1) {
      int var2 = var1.func_82212_n();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? field_110913_a : field_110912_f;
   }

   protected void func_77041_b(EntityWither var1, float var2) {
      float var3 = 2.0F;
      int var4 = var1.func_82212_n();
      if (var4 > 0) {
         var3 -= ((float)var4 - var2) / 220.0F * 0.5F;
      }

      GlStateManager.func_179152_a(var3, var3, var3);
   }
}
