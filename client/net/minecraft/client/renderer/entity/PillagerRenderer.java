package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

public class PillagerRenderer extends IllagerRenderer<Pillager> {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRenderDispatcher var1) {
      super(var1, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new ItemInHandLayer(this));
   }

   public ResourceLocation getTextureLocation(Pillager var1) {
      return PILLAGER;
   }
}
