package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;

public class CatRenderer extends AgeableMobRenderer<Cat, CatRenderState, CatModel> {
   public CatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CatModel(var1.bakeLayer(ModelLayers.CAT)), new CatModel(var1.bakeLayer(ModelLayers.CAT_BABY)), 0.4F);
      this.addLayer(new CatCollarLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(CatRenderState var1) {
      return var1.texture;
   }

   public CatRenderState createRenderState() {
      return new CatRenderState();
   }

   public void extractRenderState(Cat var1, CatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.texture = var1.getVariant().value().texture();
      var2.isCrouching = var1.isCrouching();
      var2.isSprinting = var1.isSprinting();
      var2.isSitting = var1.isInSittingPose();
      var2.lieDownAmount = var1.getLieDownAmount(var3);
      var2.lieDownAmountTail = var1.getLieDownAmountTail(var3);
      var2.relaxStateOneAmount = var1.getRelaxStateOneAmount(var3);
      var2.isLyingOnTopOfSleepingPlayer = var1.isLyingOnTopOfSleepingPlayer();
      var2.collarColor = var1.isTame() ? var1.getCollarColor() : null;
   }

   protected void setupRotations(CatRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = var1.lieDownAmount;
      if (var5 > 0.0F) {
         var2.translate(0.4F * var5, 0.15F * var5, 0.1F * var5);
         var2.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(var5, 0.0F, 90.0F)));
         if (var1.isLyingOnTopOfSleepingPlayer) {
            var2.translate(0.15F * var5, 0.0F, 0.0F);
         }
      }
   }
}
