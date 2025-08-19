package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Matrix4f;

public class BlockEntityWithBoundingBoxRenderer<T extends BlockEntity & BoundingBoxRenderable> implements BlockEntityRenderer<T> {
   public BlockEntityWithBoundingBoxRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void submit(T var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
         BoundingBoxRenderable.Mode var9 = ((BoundingBoxRenderable)var1).renderMode();
         if (var9 != BoundingBoxRenderable.Mode.NONE) {
            BoundingBoxRenderable.RenderableBox var10 = ((BoundingBoxRenderable)var1).getRenderableBox();
            BlockPos var11 = var10.localPos();
            Vec3i var12 = var10.size();
            if (var12.getX() >= 1 && var12.getY() >= 1 && var12.getZ() >= 1) {
               float var13 = 1.0F;
               float var14 = 0.9F;
               float var15 = 0.5F;
               BlockPos var16 = var11.offset(var12);
               var8.submitCustomGeometry(var3, RenderType.lines(), (var2x, var3x) -> ShapeRenderer.renderLineBox(var2x, var3x, (double)var11.getX(), (double)var11.getY(), (double)var11.getZ(), (double)var16.getX(), (double)var16.getY(), (double)var16.getZ(), 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F));
               if (var9 == BoundingBoxRenderable.Mode.BOX_AND_INVISIBLE_BLOCKS && var1.getLevel() != null) {
                  this.submitInvisibleBlocks(var1, var1.getLevel(), var11, var12, var8, var3);
               }

            }
         }
      }
   }

   private void submitInvisibleBlocks(T var1, BlockGetter var2, BlockPos var3, Vec3i var4, SubmitNodeCollector var5, PoseStack var6) {
      BlockPos var7 = var1.getBlockPos();
      BlockPos var8 = var7.offset(var3);
      var5.submitCustomGeometry(var6, RenderType.lines(), (var4x, var5x) -> {
         for(BlockPos var7x : BlockPos.betweenClosed(var8, var8.offset(var4).offset(-1, -1, -1))) {
            BlockState var8x = var2.getBlockState(var7x);
            boolean var9 = var8x.isAir();
            boolean var10 = var8x.is(Blocks.STRUCTURE_VOID);
            boolean var11 = var8x.is(Blocks.BARRIER);
            boolean var12 = var8x.is(Blocks.LIGHT);
            boolean var13 = var10 || var11 || var12;
            if (var9 || var13) {
               float var14 = var9 ? 0.05F : 0.0F;
               double var15 = (double)((float)(var7x.getX() - var7.getX()) + 0.45F - var14);
               double var17 = (double)((float)(var7x.getY() - var7.getY()) + 0.45F - var14);
               double var19 = (double)((float)(var7x.getZ() - var7.getZ()) + 0.45F - var14);
               double var21 = (double)((float)(var7x.getX() - var7.getX()) + 0.55F + var14);
               double var23 = (double)((float)(var7x.getY() - var7.getY()) + 0.55F + var14);
               double var25 = (double)((float)(var7x.getZ() - var7.getZ()) + 0.55F + var14);
               if (var9) {
                  ShapeRenderer.renderLineBox(var4x, var5x, var15, var17, var19, var21, var23, var25, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
               } else if (var10) {
                  ShapeRenderer.renderLineBox(var4x, var5x, var15, var17, var19, var21, var23, var25, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
               } else if (var11) {
                  ShapeRenderer.renderLineBox(var4x, var5x, var15, var17, var19, var21, var23, var25, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
               } else if (var12) {
                  ShapeRenderer.renderLineBox(var4x, var5x, var15, var17, var19, var21, var23, var25, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
               }
            }
         }

      });
   }

   private void renderStructureVoids(T var1, BlockPos var2, Vec3i var3, VertexConsumer var4, Matrix4f var5) {
      Level var6 = var1.getLevel();
      if (var6 != null) {
         BlockPos var7 = var1.getBlockPos();
         BitSetDiscreteVoxelShape var8 = new BitSetDiscreteVoxelShape(var3.getX(), var3.getY(), var3.getZ());

         for(BlockPos var10 : BlockPos.betweenClosed(var2, var2.offset(var3).offset(-1, -1, -1))) {
            if (var6.getBlockState(var10).is(Blocks.STRUCTURE_VOID)) {
               ((DiscreteVoxelShape)var8).fill(var10.getX() - var2.getX(), var10.getY() - var2.getY(), var10.getZ() - var2.getZ());
            }
         }

         ((DiscreteVoxelShape)var8).forAllFaces((var4x, var5x, var6x, var7x) -> {
            float var8 = 0.48F;
            float var9 = (float)(var5x + var2.getX() - var7.getX()) + 0.5F - 0.48F;
            float var10 = (float)(var6x + var2.getY() - var7.getY()) + 0.5F - 0.48F;
            float var11 = (float)(var7x + var2.getZ() - var7.getZ()) + 0.5F - 0.48F;
            float var12 = (float)(var5x + var2.getX() - var7.getX()) + 0.5F + 0.48F;
            float var13 = (float)(var6x + var2.getY() - var7.getY()) + 0.5F + 0.48F;
            float var14 = (float)(var7x + var2.getZ() - var7.getZ()) + 0.5F + 0.48F;
            ShapeRenderer.renderFace(var5, var4, var4x, var9, var10, var11, var12, var13, var14, 0.75F, 0.75F, 1.0F, 0.2F);
         });
      }
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }

   // $FF: synthetic method
   private void lambda$submitInvisibleBlocks$2(BlockEntity var1, BlockPos var2, Vec3i var3, PoseStack.Pose var4, VertexConsumer var5) {
      this.renderStructureVoids(var1, var2, var3, var5, var4.pose());
   }
}
