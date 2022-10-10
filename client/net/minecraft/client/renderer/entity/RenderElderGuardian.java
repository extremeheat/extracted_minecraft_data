package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;

public class RenderElderGuardian extends RenderGuardian {
   private static final ResourceLocation field_177116_j = new ResourceLocation("textures/entity/guardian_elder.png");

   public RenderElderGuardian(RenderManager var1) {
      super(var1);
   }

   protected void func_77041_b(EntityGuardian var1, float var2) {
      GlStateManager.func_179152_a(2.35F, 2.35F, 2.35F);
   }

   protected ResourceLocation func_110775_a(EntityGuardian var1) {
      return field_177116_j;
   }
}
