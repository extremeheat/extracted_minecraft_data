package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.sniffer.Sniffer;

public class SnifferRenderer extends MobRenderer<Sniffer, SnifferModel<Sniffer>> {
   private static final ResourceLocation SNIFFER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sniffer/sniffer.png");

   public SnifferRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SnifferModel(var1.bakeLayer(ModelLayers.SNIFFER)), 1.1F);
   }

   public ResourceLocation getTextureLocation(Sniffer var1) {
      return SNIFFER_LOCATION;
   }
}
