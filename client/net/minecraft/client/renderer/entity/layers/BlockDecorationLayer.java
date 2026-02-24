package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import org.joml.Quaternionfc;

public class BlockDecorationLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final Function<S, BlockModelRenderState> blockModel;
   private final Consumer<PoseStack> transform;

   public BlockDecorationLayer(final RenderLayerParent<S, M> renderer, final Function<S, BlockModelRenderState> blockModel, final Consumer<PoseStack> transform) {
      super(renderer);
      this.blockModel = blockModel;
      this.transform = transform;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final S state, final float yRot, final float xRot) {
      BlockModelRenderState blockModel = (BlockModelRenderState)this.blockModel.apply(state);
      if (!blockModel.isEmpty()) {
         Block block = blockModel.block;
         boolean isCopperGolemStatue = block instanceof CopperGolemStatueBlock;
         poseStack.pushPose();
         this.transform.accept(poseStack);
         if (!isCopperGolemStatue) {
            poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
         }

         if (isCopperGolemStatue || block instanceof AbstractSkullBlock || block instanceof AbstractBannerBlock || block instanceof AbstractChestBlock) {
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
         }

         if (block instanceof FlowerBedBlock) {
            poseStack.translate(-0.25, -1.5, -0.25);
         } else if (!isCopperGolemStatue) {
            poseStack.translate(-0.5, -1.5, -0.5);
         } else {
            poseStack.translate(-0.5, 0.0, -0.5);
         }

         blockModel.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
         poseStack.popPose();
      }
   }
}
