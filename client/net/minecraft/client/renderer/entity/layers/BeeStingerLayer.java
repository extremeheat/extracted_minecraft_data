package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.BeeStingerModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;

public class BeeStingerLayer<M extends PlayerModel> extends StuckInBodyLayer<M> {
   private static final ResourceLocation BEE_STINGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_stinger.png");

   public BeeStingerLayer(LivingEntityRenderer<?, PlayerRenderState, M> var1, EntityRendererProvider.Context var2) {
      super(var1, new BeeStingerModel(var2.bakeLayer(ModelLayers.BEE_STINGER)), BEE_STINGER_LOCATION, StuckInBodyLayer.PlacementStyle.ON_SURFACE);
   }

   @Override
   protected int numStuck(PlayerRenderState var1) {
      return var1.stingerCount;
   }
}
