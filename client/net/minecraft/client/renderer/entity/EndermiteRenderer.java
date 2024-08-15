package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Endermite;

public class EndermiteRenderer extends MobRenderer<Endermite, LivingEntityRenderState, EndermiteModel> {
   private static final ResourceLocation ENDERMITE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRendererProvider.Context var1) {
      super(var1, new EndermiteModel(var1.bakeLayer(ModelLayers.ENDERMITE)), 0.3F);
   }

   @Override
   protected float getFlipDegrees() {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return ENDERMITE_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }
}
