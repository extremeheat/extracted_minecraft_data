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

   public void tesselate(BlockAndTintGetter var1, BlockPos var2, VertexConsumer var3, BlockState var4, FluidState var5, double var6, double var8, double var10) {
      boolean var12 = var5.is(FluidTags.LAVA);
      TextureAtlasSprite[] var13 = var12 ? this.lavaIcons : this.waterIcons;
      int var14 = var12 ? 16777215 : BiomeColors.getAverageWaterColor(var1, var2);
      float var15 = (float)(var14 >> 16 & 0xFF) / 255.0F;
      float var16 = (float)(var14 >> 8 & 0xFF) / 255.0F;
      float var17 = (float)(var14 & 0xFF) / 255.0F;
      BlockState var18 = var1.getBlockState(var2.relative(Direction.DOWN));
      FluidState var19 = var18.getFluidState();
      BlockState var20 = var1.getBlockState(var2.relative(Direction.UP));
      FluidState var21 = var20.getFluidState();
      BlockState var22 = var1.getBlockState(var2.relative(Direction.NORTH));
      FluidState var23 = var22.getFluidState();
      BlockState var24 = var1.getBlockState(var2.relative(Direction.SOUTH));
      FluidState var25 = var24.getFluidState();
      BlockState var26 = var1.getBlockState(var2.relative(Direction.WEST));
      FluidState var27 = var26.getFluidState();
      BlockState var28 = var1.getBlockState(var2.relative(Direction.EAST));
      FluidState var29 = var28.getFluidState();
      boolean var30 = !isNeighborSameFluid(var5, var21);
      boolean var31 = shouldRenderFace(var1, var2, var5, var4, Direction.DOWN, var19)
         && !isFaceOccludedByNeighbor(var1, var2, Direction.DOWN, 0.8888889F, var18);
      boolean var32 = shouldRenderFace(var1, var2, var5, var4, Direction.NORTH, var23);
      boolean var33 = shouldRenderFace(var1, var2, var5, var4, Direction.SOUTH, var25);
      boolean var34 = shouldRenderFace(var1, var2, var5, var4, Direction.WEST, var27);
      boolean var35 = shouldRenderFace(var1, var2, var5, var4, Direction.EAST, var29);
      if (var30 || var31 || var35 || var34 || var32 || var33) {
         float var36 = var1.getShade(Direction.DOWN, true);
         float var37 = var1.getShade(Direction.UP, true);
         float var38 = var1.getShade(Direction.NORTH, true);
         float var39 = var1.getShade(Direction.WEST, true);
         Fluid var40 = var5.getType();
         float var45 = this.getHeight(var1, var40, var2, var4, var5);
         float var41;
         float var42;
         float var43;
         float var44;
         if (var45 >= 1.0F) {
            var41 = 1.0F;
            var42 = 1.0F;
            var43 = 1.0F;
            var44 = 1.0F;
         } else {
            float var46 = this.getHeight(var1, var40, var2.north(), var22, var23);
            float var47 = this.getHeight(var1, var40, var2.south(), var24, var25);
            float var48 = this.getHeight(var1, var40, var2.east(), var28, var29);
            float var49 = this.getHeight(var1, var40, var2.west(), var26, var27);
            var41 = this.calculateAverageHeight(var1, var40, var45, var46, var48, var2.relative(Direction.NORTH).relative(Direction.EAST));
            var42 = this.calculateAverageHeight(var1, var40, var45, var46, var49, var2.relative(Direction.NORTH).relative(Direction.WEST));
            var43 = this.calculateAverageHeight(var1, var40, var45, var47, var48, var2.relative(Direction.SOUTH).relative(Direction.EAST));
            var44 = this.calculateAverageHeight(var1, var40, var45, var47, var49, var2.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         float var73 = 0.001F;
         float var74 = var31 ? 0.001F : 0.0F;
         if (var30 && !isFaceOccludedByNeighbor(var1, var2, Direction.UP, Math.min(Math.min(var42, var44), Math.min(var43, var41)), var20)) {
            var42 -= 0.001F;
            var44 -= 0.001F;
            var43 -= 0.001F;
            var41 -= 0.001F;
            Vec3 var56 = var5.getFlow(var1, var2);
            float var50;
            float var51;
            float var52;
            float var53;
            float var54;
            float var55;
            float var75;
            float var79;
            if (var56.x == 0.0 && var56.z == 0.0) {
               TextureAtlasSprite var100 = var13[0];
               var75 = var100.getU(0.0F);
               var52 = var100.getV(0.0F);
               var79 = var75;
               var53 = var100.getV(1.0F);
               var50 = var100.getU(1.0F);
               var54 = var53;
               var51 = var50;
               var55 = var52;
            } else {
               TextureAtlasSprite var57 = var13[1];
               float var58 = (float)Mth.atan2(var56.z, var56.x) - 1.5707964F;
               float var59 = Mth.sin(var58) * 0.25F;
               float var60 = Mth.cos(var58) * 0.25F;
               float var61 = 0.5F;
               var75 = var57.getU(0.5F + (-var60 - var59));
               var52 = var57.getV(0.5F + -var60 + var59);
               var79 = var57.getU(0.5F + -var60 + var59);
               var53 = var57.getV(0.5F + var60 + var59);
               var50 = var57.getU(0.5F + var60 + var59);
               var54 = var57.getV(0.5F + (var60 - var59));
               var51 = var57.getU(0.5F + (var60 - var59));
               var55 = var57.getV(0.5F + (-var60 - var59));
            }

            float var101 = (var75 + var79 + var50 + var51) / 4.0F;
            float var103 = (var52 + var53 + var54 + var55) / 4.0F;
            float var104 = var13[0].uvShrinkRatio();
            var75 = Mth.lerp(var104, var75, var101);
            var79 = Mth.lerp(var104, var79, var101);
            var50 = Mth.lerp(var104, var50, var101);
            var51 = Mth.lerp(var104, var51, var101);
            var52 = Mth.lerp(var104, var52, var103);
            var53 = Mth.lerp(var104, var53, var103);
            var54 = Mth.lerp(var104, var54, var103);
            var55 = Mth.lerp(var104, var55, var103);
            int var106 = this.getLightColor(var1, var2);
            float var107 = var37 * var15;
            float var62 = var37 * var16;
            float var63 = var37 * var17;
            this.vertex(var3, var6 + 0.0, var8 + (double)var42, var10 + 0.0, var107, var62, var63, var75, var52, var106);
            this.vertex(var3, var6 + 0.0, var8 + (double)var44, var10 + 1.0, var107, var62, var63, var79, var53, var106);
            this.vertex(var3, var6 + 1.0, var8 + (double)var43, var10 + 1.0, var107, var62, var63, var50, var54, var106);
            this.vertex(var3, var6 + 1.0, var8 + (double)var41, var10 + 0.0, var107, var62, var63, var51, var55, var106);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var6 + 0.0, var8 + (double)var42, var10 + 0.0, var107, var62, var63, var75, var52, var106);
               this.vertex(var3, var6 + 1.0, var8 + (double)var41, var10 + 0.0, var107, var62, var63, var51, var55, var106);
               this.vertex(var3, var6 + 1.0, var8 + (double)var43, var10 + 1.0, var107, var62, var63, var50, var54, var106);
               this.vertex(var3, var6 + 0.0, var8 + (double)var44, var10 + 1.0, var107, var62, var63, var79, var53, var106);
            }
         }

         if (var31) {
            float var77 = var13[0].getU0();
            float var81 = var13[0].getU1();
            float var84 = var13[0].getV0();
            float var87 = var13[0].getV1();
            int var90 = this.getLightColor(var1, var2.below());
            float var93 = var36 * var15;
            float var96 = var36 * var16;
            float var98 = var36 * var17;
            this.vertex(var3, var6, var8 + (double)var74, var10 + 1.0, var93, var96, var98, var77, var87, var90);
            this.vertex(var3, var6, var8 + (double)var74, var10, var93, var96, var98, var77, var84, var90);
            this.vertex(var3, var6 + 1.0, var8 + (double)var74, var10, var93, var96, var98, var81, var84, var90);
            this.vertex(var3, var6 + 1.0, var8 + (double)var74, var10 + 1.0, var93, var96, var98, var81, var87, var90);
         }

         int var78 = this.getLightColor(var1, var2);

         for(Direction var85 : Direction.Plane.HORIZONTAL) {
            float var88;
            float var91;
            double var94;
            double var99;
            double var102;
            double var105;
            boolean var108;
            switch(var85) {
               case NORTH:
                  var88 = var42;
                  var91 = var41;
                  var94 = var6;
                  var102 = var6 + 1.0;
                  var99 = var10 + 0.0010000000474974513;
                  var105 = var10 + 0.0010000000474974513;
                  var108 = var32;
                  break;
               case SOUTH:
                  var88 = var43;
                  var91 = var44;
                  var94 = var6 + 1.0;
                  var102 = var6;
                  var99 = var10 + 1.0 - 0.0010000000474974513;
                  var105 = var10 + 1.0 - 0.0010000000474974513;
                  var108 = var33;
                  break;
               case WEST:
                  var88 = var44;
                  var91 = var42;
                  var94 = var6 + 0.0010000000474974513;
                  var102 = var6 + 0.0010000000474974513;
                  var99 = var10 + 1.0;
                  var105 = var10;
                  var108 = var34;
                  break;
               default:
                  var88 = var41;
                  var91 = var43;
                  var94 = var6 + 1.0 - 0.0010000000474974513;
                  var102 = var6 + 1.0 - 0.0010000000474974513;
                  var99 = var10;
                  var105 = var10 + 1.0;
                  var108 = var35;
            }

            if (var108 && !isFaceOccludedByNeighbor(var1, var2, var85, Math.max(var88, var91), var1.getBlockState(var2.relative(var85)))) {
               BlockPos var109 = var2.relative(var85);
               TextureAtlasSprite var110 = var13[1];
               if (!var12) {
                  Block var64 = var1.getBlockState(var109).getBlock();
                  if (var64 instanceof HalfTransparentBlock || var64 instanceof LeavesBlock) {
                     var110 = this.waterOverlay;
                  }
               }

               float var111 = var110.getU(0.0F);
               float var65 = var110.getU(0.5F);
               float var66 = var110.getV((1.0F - var88) * 0.5F);
               float var67 = var110.getV((1.0F - var91) * 0.5F);
               float var68 = var110.getV(0.5F);
               float var69 = var85.getAxis() == Direction.Axis.Z ? var38 : var39;
               float var70 = var37 * var69 * var15;
               float var71 = var37 * var69 * var16;
               float var72 = var37 * var69 * var17;
               this.vertex(var3, var94, var8 + (double)var88, var99, var70, var71, var72, var111, var66, var78);
               this.vertex(var3, var102, var8 + (double)var91, var105, var70, var71, var72, var65, var67, var78);
               this.vertex(var3, var102, var8 + (double)var74, var105, var70, var71, var72, var65, var68, var78);
               this.vertex(var3, var94, var8 + (double)var74, var99, var70, var71, var72, var111, var68, var78);
               if (var110 != this.waterOverlay) {
                  this.vertex(var3, var94, var8 + (double)var74, var99, var70, var71, var72, var111, var68, var78);
                  this.vertex(var3, var102, var8 + (double)var74, var105, var70, var71, var72, var65, var68, var78);
                  this.vertex(var3, var102, var8 + (double)var91, var105, var70, var71, var72, var65, var67, var78);
                  this.vertex(var3, var94, var8 + (double)var88, var99, var70, var71, var72, var111, var66, var78);
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
