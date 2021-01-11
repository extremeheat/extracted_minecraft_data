package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;

public class RenderMagmaCube extends RenderLiving<EntityMagmaCube> {
   private static final ResourceLocation field_110873_a = new ResourceLocation("textures/entity/slime/magmacube.png");

   public RenderMagmaCube(RenderManager var1) {
      super(var1, new ModelMagmaCube(), 0.25F);
   }

   protected ResourceLocation func_110775_a(EntityMagmaCube var1) {
      return field_110873_a;
   }

   protected void func_77041_b(EntityMagmaCube var1, float var2) {
      int var3 = var1.func_70809_q();
      float var4 = (var1.field_70812_c + (var1.field_70811_b - var1.field_70812_c) * var2) / ((float)var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      float var6 = (float)var3;
      GlStateManager.func_179152_a(var5 * var6, 1.0F / var5 * var6, var5 * var6);
   }
}
