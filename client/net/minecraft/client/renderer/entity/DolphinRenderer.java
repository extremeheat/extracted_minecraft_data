package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;

public class DolphinRenderer extends MobRenderer<Dolphin, DolphinModel<Dolphin>> {
   private static final ResourceLocation DOLPHIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRendererProvider.Context var1) {
      super(var1, new DolphinModel(var1.bakeLayer(ModelLayers.DOLPHIN)), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this, var1.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(Dolphin var1) {
      return DOLPHIN_LOCATION;
   }
}
