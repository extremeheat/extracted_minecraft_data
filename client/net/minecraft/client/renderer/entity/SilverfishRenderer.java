package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Silverfish;

public class SilverfishRenderer extends MobRenderer<Silverfish, LivingEntityRenderState, SilverfishModel> {
   private static final ResourceLocation SILVERFISH_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SilverfishModel(var1.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
   }

   @Override
   protected float getFlipDegrees() {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return SILVERFISH_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }
}
