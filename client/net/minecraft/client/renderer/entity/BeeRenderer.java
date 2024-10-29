package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public class BeeRenderer extends AgeableMobRenderer<Bee, BeeRenderState, BeeModel> {
   private static final ResourceLocation ANGRY_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee.png");
   private static final ResourceLocation NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_nectar.png");

   public BeeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BeeModel(var1.bakeLayer(ModelLayers.BEE)), new BeeModel(var1.bakeLayer(ModelLayers.BEE_BABY)), 0.4F);
   }

   public ResourceLocation getTextureLocation(BeeRenderState var1) {
      if (var1.isAngry) {
         return var1.hasNectar ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
      } else {
         return var1.hasNectar ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
      }
   }

   public BeeRenderState createRenderState() {
      return new BeeRenderState();
   }

   public void extractRenderState(Bee var1, BeeRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.rollAmount = var1.getRollAmount(var3);
      var2.hasStinger = !var1.hasStung();
      var2.isOnGround = var1.onGround() && var1.getDeltaMovement().lengthSqr() < 1.0E-7;
      var2.isAngry = var1.isAngry();
      var2.hasNectar = var1.hasNectar();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((BeeRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
