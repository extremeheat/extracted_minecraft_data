package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionfc;

public abstract class StuckInBodyLayer<M extends PlayerModel, S> extends RenderLayer<AvatarRenderState, M> {
   private final Model<S> model;
   private final S modelState;
   private final Identifier texture;
   private final PlacementStyle placementStyle;

   public StuckInBodyLayer(final LivingEntityRenderer<?, AvatarRenderState, M> renderer, final Model<S> model, final S modelState, final Identifier texture, final PlacementStyle placementStyle) {
      super(renderer);
      this.model = model;
      this.modelState = modelState;
      this.texture = texture;
      this.placementStyle = placementStyle;
   }

   protected abstract int numStuck(final AvatarRenderState state);

   private void submitStuckItem(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final float directionX, final float directionY, final float directionZ, final int outlineColor) {
      float directionXZ = Mth.sqrt(directionX * directionX + directionZ * directionZ);
      float yRot = (float)(Math.atan2((double)directionX, (double)directionZ) * 57.2957763671875);
      float xRot = (float)(Math.atan2((double)directionY, (double)directionXZ) * 57.2957763671875);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(yRot - 90.0F));
      poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(xRot));
      submitNodeCollector.submitModel(this.model, this.modelState, poseStack, this.model.renderType(this.texture), lightCoords, OverlayTexture.NO_OVERLAY, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final AvatarRenderState state, final float yRot, final float xRot) {
      int count = this.numStuck(state);
      if (count > 0) {
         RandomSource random = RandomSource.create((long)state.id);

         for(int i = 0; i < count; ++i) {
            poseStack.pushPose();
            ModelPart modelPart = ((PlayerModel)this.getParentModel()).getRandomBodyPart(random);
            ModelPart.Cube cube = modelPart.getRandomCube(random);
            modelPart.translateAndRotate(poseStack);
            float midX = random.nextFloat();
            float midY = random.nextFloat();
            float midZ = random.nextFloat();
            if (this.placementStyle == StuckInBodyLayer.PlacementStyle.ON_SURFACE) {
               int plane = random.nextInt(3);
               switch (plane) {
                  case 0 -> midX = snapToFace(midX);
                  case 1 -> midY = snapToFace(midY);
                  default -> midZ = snapToFace(midZ);
               }
            }

            poseStack.translate(Mth.lerp(midX, cube.minX, cube.maxX) / 16.0F, Mth.lerp(midY, cube.minY, cube.maxY) / 16.0F, Mth.lerp(midZ, cube.minZ, cube.maxZ) / 16.0F);
            this.submitStuckItem(poseStack, submitNodeCollector, lightCoords, -(midX * 2.0F - 1.0F), -(midY * 2.0F - 1.0F), -(midZ * 2.0F - 1.0F), state.outlineColor);
            poseStack.popPose();
         }

      }
   }

   private static float snapToFace(final float value) {
      return value > 0.5F ? 1.0F : 0.5F;
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
