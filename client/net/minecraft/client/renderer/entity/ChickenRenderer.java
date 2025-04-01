package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.ColdChickenModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.ChickenVariant;
import net.minecraft.world.level.Level;

public class ChickenRenderer extends MobRenderer<Chicken, ChickenRenderState, ChickenModel> {
   private final Map<ChickenVariant.ModelType, AdultAndBabyModelPair<ChickenModel>> models;

   public ChickenRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ChickenModel(var1.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
      this.models = bakeModels(var1);
   }

   private static Map<ChickenVariant.ModelType, AdultAndBabyModelPair<ChickenModel>> bakeModels(EntityRendererProvider.Context var0) {
      return Maps.newEnumMap(Map.of(ChickenVariant.ModelType.NORMAL, new AdultAndBabyModelPair(new ChickenModel(var0.bakeLayer(ModelLayers.CHICKEN)), new ChickenModel(var0.bakeLayer(ModelLayers.CHICKEN_BABY))), ChickenVariant.ModelType.COLD, new AdultAndBabyModelPair(new ColdChickenModel(var0.bakeLayer(ModelLayers.COLD_CHICKEN)), new ColdChickenModel(var0.bakeLayer(ModelLayers.COLD_CHICKEN_BABY)))));
   }

   public void render(ChickenRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (var1.variant != null) {
         this.model = (EntityModel)((AdultAndBabyModelPair)this.models.get(var1.variant.modelAndTexture().model())).getModel(var1.isBaby);
         super.render(var1, var2, var3, var4);
      }
   }

   public ResourceLocation getTextureLocation(ChickenRenderState var1) {
      return var1.variant == null ? MissingTextureAtlasSprite.getLocation() : var1.variant.modelAndTexture().asset().texturePath();
   }

   public ChickenRenderState createRenderState() {
      return new ChickenRenderState();
   }

   public ChickenRenderState createSpecialRenderStateBecauseImLazyMojangDevAndItsTimeToHack(@Nullable Level var1) {
      ChickenRenderState var2 = (ChickenRenderState)super.createSpecialRenderStateBecauseImLazyMojangDevAndItsTimeToHack(var1);
      var2.variant = var1 != null ? (ChickenVariant)var1.registryAccess().lookupOrThrow(Registries.CHICKEN_VARIANT).getAny().map(Holder::value).orElse((Object)null) : null;
      return var2;
   }

   public void extractRenderState(Chicken var1, ChickenRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.flap = Mth.lerp(var3, var1.oFlap, var1.flap);
      var2.flapSpeed = Mth.lerp(var3, var1.oFlapSpeed, var1.flapSpeed);
      var2.variant = (ChickenVariant)var1.getVariant().value();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ChickenRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createSpecialRenderStateBecauseImLazyMojangDevAndItsTimeToHack(@Nullable final Level var1) {
      return this.createSpecialRenderStateBecauseImLazyMojangDevAndItsTimeToHack(var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
