package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer extends MobRenderer<TropicalFish, TropicalFishRenderState, EntityModel<TropicalFishRenderState>> {
   private final EntityModel<TropicalFishRenderState> modelA = this.getModel();
   private final EntityModel<TropicalFishRenderState> modelB;
   private static final ResourceLocation MODEL_A_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a.png");
   private static final ResourceLocation MODEL_B_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b.png");

   public TropicalFishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TropicalFishModelA(var1.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.modelB = new TropicalFishModelB(var1.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
      this.addLayer(new TropicalFishPatternLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(TropicalFishRenderState var1) {
      ResourceLocation var10000;
      switch (var1.variant.base()) {
         case SMALL -> var10000 = MODEL_A_TEXTURE;
         case LARGE -> var10000 = MODEL_B_TEXTURE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public TropicalFishRenderState createRenderState() {
      return new TropicalFishRenderState();
   }

   public void extractRenderState(TropicalFish var1, TropicalFishRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      var2.baseColor = var1.getBaseColor().getTextureDiffuseColor();
      var2.patternColor = var1.getPatternColor().getTextureDiffuseColor();
   }

   public void render(TropicalFishRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      EntityModel var10001;
      switch (var1.variant.base()) {
         case SMALL -> var10001 = this.modelA;
         case LARGE -> var10001 = this.modelB;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      this.model = var10001;
      super.render(var1, var2, var3, var4);
   }

   protected int getModelTint(TropicalFishRenderState var1) {
      return var1.baseColor;
   }

   protected void setupRotations(TropicalFishRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var1.ageInTicks);
      var2.mulPose(Axis.YP.rotationDegrees(var5));
      if (!var1.isInWater) {
         var2.translate(0.2F, 0.1F, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((TropicalFishRenderState)var1);
   }

   // $FF: synthetic method
   protected int getModelTint(final LivingEntityRenderState var1) {
      return this.getModelTint((TropicalFishRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
