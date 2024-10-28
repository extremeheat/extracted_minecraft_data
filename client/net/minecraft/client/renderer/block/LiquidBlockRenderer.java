package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlockRenderer {
   private static final float MAX_FLUID_HEIGHT = 0.8888889F;
   private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
   private TextureAtlasSprite waterOverlay;

   public LiquidBlockRenderer() {
      super();
   }

   protected void setupSprites() {
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
      this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
   }

   private static boolean isNeighborSameFluid(FluidState var0, FluidState var1) {
      return var1.getType().isSame(var0.getType());
   }

   private static boolean isFaceOccludedByState(BlockGetter var0, Direction var1, float var2, BlockPos var3, BlockState var4) {
      if (var4.canOcclude()) {
         VoxelShape var5 = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)var2, 1.0);
         VoxelShape var6 = var4.getOcclusionShape(var0, var3);
         return Shapes.blockOccudes(var5, var6, var1);
      } else {
         return false;
      }
   }

   private static boolean isFaceOccludedByNeighbor(BlockGetter var0, BlockPos var1, Direction var2, float var3, BlockState var4) {
      return isFaceOccludedByState(var0, var2, var3, var1.relative(var2), var4);
   }

   private static boolean isFaceOccludedBySelf(BlockGetter var0, BlockPos var1, BlockState var2, Direction var3) {
      return isFaceOccludedByState(var0, var3.getOpposite(), 1.0F, var1, var2);
   }

   public static boolean shouldRenderFace(BlockAndTintGetter var0, BlockPos var1, FluidState var2, BlockState var3, Direction var4, FluidState var5) {
      return !isFaceOccludedBySelf(var0, var1, var3, var4) && !isNeighborSameFluid(var2, var5);
   }

   public void tesselate(BlockAndTintGetter var1, BlockPos var2, VertexConsumer var3, BlockState var4, FluidState var5) {
      boolean var6 = var5.is(FluidTags.LAVA);
      TextureAtlasSprite[] var7 = var6 ? this.lavaIcons : this.waterIcons;
      int var8 = var6 ? 16777215 : BiomeColors.getAverageWaterColor(var1, var2);
      float var9 = (float)(var8 >> 16 & 255) / 255.0F;
      float var10 = (float)(var8 >> 8 & 255) / 255.0F;
      float var11 = (float)(var8 & 255) / 255.0F;
      BlockState var12 = var1.getBlockState(var2.relative(Direction.DOWN));
      FluidState var13 = var12.getFluidState();
      BlockState var14 = var1.getBlockState(var2.relative(Direction.UP));
      FluidState var15 = var14.getFluidState();
      BlockState var16 = var1.getBlockState(var2.relative(Direction.NORTH));
      FluidState var17 = var16.getFluidState();
      BlockState var18 = var1.getBlockState(var2.relative(Direction.SOUTH));
      FluidState var19 = var18.getFluidState();
      BlockState var20 = var1.getBlockState(var2.relative(Direction.WEST));
      FluidState var21 = var20.getFluidState();
      BlockState var22 = var1.getBlockState(var2.relative(Direction.EAST));
      FluidState var23 = var22.getFluidState();
      boolean var24 = !isNeighborSameFluid(var5, var15);
      boolean var25 = shouldRenderFace(var1, var2, var5, var4, Direction.DOWN, var13) && !isFaceOccludedByNeighbor(var1, var2, Direction.DOWN, 0.8888889F, var12);
      boolean var26 = shouldRenderFace(var1, var2, var5, var4, Direction.NORTH, var17);
      boolean var27 = shouldRenderFace(var1, var2, var5, var4, Direction.SOUTH, var19);
      boolean var28 = shouldRenderFace(var1, var2, var5, var4, Direction.WEST, var21);
      boolean var29 = shouldRenderFace(var1, var2, var5, var4, Direction.EAST, var23);
      if (var24 || var25 || var29 || var28 || var26 || var27) {
         float var30 = var1.getShade(Direction.DOWN, true);
         float var31 = var1.getShade(Direction.UP, true);
         float var32 = var1.getShade(Direction.NORTH, true);
         float var33 = var1.getShade(Direction.WEST, true);
         Fluid var34 = var5.getType();
         float var39 = this.getHeight(var1, var34, var2, var4, var5);
         float var35;
         float var36;
         float var37;
         float var38;
         if (var39 >= 1.0F) {
            var35 = 1.0F;
            var36 = 1.0F;
            var37 = 1.0F;
            var38 = 1.0F;
         } else {
            float var40 = this.getHeight(var1, var34, var2.north(), var16, var17);
            float var41 = this.getHeight(var1, var34, var2.south(), var18, var19);
            float var42 = this.getHeight(var1, var34, var2.east(), var22, var23);
            float var43 = this.getHeight(var1, var34, var2.west(), var20, var21);
            var35 = this.calculateAverageHeight(var1, var34, var39, var40, var42, var2.relative(Direction.NORTH).relative(Direction.EAST));
            var36 = this.calculateAverageHeight(var1, var34, var39, var40, var43, var2.relative(Direction.NORTH).relative(Direction.WEST));
            var37 = this.calculateAverageHeight(var1, var34, var39, var41, var42, var2.relative(Direction.SOUTH).relative(Direction.EAST));
            var38 = this.calculateAverageHeight(var1, var34, var39, var41, var43, var2.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         double var73 = (double)(var2.getX() & 15);
         double var74 = (double)(var2.getY() & 15);
         double var44 = (double)(var2.getZ() & 15);
         float var46 = 0.001F;
         float var47 = var25 ? 0.001F : 0.0F;
         float var48;
         float var49;
         float var50;
         float var51;
         float var52;
         float var53;
         float var54;
         float var55;
         if (var24 && !isFaceOccludedByNeighbor(var1, var2, Direction.UP, Math.min(Math.min(var36, var38), Math.min(var37, var35)), var14)) {
            var36 -= 0.001F;
            var38 -= 0.001F;
            var37 -= 0.001F;
            var35 -= 0.001F;
            Vec3 var56 = var5.getFlow(var1, var2);
            TextureAtlasSprite var57;
            float var58;
            float var59;
            float var61;
            if (var56.x == 0.0 && var56.z == 0.0) {
               var57 = var7[0];
               var48 = var57.getU(0.0F);
               var52 = var57.getV(0.0F);
               var49 = var48;
               var53 = var57.getV(1.0F);
               var50 = var57.getU(1.0F);
               var54 = var53;
               var51 = var50;
               var55 = var52;
            } else {
               var57 = var7[1];
               var58 = (float)Mth.atan2(var56.z, var56.x) - 1.5707964F;
               var59 = Mth.sin(var58) * 0.25F;
               float var60 = Mth.cos(var58) * 0.25F;
               var61 = 0.5F;
               var48 = var57.getU(0.5F + (-var60 - var59));
               var52 = var57.getV(0.5F + -var60 + var59);
               var49 = var57.getU(0.5F + -var60 + var59);
               var53 = var57.getV(0.5F + var60 + var59);
               var50 = var57.getU(0.5F + var60 + var59);
               var54 = var57.getV(0.5F + (var60 - var59));
               var51 = var57.getU(0.5F + (var60 - var59));
               var55 = var57.getV(0.5F + (-var60 - var59));
            }

            float var81 = (var48 + var49 + var50 + var51) / 4.0F;
            var58 = (var52 + var53 + var54 + var55) / 4.0F;
            var59 = var7[0].uvShrinkRatio();
            var48 = Mth.lerp(var59, var48, var81);
            var49 = Mth.lerp(var59, var49, var81);
            var50 = Mth.lerp(var59, var50, var81);
            var51 = Mth.lerp(var59, var51, var81);
            var52 = Mth.lerp(var59, var52, var58);
            var53 = Mth.lerp(var59, var53, var58);
            var54 = Mth.lerp(var59, var54, var58);
            var55 = Mth.lerp(var59, var55, var58);
            int var84 = this.getLightColor(var1, var2);
            var61 = var31 * var9;
            float var62 = var31 * var10;
            float var63 = var31 * var11;
            this.vertex(var3, var73 + 0.0, var74 + (double)var36, var44 + 0.0, var61, var62, var63, var48, var52, var84);
            this.vertex(var3, var73 + 0.0, var74 + (double)var38, var44 + 1.0, var61, var62, var63, var49, var53, var84);
            this.vertex(var3, var73 + 1.0, var74 + (double)var37, var44 + 1.0, var61, var62, var63, var50, var54, var84);
            this.vertex(var3, var73 + 1.0, var74 + (double)var35, var44 + 0.0, var61, var62, var63, var51, var55, var84);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var73 + 0.0, var74 + (double)var36, var44 + 0.0, var61, var62, var63, var48, var52, var84);
               this.vertex(var3, var73 + 1.0, var74 + (double)var35, var44 + 0.0, var61, var62, var63, var51, var55, var84);
               this.vertex(var3, var73 + 1.0, var74 + (double)var37, var44 + 1.0, var61, var62, var63, var50, var54, var84);
               this.vertex(var3, var73 + 0.0, var74 + (double)var38, var44 + 1.0, var61, var62, var63, var49, var53, var84);
            }
         }

         if (var25) {
            var48 = var7[0].getU0();
            var49 = var7[0].getU1();
            var50 = var7[0].getV0();
            var51 = var7[0].getV1();
            int var78 = this.getLightColor(var1, var2.below());
            var53 = var30 * var9;
            var54 = var30 * var10;
            var55 = var30 * var11;
            this.vertex(var3, var73, var74 + (double)var47, var44 + 1.0, var53, var54, var55, var48, var51, var78);
            this.vertex(var3, var73, var74 + (double)var47, var44, var53, var54, var55, var48, var50, var78);
            this.vertex(var3, var73 + 1.0, var74 + (double)var47, var44, var53, var54, var55, var49, var50, var78);
            this.vertex(var3, var73 + 1.0, var74 + (double)var47, var44 + 1.0, var53, var54, var55, var49, var51, var78);
         }

         int var75 = this.getLightColor(var1, var2);
         Iterator var76 = Direction.Plane.HORIZONTAL.iterator();

         while(true) {
            Direction var77;
            double var79;
            double var80;
            double var82;
            double var83;
            boolean var85;
            do {
               do {
                  if (!var76.hasNext()) {
                     return;
                  }

                  var77 = (Direction)var76.next();
                  switch (var77) {
                     case NORTH:
                        var51 = var36;
                        var52 = var35;
                        var79 = var73;
                        var82 = var73 + 1.0;
                        var80 = var44 + 0.0010000000474974513;
                        var83 = var44 + 0.0010000000474974513;
                        var85 = var26;
                        break;
                     case SOUTH:
                        var51 = var37;
                        var52 = var38;
                        var79 = var73 + 1.0;
                        var82 = var73;
                        var80 = var44 + 1.0 - 0.0010000000474974513;
                        var83 = var44 + 1.0 - 0.0010000000474974513;
                        var85 = var27;
                        break;
                     case WEST:
                        var51 = var38;
                        var52 = var36;
                        var79 = var73 + 0.0010000000474974513;
                        var82 = var73 + 0.0010000000474974513;
                        var80 = var44 + 1.0;
                        var83 = var44;
                        var85 = var28;
                        break;
                     default:
                        var51 = var35;
                        var52 = var37;
                        var79 = var73 + 1.0 - 0.0010000000474974513;
                        var82 = var73 + 1.0 - 0.0010000000474974513;
                        var80 = var44;
                        var83 = var44 + 1.0;
                        var85 = var29;
                  }
               } while(!var85);
            } while(isFaceOccludedByNeighbor(var1, var2, var77, Math.max(var51, var52), var1.getBlockState(var2.relative(var77))));

            BlockPos var86 = var2.relative(var77);
            TextureAtlasSprite var87 = var7[1];
            if (!var6) {
               Block var64 = var1.getBlockState(var86).getBlock();
               if (var64 instanceof HalfTransparentBlock || var64 instanceof LeavesBlock) {
                  var87 = this.waterOverlay;
               }
            }

            float var88 = var87.getU(0.0F);
            float var65 = var87.getU(0.5F);
            float var66 = var87.getV((1.0F - var51) * 0.5F);
            float var67 = var87.getV((1.0F - var52) * 0.5F);
            float var68 = var87.getV(0.5F);
            float var69 = var77.getAxis() == Direction.Axis.Z ? var32 : var33;
            float var70 = var31 * var69 * var9;
            float var71 = var31 * var69 * var10;
            float var72 = var31 * var69 * var11;
            this.vertex(var3, var79, var74 + (double)var51, var80, var70, var71, var72, var88, var66, var75);
            this.vertex(var3, var82, var74 + (double)var52, var83, var70, var71, var72, var65, var67, var75);
            this.vertex(var3, var82, var74 + (double)var47, var83, var70, var71, var72, var65, var68, var75);
            this.vertex(var3, var79, var74 + (double)var47, var80, var70, var71, var72, var88, var68, var75);
            if (var87 != this.waterOverlay) {
               this.vertex(var3, var79, var74 + (double)var47, var80, var70, var71, var72, var88, var68, var75);
               this.vertex(var3, var82, var74 + (double)var47, var83, var70, var71, var72, var65, var68, var75);
               this.vertex(var3, var82, var74 + (double)var52, var83, var70, var71, var72, var65, var67, var75);
               this.vertex(var3, var79, var74 + (double)var51, var80, var70, var71, var72, var88, var66, var75);
            }
         }
      }
   }

   private float calculateAverageHeight(BlockAndTintGetter var1, Fluid var2, float var3, float var4, float var5, BlockPos var6) {
      if (!(var5 >= 1.0F) && !(var4 >= 1.0F)) {
         float[] var7 = new float[2];
         if (var5 > 0.0F || var4 > 0.0F) {
            float var8 = this.getHeight(var1, var2, var6);
            if (var8 >= 1.0F) {
               return 1.0F;
            }

            this.addWeightedHeight(var7, var8);
         }

         this.addWeightedHeight(var7, var3);
         this.addWeightedHeight(var7, var5);
         this.addWeightedHeight(var7, var4);
         return var7[0] / var7[1];
      } else {
         return 1.0F;
      }
   }

   private void addWeightedHeight(float[] var1, float var2) {
      if (var2 >= 0.8F) {
         var1[0] += var2 * 10.0F;
         var1[1] += 10.0F;
      } else if (var2 >= 0.0F) {
         var1[0] += var2;
         int var10002 = var1[1]++;
      }

   }

   private float getHeight(BlockAndTintGetter var1, Fluid var2, BlockPos var3) {
      BlockState var4 = var1.getBlockState(var3);
      return this.getHeight(var1, var2, var3, var4, var4.getFluidState());
   }

   private float getHeight(BlockAndTintGetter var1, Fluid var2, BlockPos var3, BlockState var4, FluidState var5) {
      if (var2.isSame(var5.getType())) {
         BlockState var6 = var1.getBlockState(var3.above());
         return var2.isSame(var6.getFluidState().getType()) ? 1.0F : var5.getOwnHeight();
      } else {
         return !var4.isSolid() ? 0.0F : -1.0F;
      }
   }

   private void vertex(VertexConsumer var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, float var12, int var13) {
      var1.vertex(var2, var4, var6).color(var8, var9, var10, 1.0F).uv(var11, var12).uv2(var13).normal(0.0F, 1.0F, 0.0F).endVertex();
   }

   private int getLightColor(BlockAndTintGetter var1, BlockPos var2) {
      int var3 = LevelRenderer.getLightColor(var1, var2);
      int var4 = LevelRenderer.getLightColor(var1, var2.above());
      int var5 = var3 & 255;
      int var6 = var4 & 255;
      int var7 = var3 >> 16 & 255;
      int var8 = var4 >> 16 & 255;
      return (var5 > var6 ? var5 : var6) | (var7 > var8 ? var7 : var8) << 16;
   }
}
