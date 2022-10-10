package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelGhast;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;

public class RenderGhast extends RenderLiving<EntityGhast> {
   private static final ResourceLocation field_110869_a = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation field_110868_f = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public RenderGhast(RenderManager var1) {
      super(var1, new ModelGhast(), 0.5F);
   }

   protected ResourceLocation func_110775_a(EntityGhast var1) {
      return var1.func_110182_bF() ? field_110868_f : field_110869_a;
   }

   protected void func_77041_b(EntityGhast var1, float var2) {
      float var3 = 1.0F;
      float var4 = 4.5F;
      float var5 = 4.5F;
      GlStateManager.func_179152_a(4.5F, 4.5F, 4.5F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
