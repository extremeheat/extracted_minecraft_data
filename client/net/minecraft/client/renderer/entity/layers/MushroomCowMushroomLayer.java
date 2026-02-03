package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.cow.CowModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.MushroomCowRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class MushroomCowMushroomLayer extends RenderLayer<MushroomCowRenderState, CowModel> {
   private final BlockRenderDispatcher blockRenderer;

   public MushroomCowMushroomLayer(final RenderLayerParent<MushroomCowRenderState, CowModel> renderer, final BlockRenderDispatcher blockRenderer) {
      super(renderer);
      this.blockRenderer = blockRenderer;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final MushroomCowRenderState state, final float yRot, final float xRot) {
      if (!state.isBaby) {
         boolean appearsGlowingWithInvisibility = state.appearsGlowing() && state.isInvisible;
         if (!state.isInvisible || appearsGlowingWithInvisibility) {
            BlockState mushroomBlockState = state.variant.getBlockState();
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            BlockStateModel model = this.blockRenderer.getBlockModel(mushroomBlockState);
            poseStack.pushPose();
            poseStack.translate(0.2F, -0.35F, 0.5F);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0F));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(poseStack, submitNodeCollector, lightCoords, appearsGlowingWithInvisibility, state.outlineColor, mushroomBlockState, overlayCoords, model);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0.2F, -0.35F, 0.5F);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(42.0F));
            poseStack.translate(0.1F, 0.0F, -0.6F);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0F));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(poseStack, submitNodeCollector, lightCoords, appearsGlowingWithInvisibility, state.outlineColor, mushroomBlockState, overlayCoords, model);
            poseStack.popPose();
            poseStack.pushPose();
            ((CowModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
            poseStack.translate(0.0F, -0.7F, -0.2F);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-78.0F));
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(poseStack, submitNodeCollector, lightCoords, appearsGlowingWithInvisibility, state.outlineColor, mushroomBlockState, overlayCoords, model);
            poseStack.popPose();
         }
      }
   }

   private void submitMushroomBlock(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final boolean appearsGlowingWithInvisibility, final int outlineColor, final BlockState mushroomBlockState, final int overlayCoords, final BlockStateModel model) {
      if (appearsGlowingWithInvisibility) {
         submitNodeCollector.submitBlockModel(poseStack, RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS), model, 0.0F, 0.0F, 0.0F, lightCoords, overlayCoords, outlineColor);
      } else {
         submitNodeCollector.submitBlock(poseStack, mushroomBlockState, lightCoords, overlayCoords, outlineColor);
      }

   }
}
