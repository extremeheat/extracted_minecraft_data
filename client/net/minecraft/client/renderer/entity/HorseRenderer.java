package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.EquineSaddleModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public final class HorseRenderer extends AbstractHorseRenderer<Horse, HorseRenderState, HorseModel> {
   private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT;

   public HorseRenderer(EntityRendererProvider.Context var1) {
      super(var1, new HorseModel(var1.bakeLayer(ModelLayers.HORSE)), new HorseModel(var1.bakeLayer(ModelLayers.HORSE_BABY)));
      this.addLayer(new HorseMarkingLayer(this));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_BODY, (var0) -> var0.bodyArmorItem, new HorseModel(var1.bakeLayer(ModelLayers.HORSE_ARMOR)), new HorseModel(var1.bakeLayer(ModelLayers.HORSE_BABY_ARMOR)), 2));
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_SADDLE, (var0) -> var0.saddle, new EquineSaddleModel(var1.bakeLayer(ModelLayers.HORSE_SADDLE)), new EquineSaddleModel(var1.bakeLayer(ModelLayers.HORSE_BABY_SADDLE)), 2));
   }

   public ResourceLocation getTextureLocation(HorseRenderState var1) {
      return (ResourceLocation)LOCATION_BY_VARIANT.get(var1.variant);
   }

   public HorseRenderState createRenderState() {
      return new HorseRenderState();
   }

   public void extractRenderState(Horse var1, HorseRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      var2.markings = var1.getMarkings();
      var2.bodyArmorItem = var1.getBodyArmorItem().copy();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((HorseRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      LOCATION_BY_VARIANT = Maps.newEnumMap(Map.of(Variant.WHITE, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_white.png"), Variant.CREAMY, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_creamy.png"), Variant.CHESTNUT, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_chestnut.png"), Variant.BROWN, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_brown.png"), Variant.BLACK, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_black.png"), Variant.GRAY, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_gray.png"), Variant.DARK_BROWN, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_darkbrown.png")));
   }
}
