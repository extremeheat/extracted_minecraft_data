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
         float var40;
         float var41;
         float var42;
         float var43;
         if (var39 >= 1.0F) {
            var35 = 1.0F;
            var36 = 1.0F;
            var37 = 1.0F;
            var38 = 1.0F;
         } else {
            var40 = this.getHeight(var1, var34, var2.north(), var16, var17);
            var41 = this.getHeight(var1, var34, var2.south(), var18, var19);
            var42 = this.getHeight(var1, var34, var2.east(), var22, var23);
            var43 = this.getHeight(var1, var34, var2.west(), var20, var21);
            var35 = this.calculateAverageHeight(var1, var34, var39, var40, var42, var2.relative(Direction.NORTH).relative(Direction.EAST));
            var36 = this.calculateAverageHeight(var1, var34, var39, var40, var43, var2.relative(Direction.NORTH).relative(Direction.WEST));
            var37 = this.calculateAverageHeight(var1, var34, var39, var41, var42, var2.relative(Direction.SOUTH).relative(Direction.EAST));
            var38 = this.calculateAverageHeight(var1, var34, var39, var41, var43, var2.relative(Direction.SOUTH).relative(Direction.WEST));
         }

         var40 = (float)(var2.getX() & 15);
         var41 = (float)(var2.getY() & 15);
         var42 = (float)(var2.getZ() & 15);
         var43 = 0.001F;
         float var44 = var25 ? 0.001F : 0.0F;
         float var45;
         float var46;
         float var47;
         float var48;
         float var49;
         float var50;
         float var51;
         float var52;
         float var57;
         float var58;
         float var59;
         float var60;
         if (var24 && !isFaceOccludedByNeighbor(var1, var2, Direction.UP, Math.min(Math.min(var36, var38), Math.min(var37, var35)), var14)) {
            var36 -= 0.001F;
            var38 -= 0.001F;
            var37 -= 0.001F;
            var35 -= 0.001F;
            Vec3 var53 = var5.getFlow(var1, var2);
            TextureAtlasSprite var54;
            float var55;
            float var56;
            if (var53.x == 0.0 && var53.z == 0.0) {
               var54 = var7[0];
               var45 = var54.getU(0.0F);
               var49 = var54.getV(0.0F);
               var46 = var45;
               var50 = var54.getV(1.0F);
               var47 = var54.getU(1.0F);
               var51 = var50;
               var48 = var47;
               var52 = var49;
            } else {
               var54 = var7[1];
               var55 = (float)Mth.atan2(var53.z, var53.x) - 1.5707964F;
               var56 = Mth.sin(var55) * 0.25F;
               var57 = Mth.cos(var55) * 0.25F;
               var58 = 0.5F;
               var45 = var54.getU(0.5F + (-var57 - var56));
               var49 = var54.getV(0.5F + -var57 + var56);
               var46 = var54.getU(0.5F + -var57 + var56);
               var50 = var54.getV(0.5F + var57 + var56);
               var47 = var54.getU(0.5F + var57 + var56);
               var51 = var54.getV(0.5F + (var57 - var56));
               var48 = var54.getU(0.5F + (var57 - var56));
               var52 = var54.getV(0.5F + (-var57 - var56));
            }

            float var71 = (var45 + var46 + var47 + var48) / 4.0F;
            var55 = (var49 + var50 + var51 + var52) / 4.0F;
            var56 = var7[0].uvShrinkRatio();
            var45 = Mth.lerp(var56, var45, var71);
            var46 = Mth.lerp(var56, var46, var71);
            var47 = Mth.lerp(var56, var47, var71);
            var48 = Mth.lerp(var56, var48, var71);
            var49 = Mth.lerp(var56, var49, var55);
            var50 = Mth.lerp(var56, var50, var55);
            var51 = Mth.lerp(var56, var51, var55);
            var52 = Mth.lerp(var56, var52, var55);
            int var75 = this.getLightColor(var1, var2);
            var58 = var31 * var9;
            var59 = var31 * var10;
            var60 = var31 * var11;
            this.vertex(var3, var40 + 0.0F, var41 + var36, var42 + 0.0F, var58, var59, var60, var45, var49, var75);
            this.vertex(var3, var40 + 0.0F, var41 + var38, var42 + 1.0F, var58, var59, var60, var46, var50, var75);
            this.vertex(var3, var40 + 1.0F, var41 + var37, var42 + 1.0F, var58, var59, var60, var47, var51, var75);
            this.vertex(var3, var40 + 1.0F, var41 + var35, var42 + 0.0F, var58, var59, var60, var48, var52, var75);
            if (var5.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var40 + 0.0F, var41 + var36, var42 + 0.0F, var58, var59, var60, var45, var49, var75);
               this.vertex(var3, var40 + 1.0F, var41 + var35, var42 + 0.0F, var58, var59, var60, var48, var52, var75);
               this.vertex(var3, var40 + 1.0F, var41 + var37, var42 + 1.0F, var58, var59, var60, var47, var51, var75);
               this.vertex(var3, var40 + 0.0F, var41 + var38, var42 + 1.0F, var58, var59, var60, var46, var50, var75);
            }
         }

         if (var25) {
            var45 = var7[0].getU0();
            var46 = var7[0].getU1();
            var47 = var7[0].getV0();
            var48 = var7[0].getV1();
            int var69 = this.getLightColor(var1, var2.below());
            var50 = var30 * var9;
            var51 = var30 * var10;
            var52 = var30 * var11;
            this.vertex(var3, var40, var41 + var44, var42 + 1.0F, var50, var51, var52, var45, var48, var69);
            this.vertex(var3, var40, var41 + var44, var42, var50, var51, var52, var45, var47, var69);
            this.vertex(var3, var40 + 1.0F, var41 + var44, var42, var50, var51, var52, var46, var47, var69);
            this.vertex(var3, var40 + 1.0F, var41 + var44, var42 + 1.0F, var50, var51, var52, var46, var48, var69);
         }

         int var66 = this.getLightColor(var1, var2);
         Iterator var67 = Direction.Plane.HORIZONTAL.iterator();

         while(true) {
            Direction var68;
            float var70;
            boolean var72;
            do {
               do {
                  if (!var67.hasNext()) {
                     return;
                  }

                  var68 = (Direction)var67.next();
                  switch (var68) {
                     case NORTH:
                        var48 = var36;
                        var49 = var35;
                        var50 = var40;
                        var52 = var40 + 1.0F;
                        var51 = var42 + 0.001F;
                        var70 = var42 + 0.001F;
                        var72 = var26;
                        break;
                     case SOUTH:
                        var48 = var37;
                        var49 = var38;
                        var50 = var40 + 1.0F;
                        var52 = var40;
                        var51 = var42 + 1.0F - 0.001F;
                        var70 = var42 + 1.0F - 0.001F;
                        var72 = var27;
                        break;
                     case WEST:
                        var48 = var38;
                        var49 = var36;
                        var50 = var40 + 0.001F;
                        var52 = var40 + 0.001F;
                        var51 = var42 + 1.0F;
                        var70 = var42;
                        var72 = var28;
                        break;
                     default:
                        var48 = var35;
                        var49 = var37;
                        var50 = var40 + 1.0F - 0.001F;
                        var52 = var40 + 1.0F - 0.001F;
                        var51 = var42;
                        var70 = var42 + 1.0F;
                        var72 = var29;
                  }
               } while(!var72);
            } while(isFaceOccludedByNeighbor(var1, var2, var68, Math.max(var48, var49), var1.getBlockState(var2.relative(var68))));

            BlockPos var73 = var2.relative(var68);
            TextureAtlasSprite var74 = var7[1];
            if (!var6) {
               Block var76 = var1.getBlockState(var73).getBlock();
               if (var76 instanceof HalfTransparentBlock || var76 instanceof LeavesBlock) {
                  var74 = this.waterOverlay;
               }
            }

            var57 = var74.getU(0.0F);
            var58 = var74.getU(0.5F);
            var59 = var74.getV((1.0F - var48) * 0.5F);
            var60 = var74.getV((1.0F - var49) * 0.5F);
            float var61 = var74.getV(0.5F);
            float var62 = var68.getAxis() == Direction.Axis.Z ? var32 : var33;
            float var63 = var31 * var62 * var9;
            float var64 = var31 * var62 * var10;
            float var65 = var31 * var62 * var11;
            this.vertex(var3, var50, var41 + var48, var51, var63, var64, var65, var57, var59, var66);
            this.vertex(var3, var52, var41 + var49, var70, var63, var64, var65, var58, var60, var66);
            this.vertex(var3, var52, var41 + var44, var70, var63, var64, var65, var58, var61, var66);
            this.vertex(var3, var50, var41 + var44, var51, var63, var64, var65, var57, var61, var66);
            if (var74 != this.waterOverlay) {
               this.vertex(var3, var50, var41 + var44, var51, var63, var64, var65, var57, var61, var66);
               this.vertex(var3, var52, var41 + var44, var70, var63, var64, var65, var58, var61, var66);
               this.vertex(var3, var52, var41 + var49, var70, var63, var64, var65, var58, var60, var66);
               this.vertex(var3, var50, var41 + var48, var51, var63, var64, var65, var57, var59, var66);
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
