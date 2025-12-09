package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;

public class CreeperPowerLayer extends EnergySwirlLayer<CreeperRenderState, CreeperModel> {
   private static final Identifier POWER_LOCATION = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel model;

   public CreeperPowerLayer(RenderLayerParent<CreeperRenderState, CreeperModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new CreeperModel(var2.bakeLayer(ModelLayers.CREEPER_ARMOR));
   }

   protected boolean isPowered(CreeperRenderState var1) {
      return var1.isPowered;
   }

   protected float xOffset(float var1) {
      return var1 * 0.01F;
   }

   protected Identifier getTextureLocation() {
      return POWER_LOCATION;
   }

   protected CreeperModel model() {
      return this.model;
   }

   // $FF: synthetic method
   protected EntityModel model() {
      return this.model();
   }
}
