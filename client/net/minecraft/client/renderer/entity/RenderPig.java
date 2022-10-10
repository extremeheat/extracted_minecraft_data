package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSaddle;
import net.minecraft.client.renderer.entity.model.ModelPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class RenderPig extends RenderLiving<EntityPig> {
   private static final ResourceLocation field_110887_f = new ResourceLocation("textures/entity/pig/pig.png");

   public RenderPig(RenderManager var1) {
      super(var1, new ModelPig(), 0.7F);
      this.func_177094_a(new LayerSaddle(this));
   }

   protected ResourceLocation func_110775_a(EntityPig var1) {
      return field_110887_f;
   }
}
