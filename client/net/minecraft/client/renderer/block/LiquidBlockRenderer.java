package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndBiomeGetter;
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

   public LiquidBlockRenderer() {
      super();
   }

   protected void setupSprites() {
      TextureAtlas var1 = Minecraft.getInstance().getTextureAtlas();
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = var1.getSprite(ModelBakery.LAVA_FLOW);
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = var1.getSprite(ModelBakery.WATER_FLOW);
      this.waterOverlay = var1.getSprite(ModelBakery.WATER_OVERLAY);
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

   public boolean tesselate(BlockAndBiomeGetter var1, BlockPos var2, BufferBuilder var3, FluidState var4) {
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
         double var26 = (double)var2.getX();
         double var28 = (double)var2.getY();
         double var30 = (double)var2.getZ();
         float var32 = 0.001F;
         float var33;
         float var34;
         float var35;
         float var36;
         float var40;
         float var50;
         float var51;
         float var52;
         float var68;
         if (var11 && !isFaceOccluded(var1, var2, Direction.UP, Math.min(Math.min(var22, var23), Math.min(var24, var25)))) {
            var17 = true;
            var22 -= 0.001F;
            var23 -= 0.001F;
            var24 -= 0.001F;
            var25 -= 0.001F;
            Vec3 var41 = var4.getFlow(var1, var2);
            float var37;
            float var38;
            float var39;
            TextureAtlasSprite var42;
            float var43;
            float var44;
            float var45;
            float var46;
            if (var41.x == 0.0D && var41.z == 0.0D) {
               var42 = var6[0];
               var33 = var42.getU(0.0D);
               var37 = var42.getV(0.0D);
               var34 = var33;
               var38 = var42.getV(16.0D);
               var35 = var42.getU(16.0D);
               var39 = var38;
               var36 = var35;
               var40 = var37;
            } else {
               var42 = var6[1];
               var43 = (float)Mth.atan2(var41.z, var41.x) - 1.5707964F;
               var44 = Mth.sin(var43) * 0.25F;
               var45 = Mth.cos(var43) * 0.25F;
               var46 = 8.0F;
               var33 = var42.getU((double)(8.0F + (-var45 - var44) * 16.0F));
               var37 = var42.getV((double)(8.0F + (-var45 + var44) * 16.0F));
               var34 = var42.getU((double)(8.0F + (-var45 + var44) * 16.0F));
               var38 = var42.getV((double)(8.0F + (var45 + var44) * 16.0F));
               var35 = var42.getU((double)(8.0F + (var45 + var44) * 16.0F));
               var39 = var42.getV((double)(8.0F + (var45 - var44) * 16.0F));
               var36 = var42.getU((double)(8.0F + (var45 - var44) * 16.0F));
               var40 = var42.getV((double)(8.0F + (-var45 - var44) * 16.0F));
            }

            var68 = (var33 + var34 + var35 + var36) / 4.0F;
            var43 = (var37 + var38 + var39 + var40) / 4.0F;
            var44 = (float)var6[0].getWidth() / (var6[0].getU1() - var6[0].getU0());
            var45 = (float)var6[0].getHeight() / (var6[0].getV1() - var6[0].getV0());
            var46 = 4.0F / Math.max(var45, var44);
            var33 = Mth.lerp(var46, var33, var68);
            var34 = Mth.lerp(var46, var34, var68);
            var35 = Mth.lerp(var46, var35, var68);
            var36 = Mth.lerp(var46, var36, var68);
            var37 = Mth.lerp(var46, var37, var43);
            var38 = Mth.lerp(var46, var38, var43);
            var39 = Mth.lerp(var46, var39, var43);
            var40 = Mth.lerp(var46, var40, var43);
            int var47 = this.getLightColor(var1, var2);
            int var48 = var47 >> 16 & '\uffff';
            int var49 = var47 & '\uffff';
            var50 = 1.0F * var8;
            var51 = 1.0F * var9;
            var52 = 1.0F * var10;
            var3.vertex(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var33, (double)var37).uv2(var48, var49).endVertex();
            var3.vertex(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var34, (double)var38).uv2(var48, var49).endVertex();
            var3.vertex(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var35, (double)var39).uv2(var48, var49).endVertex();
            var3.vertex(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var36, (double)var40).uv2(var48, var49).endVertex();
            if (var4.shouldRenderBackwardUpFace(var1, var2.above())) {
               var3.vertex(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var33, (double)var37).uv2(var48, var49).endVertex();
               var3.vertex(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var36, (double)var40).uv2(var48, var49).endVertex();
               var3.vertex(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var35, (double)var39).uv2(var48, var49).endVertex();
               var3.vertex(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var34, (double)var38).uv2(var48, var49).endVertex();
            }
         }

         if (var12) {
            var33 = var6[0].getU0();
            var34 = var6[0].getU1();
            var35 = var6[0].getV0();
            var36 = var6[0].getV1();
            int var62 = this.getLightColor(var1, var2.below());
            int var63 = var62 >> 16 & '\uffff';
            int var65 = var62 & '\uffff';
            var40 = 0.5F * var8;
            float var66 = 0.5F * var9;
            var68 = 0.5F * var10;
            var3.vertex(var26, var28, var30 + 1.0D).color(var40, var66, var68, 1.0F).uv((double)var33, (double)var36).uv2(var63, var65).endVertex();
            var3.vertex(var26, var28, var30).color(var40, var66, var68, 1.0F).uv((double)var33, (double)var35).uv2(var63, var65).endVertex();
            var3.vertex(var26 + 1.0D, var28, var30).color(var40, var66, var68, 1.0F).uv((double)var34, (double)var35).uv2(var63, var65).endVertex();
            var3.vertex(var26 + 1.0D, var28, var30 + 1.0D).color(var40, var66, var68, 1.0F).uv((double)var34, (double)var36).uv2(var63, var65).endVertex();
            var17 = true;
         }

         for(int var60 = 0; var60 < 4; ++var60) {
            double var61;
            double var64;
            double var67;
            double var69;
            Direction var70;
            boolean var71;
            if (var60 == 0) {
               var34 = var22;
               var35 = var25;
               var61 = var26;
               var67 = var26 + 1.0D;
               var64 = var30 + 0.0010000000474974513D;
               var69 = var30 + 0.0010000000474974513D;
               var70 = Direction.NORTH;
               var71 = var13;
            } else if (var60 == 1) {
               var34 = var24;
               var35 = var23;
               var61 = var26 + 1.0D;
               var67 = var26;
               var64 = var30 + 1.0D - 0.0010000000474974513D;
               var69 = var30 + 1.0D - 0.0010000000474974513D;
               var70 = Direction.SOUTH;
               var71 = var14;
            } else if (var60 == 2) {
               var34 = var23;
               var35 = var22;
               var61 = var26 + 0.0010000000474974513D;
               var67 = var26 + 0.0010000000474974513D;
               var64 = var30 + 1.0D;
               var69 = var30;
               var70 = Direction.WEST;
               var71 = var15;
            } else {
               var34 = var25;
               var35 = var24;
               var61 = var26 + 1.0D - 0.0010000000474974513D;
               var67 = var26 + 1.0D - 0.0010000000474974513D;
               var64 = var30;
               var69 = var30 + 1.0D;
               var70 = Direction.EAST;
               var71 = var16;
            }

            if (var71 && !isFaceOccluded(var1, var2, var70, Math.max(var34, var35))) {
               var17 = true;
               BlockPos var72 = var2.relative(var70);
               TextureAtlasSprite var73 = var6[1];
               if (!var5) {
                  Block var74 = var1.getBlockState(var72).getBlock();
                  if (var74 == Blocks.GLASS || var74 instanceof StainedGlassBlock) {
                     var73 = this.waterOverlay;
                  }
               }

               float var75 = var73.getU(0.0D);
               float var76 = var73.getU(8.0D);
               var50 = var73.getV((double)((1.0F - var34) * 16.0F * 0.5F));
               var51 = var73.getV((double)((1.0F - var35) * 16.0F * 0.5F));
               var52 = var73.getV(8.0D);
               int var53 = this.getLightColor(var1, var72);
               int var54 = var53 >> 16 & '\uffff';
               int var55 = var53 & '\uffff';
               float var56 = var60 < 2 ? 0.8F : 0.6F;
               float var57 = 1.0F * var56 * var8;
               float var58 = 1.0F * var56 * var9;
               float var59 = 1.0F * var56 * var10;
               var3.vertex(var61, var28 + (double)var34, var64).color(var57, var58, var59, 1.0F).uv((double)var75, (double)var50).uv2(var54, var55).endVertex();
               var3.vertex(var67, var28 + (double)var35, var69).color(var57, var58, var59, 1.0F).uv((double)var76, (double)var51).uv2(var54, var55).endVertex();
               var3.vertex(var67, var28 + 0.0D, var69).color(var57, var58, var59, 1.0F).uv((double)var76, (double)var52).uv2(var54, var55).endVertex();
               var3.vertex(var61, var28 + 0.0D, var64).color(var57, var58, var59, 1.0F).uv((double)var75, (double)var52).uv2(var54, var55).endVertex();
               if (var73 != this.waterOverlay) {
                  var3.vertex(var61, var28 + 0.0D, var64).color(var57, var58, var59, 1.0F).uv((double)var75, (double)var52).uv2(var54, var55).endVertex();
                  var3.vertex(var67, var28 + 0.0D, var69).color(var57, var58, var59, 1.0F).uv((double)var76, (double)var52).uv2(var54, var55).endVertex();
                  var3.vertex(var67, var28 + (double)var35, var69).color(var57, var58, var59, 1.0F).uv((double)var76, (double)var51).uv2(var54, var55).endVertex();
                  var3.vertex(var61, var28 + (double)var34, var64).color(var57, var58, var59, 1.0F).uv((double)var75, (double)var50).uv2(var54, var55).endVertex();
               }
            }
         }

         return var17;
      }
   }

   private int getLightColor(BlockAndBiomeGetter var1, BlockPos var2) {
      int var3 = var1.getLightColor(var2, 0);
      int var4 = var1.getLightColor(var2.above(), 0);
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
