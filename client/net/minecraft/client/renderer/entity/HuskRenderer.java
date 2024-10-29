package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.HUSK, ModelLayers.HUSK_BABY, ModelLayers.HUSK_INNER_ARMOR, ModelLayers.HUSK_OUTER_ARMOR, ModelLayers.HUSK_BABY_INNER_ARMOR, ModelLayers.HUSK_BABY_OUTER_ARMOR);
   }

   public ResourceLocation getTextureLocation(ZombieRenderState var1) {
      return HUSK_LOCATION;
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombieRenderState)var1);
   }
}
