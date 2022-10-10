package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelVex;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.ResourceLocation;

public class RenderVex extends RenderBiped<EntityVex> {
   private static final ResourceLocation field_191343_a = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation field_191344_j = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public RenderVex(RenderManager var1) {
      super(var1, new ModelVex(), 0.3F);
   }

   protected ResourceLocation func_110775_a(EntityVex var1) {
      return var1.func_190647_dj() ? field_191344_j : field_191343_a;
   }

   protected void func_77041_b(EntityVex var1, float var2) {
      GlStateManager.func_179152_a(0.4F, 0.4F, 0.4F);
   }
}
