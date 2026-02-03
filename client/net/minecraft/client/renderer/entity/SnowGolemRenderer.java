package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.SnowGolem;

public class SnowGolemRenderer extends MobRenderer<SnowGolem, SnowGolemRenderState, SnowGolemModel> {
   private static final Identifier SNOW_GOLEM_LOCATION = Identifier.withDefaultNamespace("textures/entity/snow_golem/snow_golem.png");

   public SnowGolemRenderer(final EntityRendererProvider.Context context) {
      super(context, new SnowGolemModel(context.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this, context.getBlockRenderDispatcher()));
   }

   public Identifier getTextureLocation(final SnowGolemRenderState state) {
      return SNOW_GOLEM_LOCATION;
   }

   public SnowGolemRenderState createRenderState() {
      return new SnowGolemRenderState();
   }

   public void extractRenderState(final SnowGolem entity, final SnowGolemRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.hasPumpkin = entity.hasPumpkin();
   }
}
