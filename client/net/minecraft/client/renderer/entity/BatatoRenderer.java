package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BatatoModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ambient.Batato;

public class BatatoRenderer extends MobRenderer<Batato, BatatoModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/batato.png");

   public BatatoRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BatatoModel(var1.bakeLayer(ModelLayers.BATATO)), 0.25F);
   }

   public ResourceLocation getTextureLocation(Batato var1) {
      return TEXTURE_LOCATION;
   }
}
