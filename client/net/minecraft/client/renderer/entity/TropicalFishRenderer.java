package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderer extends MobRenderer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
   private final ColorableHierarchicalModel<TropicalFish> modelA = this.getModel();
   private final ColorableHierarchicalModel<TropicalFish> modelB;
   private static final ResourceLocation MODEL_A_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a.png");
   private static final ResourceLocation MODEL_B_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b.png");

   public TropicalFishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TropicalFishModelA<>(var1.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.modelB = new TropicalFishModelB<>(var1.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
      this.addLayer(new TropicalFishPatternLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(TropicalFish var1) {
      return switch (var1.getVariant().base()) {
         case SMALL -> MODEL_A_TEXTURE;
         case LARGE -> MODEL_B_TEXTURE;
      };
   }

   public void render(TropicalFish var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      ColorableHierarchicalModel var7 = switch (var1.getVariant().base()) {
         case SMALL -> this.modelA;
         case LARGE -> this.modelB;
      };
      this.model = var7;
      var7.setColor(var1.getBaseColor().getTextureDiffuseColor());
      super.render(var1, var2, var3, var4, var5, var6);
      var7.setColor(-1);
   }

   protected void setupRotations(TropicalFish var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var7 = 4.3F * Mth.sin(0.6F * var3);
      var2.mulPose(Axis.YP.rotationDegrees(var7));
      if (!var1.isInWater()) {
         var2.translate(0.2F, 0.1F, 0.0F);
         var2.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }
   }
}
