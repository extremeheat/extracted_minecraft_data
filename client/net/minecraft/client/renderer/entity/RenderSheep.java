package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.client.renderer.entity.model.ModelSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class RenderSheep extends RenderLiving<EntitySheep> {
   private static final ResourceLocation field_110884_f = new ResourceLocation("textures/entity/sheep/sheep.png");

   public RenderSheep(RenderManager var1) {
      super(var1, new ModelSheep(), 0.7F);
      this.func_177094_a(new LayerSheepWool(this));
   }

   protected ResourceLocation func_110775_a(EntitySheep var1) {
      return field_110884_f;
   }
}
