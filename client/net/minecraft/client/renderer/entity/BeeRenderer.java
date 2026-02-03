package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.bee.Bee;

public class BeeRenderer extends AgeableMobRenderer<Bee, BeeRenderState, BeeModel> {
   private static final Identifier ANGRY_BEE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/bee/bee_angry.png");
   private static final Identifier ANGRY_NECTAR_BEE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/bee/bee_angry_nectar.png");
   private static final Identifier BEE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/bee/bee.png");
   private static final Identifier NECTAR_BEE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/bee/bee_nectar.png");

   public BeeRenderer(final EntityRendererProvider.Context context) {
      super(context, new BeeModel(context.bakeLayer(ModelLayers.BEE)), new BeeModel(context.bakeLayer(ModelLayers.BEE_BABY)), 0.4F);
   }

   public Identifier getTextureLocation(final BeeRenderState state) {
      if (state.isAngry) {
         return state.hasNectar ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
      } else {
         return state.hasNectar ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
      }
   }

   public BeeRenderState createRenderState() {
      return new BeeRenderState();
   }

   public void extractRenderState(final Bee entity, final BeeRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.rollAmount = entity.getRollAmount(partialTicks);
      state.hasStinger = !entity.hasStung();
      state.isOnGround = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7;
      state.isAngry = entity.isAngry();
      state.hasNectar = entity.hasNectar();
   }
}
