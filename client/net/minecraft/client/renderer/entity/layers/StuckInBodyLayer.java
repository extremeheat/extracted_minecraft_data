package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionfc;

public abstract class StuckInBodyLayer<M extends PlayerModel, S> extends RenderLayer<PlayerRenderState, M> {
   private final Model<S> model;
   private final S modelState;
   private final ResourceLocation texture;
   private final PlacementStyle placementStyle;

   public StuckInBodyLayer(LivingEntityRenderer<?, PlayerRenderState, M> var1, Model<S> var2, S var3, ResourceLocation var4, PlacementStyle var5) {
      super(var1);
      this.model = var2;
      this.modelState = var3;
      this.texture = var4;
      this.placementStyle = var5;
   }

   protected abstract int numStuck(PlayerRenderState var1);

   private void submitStuckItem(PoseStack var1, SubmitNodeCollector var2, int var3, float var4, float var5, float var6, int var7) {
      float var8 = Mth.sqrt(var4 * var4 + var6 * var6);
      float var9 = (float)(Math.atan2((double)var4, (double)var6) * 57.2957763671875);
      float var10 = (float)(Math.atan2((double)var5, (double)var8) * 57.2957763671875);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var9 - 90.0F));
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var10));
      var2.submitModel(this.model, this.modelState, var1, this.model.renderType(this.texture), var3, OverlayTexture.NO_OVERLAY, var7, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, PlayerRenderState var4, float var5, float var6) {
      int var7 = this.numStuck(var4);
      if (var7 > 0) {
         RandomSource var8 = RandomSource.create((long)var4.id);

         for(int var9 = 0; var9 < var7; ++var9) {
            var1.pushPose();
            ModelPart var10 = ((PlayerModel)this.getParentModel()).getRandomBodyPart(var8);
            ModelPart.Cube var11 = var10.getRandomCube(var8);
            var10.translateAndRotate(var1);
            float var12 = var8.nextFloat();
            float var13 = var8.nextFloat();
            float var14 = var8.nextFloat();
            if (this.placementStyle == StuckInBodyLayer.PlacementStyle.ON_SURFACE) {
               int var15 = var8.nextInt(3);
               switch (var15) {
                  case 0 -> var12 = snapToFace(var12);
                  case 1 -> var13 = snapToFace(var13);
                  default -> var14 = snapToFace(var14);
               }
            }

            var1.translate(Mth.lerp(var12, var11.minX, var11.maxX) / 16.0F, Mth.lerp(var13, var11.minY, var11.maxY) / 16.0F, Mth.lerp(var14, var11.minZ, var11.maxZ) / 16.0F);
            this.submitStuckItem(var1, var2, var3, -(var12 * 2.0F - 1.0F), -(var13 * 2.0F - 1.0F), -(var14 * 2.0F - 1.0F), var4.outlineColor);
            var1.popPose();
         }

      }
   }

   private static float snapToFace(float var0) {
      return var0 > 0.5F ? 1.0F : 0.5F;
   }

   public static enum PlacementStyle {
      IN_CUBE,
      ON_SURFACE;

      private PlacementStyle() {
      }

      // $FF: synthetic method
      private static PlacementStyle[] $values() {
         return new PlacementStyle[]{IN_CUBE, ON_SURFACE};
      }
   }
}
