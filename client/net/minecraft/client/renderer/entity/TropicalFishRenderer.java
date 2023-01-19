package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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

   public TropicalFishRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TropicalFishModelA<>(var1.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.modelB = new TropicalFishModelB<>(var1.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
      this.addLayer(new TropicalFishPatternLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(TropicalFish var1) {
      return var1.getBaseTextureLocation();
   }

   public void render(TropicalFish var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      ColorableHierarchicalModel var7 = var1.getBaseVariant() == 0 ? this.modelA : this.modelB;
      this.model = var7;
      float[] var8 = var1.getBaseColor();
      var7.setColor(var8[0], var8[1], var8[2]);
      super.render(var1, var2, var3, var4, var5, var6);
      var7.setColor(1.0F, 1.0F, 1.0F);
   }

   protected void setupRotations(TropicalFish var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4, var5);
      float var6 = 4.3F * Mth.sin(0.6F * var3);
      var2.mulPose(Vector3f.YP.rotationDegrees(var6));
      if (!var1.isInWater()) {
         var2.translate(0.20000000298023224, 0.10000000149011612, 0.0);
         var2.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }
   }
}
