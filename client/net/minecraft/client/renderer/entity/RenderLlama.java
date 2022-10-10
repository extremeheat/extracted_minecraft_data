package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerLlamaDecor;
import net.minecraft.client.renderer.entity.model.ModelLlama;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.ResourceLocation;

public class RenderLlama extends RenderLiving<EntityLlama> {
   private static final ResourceLocation[] field_191350_a = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/creamy.png"), new ResourceLocation("textures/entity/llama/white.png"), new ResourceLocation("textures/entity/llama/brown.png"), new ResourceLocation("textures/entity/llama/gray.png")};

   public RenderLlama(RenderManager var1) {
      super(var1, new ModelLlama(0.0F), 0.7F);
      this.func_177094_a(new LayerLlamaDecor(this));
   }

   protected ResourceLocation func_110775_a(EntityLlama var1) {
      return field_191350_a[var1.func_190719_dM()];
   }
}
