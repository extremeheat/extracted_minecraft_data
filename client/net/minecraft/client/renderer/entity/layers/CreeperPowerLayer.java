package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperPowerLayer extends EnergySwirlLayer<Creeper, CreeperModel<Creeper>> {
   private static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel<Creeper> model;

   public CreeperPowerLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new CreeperModel<>(var2.bakeLayer(ModelLayers.CREEPER_ARMOR));
   }

   @Override
   protected float xOffset(float var1) {
      return var1 * 0.01F;
   }

   @Override
   protected ResourceLocation getTextureLocation() {
      return POWER_LOCATION;
   }

   @Override
   protected EntityModel<Creeper> model() {
      return this.model;
   }
}
