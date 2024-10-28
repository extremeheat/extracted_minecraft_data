package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenRenderer extends MobRenderer<Warden, WardenModel<Warden>> {
   private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden.png");
   private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_bioluminescent_layer.png");
   private static final ResourceLocation HEART_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_heart.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_1.png");
   private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = ResourceLocation.withDefaultNamespace("textures/entity/warden/warden_pulsating_spots_2.png");

   public WardenRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WardenModel(var1.bakeLayer(ModelLayers.WARDEN)), 0.9F);
      this.addLayer(new WardenEmissiveLayer(this, BIOLUMINESCENT_LAYER_TEXTURE, (var0, var1x, var2) -> {
         return 1.0F;
      }, WardenModel::getBioluminescentLayerModelParts));
      this.addLayer(new WardenEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_1, (var0, var1x, var2) -> {
         return Math.max(0.0F, Mth.cos(var2 * 0.045F) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_2, (var0, var1x, var2) -> {
         return Math.max(0.0F, Mth.cos(var2 * 0.045F + 3.1415927F) * 0.25F);
      }, WardenModel::getPulsatingSpotsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer(this, TEXTURE, (var0, var1x, var2) -> {
         return var0.getTendrilAnimation(var1x);
      }, WardenModel::getTendrilsLayerModelParts));
      this.addLayer(new WardenEmissiveLayer(this, HEART_TEXTURE, (var0, var1x, var2) -> {
         return var0.getHeartAnimation(var1x);
      }, WardenModel::getHeartLayerModelParts));
   }

   public ResourceLocation getTextureLocation(Warden var1) {
      return TEXTURE;
   }
}
