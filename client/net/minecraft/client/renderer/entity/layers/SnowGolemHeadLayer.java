package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolemRenderState, SnowGolemModel> {
   private final BlockRenderDispatcher blockRenderer;

   public SnowGolemHeadLayer(final RenderLayerParent<SnowGolemRenderState, SnowGolemModel> renderer, final BlockRenderDispatcher blockRenderer) {
      super(renderer);
      this.blockRenderer = blockRenderer;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final SnowGolemRenderState state, final float yRot, final float xRot) {
      if (state.hasPumpkin) {
         if (!state.isInvisible || state.appearsGlowing()) {
            poseStack.pushPose();
            ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
            float s = 0.625F;
            poseStack.translate(0.0F, -0.34375F, 0.0F);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(0.625F, -0.625F, -0.625F);
            BlockState pumpkinBlockState = Blocks.CARVED_PUMPKIN.defaultBlockState();
            BlockStateModel model = this.blockRenderer.getBlockModel(pumpkinBlockState);
            int overlayCoords = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            RenderType renderType = state.appearsGlowing() && state.isInvisible ? RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS) : ItemBlockRenderTypes.getBlockModelRenderType(model);
            submitNodeCollector.submitBlockModel(poseStack, renderType, model, -16777216, lightCoords, overlayCoords, state.outlineColor);
            poseStack.popPose();
         }
      }
   }
}
