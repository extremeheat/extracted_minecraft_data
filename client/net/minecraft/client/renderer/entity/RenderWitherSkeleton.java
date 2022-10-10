package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderWitherSkeleton extends RenderSkeleton {
   private static final ResourceLocation field_110861_l = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderWitherSkeleton(RenderManager var1) {
      super(var1);
   }

   protected ResourceLocation func_110775_a(AbstractSkeleton var1) {
      return field_110861_l;
   }

   protected void func_77041_b(AbstractSkeleton var1, float var2) {
      GlStateManager.func_179152_a(1.2F, 1.2F, 1.2F);
   }
}
