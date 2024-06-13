package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
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
      float var9 = (float)(var8 >> 16 & 0xFF) / 255.0F;
      float var10 = (float)(var8 >> 8 & 0xFF) / 255.0F;
      float var11 = (float)(var8 & 0xFF) / 255.0F;
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
      boolean var25 = shouldRenderFace(var1, var2, var5, var4, Direction.DOWN, var13)
         && !isFaceOccludedByNeighbor(var1, var2, Direction.DOWN, 0.8888889F, var12);
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

         float var66 = (float)(var2.getX() & 15);
         float var67 = (float)(var2.getY() & 15);
         float var68 = (float)(var2.getZ() & 15);
         float var69 = 0.001F;
         float var44 = var25 ? 0.001F : 0.0F;
         if (var24 && !isFaceOccludedByNeighbor(var1, var2, Direction.UP, Math.min(Math.min(var36, var38), Math.min(var37, var35)), var14)) {
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
               TextureAtlasSprite var95 = var7[0];
               var45 = var95.getU(0.0F);
               var49 = var95.getV(0.0F);
               var46 = var45;
               var50 = var95.getV(1.0F);
               var47 = var95.getU(1.0F);
               var51 = var50;
               var48 = var47;
               var52 = var49;
            } else {
               TextureAtlasSprite var54 = var7[1];
               float var55 = (float)Mth.atan2(var53.z, var53.x) - 1.5707964F;
               float var56 = Mth.sin(var55) * 0.25F;
               float var57 = Mth.cos(var55) * 0.25F;
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

            float var96 = (var45 + var46 + var47 + var48) / 4.0F;
            float var98 = (var49 + var50 + var51 + var52) / 4.0F;
            float var100 = var7[0].uvShrinkRatio();
            var45 = Mth.lerp(var100, var45, var96);
            var46 = Mth.lerp(var100, var46, var96);
            var47 = Mth.lerp(var100, var47, var96);
            var48 = Mth.lerp(var100, var48, var96);
            var49 = Mth.lerp(var100, var49, var98);
            var50 = Mth.lerp(var100, var50, var98);
            var51 = Mth.lerp(var100, var51, var98);
            var52 = Mth.lerp(var100, var52, var98);
            int var102 = this.getLightColor(var1, var2);
            float var105 = var31 * var9;
            float var59 = var31 * var10;
            float var60 = var31 * var11;
            this.vertex(var3, var66 + 0.0F, var67 + var36, var68 + 0.0F, var105, var59, var60, var45, var49, var102);
            this.vertex(var3, var66 + 0.0F, var67 + var38, var68 + 1.0F, var105, var59, var60, var46, var50, var102);
            this.vertex(var3, var66 + 1.0F, var67 + var37, var68 + 1.0F, var105, var59, var60, var47, var51, var102);
            this.vertex(var3, var66 + 1.0F, var67 + var35, var68 + 0.0F, var105, var59, var60, var48, var52, var102);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var66 + 0.0F, var67 + var36, var68 + 0.0F, var105, var59, var60, var45, var49, var102);
               this.vertex(var3, var66 + 1.0F, var67 + var35, var68 + 0.0F, var105, var59, var60, var48, var52, var102);
               this.vertex(var3, var66 + 1.0F, var67 + var37, var68 + 1.0F, var105, var59, var60, var47, var51, var102);
               this.vertex(var3, var66 + 0.0F, var67 + var38, var68 + 1.0F, var105, var59, var60, var46, var50, var102);
            }
         }

         if (var25) {
            float var71 = var7[0].getU0();
            float var74 = var7[0].getU1();
            float var77 = var7[0].getV0();
            float var80 = var7[0].getV1();
            int var83 = this.getLightColor(var1, var2.below());
            float var86 = var30 * var9;
            float var89 = var30 * var10;
            float var92 = var30 * var11;
            this.vertex(var3, var66, var67 + var44, var68 + 1.0F, var86, var89, var92, var71, var80, var83);
            this.vertex(var3, var66, var67 + var44, var68, var86, var89, var92, var71, var77, var83);
            this.vertex(var3, var66 + 1.0F, var67 + var44, var68, var86, var89, var92, var74, var77, var83);
            this.vertex(var3, var66 + 1.0F, var67 + var44, var68 + 1.0F, var86, var89, var92, var74, var80, var83);
         }

         int var72 = this.getLightColor(var1, var2);

         for (Direction var78 : Direction.Plane.HORIZONTAL) {
            float var81;
            float var84;
            float var87;
            float var90;
            float var93;
            float var94;
            boolean var97;
            switch (var78) {
               case NORTH:
                  var81 = var36;
                  var84 = var35;
                  var87 = var66;
                  var93 = var66 + 1.0F;
                  var90 = var68 + 0.001F;
                  var94 = var68 + 0.001F;
                  var97 = var26;
                  break;
               case SOUTH:
                  var81 = var37;
                  var84 = var38;
                  var87 = var66 + 1.0F;
                  var93 = var66;
                  var90 = var68 + 1.0F - 0.001F;
                  var94 = var68 + 1.0F - 0.001F;
                  var97 = var27;
                  break;
               case WEST:
                  var81 = var38;
                  var84 = var36;
                  var87 = var66 + 0.001F;
                  var93 = var66 + 0.001F;
                  var90 = var68 + 1.0F;
                  var94 = var68;
                  var97 = var28;
                  break;
               default:
                  var81 = var35;
                  var84 = var37;
                  var87 = var66 + 1.0F - 0.001F;
                  var93 = var66 + 1.0F - 0.001F;
                  var90 = var68;
                  var94 = var68 + 1.0F;
                  var97 = var29;
            }

            if (var97 && !isFaceOccludedByNeighbor(var1, var2, var78, Math.max(var81, var84), var1.getBlockState(var2.relative(var78)))) {
               BlockPos var99 = var2.relative(var78);
               TextureAtlasSprite var101 = var7[1];
               if (!var6) {
                  Block var103 = var1.getBlockState(var99).getBlock();
                  if (var103 instanceof HalfTransparentBlock || var103 instanceof LeavesBlock) {
                     var101 = this.waterOverlay;
                  }
               }

               float var104 = var101.getU(0.0F);
               float var106 = var101.getU(0.5F);
               float var107 = var101.getV((1.0F - var81) * 0.5F);
               float var108 = var101.getV((1.0F - var84) * 0.5F);
               float var61 = var101.getV(0.5F);
               float var62 = var78.getAxis() == Direction.Axis.Z ? var32 : var33;
               float var63 = var31 * var62 * var9;
               float var64 = var31 * var62 * var10;
               float var65 = var31 * var62 * var11;
               this.vertex(var3, var87, var67 + var81, var90, var63, var64, var65, var104, var107, var72);
               this.vertex(var3, var93, var67 + var84, var94, var63, var64, var65, var106, var108, var72);
               this.vertex(var3, var93, var67 + var44, var94, var63, var64, var65, var106, var61, var72);
               this.vertex(var3, var87, var67 + var44, var90, var63, var64, var65, var104, var61, var72);
               if (var101 != this.waterOverlay) {
                  this.vertex(var3, var87, var67 + var44, var90, var63, var64, var65, var104, var61, var72);
                  this.vertex(var3, var93, var67 + var44, var94, var63, var64, var65, var106, var61, var72);
                  this.vertex(var3, var93, var67 + var84, var94, var63, var64, var65, var106, var108, var72);
                  this.vertex(var3, var87, var67 + var81, var90, var63, var64, var65, var104, var107, var72);
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
         var1[1]++;
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
      int var5 = var3 & 0xFF;
      int var6 = var4 & 0xFF;
      int var7 = var3 >> 16 & 0xFF;
      int var8 = var4 >> 16 & 0xFF;
      return (var5 > var6 ? var5 : var6) | (var7 > var8 ? var7 : var8) << 16;
   }
}
