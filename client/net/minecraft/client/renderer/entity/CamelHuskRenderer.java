package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.camel.CamelSaddleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;

public class CamelHuskRenderer extends CamelRenderer {
   private static final Identifier CAMEL_HUSK_LOCATION = Identifier.withDefaultNamespace("textures/entity/camel/camel_husk.png");

   public CamelHuskRenderer(final EntityRendererProvider.Context context) {
      super(context);
   }

   protected SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel> createCamelSaddleLayer(final EntityRendererProvider.Context context) {
      return new SimpleEquipmentLayer<CamelRenderState, CamelModel, CamelSaddleModel>(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.CAMEL_HUSK_SADDLE, (state) -> state.saddle, new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_HUSK_SADDLE)), new CamelSaddleModel(context.bakeLayer(ModelLayers.CAMEL_HUSK_BABY_SADDLE)));
   }

   public Identifier getTextureLocation(final CamelRenderState state) {
      return CAMEL_HUSK_LOCATION;
   }
}
