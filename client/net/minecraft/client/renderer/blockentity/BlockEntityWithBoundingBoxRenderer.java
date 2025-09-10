package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Matrix4f;

public class BlockEntityWithBoundingBoxRenderer<T extends BlockEntity & BoundingBoxRenderable> implements BlockEntityRenderer<T, BlockEntityWithBoundingBoxRenderState> {
   public BlockEntityWithBoundingBoxRenderer() {
      super();
   }

   public BlockEntityWithBoundingBoxRenderState createRenderState() {
      return new BlockEntityWithBoundingBoxRenderState();
   }

   public void extractRenderState(T var1, BlockEntityWithBoundingBoxRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      extract(var1, var2);
   }

   public static <T extends BlockEntity & BoundingBoxRenderable> void extract(T var0, BlockEntityWithBoundingBoxRenderState var1) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      var1.isVisible = var2.canUseGameMasterBlocks() || var2.isSpectator();
      var1.box = ((BoundingBoxRenderable)var0).getRenderableBox();
      var1.mode = ((BoundingBoxRenderable)var0).renderMode();
      BlockPos var3 = var1.box.localPos();
      Vec3i var4 = var1.box.size();
      BlockPos var5 = var1.blockPos;
      BlockPos var6 = var5.offset(var3);
      if (var1.isVisible && var0.getLevel() != null && var1.mode == BoundingBoxRenderable.Mode.BOX_AND_INVISIBLE_BLOCKS) {
         var1.invisibleBlocks = new BlockEntityWithBoundingBoxRenderState.InvisibleBlockType[var4.getX() * var4.getY() * var4.getZ()];

         for(int var7 = 0; var7 < var4.getX(); ++var7) {
            for(int var8 = 0; var8 < var4.getY(); ++var8) {
               for(int var9 = 0; var9 < var4.getZ(); ++var9) {
                  int var10 = var9 * var4.getX() * var4.getY() + var8 * var4.getX() + var7;
                  BlockState var11 = var0.getLevel().getBlockState(var6.offset(var7, var8, var9));
                  if (var11.isAir()) {
                     var1.invisibleBlocks[var10] = BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.AIR;
                  } else if (var11.is(Blocks.STRUCTURE_VOID)) {
                     var1.invisibleBlocks[var10] = BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.STRUCUTRE_VOID;
                  } else if (var11.is(Blocks.BARRIER)) {
                     var1.invisibleBlocks[var10] = BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.BARRIER;
                  } else if (var11.is(Blocks.LIGHT)) {
                     var1.invisibleBlocks[var10] = BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.LIGHT;
                  }
               }
            }
         }
      } else {
         var1.invisibleBlocks = null;
      }

      if (var1.isVisible) {
      }

      var1.structureVoids = null;
   }

   public void submit(BlockEntityWithBoundingBoxRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      if (var1.isVisible) {
         BoundingBoxRenderable.Mode var4 = var1.mode;
         if (var4 != BoundingBoxRenderable.Mode.NONE) {
            BoundingBoxRenderable.RenderableBox var5 = var1.box;
            BlockPos var6 = var5.localPos();
            Vec3i var7 = var5.size();
            if (var7.getX() >= 1 && var7.getY() >= 1 && var7.getZ() >= 1) {
               float var8 = 1.0F;
               float var9 = 0.9F;
               float var10 = 0.5F;
               BlockPos var11 = var6.offset(var7);
               var3.submitCustomGeometry(var2, RenderType.lines(), (var2x, var3x) -> ShapeRenderer.renderLineBox(var2x, var3x, (double)var6.getX(), (double)var6.getY(), (double)var6.getZ(), (double)var11.getX(), (double)var11.getY(), (double)var11.getZ(), 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F));
               this.submitInvisibleBlocks(var1, var6, var7, var3, var2);
            }
         }
      }
   }

   private void submitInvisibleBlocks(BlockEntityWithBoundingBoxRenderState var1, BlockPos var2, Vec3i var3, SubmitNodeCollector var4, PoseStack var5) {
      if (var1.invisibleBlocks != null) {
         BlockPos var6 = var1.blockPos;
         BlockPos var7 = var6.offset(var2);
         var4.submitCustomGeometry(var5, RenderType.lines(), (var4x, var5x) -> {
            for(int var6x = 0; var6x < var3.getX(); ++var6x) {
               for(int var7x = 0; var7x < var3.getY(); ++var7x) {
                  for(int var8 = 0; var8 < var3.getZ(); ++var8) {
                     int var9 = var8 * var3.getX() * var3.getY() + var7x * var3.getX() + var6x;
                     BlockEntityWithBoundingBoxRenderState.InvisibleBlockType var10 = var1.invisibleBlocks[var9];
                     if (var10 != null) {
                        float var11 = var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.AIR ? 0.05F : 0.0F;
                        double var12 = (double)((float)(var7.getX() + var6x - var6.getX()) + 0.45F - var11);
                        double var14 = (double)((float)(var7.getY() + var7x - var6.getY()) + 0.45F - var11);
                        double var16 = (double)((float)(var7.getZ() + var8 - var6.getZ()) + 0.45F - var11);
                        double var18 = (double)((float)(var7.getX() + var6x - var6.getX()) + 0.55F + var11);
                        double var20 = (double)((float)(var7.getY() + var7x - var6.getY()) + 0.55F + var11);
                        double var22 = (double)((float)(var7.getZ() + var8 - var6.getZ()) + 0.55F + var11);
                        if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.AIR) {
                           ShapeRenderer.renderLineBox(var4x, var5x, var12, var14, var16, var18, var20, var22, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
                        } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.STRUCUTRE_VOID) {
                           ShapeRenderer.renderLineBox(var4x, var5x, var12, var14, var16, var18, var20, var22, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
                        } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.BARRIER) {
                           ShapeRenderer.renderLineBox(var4x, var5x, var12, var14, var16, var18, var20, var22, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
                        } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.LIGHT) {
                           ShapeRenderer.renderLineBox(var4x, var5x, var12, var14, var16, var18, var20, var22, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
                        }
                     }
                  }
               }
            }

         });
      }
   }

   private void renderStructureVoids(BlockEntityWithBoundingBoxRenderState var1, BlockPos var2, Vec3i var3, VertexConsumer var4, Matrix4f var5) {
      if (var1.structureVoids != null) {
         BlockPos var6 = var1.blockPos;
         BitSetDiscreteVoxelShape var7 = new BitSetDiscreteVoxelShape(var3.getX(), var3.getY(), var3.getZ());

         for(int var8 = 0; var8 < var3.getX(); ++var8) {
            for(int var9 = 0; var9 < var3.getY(); ++var9) {
               for(int var10 = 0; var10 < var3.getZ(); ++var10) {
                  int var11 = var10 * var3.getX() * var3.getY() + var9 * var3.getX() + var8;
                  if (var1.structureVoids[var11]) {
                     ((DiscreteVoxelShape)var7).fill(var8, var9, var10);
                  }
               }
            }
         }

         ((DiscreteVoxelShape)var7).forAllFaces((var4x, var5x, var6x, var7x) -> {
            float var8 = 0.48F;
            float var9 = (float)(var5x + var2.getX() - var6.getX()) + 0.5F - 0.48F;
            float var10 = (float)(var6x + var2.getY() - var6.getY()) + 0.5F - 0.48F;
            float var11 = (float)(var7x + var2.getZ() - var6.getZ()) + 0.5F - 0.48F;
            float var12 = (float)(var5x + var2.getX() - var6.getX()) + 0.5F + 0.48F;
            float var13 = (float)(var6x + var2.getY() - var6.getY()) + 0.5F + 0.48F;
            float var14 = (float)(var7x + var2.getZ() - var6.getZ()) + 0.5F + 0.48F;
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
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   private void lambda$submitInvisibleBlocks$2(BlockEntityWithBoundingBoxRenderState var1, BlockPos var2, Vec3i var3, PoseStack.Pose var4, VertexConsumer var5) {
      this.renderStructureVoids(var1, var2, var3, var5, var4.pose());
   }
}
