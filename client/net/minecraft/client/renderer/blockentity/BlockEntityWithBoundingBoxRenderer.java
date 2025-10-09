package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class BlockEntityWithBoundingBoxRenderer<T extends BlockEntity & BoundingBoxRenderable> implements BlockEntityRenderer<T, BlockEntityWithBoundingBoxRenderState> {
   public static final int STRUCTURE_VOIDS_COLOR = ARGB.colorFromFloat(0.2F, 0.75F, 0.75F, 1.0F);

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
                     var1.invisibleBlocks[var10] = BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.STRUCTURE_VOID;
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

   public void submit(BlockEntityWithBoundingBoxRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (var1.isVisible) {
         BoundingBoxRenderable.Mode var5 = var1.mode;
         if (var5 != BoundingBoxRenderable.Mode.NONE) {
            BoundingBoxRenderable.RenderableBox var6 = var1.box;
            BlockPos var7 = var6.localPos();
            Vec3i var8 = var6.size();
            if (var8.getX() >= 1 && var8.getY() >= 1 && var8.getZ() >= 1) {
               float var9 = 1.0F;
               float var10 = 0.9F;
               BlockPos var11 = var7.offset(var8);
               Gizmos.cuboid((new AABB((double)var7.getX(), (double)var7.getY(), (double)var7.getZ(), (double)var11.getX(), (double)var11.getY(), (double)var11.getZ())).move(var1.blockPos), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.9F, 0.9F, 0.9F)), true);
               this.renderInvisibleBlocks(var1, var7, var8);
            }
         }
      }
   }

   private void renderInvisibleBlocks(BlockEntityWithBoundingBoxRenderState var1, BlockPos var2, Vec3i var3) {
      if (var1.invisibleBlocks != null) {
         BlockPos var4 = var1.blockPos;
         BlockPos var5 = var4.offset(var2);

         for(int var6 = 0; var6 < var3.getX(); ++var6) {
            for(int var7 = 0; var7 < var3.getY(); ++var7) {
               for(int var8 = 0; var8 < var3.getZ(); ++var8) {
                  int var9 = var8 * var3.getX() * var3.getY() + var7 * var3.getX() + var6;
                  BlockEntityWithBoundingBoxRenderState.InvisibleBlockType var10 = var1.invisibleBlocks[var9];
                  if (var10 != null) {
                     float var11 = var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.AIR ? 0.05F : 0.0F;
                     double var12 = (double)((float)(var5.getX() + var6) + 0.45F - var11);
                     double var14 = (double)((float)(var5.getY() + var7) + 0.45F - var11);
                     double var16 = (double)((float)(var5.getZ() + var8) + 0.45F - var11);
                     double var18 = (double)((float)(var5.getX() + var6) + 0.55F + var11);
                     double var20 = (double)((float)(var5.getY() + var7) + 0.55F + var11);
                     double var22 = (double)((float)(var5.getZ() + var8) + 0.55F + var11);
                     AABB var24 = new AABB(var12, var14, var16, var18, var20, var22);
                     if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.AIR) {
                        Gizmos.cuboid(var24, GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 1.0F)));
                     } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.STRUCTURE_VOID) {
                        Gizmos.cuboid(var24, GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 1.0F, 0.75F, 0.75F)));
                     } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.BARRIER) {
                        Gizmos.cuboid(var24, GizmoStyle.stroke(-65536));
                     } else if (var10 == BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.LIGHT) {
                        Gizmos.cuboid(var24, GizmoStyle.stroke(-256));
                     }
                  }
               }
            }
         }

      }
   }

   private void renderStructureVoids(BlockEntityWithBoundingBoxRenderState var1, BlockPos var2, Vec3i var3) {
      if (var1.structureVoids != null) {
         BitSetDiscreteVoxelShape var4 = new BitSetDiscreteVoxelShape(var3.getX(), var3.getY(), var3.getZ());

         for(int var5 = 0; var5 < var3.getX(); ++var5) {
            for(int var6 = 0; var6 < var3.getY(); ++var6) {
               for(int var7 = 0; var7 < var3.getZ(); ++var7) {
                  int var8 = var7 * var3.getX() * var3.getY() + var6 * var3.getX() + var5;
                  if (var1.structureVoids[var8]) {
                     ((DiscreteVoxelShape)var4).fill(var5, var6, var7);
                  }
               }
            }
         }

         ((DiscreteVoxelShape)var4).forAllFaces((var1x, var2x, var3x, var4x) -> {
            float var5 = 0.48F;
            float var6 = (float)(var2x + var2.getX()) + 0.5F - 0.48F;
            float var7 = (float)(var3x + var2.getY()) + 0.5F - 0.48F;
            float var8 = (float)(var4x + var2.getZ()) + 0.5F - 0.48F;
            float var9 = (float)(var2x + var2.getX()) + 0.5F + 0.48F;
            float var10 = (float)(var3x + var2.getY()) + 0.5F + 0.48F;
            float var11 = (float)(var4x + var2.getZ()) + 0.5F + 0.48F;
            Gizmos.rect(new Vec3((double)var6, (double)var7, (double)var8), new Vec3((double)var9, (double)var10, (double)var11), var1x, GizmoStyle.fill(STRUCTURE_VOIDS_COLOR));
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
}
