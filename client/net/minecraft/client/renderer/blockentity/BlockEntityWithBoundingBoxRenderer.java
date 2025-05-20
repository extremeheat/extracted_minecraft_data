package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
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

public class BlockEntityWithBoundingBoxRenderer<T extends BlockEntity & BoundingBoxRenderable> implements BlockEntityRenderer<T> {
   public BlockEntityWithBoundingBoxRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
         BoundingBoxRenderable.Mode var8 = ((BoundingBoxRenderable)var1).renderMode();
         if (var8 != BoundingBoxRenderable.Mode.NONE) {
            BoundingBoxRenderable.RenderableBox var9 = ((BoundingBoxRenderable)var1).getRenderableBox();
            BlockPos var10 = var9.localPos();
            Vec3i var11 = var9.size();
            if (var11.getX() >= 1 && var11.getY() >= 1 && var11.getZ() >= 1) {
               float var12 = 1.0F;
               float var13 = 0.9F;
               float var14 = 0.5F;
               VertexConsumer var15 = var4.getBuffer(RenderType.lines());
               BlockPos var16 = var10.offset(var11);
               ShapeRenderer.renderLineBox(var3, var15, (double)var10.getX(), (double)var10.getY(), (double)var10.getZ(), (double)var16.getX(), (double)var16.getY(), (double)var16.getZ(), 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
               if (var8 == BoundingBoxRenderable.Mode.BOX_AND_INVISIBLE_BLOCKS && var1.getLevel() != null) {
                  this.renderInvisibleBlocks(var1, var1.getLevel(), var10, var11, var4, var3);
               }

            }
         }
      }
   }

   private void renderInvisibleBlocks(T var1, BlockGetter var2, BlockPos var3, Vec3i var4, MultiBufferSource var5, PoseStack var6) {
      VertexConsumer var7 = var5.getBuffer(RenderType.lines());
      BlockPos var8 = var1.getBlockPos();
      BlockPos var9 = var8.offset(var3);

      for(BlockPos var11 : BlockPos.betweenClosed(var9, var9.offset(var4).offset(-1, -1, -1))) {
         BlockState var12 = var2.getBlockState(var11);
         boolean var13 = var12.isAir();
         boolean var14 = var12.is(Blocks.STRUCTURE_VOID);
         boolean var15 = var12.is(Blocks.BARRIER);
         boolean var16 = var12.is(Blocks.LIGHT);
         boolean var17 = var14 || var15 || var16;
         if (var13 || var17) {
            float var18 = var13 ? 0.05F : 0.0F;
            double var19 = (double)((float)(var11.getX() - var8.getX()) + 0.45F - var18);
            double var21 = (double)((float)(var11.getY() - var8.getY()) + 0.45F - var18);
            double var23 = (double)((float)(var11.getZ() - var8.getZ()) + 0.45F - var18);
            double var25 = (double)((float)(var11.getX() - var8.getX()) + 0.55F + var18);
            double var27 = (double)((float)(var11.getY() - var8.getY()) + 0.55F + var18);
            double var29 = (double)((float)(var11.getZ() - var8.getZ()) + 0.55F + var18);
            if (var13) {
               ShapeRenderer.renderLineBox(var6, var7, var19, var21, var23, var25, var27, var29, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
            } else if (var14) {
               ShapeRenderer.renderLineBox(var6, var7, var19, var21, var23, var25, var27, var29, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
            } else if (var15) {
               ShapeRenderer.renderLineBox(var6, var7, var19, var21, var23, var25, var27, var29, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
            } else if (var16) {
               ShapeRenderer.renderLineBox(var6, var7, var19, var21, var23, var25, var27, var29, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
            }
         }
      }

   }

   private void renderStructureVoids(T var1, BlockPos var2, Vec3i var3, VertexConsumer var4, PoseStack var5) {
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
}
