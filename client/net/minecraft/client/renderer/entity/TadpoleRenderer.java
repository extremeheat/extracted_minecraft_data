package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.frog.TadpoleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.frog.Tadpole;

public class TadpoleRenderer extends MobRenderer<Tadpole, LivingEntityRenderState, TadpoleModel> {
   private static final Identifier TADPOLE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/tadpole/tadpole.png");

   public TadpoleRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TadpoleModel(var1.bakeLayer(ModelLayers.TADPOLE)), 0.14F);
   }

   public Identifier getTextureLocation(LivingEntityRenderState var1) {
      return TADPOLE_TEXTURE;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
