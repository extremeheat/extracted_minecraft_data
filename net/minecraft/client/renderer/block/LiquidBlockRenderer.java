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
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlockRenderer {
   private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
   private TextureAtlasSprite waterOverlay;

   protected void setupSprites() {
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
      this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
   }

   private static boolean isNeighborSameFluid(BlockGetter var0, BlockPos var1, Direction var2, FluidState var3) {
      BlockPos var4 = var1.relative(var2);
      FluidState var5 = var0.getFluidState(var4);
      return var5.getType().isSame(var3.getType());
   }

   private static boolean isFaceOccluded(BlockGetter var0, BlockPos var1, Direction var2, float var3) {
      BlockPos var4 = var1.relative(var2);
      BlockState var5 = var0.getBlockState(var4);
      if (var5.canOcclude()) {
         VoxelShape var6 = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)var3, 1.0D);
         VoxelShape var7 = var5.getOcclusionShape(var0, var4);
         return Shapes.blockOccudes(var6, var7, var2);
      } else {
         return false;
      }
   }

   public boolean tesselate(BlockAndTintGetter var1, BlockPos var2, VertexConsumer var3, FluidState var4) {
      boolean var5 = var4.is(FluidTags.LAVA);
      TextureAtlasSprite[] var6 = var5 ? this.lavaIcons : this.waterIcons;
      int var7 = var5 ? 16777215 : BiomeColors.getAverageWaterColor(var1, var2);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      boolean var11 = !isNeighborSameFluid(var1, var2, Direction.UP, var4);
      boolean var12 = !isNeighborSameFluid(var1, var2, Direction.DOWN, var4) && !isFaceOccluded(var1, var2, Direction.DOWN, 0.8888889F);
      boolean var13 = !isNeighborSameFluid(var1, var2, Direction.NORTH, var4);
      boolean var14 = !isNeighborSameFluid(var1, var2, Direction.SOUTH, var4);
      boolean var15 = !isNeighborSameFluid(var1, var2, Direction.WEST, var4);
      boolean var16 = !isNeighborSameFluid(var1, var2, Direction.EAST, var4);
      if (!var11 && !var12 && !var16 && !var15 && !var13 && !var14) {
         return false;
      } else {
         boolean var17 = false;
         float var18 = 0.5F;
         float var19 = 1.0F;
         float var20 = 0.8F;
         float var21 = 0.6F;
         float var22 = this.getWaterHeight(var1, var2, var4.getType());
         float var23 = this.getWaterHeight(var1, var2.south(), var4.getType());
         float var24 = this.getWaterHeight(var1, var2.east().south(), var4.getType());
         float var25 = this.getWaterHeight(var1, var2.east(), var4.getType());
         double var26 = (double)(var2.getX() & 15);
         double var28 = (double)(var2.getY() & 15);
         double var30 = (double)(var2.getZ() & 15);
         float var32 = 0.001F;
         float var33 = var12 ? 0.001F : 0.0F;
         float var34;
         float var35;
         float var36;
         float var37;
         float var39;
         float var40;
         float var41;
         float var49;
         float var50;
         float var51;
         if (var11 && !isFaceOccluded(var1, var2, Direction.UP, Math.min(Math.min(var22, var23), Math.min(var24, var25)))) {
            var17 = true;
            var22 -= 0.001F;
            var23 -= 0.001F;
            var24 -= 0.001F;
            var25 -= 0.001F;
            Vec3 var42 = var4.getFlow(var1, var2);
            float var38;
            TextureAtlasSprite var43;
            float var44;
            float var45;
            float var46;
            float var47;
            if (var42.x == 0.0D && var42.z == 0.0D) {
               var43 = var6[0];
               var34 = var43.getU(0.0D);
               var38 = var43.getV(0.0D);
               var35 = var34;
               var39 = var43.getV(16.0D);
               var36 = var43.getU(16.0D);
               var40 = var39;
               var37 = var36;
               var41 = var38;
            } else {
               var43 = var6[1];
               var44 = (float)Mth.atan2(var42.z, var42.x) - 1.5707964F;
               var45 = Mth.sin(var44) * 0.25F;
               var46 = Mth.cos(var44) * 0.25F;
               var47 = 8.0F;
               var34 = var43.getU((double)(8.0F + (-var46 - var45) * 16.0F));
               var38 = var43.getV((double)(8.0F + (-var46 + var45) * 16.0F));
               var35 = var43.getU((double)(8.0F + (-var46 + var45) * 16.0F));
               var39 = var43.getV((double)(8.0F + (var46 + var45) * 16.0F));
               var36 = var43.getU((double)(8.0F + (var46 + var45) * 16.0F));
               var40 = var43.getV((double)(8.0F + (var46 - var45) * 16.0F));
               var37 = var43.getU((double)(8.0F + (var46 - var45) * 16.0F));
               var41 = var43.getV((double)(8.0F + (-var46 - var45) * 16.0F));
            }

            float var64 = (var34 + var35 + var36 + var37) / 4.0F;
            var44 = (var38 + var39 + var40 + var41) / 4.0F;
            var45 = (float)var6[0].getWidth() / (var6[0].getU1() - var6[0].getU0());
            var46 = (float)var6[0].getHeight() / (var6[0].getV1() - var6[0].getV0());
            var47 = 4.0F / Math.max(var46, var45);
            var34 = Mth.lerp(var47, var34, var64);
            var35 = Mth.lerp(var47, var35, var64);
            var36 = Mth.lerp(var47, var36, var64);
            var37 = Mth.lerp(var47, var37, var64);
            var38 = Mth.lerp(var47, var38, var44);
            var39 = Mth.lerp(var47, var39, var44);
            var40 = Mth.lerp(var47, var40, var44);
            var41 = Mth.lerp(var47, var41, var44);
            int var48 = this.getLightColor(var1, var2);
            var49 = 1.0F * var8;
            var50 = 1.0F * var9;
            var51 = 1.0F * var10;
            this.vertex(var3, var26 + 0.0D, var28 + (double)var22, var30 + 0.0D, var49, var50, var51, var34, var38, var48);
            this.vertex(var3, var26 + 0.0D, var28 + (double)var23, var30 + 1.0D, var49, var50, var51, var35, var39, var48);
            this.vertex(var3, var26 + 1.0D, var28 + (double)var24, var30 + 1.0D, var49, var50, var51, var36, var40, var48);
            this.vertex(var3, var26 + 1.0D, var28 + (double)var25, var30 + 0.0D, var49, var50, var51, var37, var41, var48);
            if (var4.shouldRenderBackwardUpFace(var1, var2.above())) {
               this.vertex(var3, var26 + 0.0D, var28 + (double)var22, var30 + 0.0D, var49, var50, var51, var34, var38, var48);
               this.vertex(var3, var26 + 1.0D, var28 + (double)var25, var30 + 0.0D, var49, var50, var51, var37, var41, var48);
               this.vertex(var3, var26 + 1.0D, var28 + (double)var24, var30 + 1.0D, var49, var50, var51, var36, var40, var48);
               this.vertex(var3, var26 + 0.0D, var28 + (double)var23, var30 + 1.0D, var49, var50, var51, var35, var39, var48);
            }
         }

         if (var12) {
            var34 = var6[0].getU0();
            var35 = var6[0].getU1();
            var36 = var6[0].getV0();
            var37 = var6[0].getV1();
            int var61 = this.getLightColor(var1, var2.below());
            var39 = 0.5F * var8;
            var40 = 0.5F * var9;
            var41 = 0.5F * var10;
            this.vertex(var3, var26, var28 + (double)var33, var30 + 1.0D, var39, var40, var41, var34, var37, var61);
            this.vertex(var3, var26, var28 + (double)var33, var30, var39, var40, var41, var34, var36, var61);
            this.vertex(var3, var26 + 1.0D, var28 + (double)var33, var30, var39, var40, var41, var35, var36, var61);
            this.vertex(var3, var26 + 1.0D, var28 + (double)var33, var30 + 1.0D, var39, var40, var41, var35, var37, var61);
            var17 = true;
         }

         for(int var59 = 0; var59 < 4; ++var59) {
            double var60;
            double var62;
            double var63;
            double var65;
            Direction var66;
            boolean var67;
            if (var59 == 0) {
               var35 = var22;
               var36 = var25;
               var60 = var26;
               var63 = var26 + 1.0D;
               var62 = var30 + 0.0010000000474974513D;
               var65 = var30 + 0.0010000000474974513D;
               var66 = Direction.NORTH;
               var67 = var13;
            } else if (var59 == 1) {
               var35 = var24;
               var36 = var23;
               var60 = var26 + 1.0D;
               var63 = var26;
               var62 = var30 + 1.0D - 0.0010000000474974513D;
               var65 = var30 + 1.0D - 0.0010000000474974513D;
               var66 = Direction.SOUTH;
               var67 = var14;
            } else if (var59 == 2) {
               var35 = var23;
               var36 = var22;
               var60 = var26 + 0.0010000000474974513D;
               var63 = var26 + 0.0010000000474974513D;
               var62 = var30 + 1.0D;
               var65 = var30;
               var66 = Direction.WEST;
               var67 = var15;
            } else {
               var35 = var25;
               var36 = var24;
               var60 = var26 + 1.0D - 0.0010000000474974513D;
               var63 = var26 + 1.0D - 0.0010000000474974513D;
               var62 = var30;
               var65 = var30 + 1.0D;
               var66 = Direction.EAST;
               var67 = var16;
            }

            if (var67 && !isFaceOccluded(var1, var2, var66, Math.max(var35, var36))) {
               var17 = true;
               BlockPos var68 = var2.relative(var66);
               TextureAtlasSprite var69 = var6[1];
               if (!var5) {
                  Block var70 = var1.getBlockState(var68).getBlock();
                  if (var70 == Blocks.GLASS || var70 instanceof StainedGlassBlock) {
                     var69 = this.waterOverlay;
                  }
               }

               var49 = var69.getU(0.0D);
               var50 = var69.getU(8.0D);
               var51 = var69.getV((double)((1.0F - var35) * 16.0F * 0.5F));
               float var52 = var69.getV((double)((1.0F - var36) * 16.0F * 0.5F));
               float var53 = var69.getV(8.0D);
               int var54 = this.getLightColor(var1, var68);
               float var55 = var59 < 2 ? 0.8F : 0.6F;
               float var56 = 1.0F * var55 * var8;
               float var57 = 1.0F * var55 * var9;
               float var58 = 1.0F * var55 * var10;
               this.vertex(var3, var60, var28 + (double)var35, var62, var56, var57, var58, var49, var51, var54);
               this.vertex(var3, var63, var28 + (double)var36, var65, var56, var57, var58, var50, var52, var54);
               this.vertex(var3, var63, var28 + (double)var33, var65, var56, var57, var58, var50, var53, var54);
               this.vertex(var3, var60, var28 + (double)var33, var62, var56, var57, var58, var49, var53, var54);
               if (var69 != this.waterOverlay) {
                  this.vertex(var3, var60, var28 + (double)var33, var62, var56, var57, var58, var49, var53, var54);
                  this.vertex(var3, var63, var28 + (double)var33, var65, var56, var57, var58, var50, var53, var54);
                  this.vertex(var3, var63, var28 + (double)var36, var65, var56, var57, var58, var50, var52, var54);
                  this.vertex(var3, var60, var28 + (double)var35, var62, var56, var57, var58, var49, var51, var54);
               }
            }
         }

         return var17;
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

   private float getWaterHeight(BlockGetter var1, BlockPos var2, Fluid var3) {
      int var4 = 0;
      float var5 = 0.0F;

      for(int var6 = 0; var6 < 4; ++var6) {
         BlockPos var7 = var2.offset(-(var6 & 1), 0, -(var6 >> 1 & 1));
         if (var1.getFluidState(var7.above()).getType().isSame(var3)) {
            return 1.0F;
         }

         FluidState var8 = var1.getFluidState(var7);
         if (var8.getType().isSame(var3)) {
            float var9 = var8.getHeight(var1, var7);
            if (var9 >= 0.8F) {
               var5 += var9 * 10.0F;
               var4 += 10;
            } else {
               var5 += var9;
               ++var4;
            }
         } else if (!var1.getBlockState(var7).getMaterial().isSolid()) {
            ++var4;
         }
      }

      return var5 / (float)var4;
   }
}
