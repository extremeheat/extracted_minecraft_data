package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.CowVariant;

public class CowRenderer extends MobRenderer<Cow, CowRenderState, CowModel> {
   private final Map<CowVariant.ModelType, AdultAndBabyModelPair<CowModel>> models;

   public CowRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CowModel(var1.bakeLayer(ModelLayers.COW)), 0.7F);
      this.models = bakeModels(var1);
   }

   private static Map<CowVariant.ModelType, AdultAndBabyModelPair<CowModel>> bakeModels(EntityRendererProvider.Context var0) {
      return Maps.newEnumMap(Map.of(CowVariant.ModelType.NORMAL, new AdultAndBabyModelPair(new CowModel(var0.bakeLayer(ModelLayers.COW)), new CowModel(var0.bakeLayer(ModelLayers.COW_BABY))), CowVariant.ModelType.WARM, new AdultAndBabyModelPair(new CowModel(var0.bakeLayer(ModelLayers.WARM_COW)), new CowModel(var0.bakeLayer(ModelLayers.WARM_COW_BABY))), CowVariant.ModelType.COLD, new AdultAndBabyModelPair(new CowModel(var0.bakeLayer(ModelLayers.COLD_COW)), new CowModel(var0.bakeLayer(ModelLayers.COLD_COW_BABY)))));
   }

   public ResourceLocation getTextureLocation(CowRenderState var1) {
      return var1.variant == null ? MissingTextureAtlasSprite.getLocation() : var1.variant.modelAndTexture().asset().texturePath();
   }

   public CowRenderState createRenderState() {
      return new CowRenderState();
   }

   public void extractRenderState(Cow var1, CowRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = (CowVariant)var1.getVariant().value();
   }

   public void render(CowRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (var1.variant != null) {
         this.model = (EntityModel)((AdultAndBabyModelPair)this.models.get(var1.variant.modelAndTexture().model())).getModel(var1.isBaby);
         super.render(var1, var2, var3, var4);
      }
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CowRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
