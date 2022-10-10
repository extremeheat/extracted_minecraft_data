package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPolarBear;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.util.ResourceLocation;

public class RenderPolarBear extends RenderLiving<EntityPolarBear> {
   private static final ResourceLocation field_190090_a = new ResourceLocation("textures/entity/bear/polarbear.png");

   public RenderPolarBear(RenderManager var1) {
      super(var1, new ModelPolarBear(), 0.7F);
   }

   protected ResourceLocation func_110775_a(EntityPolarBear var1) {
      return field_190090_a;
   }

   public void func_76986_a(EntityPolarBear var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected void func_77041_b(EntityPolarBear var1, float var2) {
      GlStateManager.func_179152_a(1.2F, 1.2F, 1.2F);
      super.func_77041_b(var1, var2);
   }
}
