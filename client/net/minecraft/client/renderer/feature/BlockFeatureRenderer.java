package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class BlockFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public BlockFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeStorage var1, MultiBufferSource.BufferSource var2, BlockRenderDispatcher var3) {
      for(SubmitNodeStorage.FallingBlockSubmit var5 : var1.getFallingBlockSubmits()) {
         FallingBlockRenderState var6 = var5.fallingBlockRenderState();
         BlockState var7 = var6.blockState;
         List var8 = var3.getBlockModel(var7).collectParts(RandomSource.create(var7.getSeed(var6.startBlockPos)));
         PoseStack var9 = new PoseStack();
         var9.mulPose((Matrix4fc)var5.pose());
         var3.getModelRenderer().tesselateBlock(var6, var8, var7, var6.blockPos, var9, var2.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(var7)), false, OverlayTexture.NO_OVERLAY);
      }

      for(SubmitNodeStorage.BlockSubmit var12 : var1.getBlockSubmits()) {
         this.poseStack.pushPose();
         this.poseStack.last().set(var12.pose());
         var3.renderSingleBlock(var12.state(), this.poseStack, var2, var12.lightCoords(), var12.overlayCoords());
         this.poseStack.popPose();
      }

      for(SubmitNodeStorage.BlockModelSubmit var13 : var1.getBlockModelSubmits()) {
         ModelBlockRenderer.renderModel(var13.pose(), var2.getBuffer(var13.renderType()), var13.model(), var13.r(), var13.g(), var13.b(), var13.lightCoords(), var13.overlayCoords());
      }

   }
}
