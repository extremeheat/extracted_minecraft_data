package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeRenderer extends MobRenderer<Breeze, BreezeModel<Breeze>> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze.png");
   private static final ResourceLocation WIND_TEXTURE_LOCATION = new ResourceLocation("textures/entity/breeze/breeze_wind.png");

   public BreezeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BreezeModel<>(var1.bakeLayer(ModelLayers.BREEZE)), 0.8F);
      this.addLayer(new BreezeWindLayer(this, var1.getModelSet(), WIND_TEXTURE_LOCATION));
      this.addLayer(new BreezeEyesLayer(this, var1.getModelSet(), TEXTURE_LOCATION));
   }

   public ResourceLocation getTextureLocation(Breeze var1) {
      return TEXTURE_LOCATION;
   }
}
