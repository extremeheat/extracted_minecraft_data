package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.ColdPigModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;

public class PigRenderer extends MobRenderer<Pig, PigRenderState, PigModel> {
   private final Map<PigVariant.ModelType, AdultAndBabyModelPair<PigModel>> models;

   public PigRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PigModel(var1.bakeLayer(ModelLayers.PIG)), 0.7F);
      this.models = bakeModels(var1);
      this.addLayer(new SimpleEquipmentLayer(this, var1.getEquipmentRenderer(), EquipmentClientInfo.LayerType.PIG_SADDLE, (var0) -> var0.saddle, new PigModel(var1.bakeLayer(ModelLayers.PIG_SADDLE)), new PigModel(var1.bakeLayer(ModelLayers.PIG_BABY_SADDLE))));
   }

   private static Map<PigVariant.ModelType, AdultAndBabyModelPair<PigModel>> bakeModels(EntityRendererProvider.Context var0) {
      return Maps.newEnumMap(Map.of(PigVariant.ModelType.NORMAL, new AdultAndBabyModelPair(new PigModel(var0.bakeLayer(ModelLayers.PIG)), new PigModel(var0.bakeLayer(ModelLayers.PIG_BABY))), PigVariant.ModelType.COLD, new AdultAndBabyModelPair(new ColdPigModel(var0.bakeLayer(ModelLayers.COLD_PIG)), new ColdPigModel(var0.bakeLayer(ModelLayers.COLD_PIG_BABY)))));
   }

   public void submit(PigRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      if (var1.variant != null) {
         this.model = (EntityModel)((AdultAndBabyModelPair)this.models.get(var1.variant.modelAndTexture().model())).getModel(var1.isBaby);
         super.submit(var1, var2, var3);
      }
   }

   public ResourceLocation getTextureLocation(PigRenderState var1) {
      return var1.variant == null ? MissingTextureAtlasSprite.getLocation() : var1.variant.modelAndTexture().asset().texturePath();
   }

   public PigRenderState createRenderState() {
      return new PigRenderState();
   }

   public void extractRenderState(Pig var1, PigRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.saddle = var1.getItemBySlot(EquipmentSlot.SADDLE).copy();
      var2.variant = (PigVariant)var1.getVariant().value();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((PigRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
