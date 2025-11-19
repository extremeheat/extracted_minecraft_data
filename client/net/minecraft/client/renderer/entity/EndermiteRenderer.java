package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.endermite.EndermiteModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Endermite;

public class EndermiteRenderer extends MobRenderer<Endermite, LivingEntityRenderState, EndermiteModel> {
   private static final Identifier ENDERMITE_LOCATION = Identifier.withDefaultNamespace("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRendererProvider.Context var1) {
      super(var1, new EndermiteModel(var1.bakeLayer(ModelLayers.ENDERMITE)), 0.3F);
   }

   protected float getFlipDegrees() {
      return 180.0F;
   }

   public Identifier getTextureLocation(LivingEntityRenderState var1) {
      return ENDERMITE_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
