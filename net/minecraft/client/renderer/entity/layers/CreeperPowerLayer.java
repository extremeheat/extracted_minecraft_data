package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class CreeperPowerLayer extends EnergySwirlLayer {
   private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel model = new CreeperModel(2.0F);

   public CreeperPowerLayer(RenderLayerParent var1) {
      super(var1);
   }

   protected float xOffset(float var1) {
      return var1 * 0.01F;
   }

   protected ResourceLocation getTextureLocation() {
      return POWER_LOCATION;
   }

   protected EntityModel model() {
      return this.model;
   }
}
