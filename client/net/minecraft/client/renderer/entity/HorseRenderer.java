package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public final class HorseRenderer extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
   private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = (Map)Util.make(Maps.newEnumMap(Variant.class), (var0) -> {
      var0.put(Variant.WHITE, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_white.png"));
      var0.put(Variant.CREAMY, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_creamy.png"));
      var0.put(Variant.CHESTNUT, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_chestnut.png"));
      var0.put(Variant.BROWN, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_brown.png"));
      var0.put(Variant.BLACK, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_black.png"));
      var0.put(Variant.GRAY, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_gray.png"));
      var0.put(Variant.DARK_BROWN, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_darkbrown.png"));
   });

   public HorseRenderer(EntityRendererProvider.Context var1) {
      super(var1, new HorseModel(var1.bakeLayer(ModelLayers.HORSE)), 1.1F);
      this.addLayer(new HorseMarkingLayer(this));
      this.addLayer(new HorseArmorLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Horse var1) {
      return (ResourceLocation)LOCATION_BY_VARIANT.get(var1.getVariant());
   }
}
