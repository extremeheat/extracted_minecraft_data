package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBat;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderBat extends RenderLiving<EntityBat> {
   private static final ResourceLocation field_110835_a = new ResourceLocation("textures/entity/bat.png");

   public RenderBat(RenderManager var1) {
      super(var1, new ModelBat(), 0.25F);
   }

   protected ResourceLocation func_110775_a(EntityBat var1) {
      return field_110835_a;
   }

   protected void func_77041_b(EntityBat var1, float var2) {
      GlStateManager.func_179152_a(0.35F, 0.35F, 0.35F);
   }

   protected void func_77043_a(EntityBat var1, float var2, float var3, float var4) {
      if (var1.func_82235_h()) {
         GlStateManager.func_179109_b(0.0F, -0.1F, 0.0F);
      } else {
         GlStateManager.func_179109_b(0.0F, MathHelper.func_76134_b(var2 * 0.3F) * 0.1F, 0.0F);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
