package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
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
      return switch (var1.variant.base()) {
         case SMALL -> MODEL_A_TEXTURE;
         case LARGE -> MODEL_B_TEXTURE;
      };
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
      this.model = switch (var1.variant.base()) {
         case SMALL -> this.modelA;
         case LARGE -> this.modelB;
      };
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
}
