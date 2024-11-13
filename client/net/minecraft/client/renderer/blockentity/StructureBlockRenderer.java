package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureBlockRenderer implements BlockEntityRenderer<StructureBlockEntity> {
   public StructureBlockRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void render(StructureBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
         BlockPos var7 = var1.getStructurePos();
         Vec3i var8 = var1.getStructureSize();
         if (var8.getX() >= 1 && var8.getY() >= 1 && var8.getZ() >= 1) {
            if (var1.getMode() == StructureMode.SAVE || var1.getMode() == StructureMode.LOAD) {
               double var9 = (double)var7.getX();
               double var11 = (double)var7.getZ();
               double var19 = (double)var7.getY();
               double var25 = var19 + (double)var8.getY();
               double var13;
               double var15;
               switch (var1.getMirror()) {
                  case LEFT_RIGHT:
                     var13 = (double)var8.getX();
                     var15 = (double)(-var8.getZ());
                     break;
                  case FRONT_BACK:
                     var13 = (double)(-var8.getX());
                     var15 = (double)var8.getZ();
                     break;
                  default:
                     var13 = (double)var8.getX();
                     var15 = (double)var8.getZ();
               }

               double var17;
               double var21;
               double var23;
               double var27;
               switch (var1.getRotation()) {
                  case CLOCKWISE_90:
                     var17 = var15 < 0.0 ? var9 : var9 + 1.0;
                     var21 = var13 < 0.0 ? var11 + 1.0 : var11;
                     var23 = var17 - var15;
                     var27 = var21 + var13;
                     break;
                  case CLOCKWISE_180:
                     var17 = var13 < 0.0 ? var9 : var9 + 1.0;
                     var21 = var15 < 0.0 ? var11 : var11 + 1.0;
                     var23 = var17 - var13;
                     var27 = var21 - var15;
                     break;
                  case COUNTERCLOCKWISE_90:
                     var17 = var15 < 0.0 ? var9 + 1.0 : var9;
                     var21 = var13 < 0.0 ? var11 : var11 + 1.0;
                     var23 = var17 + var15;
                     var27 = var21 - var13;
                     break;
                  default:
                     var17 = var13 < 0.0 ? var9 + 1.0 : var9;
                     var21 = var15 < 0.0 ? var11 + 1.0 : var11;
                     var23 = var17 + var13;
                     var27 = var21 + var15;
               }

               float var29 = 1.0F;
               float var30 = 0.9F;
               float var31 = 0.5F;
               if (var1.getMode() == StructureMode.SAVE || var1.getShowBoundingBox()) {
                  VertexConsumer var32 = var4.getBuffer(RenderType.lines());
                  ShapeRenderer.renderLineBox(var3, var32, var17, var19, var21, var23, var25, var27, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
               }

               if (var1.getMode() == StructureMode.SAVE && var1.getShowAir()) {
                  this.renderInvisibleBlocks(var1, var4, var3);
               }

            }
         }
      }
   }

   private void renderInvisibleBlocks(StructureBlockEntity var1, MultiBufferSource var2, PoseStack var3) {
      Level var4 = var1.getLevel();
      VertexConsumer var5 = var2.getBuffer(RenderType.lines());
      BlockPos var6 = var1.getBlockPos();
      BlockPos var7 = StructureUtils.getStructureOrigin(var1);

      for(BlockPos var9 : BlockPos.betweenClosed(var7, var7.offset(var1.getStructureSize()).offset(-1, -1, -1))) {
         BlockState var10 = var4.getBlockState(var9);
         boolean var11 = var10.isAir();
         boolean var12 = var10.is(Blocks.STRUCTURE_VOID);
         boolean var13 = var10.is(Blocks.BARRIER);
         boolean var14 = var10.is(Blocks.LIGHT);
         boolean var15 = var12 || var13 || var14;
         if (var11 || var15) {
            float var16 = var11 ? 0.05F : 0.0F;
            double var17 = (double)((float)(var9.getX() - var6.getX()) + 0.45F - var16);
            double var19 = (double)((float)(var9.getY() - var6.getY()) + 0.45F - var16);
            double var21 = (double)((float)(var9.getZ() - var6.getZ()) + 0.45F - var16);
            double var23 = (double)((float)(var9.getX() - var6.getX()) + 0.55F + var16);
            double var25 = (double)((float)(var9.getY() - var6.getY()) + 0.55F + var16);
            double var27 = (double)((float)(var9.getZ() - var6.getZ()) + 0.55F + var16);
            if (var11) {
               ShapeRenderer.renderLineBox(var3, var5, var17, var19, var21, var23, var25, var27, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
            } else if (var12) {
               ShapeRenderer.renderLineBox(var3, var5, var17, var19, var21, var23, var25, var27, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
            } else if (var13) {
               ShapeRenderer.renderLineBox(var3, var5, var17, var19, var21, var23, var25, var27, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
            } else if (var14) {
               ShapeRenderer.renderLineBox(var3, var5, var17, var19, var21, var23, var25, var27, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
            }
         }
      }

   }

   private void renderStructureVoids(StructureBlockEntity var1, VertexConsumer var2, PoseStack var3) {
      Level var4 = var1.getLevel();
      if (var4 != null) {
         BlockPos var5 = var1.getBlockPos();
         BlockPos var6 = StructureUtils.getStructureOrigin(var1);
         Vec3i var7 = var1.getStructureSize();
         BitSetDiscreteVoxelShape var8 = new BitSetDiscreteVoxelShape(var7.getX(), var7.getY(), var7.getZ());

         for(BlockPos var10 : BlockPos.betweenClosed(var6, var6.offset(var7).offset(-1, -1, -1))) {
            if (var4.getBlockState(var10).is(Blocks.STRUCTURE_VOID)) {
               ((DiscreteVoxelShape)var8).fill(var10.getX() - var6.getX(), var10.getY() - var6.getY(), var10.getZ() - var6.getZ());
            }
         }

         ((DiscreteVoxelShape)var8).forAllFaces((var4x, var5x, var6x, var7x) -> {
            float var8 = 0.48F;
            float var9 = (float)(var5x + var6.getX() - var5.getX()) + 0.5F - 0.48F;
            float var10 = (float)(var6x + var6.getY() - var5.getY()) + 0.5F - 0.48F;
            float var11 = (float)(var7x + var6.getZ() - var5.getZ()) + 0.5F - 0.48F;
            float var12 = (float)(var5x + var6.getX() - var5.getX()) + 0.5F + 0.48F;
            float var13 = (float)(var6x + var6.getY() - var5.getY()) + 0.5F + 0.48F;
            float var14 = (float)(var7x + var6.getZ() - var5.getZ()) + 0.5F + 0.48F;
            ShapeRenderer.renderFace(var3, var2, var4x, var9, var10, var11, var12, var13, var14, 0.75F, 0.75F, 1.0F, 0.2F);
         });
      }
   }

   public boolean shouldRenderOffScreen(StructureBlockEntity var1) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
