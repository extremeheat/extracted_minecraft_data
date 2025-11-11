package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
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
   private final TextureAtlasSprite lavaStill;
   private final TextureAtlasSprite lavaFlowing;
   private final TextureAtlasSprite waterStill;
   private final TextureAtlasSprite waterFlowing;
   private final TextureAtlasSprite waterOverlay;

   public LiquidBlockRenderer(MaterialSet var1) {
      super();
      this.lavaStill = var1.get(ModelBakery.LAVA_STILL);
      this.lavaFlowing = var1.get(ModelBakery.LAVA_FLOW);
      this.waterStill = var1.get(ModelBakery.WATER_STILL);
      this.waterFlowing = var1.get(ModelBakery.WATER_FLOW);
      this.waterOverlay = var1.get(ModelBakery.WATER_OVERLAY);
   }

   private static boolean isNeighborSameFluid(FluidState var0, FluidState var1) {
      return var1.getType().isSame(var0.getType());
   }

   private static boolean isFaceOccludedByState(Direction var0, float var1, BlockState var2) {
      VoxelShape var3 = var2.getFaceOcclusionShape(var0.getOpposite());
      if (var3 == Shapes.empty()) {
         return false;
      } else if (var3 == Shapes.block()) {
         boolean var5 = var1 == 1.0F;
         return var0 != Direction.UP || var5;
      } else {
         VoxelShape var4 = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)var1, 1.0);
         return Shapes.blockOccludes(var4, var3, var0);
      }
   }

   private static boolean isFaceOccludedByNeighbor(Direction var0, float var1, BlockState var2) {
      return isFaceOccludedByState(var0, var1, var2);
   }

   private static boolean isFaceOccludedBySelf(BlockState var0, Direction var1) {
      return isFaceOccludedByState(var1.getOpposite(), 1.0F, var0);
   }

   public static boolean shouldRenderFace(FluidState var0, BlockState var1, Direction var2, FluidState var3) {
      return !isFaceOccludedBySelf(var1, var2) && !isNeighborSameFluid(var0, var3);
   }

   public void tesselate(BlockAndTintGetter var1, BlockPos var2, VertexConsumer var3, BlockState var4, FluidState var5) {
      boolean var6 = var5.is(FluidTags.LAVA);
      TextureAtlasSprite var7 = var6 ? this.lavaStill : this.waterStill;
      TextureAtlasSprite var8 = var6 ? this.lavaFlowing : this.waterFlowing;
      int var9 = var6 ? 16777215 : BiomeColors.getAverageWaterColor(var1, var2);
      float var10 = (float)(var9 >> 16 & 255) / 255.0F;
      float var11 = (float)(var9 >> 8 & 255) / 255.0F;
      float var12 = (float)(var9 & 255) / 255.0F;
      BlockState var13 = var1.getBlockState(var2.relative(Direction.DOWN));
      FluidState var14 = var13.getFluidState();
      BlockState var15 = var1.getBlockState(var2.relative(Direction.UP));
      FluidState var16 = var15.getFluidState();
      BlockState var17 = var1.getBlockState(var2.relative(Direction.NORTH));
      FluidState var18 = var17.getFluidState();
      BlockState var19 = var1.getBlockState(var2.relative(Direction.SOUTH));
      FluidState var20 = var19.getFluidState();
      BlockState var21 = var1.getBlockState(var2.relative(Direction.WEST));
      FluidState var22 = var21.getFluidState();
      BlockState var23 = var1.getBlockState(var2.relative(Direction.EAST));
      FluidState var24 = var23.getFluidState();
      boolean var25 = !isNeighborSameFluid(var5, var16);
      boolean var26 = shouldRenderFace(var5, var4, Direction.DOWN, var14) && !isFaceOccludedByNeighbor(Direction.DOWN, 0.8888889F, var13);
      boolean var27 = shouldRenderFace(var5, var4, Direction.NORTH, var18);
      boolean var28 = shouldRenderFace(var5, var4, Direction.SOUTH, var20);
      boolean var29 = shouldRenderFace(var5, var4, Direction.WEST, var22);
      boolean var30 = shouldRenderFace(var5, var4, Direction.EAST, var24);
      if (var25 || var26 || var30 || var29 || var27 || var28) {
         float var31 = var1.getShade(Direction.DOWN, true);
         float var32 = var1.getShade(Direction.UP, true);
         float var33 = var1.getShade(Direction.NORTH, true);
         float var34 = var1.getShade(Direction.WEST, true);
         Fluid var35 = var5.getType();
         float var40 = this.getHeight(var1, var35, var2, var4, var5);
         float var36;
         float var37;
         float var38;
         float var39;
         if (var40 >= 1.0F) {
            var36 = 1.0F;
            var37 = 1.0F;
            var38 = 1.0F;
            var39 = 1.0F;
         } else {
            float var41 = this.getHeight(var1, var35, var2.north(), var17, var18);
            float var42 = this.getHeight(var1, var35, var2.south(), var19, var20);
            float var43 = this.getHeight(var1, var35, var2.east(), var23, var24);
            float var44 = this.getHeight(var1, var35, var2.west(), var21, var22);
            var36 = this.calculateAverageHeight(var1, var35, var40, var41, var43, var2.relative(Direction.NORTH).relative(Direction.EAST));
            var37 = this.calculateAverageHeight(var1, var35, var40, var41, var44, var2.relative(Direction.NORTH).relative(Direction.WEST));
            var38 = this.calculateAverageHeight(var1, var35, var40, var42, var43, var2.relative(Direction.SOUTH).relative(Direction.EAST));
            var39 = this.calculateAverageHeight(var1, var35, var40, var42, var44, var2.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         float var67 = (float)(var2.getX() & 15);
         float var68 = (float)(var2.getY() & 15);
         float var69 = (float)(var2.getZ() & 15);
         float var70 = 0.001F;
         float var45 = var26 ? 0.001F : 0.0F;
         if (var25 && !isFaceOccludedByNeighbor(Direction.UP, Math.min(Math.min(var37, var39), Math.min(var38, var36)), var15)) {
            var37 -= 0.001F;
            var39 -= 0.001F;
            var38 -= 0.001F;
            var36 -= 0.001F;
            Vec3 var54 = var5.getFlow(var1, var2);
            float var46;
            float var47;
            float var48;
            float var49;
            float var50;
            float var51;
            float var52;
            float var53;
            if (var54.x == 0.0 && var54.z == 0.0) {
               var46 = var7.getU(0.0F);
               var50 = var7.getV(0.0F);
               var47 = var46;
               var51 = var7.getV(1.0F);
               var48 = var7.getU(1.0F);
               var52 = var51;
               var49 = var48;
               var53 = var50;
            } else {
               float var55 = (float)Mth.atan2(var54.z, var54.x) - 1.5707964F;
               float var56 = Mth.sin((double)var55) * 0.25F;
               float var57 = Mth.cos((double)var55) * 0.25F;
               float var58 = 0.5F;
               var46 = var8.getU(0.5F + (-var57 - var56));
               var50 = var8.getV(0.5F + -var57 + var56);
               var47 = var8.getU(0.5F + -var57 + var56);
               var51 = var8.getV(0.5F + var57 + var56);
               var48 = var8.getU(0.5F + var57 + var56);
               var52 = var8.getV(0.5F + (var57 - var56));
               var49 = var8.getU(0.5F + (var57 - var56));
               var53 = var8.getV(0.5F + (-var57 - var56));
            }

            int var88 = this.getLightColor(var1, var2);
            float var90 = var32 * var10;
            float var92 = var32 * var11;
            float var94 = var32 * var12;
            this.vertex(var3, var67 + 0.0F, var68 + var37, var69 + 0.0F, var90, var92, var94, var46, var50, var88);
            this.vertex(var3, var67 + 0.0F, var68 + var39, var69 + 1.0F, var90, var92, var94, var47, var51, var88);
            this.vertex(var3, var67 + 1.0F, var68 + var38, var69 + 1.0F, var90, var92, var94, var48, var52, var88);
            this.vertex(var3, var67 + 1.0F, var68 + var36, var69 + 0.0F, var90, var92, var94, var49, var53, var88);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var67 + 0.0F, var68 + var37, var69 + 0.0F, var90, var92, var94, var46, var50, var88);
               this.vertex(var3, var67 + 1.0F, var68 + var36, var69 + 0.0F, var90, var92, var94, var49, var53, var88);
               this.vertex(var3, var67 + 1.0F, var68 + var38, var69 + 1.0F, var90, var92, var94, var48, var52, var88);
               this.vertex(var3, var67 + 0.0F, var68 + var39, var69 + 1.0F, var90, var92, var94, var47, var51, var88);
            }
         }

         if (var26) {
            float var71 = var7.getU0();
            float var73 = var7.getU1();
            float var75 = var7.getV0();
            float var77 = var7.getV1();
            int var79 = this.getLightColor(var1, var2.below());
            float var81 = var31 * var10;
            float var83 = var31 * var11;
            float var85 = var31 * var12;
            this.vertex(var3, var67, var68 + var45, var69 + 1.0F, var81, var83, var85, var71, var77, var79);
            this.vertex(var3, var67, var68 + var45, var69, var81, var83, var85, var71, var75, var79);
            this.vertex(var3, var67 + 1.0F, var68 + var45, var69, var81, var83, var85, var73, var75, var79);
            this.vertex(var3, var67 + 1.0F, var68 + var45, var69 + 1.0F, var81, var83, var85, var73, var77, var79);
         }

         int var72 = this.getLightColor(var1, var2);

         for(Direction var76 : Direction.Plane.HORIZONTAL) {
            float var78;
            float var80;
            float var82;
            float var84;
            float var86;
            float var87;
            boolean var89;
            switch (var76) {
               case NORTH:
                  var78 = var37;
                  var80 = var36;
                  var82 = var67;
                  var86 = var67 + 1.0F;
                  var84 = var69 + 0.001F;
                  var87 = var69 + 0.001F;
                  var89 = var27;
                  break;
               case SOUTH:
                  var78 = var38;
                  var80 = var39;
                  var82 = var67 + 1.0F;
                  var86 = var67;
                  var84 = var69 + 1.0F - 0.001F;
                  var87 = var69 + 1.0F - 0.001F;
                  var89 = var28;
                  break;
               case WEST:
                  var78 = var39;
                  var80 = var37;
                  var82 = var67 + 0.001F;
                  var86 = var67 + 0.001F;
                  var84 = var69 + 1.0F;
                  var87 = var69;
                  var89 = var29;
                  break;
               default:
                  var78 = var36;
                  var80 = var38;
                  var82 = var67 + 1.0F - 0.001F;
                  var86 = var67 + 1.0F - 0.001F;
                  var84 = var69;
                  var87 = var69 + 1.0F;
                  var89 = var30;
            }

            if (var89 && !isFaceOccludedByNeighbor(var76, Math.max(var78, var80), var1.getBlockState(var2.relative(var76)))) {
               BlockPos var91 = var2.relative(var76);
               TextureAtlasSprite var93 = var8;
               if (!var6) {
                  Block var95 = var1.getBlockState(var91).getBlock();
                  if (var95 instanceof HalfTransparentBlock || var95 instanceof LeavesBlock) {
                     var93 = this.waterOverlay;
                  }
               }

               float var96 = var93.getU(0.0F);
               float var59 = var93.getU(0.5F);
               float var60 = var93.getV((1.0F - var78) * 0.5F);
               float var61 = var93.getV((1.0F - var80) * 0.5F);
               float var62 = var93.getV(0.5F);
               float var63 = var76.getAxis() == Direction.Axis.Z ? var33 : var34;
               float var64 = var32 * var63 * var10;
               float var65 = var32 * var63 * var11;
               float var66 = var32 * var63 * var12;
               this.vertex(var3, var82, var68 + var78, var84, var64, var65, var66, var96, var60, var72);
               this.vertex(var3, var86, var68 + var80, var87, var64, var65, var66, var59, var61, var72);
               this.vertex(var3, var86, var68 + var45, var87, var64, var65, var66, var59, var62, var72);
               this.vertex(var3, var82, var68 + var45, var84, var64, var65, var66, var96, var62, var72);
               if (var93 != this.waterOverlay) {
                  this.vertex(var3, var82, var68 + var45, var84, var64, var65, var66, var96, var62, var72);
                  this.vertex(var3, var86, var68 + var45, var87, var64, var65, var66, var59, var62, var72);
                  this.vertex(var3, var86, var68 + var80, var87, var64, var65, var66, var59, var61, var72);
                  this.vertex(var3, var82, var68 + var78, var84, var64, var65, var66, var96, var60, var72);
               }
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

   private void vertex(VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      var1.addVertex(var2, var3, var4).setColor(var5, var6, var7, 1.0F).setUv(var8, var9).setLight(var10).setNormal(0.0F, 1.0F, 0.0F);
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
