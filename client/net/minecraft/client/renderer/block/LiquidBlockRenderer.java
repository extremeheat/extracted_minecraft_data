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

   protected void setupSprites(BlockModelShaper var1, MaterialSet var2) {
      this.lavaIcons[0] = var1.getBlockModel(Blocks.LAVA.defaultBlockState()).particleIcon();
      this.lavaIcons[1] = var2.get(ModelBakery.LAVA_FLOW);
      this.waterIcons[0] = var1.getBlockModel(Blocks.WATER.defaultBlockState()).particleIcon();
      this.waterIcons[1] = var2.get(ModelBakery.WATER_FLOW);
      this.waterOverlay = var2.get(ModelBakery.WATER_OVERLAY);
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
      boolean var25 = shouldRenderFace(var5, var4, Direction.DOWN, var13) && !isFaceOccludedByNeighbor(Direction.DOWN, 0.8888889F, var12);
      boolean var26 = shouldRenderFace(var5, var4, Direction.NORTH, var17);
      boolean var27 = shouldRenderFace(var5, var4, Direction.SOUTH, var19);
      boolean var28 = shouldRenderFace(var5, var4, Direction.WEST, var21);
      boolean var29 = shouldRenderFace(var5, var4, Direction.EAST, var23);
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

         float var66 = (float)(var2.getX() & 15);
         float var67 = (float)(var2.getY() & 15);
         float var68 = (float)(var2.getZ() & 15);
         float var69 = 0.001F;
         float var44 = var25 ? 0.001F : 0.0F;
         if (var24 && !isFaceOccludedByNeighbor(Direction.UP, Math.min(Math.min(var36, var38), Math.min(var37, var35)), var14)) {
            var36 -= 0.001F;
            var38 -= 0.001F;
            var37 -= 0.001F;
            var35 -= 0.001F;
            Vec3 var53 = var5.getFlow(var1, var2);
            float var45;
            float var46;
            float var47;
            float var48;
            float var49;
            float var50;
            float var51;
            float var52;
            if (var53.x == 0.0 && var53.z == 0.0) {
               TextureAtlasSprite var87 = var7[0];
               var45 = var87.getU(0.0F);
               var49 = var87.getV(0.0F);
               var46 = var45;
               var50 = var87.getV(1.0F);
               var47 = var87.getU(1.0F);
               var51 = var50;
               var48 = var47;
               var52 = var49;
            } else {
               TextureAtlasSprite var54 = var7[1];
               float var55 = (float)Mth.atan2(var53.z, var53.x) - 1.5707964F;
               float var56 = Mth.sin((double)var55) * 0.25F;
               float var57 = Mth.cos((double)var55) * 0.25F;
               float var58 = 0.5F;
               var45 = var54.getU(0.5F + (-var57 - var56));
               var49 = var54.getV(0.5F + -var57 + var56);
               var46 = var54.getU(0.5F + -var57 + var56);
               var50 = var54.getV(0.5F + var57 + var56);
               var47 = var54.getU(0.5F + var57 + var56);
               var51 = var54.getV(0.5F + (var57 - var56));
               var48 = var54.getU(0.5F + (var57 - var56));
               var52 = var54.getV(0.5F + (-var57 - var56));
            }

            int var88 = this.getLightColor(var1, var2);
            float var90 = var31 * var9;
            float var92 = var31 * var10;
            float var94 = var31 * var11;
            this.vertex(var3, var66 + 0.0F, var67 + var36, var68 + 0.0F, var90, var92, var94, var45, var49, var88);
            this.vertex(var3, var66 + 0.0F, var67 + var38, var68 + 1.0F, var90, var92, var94, var46, var50, var88);
            this.vertex(var3, var66 + 1.0F, var67 + var37, var68 + 1.0F, var90, var92, var94, var47, var51, var88);
            this.vertex(var3, var66 + 1.0F, var67 + var35, var68 + 0.0F, var90, var92, var94, var48, var52, var88);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var66 + 0.0F, var67 + var36, var68 + 0.0F, var90, var92, var94, var45, var49, var88);
               this.vertex(var3, var66 + 1.0F, var67 + var35, var68 + 0.0F, var90, var92, var94, var48, var52, var88);
               this.vertex(var3, var66 + 1.0F, var67 + var37, var68 + 1.0F, var90, var92, var94, var47, var51, var88);
               this.vertex(var3, var66 + 0.0F, var67 + var38, var68 + 1.0F, var90, var92, var94, var46, var50, var88);
            }
         }

         if (var25) {
            float var70 = var7[0].getU0();
            float var72 = var7[0].getU1();
            float var74 = var7[0].getV0();
            float var76 = var7[0].getV1();
            int var78 = this.getLightColor(var1, var2.below());
            float var80 = var30 * var9;
            float var82 = var30 * var10;
            float var84 = var30 * var11;
            this.vertex(var3, var66, var67 + var44, var68 + 1.0F, var80, var82, var84, var70, var76, var78);
            this.vertex(var3, var66, var67 + var44, var68, var80, var82, var84, var70, var74, var78);
            this.vertex(var3, var66 + 1.0F, var67 + var44, var68, var80, var82, var84, var72, var74, var78);
            this.vertex(var3, var66 + 1.0F, var67 + var44, var68 + 1.0F, var80, var82, var84, var72, var76, var78);
         }

         int var71 = this.getLightColor(var1, var2);

         for(Direction var75 : Direction.Plane.HORIZONTAL) {
            float var77;
            float var79;
            float var81;
            float var83;
            float var85;
            float var86;
            boolean var89;
            switch (var75) {
               case NORTH:
                  var77 = var36;
                  var79 = var35;
                  var81 = var66;
                  var85 = var66 + 1.0F;
                  var83 = var68 + 0.001F;
                  var86 = var68 + 0.001F;
                  var89 = var26;
                  break;
               case SOUTH:
                  var77 = var37;
                  var79 = var38;
                  var81 = var66 + 1.0F;
                  var85 = var66;
                  var83 = var68 + 1.0F - 0.001F;
                  var86 = var68 + 1.0F - 0.001F;
                  var89 = var27;
                  break;
               case WEST:
                  var77 = var38;
                  var79 = var36;
                  var81 = var66 + 0.001F;
                  var85 = var66 + 0.001F;
                  var83 = var68 + 1.0F;
                  var86 = var68;
                  var89 = var28;
                  break;
               default:
                  var77 = var35;
                  var79 = var37;
                  var81 = var66 + 1.0F - 0.001F;
                  var85 = var66 + 1.0F - 0.001F;
                  var83 = var68;
                  var86 = var68 + 1.0F;
                  var89 = var29;
            }

            if (var89 && !isFaceOccludedByNeighbor(var75, Math.max(var77, var79), var1.getBlockState(var2.relative(var75)))) {
               BlockPos var91 = var2.relative(var75);
               TextureAtlasSprite var93 = var7[1];
               if (!var6) {
                  Block var95 = var1.getBlockState(var91).getBlock();
                  if (var95 instanceof HalfTransparentBlock || var95 instanceof LeavesBlock) {
                     var93 = this.waterOverlay;
                  }
               }

               float var96 = var93.getU(0.0F);
               float var97 = var93.getU(0.5F);
               float var59 = var93.getV((1.0F - var77) * 0.5F);
               float var60 = var93.getV((1.0F - var79) * 0.5F);
               float var61 = var93.getV(0.5F);
               float var62 = var75.getAxis() == Direction.Axis.Z ? var32 : var33;
               float var63 = var31 * var62 * var9;
               float var64 = var31 * var62 * var10;
               float var65 = var31 * var62 * var11;
               this.vertex(var3, var81, var67 + var77, var83, var63, var64, var65, var96, var59, var71);
               this.vertex(var3, var85, var67 + var79, var86, var63, var64, var65, var97, var60, var71);
               this.vertex(var3, var85, var67 + var44, var86, var63, var64, var65, var97, var61, var71);
               this.vertex(var3, var81, var67 + var44, var83, var63, var64, var65, var96, var61, var71);
               if (var93 != this.waterOverlay) {
                  this.vertex(var3, var81, var67 + var44, var83, var63, var64, var65, var96, var61, var71);
                  this.vertex(var3, var85, var67 + var44, var86, var63, var64, var65, var97, var61, var71);
                  this.vertex(var3, var85, var67 + var79, var86, var63, var64, var65, var97, var60, var71);
                  this.vertex(var3, var81, var67 + var77, var83, var63, var64, var65, var96, var59, var71);
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
