package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinRenderer extends MobRenderer<Dolphin, DolphinModel<Dolphin>> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRenderDispatcher var1) {
      super(var1, new DolphinModel(), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this));
   }

   public ResourceLocation getTextureLocation(Dolphin var1) {
      return DOLPHIN_LOCATION;
   }
}
