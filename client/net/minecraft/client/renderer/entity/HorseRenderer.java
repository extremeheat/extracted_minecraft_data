package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.equine.EquineSaddleModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Variant;

public final class HorseRenderer extends AbstractHorseRenderer<Horse, HorseRenderState, HorseModel> {
   private static final Map<Variant, HorseTextures> LOCATION_BY_VARIANT;

   public HorseRenderer(final EntityRendererProvider.Context context) {
      super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new HorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
      this.addLayer(new HorseMarkingLayer(this));
      this.addLayer(new SimpleEquipmentLayer(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_BODY, (state) -> state.bodyArmorItem, new HorseModel(context.bakeLayer(ModelLayers.HORSE_ARMOR)), (EntityModel)null, 2));
      this.addLayer(new SimpleEquipmentLayer(this, context.getEquipmentRenderer(), EquipmentClientInfo.LayerType.HORSE_SADDLE, (state) -> state.saddle, new EquineSaddleModel(context.bakeLayer(ModelLayers.HORSE_SADDLE)), (EntityModel)null, 2));
   }

   public Identifier getTextureLocation(final HorseRenderState state) {
      HorseTextures variant = (HorseTextures)LOCATION_BY_VARIANT.get(state.variant);
      return state.isBaby ? variant.baby : variant.adult;
   }

   public HorseRenderState createRenderState() {
      return new HorseRenderState();
   }

   public void extractRenderState(final Horse entity, final HorseRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.variant = entity.getVariant();
      state.markings = entity.getMarkings();
      state.bodyArmorItem = entity.getBodyArmorItem().copy();
   }

   static {
      LOCATION_BY_VARIANT = Maps.newEnumMap(Map.of(Variant.WHITE, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_white.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_white_baby.png")), Variant.CREAMY, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_creamy.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_creamy_baby.png")), Variant.CHESTNUT, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_chestnut.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_chestnut_baby.png")), Variant.BROWN, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_brown.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_brown_baby.png")), Variant.BLACK, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_black.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_black_baby.png")), Variant.GRAY, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_gray.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_gray_baby.png")), Variant.DARK_BROWN, new HorseTextures(Identifier.withDefaultNamespace("textures/entity/horse/horse_darkbrown.png"), Identifier.withDefaultNamespace("textures/entity/horse/horse_darkbrown_baby.png"))));
   }

   private static record HorseTextures(Identifier adult, Identifier baby) {
      private HorseTextures {
         super();
      }
   }
}
