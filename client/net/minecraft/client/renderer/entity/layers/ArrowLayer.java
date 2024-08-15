package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ArrowModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

public class ArrowLayer<M extends PlayerModel> extends StuckInBodyLayer<M> {
   public ArrowLayer(LivingEntityRenderer<?, PlayerRenderState, M> var1, EntityRendererProvider.Context var2) {
      super(var1, new ArrowModel(var2.bakeLayer(ModelLayers.ARROW)), TippableArrowRenderer.NORMAL_ARROW_LOCATION, StuckInBodyLayer.PlacementStyle.IN_CUBE);
   }

   @Override
   protected int numStuck(PlayerRenderState var1) {
      return var1.arrowCount;
   }
}
