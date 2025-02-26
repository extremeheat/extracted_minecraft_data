package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModelBlockRenderer {
   private static final int FACE_CUBIC = 0;
   private static final int FACE_PARTIAL = 1;
   static final Direction[] DIRECTIONS = Direction.values();
   private final BlockColors blockColors;
   private static final int CACHE_SIZE = 100;
   static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

   public ModelBlockRenderer(BlockColors var1) {
      super();
      this.blockColors = var1;
   }

   public void tesselateBlock(BlockAndTintGetter var1, List<BlockModelPart> var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, int var8) {
      if (!var2.isEmpty()) {
         boolean var9 = Minecraft.useAmbientOcclusion() && var3.getLightEmission() == 0 && ((BlockModelPart)var2.getFirst()).useAmbientOcclusion();
         var5.translate(var3.getOffset(var4));

         try {
            if (var9) {
               this.tesselateWithAO(var1, var2, var3, var4, var5, var6, var7, var8);
            } else {
               this.tesselateWithoutAO(var1, var2, var3, var4, var5, var6, var7, var8);
            }

         } catch (Throwable var13) {
            CrashReport var11 = CrashReport.forThrowable(var13, "Tesselating block model");
            CrashReportCategory var12 = var11.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(var12, var1, var4, var3);
            var12.setDetail("Using AO", var9);
            throw new ReportedException(var11);
         }
      }
   }

   public void tesselateWithAO(BlockAndTintGetter var1, List<BlockModelPart> var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, int var8) {
      float[] var9 = new float[DIRECTIONS.length * 2];
      BitSet var10 = new BitSet(3);
      AmbientOcclusionFace var11 = new AmbientOcclusionFace();
      BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

      for(BlockModelPart var14 : var2) {
         for(Direction var18 : DIRECTIONS) {
            List var19 = var14.getQuads(var18);
            if (!var19.isEmpty() && (!var7 || Block.shouldRenderFace(var3, var1.getBlockState(var12.setWithOffset(var4, (Direction)var18)), var18))) {
               this.renderModelFaceAO(var1, var3, var4, var5, var6, var19, var9, var10, var11, var8);
            }
         }

         List var20 = var14.getQuads((Direction)null);
         if (!var20.isEmpty()) {
            this.renderModelFaceAO(var1, var3, var4, var5, var6, var20, var9, var10, var11, var8);
         }
      }

   }

   public void tesselateWithoutAO(BlockAndTintGetter var1, List<BlockModelPart> var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, int var8) {
      BitSet var9 = new BitSet(3);
      BlockPos.MutableBlockPos var10 = var4.mutable();

      for(BlockModelPart var12 : var2) {
         for(Direction var16 : DIRECTIONS) {
            List var17 = var12.getQuads(var16);
            if (!var17.isEmpty()) {
               var10.setWithOffset(var4, (Direction)var16);
               if (!var7 || Block.shouldRenderFace(var3, var1.getBlockState(var10), var16)) {
                  int var18 = LevelRenderer.getLightColor(var1, var3, var10);
                  this.renderModelFaceFlat(var1, var3, var4, var18, var8, false, var5, var6, var17, var9);
               }
            }
         }

         List var19 = var12.getQuads((Direction)null);
         if (!var19.isEmpty()) {
            this.renderModelFaceFlat(var1, var3, var4, -1, var8, true, var5, var6, var19, var9);
         }
      }

   }

   private void renderModelFaceAO(BlockAndTintGetter var1, BlockState var2, BlockPos var3, PoseStack var4, VertexConsumer var5, List<BakedQuad> var6, float[] var7, BitSet var8, AmbientOcclusionFace var9, int var10) {
      for(BakedQuad var12 : var6) {
         this.calculateShape(var1, var2, var3, var12.vertices(), var12.direction(), var7, var8);
         var9.calculate(var1, var2, var3, var12.direction(), var7, var8, var12.shade());
         this.putQuadData(var1, var2, var3, var5, var4.last(), var12, var9.brightness, var9.lightmap, var10);
      }

   }

   private void putQuadData(BlockAndTintGetter var1, BlockState var2, BlockPos var3, VertexConsumer var4, PoseStack.Pose var5, BakedQuad var6, float[] var7, int[] var8, int var9) {
      float var10;
      float var11;
      float var12;
      if (var6.isTinted()) {
         int var13 = this.blockColors.getColor(var2, var1, var3, var6.tintIndex());
         var10 = (float)(var13 >> 16 & 255) / 255.0F;
         var11 = (float)(var13 >> 8 & 255) / 255.0F;
         var12 = (float)(var13 & 255) / 255.0F;
      } else {
         var10 = 1.0F;
         var11 = 1.0F;
         var12 = 1.0F;
      }

      var4.putBulkData(var5, var6, var7, var10, var11, var12, 1.0F, var8, var9, true);
   }

   private void calculateShape(BlockAndTintGetter var1, BlockState var2, BlockPos var3, int[] var4, Direction var5, @Nullable float[] var6, BitSet var7) {
      float var8 = 32.0F;
      float var9 = 32.0F;
      float var10 = 32.0F;
      float var11 = -32.0F;
      float var12 = -32.0F;
      float var13 = -32.0F;

      for(int var14 = 0; var14 < 4; ++var14) {
         float var15 = Float.intBitsToFloat(var4[var14 * 8]);
         float var16 = Float.intBitsToFloat(var4[var14 * 8 + 1]);
         float var17 = Float.intBitsToFloat(var4[var14 * 8 + 2]);
         var8 = Math.min(var8, var15);
         var9 = Math.min(var9, var16);
         var10 = Math.min(var10, var17);
         var11 = Math.max(var11, var15);
         var12 = Math.max(var12, var16);
         var13 = Math.max(var13, var17);
      }

      if (var6 != null) {
         var6[Direction.WEST.get3DDataValue()] = var8;
         var6[Direction.EAST.get3DDataValue()] = var11;
         var6[Direction.DOWN.get3DDataValue()] = var9;
         var6[Direction.UP.get3DDataValue()] = var12;
         var6[Direction.NORTH.get3DDataValue()] = var10;
         var6[Direction.SOUTH.get3DDataValue()] = var13;
         int var18 = DIRECTIONS.length;
         var6[Direction.WEST.get3DDataValue() + var18] = 1.0F - var8;
         var6[Direction.EAST.get3DDataValue() + var18] = 1.0F - var11;
         var6[Direction.DOWN.get3DDataValue() + var18] = 1.0F - var9;
         var6[Direction.UP.get3DDataValue() + var18] = 1.0F - var12;
         var6[Direction.NORTH.get3DDataValue() + var18] = 1.0F - var10;
         var6[Direction.SOUTH.get3DDataValue() + var18] = 1.0F - var13;
      }

      float var19 = 1.0E-4F;
      float var20 = 0.9999F;
      switch (var5) {
         case DOWN:
            var7.set(1, var8 >= 1.0E-4F || var10 >= 1.0E-4F || var11 <= 0.9999F || var13 <= 0.9999F);
            var7.set(0, var9 == var12 && (var9 < 1.0E-4F || var2.isCollisionShapeFullBlock(var1, var3)));
            break;
         case UP:
            var7.set(1, var8 >= 1.0E-4F || var10 >= 1.0E-4F || var11 <= 0.9999F || var13 <= 0.9999F);
            var7.set(0, var9 == var12 && (var12 > 0.9999F || var2.isCollisionShapeFullBlock(var1, var3)));
            break;
         case NORTH:
            var7.set(1, var8 >= 1.0E-4F || var9 >= 1.0E-4F || var11 <= 0.9999F || var12 <= 0.9999F);
            var7.set(0, var10 == var13 && (var10 < 1.0E-4F || var2.isCollisionShapeFullBlock(var1, var3)));
            break;
         case SOUTH:
            var7.set(1, var8 >= 1.0E-4F || var9 >= 1.0E-4F || var11 <= 0.9999F || var12 <= 0.9999F);
            var7.set(0, var10 == var13 && (var13 > 0.9999F || var2.isCollisionShapeFullBlock(var1, var3)));
            break;
         case WEST:
            var7.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
            var7.set(0, var8 == var11 && (var8 < 1.0E-4F || var2.isCollisionShapeFullBlock(var1, var3)));
            break;
         case EAST:
            var7.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
            var7.set(0, var8 == var11 && (var11 > 0.9999F || var2.isCollisionShapeFullBlock(var1, var3)));
      }

   }

   private void renderModelFaceFlat(BlockAndTintGetter var1, BlockState var2, BlockPos var3, int var4, int var5, boolean var6, PoseStack var7, VertexConsumer var8, List<BakedQuad> var9, BitSet var10) {
      for(BakedQuad var12 : var9) {
         if (var6) {
            this.calculateShape(var1, var2, var3, var12.vertices(), var12.direction(), (float[])null, var10);
            BlockPos var13 = var10.get(0) ? var3.relative(var12.direction()) : var3;
            var4 = LevelRenderer.getLightColor(var1, var2, var13);
         }

         float var14 = var1.getShade(var12.direction(), var12.shade());
         this.putQuadData(var1, var2, var3, var8, var7.last(), var12, new float[]{var14, var14, var14, var14}, new int[]{var4, var4, var4, var4}, var5);
      }

   }

   public void renderModel(PoseStack.Pose var1, VertexConsumer var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8) {
      for(BlockModelPart var10 : var3.collectParts(RandomSource.create(42L))) {
         for(Direction var14 : DIRECTIONS) {
            renderQuadList(var1, var2, var4, var5, var6, var10.getQuads(var14), var7, var8);
         }

         renderQuadList(var1, var2, var4, var5, var6, var10.getQuads((Direction)null), var7, var8);
      }

   }

   private static void renderQuadList(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, List<BakedQuad> var5, int var6, int var7) {
      for(BakedQuad var9 : var5) {
         float var10;
         float var11;
         float var12;
         if (var9.isTinted()) {
            var10 = Mth.clamp(var2, 0.0F, 1.0F);
            var11 = Mth.clamp(var3, 0.0F, 1.0F);
            var12 = Mth.clamp(var4, 0.0F, 1.0F);
         } else {
            var10 = 1.0F;
            var11 = 1.0F;
            var12 = 1.0F;
         }

         var1.putBulkData(var0, var9, var10, var11, var12, 1.0F, var6, var7);
      }

   }

   public static void enableCaching() {
      ((Cache)CACHE.get()).enable();
   }

   public static void clearCache() {
      ((Cache)CACHE.get()).disable();
   }

   static enum AmbientVertexRemap {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      final int vert0;
      final int vert1;
      final int vert2;
      final int vert3;
      private static final AmbientVertexRemap[] BY_FACING = (AmbientVertexRemap[])Util.make(new AmbientVertexRemap[6], (var0) -> {
         var0[Direction.DOWN.get3DDataValue()] = DOWN;
         var0[Direction.UP.get3DDataValue()] = UP;
         var0[Direction.NORTH.get3DDataValue()] = NORTH;
         var0[Direction.SOUTH.get3DDataValue()] = SOUTH;
         var0[Direction.WEST.get3DDataValue()] = WEST;
         var0[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AmbientVertexRemap(final int var3, final int var4, final int var5, final int var6) {
         this.vert0 = var3;
         this.vert1 = var4;
         this.vert2 = var5;
         this.vert3 = var6;
      }

      public static AmbientVertexRemap fromFacing(Direction var0) {
         return BY_FACING[var0.get3DDataValue()];
      }

      // $FF: synthetic method
      private static AmbientVertexRemap[] $values() {
         return new AmbientVertexRemap[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }

   static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
         Long2IntLinkedOpenHashMap var1 = new Long2IntLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int var1) {
            }
         };
         var1.defaultReturnValue(2147483647);
         return var1;
      });
      private final Long2FloatLinkedOpenHashMap brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap var1 = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int var1) {
            }
         };
         var1.defaultReturnValue(0.0F / 0.0F);
         return var1;
      });

      private Cache() {
         super();
      }

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.colorCache.clear();
         this.brightnessCache.clear();
      }

      public int getLightColor(BlockState var1, BlockAndTintGetter var2, BlockPos var3) {
         long var4 = var3.asLong();
         if (this.enabled) {
            int var6 = this.colorCache.get(var4);
            if (var6 != 2147483647) {
               return var6;
            }
         }

         int var7 = LevelRenderer.getLightColor(var2, var1, var3);
         if (this.enabled) {
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(var4, var7);
         }

         return var7;
      }

      public float getShadeBrightness(BlockState var1, BlockAndTintGetter var2, BlockPos var3) {
         long var4 = var3.asLong();
         if (this.enabled) {
            float var6 = this.brightnessCache.get(var4);
            if (!Float.isNaN(var6)) {
               return var6;
            }
         }

         float var7 = var1.getShadeBrightness(var2, var3);
         if (this.enabled) {
            if (this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(var4, var7);
         }

         return var7;
      }
   }

   static class AmbientOcclusionFace {
      final float[] brightness = new float[4];
      final int[] lightmap = new int[4];

      public AmbientOcclusionFace() {
         super();
      }

      public void calculate(BlockAndTintGetter var1, BlockState var2, BlockPos var3, Direction var4, float[] var5, BitSet var6, boolean var7) {
         BlockPos var8 = var6.get(0) ? var3.relative(var4) : var3;
         AdjacencyInfo var9 = ModelBlockRenderer.AdjacencyInfo.fromFacing(var4);
         BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
         Cache var11 = (Cache)ModelBlockRenderer.CACHE.get();
         var10.setWithOffset(var8, (Direction)var9.corners[0]);
         BlockState var12 = var1.getBlockState(var10);
         int var13 = var11.getLightColor(var12, var1, var10);
         float var14 = var11.getShadeBrightness(var12, var1, var10);
         var10.setWithOffset(var8, (Direction)var9.corners[1]);
         BlockState var15 = var1.getBlockState(var10);
         int var16 = var11.getLightColor(var15, var1, var10);
         float var17 = var11.getShadeBrightness(var15, var1, var10);
         var10.setWithOffset(var8, (Direction)var9.corners[2]);
         BlockState var18 = var1.getBlockState(var10);
         int var19 = var11.getLightColor(var18, var1, var10);
         float var20 = var11.getShadeBrightness(var18, var1, var10);
         var10.setWithOffset(var8, (Direction)var9.corners[3]);
         BlockState var21 = var1.getBlockState(var10);
         int var22 = var11.getLightColor(var21, var1, var10);
         float var23 = var11.getShadeBrightness(var21, var1, var10);
         BlockState var24 = var1.getBlockState(var10.setWithOffset(var8, (Direction)var9.corners[0]).move(var4));
         boolean var25 = !var24.isViewBlocking(var1, var10) || var24.getLightBlock() == 0;
         BlockState var26 = var1.getBlockState(var10.setWithOffset(var8, (Direction)var9.corners[1]).move(var4));
         boolean var27 = !var26.isViewBlocking(var1, var10) || var26.getLightBlock() == 0;
         BlockState var28 = var1.getBlockState(var10.setWithOffset(var8, (Direction)var9.corners[2]).move(var4));
         boolean var29 = !var28.isViewBlocking(var1, var10) || var28.getLightBlock() == 0;
         BlockState var30 = var1.getBlockState(var10.setWithOffset(var8, (Direction)var9.corners[3]).move(var4));
         boolean var31 = !var30.isViewBlocking(var1, var10) || var30.getLightBlock() == 0;
         float var32;
         int var36;
         if (!var29 && !var25) {
            var32 = var14;
            var36 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[0]).move(var9.corners[2]);
            BlockState var40 = var1.getBlockState(var10);
            var32 = var11.getShadeBrightness(var40, var1, var10);
            var36 = var11.getLightColor(var40, var1, var10);
         }

         float var33;
         int var37;
         if (!var31 && !var25) {
            var33 = var14;
            var37 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[0]).move(var9.corners[3]);
            BlockState var68 = var1.getBlockState(var10);
            var33 = var11.getShadeBrightness(var68, var1, var10);
            var37 = var11.getLightColor(var68, var1, var10);
         }

         float var34;
         int var38;
         if (!var29 && !var27) {
            var34 = var14;
            var38 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[1]).move(var9.corners[2]);
            BlockState var69 = var1.getBlockState(var10);
            var34 = var11.getShadeBrightness(var69, var1, var10);
            var38 = var11.getLightColor(var69, var1, var10);
         }

         float var35;
         int var39;
         if (!var31 && !var27) {
            var35 = var14;
            var39 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[1]).move(var9.corners[3]);
            BlockState var70 = var1.getBlockState(var10);
            var35 = var11.getShadeBrightness(var70, var1, var10);
            var39 = var11.getLightColor(var70, var1, var10);
         }

         int var71 = var11.getLightColor(var2, var1, var3);
         var10.setWithOffset(var3, (Direction)var4);
         BlockState var41 = var1.getBlockState(var10);
         if (var6.get(0) || !var41.isSolidRender()) {
            var71 = var11.getLightColor(var41, var1, var10);
         }

         float var42 = var6.get(0) ? var11.getShadeBrightness(var1.getBlockState(var8), var1, var8) : var11.getShadeBrightness(var1.getBlockState(var3), var1, var3);
         AmbientVertexRemap var43 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(var4);
         if (var6.get(1) && var9.doNonCubicWeight) {
            float var72 = (var23 + var14 + var33 + var42) * 0.25F;
            float var74 = (var20 + var14 + var32 + var42) * 0.25F;
            float var76 = (var20 + var17 + var34 + var42) * 0.25F;
            float var77 = (var23 + var17 + var35 + var42) * 0.25F;
            float var48 = var5[var9.vert0Weights[0].shape] * var5[var9.vert0Weights[1].shape];
            float var49 = var5[var9.vert0Weights[2].shape] * var5[var9.vert0Weights[3].shape];
            float var50 = var5[var9.vert0Weights[4].shape] * var5[var9.vert0Weights[5].shape];
            float var51 = var5[var9.vert0Weights[6].shape] * var5[var9.vert0Weights[7].shape];
            float var52 = var5[var9.vert1Weights[0].shape] * var5[var9.vert1Weights[1].shape];
            float var53 = var5[var9.vert1Weights[2].shape] * var5[var9.vert1Weights[3].shape];
            float var54 = var5[var9.vert1Weights[4].shape] * var5[var9.vert1Weights[5].shape];
            float var55 = var5[var9.vert1Weights[6].shape] * var5[var9.vert1Weights[7].shape];
            float var56 = var5[var9.vert2Weights[0].shape] * var5[var9.vert2Weights[1].shape];
            float var57 = var5[var9.vert2Weights[2].shape] * var5[var9.vert2Weights[3].shape];
            float var58 = var5[var9.vert2Weights[4].shape] * var5[var9.vert2Weights[5].shape];
            float var59 = var5[var9.vert2Weights[6].shape] * var5[var9.vert2Weights[7].shape];
            float var60 = var5[var9.vert3Weights[0].shape] * var5[var9.vert3Weights[1].shape];
            float var61 = var5[var9.vert3Weights[2].shape] * var5[var9.vert3Weights[3].shape];
            float var62 = var5[var9.vert3Weights[4].shape] * var5[var9.vert3Weights[5].shape];
            float var63 = var5[var9.vert3Weights[6].shape] * var5[var9.vert3Weights[7].shape];
            this.brightness[var43.vert0] = Math.clamp(var72 * var48 + var74 * var49 + var76 * var50 + var77 * var51, 0.0F, 1.0F);
            this.brightness[var43.vert1] = Math.clamp(var72 * var52 + var74 * var53 + var76 * var54 + var77 * var55, 0.0F, 1.0F);
            this.brightness[var43.vert2] = Math.clamp(var72 * var56 + var74 * var57 + var76 * var58 + var77 * var59, 0.0F, 1.0F);
            this.brightness[var43.vert3] = Math.clamp(var72 * var60 + var74 * var61 + var76 * var62 + var77 * var63, 0.0F, 1.0F);
            int var64 = this.blend(var22, var13, var37, var71);
            int var65 = this.blend(var19, var13, var36, var71);
            int var66 = this.blend(var19, var16, var38, var71);
            int var67 = this.blend(var22, var16, var39, var71);
            this.lightmap[var43.vert0] = this.blend(var64, var65, var66, var67, var48, var49, var50, var51);
            this.lightmap[var43.vert1] = this.blend(var64, var65, var66, var67, var52, var53, var54, var55);
            this.lightmap[var43.vert2] = this.blend(var64, var65, var66, var67, var56, var57, var58, var59);
            this.lightmap[var43.vert3] = this.blend(var64, var65, var66, var67, var60, var61, var62, var63);
         } else {
            float var44 = (var23 + var14 + var33 + var42) * 0.25F;
            float var45 = (var20 + var14 + var32 + var42) * 0.25F;
            float var46 = (var20 + var17 + var34 + var42) * 0.25F;
            float var47 = (var23 + var17 + var35 + var42) * 0.25F;
            this.lightmap[var43.vert0] = this.blend(var22, var13, var37, var71);
            this.lightmap[var43.vert1] = this.blend(var19, var13, var36, var71);
            this.lightmap[var43.vert2] = this.blend(var19, var16, var38, var71);
            this.lightmap[var43.vert3] = this.blend(var22, var16, var39, var71);
            this.brightness[var43.vert0] = var44;
            this.brightness[var43.vert1] = var45;
            this.brightness[var43.vert2] = var46;
            this.brightness[var43.vert3] = var47;
         }

         float var73 = var1.getShade(var4, var7);

         for(int var75 = 0; var75 < this.brightness.length; ++var75) {
            float[] var10000 = this.brightness;
            var10000[var75] *= var73;
         }

      }

      private int blend(int var1, int var2, int var3, int var4) {
         if (var1 == 0) {
            var1 = var4;
         }

         if (var2 == 0) {
            var2 = var4;
         }

         if (var3 == 0) {
            var3 = var4;
         }

         return var1 + var2 + var3 + var4 >> 2 & 16711935;
      }

      private int blend(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         int var9 = (int)((float)(var1 >> 16 & 255) * var5 + (float)(var2 >> 16 & 255) * var6 + (float)(var3 >> 16 & 255) * var7 + (float)(var4 >> 16 & 255) * var8) & 255;
         int var10 = (int)((float)(var1 & 255) * var5 + (float)(var2 & 255) * var6 + (float)(var3 & 255) * var7 + (float)(var4 & 255) * var8) & 255;
         return var9 << 16 | var10;
      }
   }

   protected static enum SizeInfo {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      final int shape;

      private SizeInfo(final Direction var3, final boolean var4) {
         this.shape = var3.get3DDataValue() + (var4 ? ModelBlockRenderer.DIRECTIONS.length : 0);
      }

      // $FF: synthetic method
      private static SizeInfo[] $values() {
         return new SizeInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
      }
   }

   protected static enum AdjacencyInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH});

      final Direction[] corners;
      final boolean doNonCubicWeight;
      final SizeInfo[] vert0Weights;
      final SizeInfo[] vert1Weights;
      final SizeInfo[] vert2Weights;
      final SizeInfo[] vert3Weights;
      private static final AdjacencyInfo[] BY_FACING = (AdjacencyInfo[])Util.make(new AdjacencyInfo[6], (var0) -> {
         var0[Direction.DOWN.get3DDataValue()] = DOWN;
         var0[Direction.UP.get3DDataValue()] = UP;
         var0[Direction.NORTH.get3DDataValue()] = NORTH;
         var0[Direction.SOUTH.get3DDataValue()] = SOUTH;
         var0[Direction.WEST.get3DDataValue()] = WEST;
         var0[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AdjacencyInfo(final Direction[] var3, final float var4, final boolean var5, final SizeInfo[] var6, final SizeInfo[] var7, final SizeInfo[] var8, final SizeInfo[] var9) {
         this.corners = var3;
         this.doNonCubicWeight = var5;
         this.vert0Weights = var6;
         this.vert1Weights = var7;
         this.vert2Weights = var8;
         this.vert3Weights = var9;
      }

      public static AdjacencyInfo fromFacing(Direction var0) {
         return BY_FACING[var0.get3DDataValue()];
      }

      // $FF: synthetic method
      private static AdjacencyInfo[] $values() {
         return new AdjacencyInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }
}
