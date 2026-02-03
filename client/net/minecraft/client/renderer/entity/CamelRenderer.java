package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer extends AgeableMobRenderer<Camel, CamelRenderState, CamelModel> {
   private static final Identifier CAMEL_LOCATION = Identifier.withDefaultNamespace("textures/entity/camel/camel.png");

   public CamelRenderer(final EntityRendererProvider.Context context) {
      super(context, new CamelModel(context.bakeLayer(ModelLayers.CAMEL)), new CamelModel(context.bakeLayer(ModelLayers.CAMEL_BABY)), 0.7F);
      this.addLayer(this.createCamelSaddleLayer(context));
   }

   protected SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(final EntityRendererProvider.Context context) {
      return new SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel>(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.CAMEL_SADDLE, (state) -> state.saddle, new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_SADDLE)), new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_BABY_SADDLE)));
   }

   public Identifier getTextureLocation(final CamelRenderState state) {
      return CAMEL_LOCATION;
   }

   public CamelRenderState createRenderState() {
      return new CamelRenderState();
   }

   public void extractRenderState(final Camel entity, final CamelRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.saddle = entity.getItemBySlot(EquipmentSlot.SADDLE).copy();
      state.isRidden = entity.isVehicle();
      state.jumpCooldown = Math.max((float)entity.getJumpCooldown() - partialTicks, 0.0F);
      state.sitAnimationState.copyFrom(entity.sitAnimationState);
      state.sitPoseAnimationState.copyFrom(entity.sitPoseAnimationState);
      state.sitUpAnimationState.copyFrom(entity.sitUpAnimationState);
      state.idleAnimationState.copyFrom(entity.idleAnimationState);
      state.dashAnimationState.copyFrom(entity.dashAnimationState);
   }
}
