package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.camel.Camel;

public class CamelRenderer extends AgeableMobRenderer<Camel, CamelRenderState, CamelModel> {
   private static final Identifier CAMEL_LOCATION = Identifier.withDefaultNamespace("textures/entity/camel/camel.png");

   public CamelRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CamelModel(var1.bakeLayer(ModelLayers.CAMEL)), new CamelModel(var1.bakeLayer(ModelLayers.CAMEL_BABY)), 0.7F);
      this.addLayer(this.createCamelSaddleLayer(var1));
   }

   protected SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(EntityRendererProvider.Context var1) {
      return new SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel>(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.CAMEL_SADDLE, (var0) -> var0.saddle, new CamelSaddleModel(var1.bakeLayer(ModelLayers.CAMEL_SADDLE)), new CamelSaddleModel(var1.bakeLayer(ModelLayers.CAMEL_BABY_SADDLE)));
   }

   public Identifier getTextureLocation(CamelRenderState var1) {
      return CAMEL_LOCATION;
   }

   public CamelRenderState createRenderState() {
      return new CamelRenderState();
   }

   public void extractRenderState(Camel var1, CamelRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.saddle = var1.getItemBySlot(EquipmentSlot.SADDLE).copy();
      var2.isRidden = var1.isVehicle();
      var2.jumpCooldown = Math.max((float)var1.getJumpCooldown() - var3, 0.0F);
      var2.sitAnimationState.copyFrom(var1.sitAnimationState);
      var2.sitPoseAnimationState.copyFrom(var1.sitPoseAnimationState);
      var2.sitUpAnimationState.copyFrom(var1.sitUpAnimationState);
      var2.idleAnimationState.copyFrom(var1.idleAnimationState);
      var2.dashAnimationState.copyFrom(var1.dashAnimationState);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CamelRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
