package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.animal.bee.BeeStingerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class BeeStingerLayer<M extends PlayerModel> extends StuckInBodyLayer<M, Unit> {
   private static final Identifier BEE_STINGER_LOCATION = Identifier.withDefaultNamespace("textures/entity/bee/bee_stinger.png");

   public BeeStingerLayer(LivingEntityRenderer<?, AvatarRenderState, M> var1, EntityRendererProvider.Context var2) {
      super(var1, new BeeStingerModel(var2.bakeLayer(ModelLayers.BEE_STINGER)), Unit.INSTANCE, BEE_STINGER_LOCATION, StuckInBodyLayer.PlacementStyle.ON_SURFACE);
   }

   protected int numStuck(AvatarRenderState var1) {
      return var1.stingerCount;
   }
}
