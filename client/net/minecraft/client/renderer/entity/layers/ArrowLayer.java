package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.ArrowModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class ArrowLayer<M extends PlayerModel> extends StuckInBodyLayer<M, ArrowRenderState> {
   public ArrowLayer(LivingEntityRenderer<?, AvatarRenderState, M> var1, EntityRendererProvider.Context var2) {
      super(var1, new ArrowModel(var2.bakeLayer(ModelLayers.ARROW)), new ArrowRenderState(), TippableArrowRenderer.NORMAL_ARROW_LOCATION, StuckInBodyLayer.PlacementStyle.IN_CUBE);
   }

   protected int numStuck(AvatarRenderState var1) {
      return var1.arrowCount;
   }
}
