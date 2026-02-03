package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;

public class HuskRenderer extends ZombieRenderer {
   private static final Identifier HUSK_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/husk.png");

   public HuskRenderer(final EntityRendererProvider.Context context) {
      super(context, ModelLayers.HUSK, ModelLayers.HUSK_BABY, ModelLayers.HUSK_ARMOR, ModelLayers.HUSK_BABY_ARMOR);
   }

   public Identifier getTextureLocation(final ZombieRenderState state) {
      return HUSK_LOCATION;
   }
}
