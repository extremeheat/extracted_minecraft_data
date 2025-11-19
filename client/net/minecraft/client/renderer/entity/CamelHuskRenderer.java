package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;

public class CamelHuskRenderer extends CamelRenderer {
   private static final Identifier CAMEL_HUSK_LOCATION = Identifier.withDefaultNamespace("textures/entity/camel/camel_husk.png");

   public CamelHuskRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   protected SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(EntityRendererProvider.Context var1) {
      return new SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel>(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.CAMEL_HUSK_SADDLE, (var0) -> var0.saddle, new CamelSaddleModel(var1.bakeLayer(ModelLayers.CAMEL_HUSK_SADDLE)), new CamelSaddleModel(var1.bakeLayer(ModelLayers.CAMEL_HUSK_BABY_SADDLE)));
   }

   public Identifier getTextureLocation(CamelRenderState var1) {
      return CAMEL_HUSK_LOCATION;
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CamelRenderState)var1);
   }
}
