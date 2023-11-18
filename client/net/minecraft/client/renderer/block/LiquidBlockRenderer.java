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

         double var73 = (double)(var2.getX() & 15);
         double var74 = (double)(var2.getY() & 15);
         double var44 = (double)(var2.getZ() & 15);
         float var46 = 0.001F;
         float var47 = var25 ? 0.001F : 0.0F;
         if (var24 && !isFaceOccludedByNeighbor(var1, var2, Direction.UP, Math.min(Math.min(var36, var38), Math.min(var37, var35)), var14)) {
            var36 -= 0.001F;
            var38 -= 0.001F;
            var37 -= 0.001F;
            var35 -= 0.001F;
            Vec3 var56 = var5.getFlow(var1, var2);
            float var48;
            float var49;
            float var50;
            float var51;
            float var52;
            float var53;
            float var54;
            float var55;
            if (var56.x == 0.0 && var56.z == 0.0) {
               TextureAtlasSprite var98 = var7[0];
               var48 = var98.getU(0.0F);
               var52 = var98.getV(0.0F);
               var49 = var48;
               var53 = var98.getV(1.0F);
               var50 = var98.getU(1.0F);
               var54 = var53;
               var51 = var50;
               var55 = var52;
            } else {
               TextureAtlasSprite var57 = var7[1];
               float var58 = (float)Mth.atan2(var56.z, var56.x) - 1.5707964F;
               float var59 = Mth.sin(var58) * 0.25F;
               float var60 = Mth.cos(var58) * 0.25F;
               float var61 = 0.5F;
               var48 = var57.getU(0.5F + (-var60 - var59));
               var52 = var57.getV(0.5F + -var60 + var59);
               var49 = var57.getU(0.5F + -var60 + var59);
               var53 = var57.getV(0.5F + var60 + var59);
               var50 = var57.getU(0.5F + var60 + var59);
               var54 = var57.getV(0.5F + (var60 - var59));
               var51 = var57.getU(0.5F + (var60 - var59));
               var55 = var57.getV(0.5F + (-var60 - var59));
            }

            float var99 = (var48 + var49 + var50 + var51) / 4.0F;
            float var101 = (var52 + var53 + var54 + var55) / 4.0F;
            float var102 = var7[0].uvShrinkRatio();
            var48 = Mth.lerp(var102, var48, var99);
            var49 = Mth.lerp(var102, var49, var99);
            var50 = Mth.lerp(var102, var50, var99);
            var51 = Mth.lerp(var102, var51, var99);
            var52 = Mth.lerp(var102, var52, var101);
            var53 = Mth.lerp(var102, var53, var101);
            var54 = Mth.lerp(var102, var54, var101);
            var55 = Mth.lerp(var102, var55, var101);
            int var104 = this.getLightColor(var1, var2);
            float var105 = var31 * var9;
            float var62 = var31 * var10;
            float var63 = var31 * var11;
            this.vertex(var3, var73 + 0.0, var74 + (double)var36, var44 + 0.0, var105, var62, var63, var48, var52, var104);
            this.vertex(var3, var73 + 0.0, var74 + (double)var38, var44 + 1.0, var105, var62, var63, var49, var53, var104);
            this.vertex(var3, var73 + 1.0, var74 + (double)var37, var44 + 1.0, var105, var62, var63, var50, var54, var104);
            this.vertex(var3, var73 + 1.0, var74 + (double)var35, var44 + 0.0, var105, var62, var63, var51, var55, var104);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var73 + 0.0, var74 + (double)var36, var44 + 0.0, var105, var62, var63, var48, var52, var104);
               this.vertex(var3, var73 + 1.0, var74 + (double)var35, var44 + 0.0, var105, var62, var63, var51, var55, var104);
               this.vertex(var3, var73 + 1.0, var74 + (double)var37, var44 + 1.0, var105, var62, var63, var50, var54, var104);
               this.vertex(var3, var73 + 0.0, var74 + (double)var38, var44 + 1.0, var105, var62, var63, var49, var53, var104);
            }
         }

         if (var25) {
            float var76 = var7[0].getU0();
            float var79 = var7[0].getU1();
            float var82 = var7[0].getV0();
            float var85 = var7[0].getV1();
            int var88 = this.getLightColor(var1, var2.below());
            float var91 = var30 * var9;
            float var94 = var30 * var10;
            float var96 = var30 * var11;
            this.vertex(var3, var73, var74 + (double)var47, var44 + 1.0, var91, var94, var96, var76, var85, var88);
            this.vertex(var3, var73, var74 + (double)var47, var44, var91, var94, var96, var76, var82, var88);
            this.vertex(var3, var73 + 1.0, var74 + (double)var47, var44, var91, var94, var96, var79, var82, var88);
            this.vertex(var3, var73 + 1.0, var74 + (double)var47, var44 + 1.0, var91, var94, var96, var79, var85, var88);
         }

         int var77 = this.getLightColor(var1, var2);

         for(Direction var83 : Direction.Plane.HORIZONTAL) {
            float var86;
            float var89;
            double var92;
            double var97;
            double var100;
            double var103;
            boolean var106;
            switch(var83) {
               case NORTH:
                  var86 = var36;
                  var89 = var35;
                  var92 = var73;
                  var100 = var73 + 1.0;
                  var97 = var44 + 0.0010000000474974513;
                  var103 = var44 + 0.0010000000474974513;
                  var106 = var26;
                  break;
               case SOUTH:
                  var86 = var37;
                  var89 = var38;
                  var92 = var73 + 1.0;
                  var100 = var73;
                  var97 = var44 + 1.0 - 0.0010000000474974513;
                  var103 = var44 + 1.0 - 0.0010000000474974513;
                  var106 = var27;
                  break;
               case WEST:
                  var86 = var38;
                  var89 = var36;
                  var92 = var73 + 0.0010000000474974513;
                  var100 = var73 + 0.0010000000474974513;
                  var97 = var44 + 1.0;
                  var103 = var44;
                  var106 = var28;
                  break;
               default:
                  var86 = var35;
                  var89 = var37;
                  var92 = var73 + 1.0 - 0.0010000000474974513;
                  var100 = var73 + 1.0 - 0.0010000000474974513;
                  var97 = var44;
                  var103 = var44 + 1.0;
                  var106 = var29;
            }

            if (var106 && !isFaceOccludedByNeighbor(var1, var2, var83, Math.max(var86, var89), var1.getBlockState(var2.relative(var83)))) {
               BlockPos var107 = var2.relative(var83);
               TextureAtlasSprite var108 = var7[1];
               if (!var6) {
                  Block var64 = var1.getBlockState(var107).getBlock();
                  if (var64 instanceof HalfTransparentBlock || var64 instanceof LeavesBlock) {
                     var108 = this.waterOverlay;
                  }
               }

               float var109 = var108.getU(0.0F);
               float var65 = var108.getU(0.5F);
               float var66 = var108.getV((1.0F - var86) * 0.5F);
               float var67 = var108.getV((1.0F - var89) * 0.5F);
               float var68 = var108.getV(0.5F);
               float var69 = var83.getAxis() == Direction.Axis.Z ? var32 : var33;
               float var70 = var31 * var69 * var9;
               float var71 = var31 * var69 * var10;
               float var72 = var31 * var69 * var11;
               this.vertex(var3, var92, var74 + (double)var86, var97, var70, var71, var72, var109, var66, var77);
               this.vertex(var3, var100, var74 + (double)var89, var103, var70, var71, var72, var65, var67, var77);
               this.vertex(var3, var100, var74 + (double)var47, var103, var70, var71, var72, var65, var68, var77);
               this.vertex(var3, var92, var74 + (double)var47, var97, var70, var71, var72, var109, var68, var77);
               if (var108 != this.waterOverlay) {
                  this.vertex(var3, var92, var74 + (double)var47, var97, var70, var71, var72, var109, var68, var77);
                  this.vertex(var3, var100, var74 + (double)var47, var103, var70, var71, var72, var65, var68, var77);
                  this.vertex(var3, var100, var74 + (double)var89, var103, var70, var71, var72, var65, var67, var77);
                  this.vertex(var3, var92, var74 + (double)var86, var97, var70, var71, var72, var109, var66, var77);
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

   private void vertex(VertexConsumer var1, double var2, double var4, double var6, float var8, float var9, float var10, float var11, float var12, int var13) {
      var1.vertex(var2, var4, var6).color(var8, var9, var10, 1.0F).uv(var11, var12).uv2(var13).normal(0.0F, 1.0F, 0.0F).endVertex();
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
