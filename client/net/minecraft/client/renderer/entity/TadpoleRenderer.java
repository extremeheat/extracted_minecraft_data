package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Tadpole;

public class TadpoleRenderer extends MobRenderer<Tadpole, LivingEntityRenderState, TadpoleModel> {
   private static final ResourceLocation TADPOLE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/tadpole/tadpole.png");

   public TadpoleRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TadpoleModel(var1.bakeLayer(ModelLayers.TADPOLE)), 0.14F);
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return TADPOLE_TEXTURE;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }
}
