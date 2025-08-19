package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;

public class BlockFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public BlockFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, BlockRenderDispatcher var3, OutlineBufferSource var4) {
      for(SubmitNodeStorage.MovingBlockSubmit var6 : var1.getMovingBlockSubmits()) {
         MovingBlockRenderState var7 = var6.movingBlockRenderState();
         BlockState var8 = var7.blockState;
         List var9 = var3.getBlockModel(var8).collectParts(RandomSource.create(var8.getSeed(var7.randomSeedPos)));
         PoseStack var10 = new PoseStack();
         var10.mulPose((Matrix4fc)var6.pose());
         var3.getModelRenderer().tesselateBlock(var7, var9, var8, var7.blockPos, var10, var2.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(var8)), false, OverlayTexture.NO_OVERLAY);
      }

      for(SubmitNodeStorage.BlockSubmit var13 : var1.getBlockSubmits()) {
         this.poseStack.pushPose();
         this.poseStack.last().set(var13.pose());
         var3.renderSingleBlock(var13.state(), this.poseStack, var2, var13.lightCoords(), var13.overlayCoords());
         if (var13.outlineColor() != 0) {
            var4.setColor(var13.outlineColor());
            var3.renderSingleBlock(var13.state(), this.poseStack, var4, var13.lightCoords(), var13.overlayCoords());
         }

         this.poseStack.popPose();
      }

      for(SubmitNodeStorage.BlockModelSubmit var14 : var1.getBlockModelSubmits()) {
         ModelBlockRenderer.renderModel(var14.pose(), var2.getBuffer(var14.renderType()), var14.model(), var14.r(), var14.g(), var14.b(), var14.lightCoords(), var14.overlayCoords());
         if (var14.outlineColor() != 0) {
            var4.setColor(var14.outlineColor());
            ModelBlockRenderer.renderModel(var14.pose(), var4.getBuffer(var14.renderType()), var14.model(), var14.r(), var14.g(), var14.b(), var14.lightCoords(), var14.overlayCoords());
         }
      }

   }
}
