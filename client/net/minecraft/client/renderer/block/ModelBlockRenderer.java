package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.List;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ModelBlockRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
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

   private static boolean shouldRenderFace(BlockAndTintGetter var0, BlockState var1, boolean var2, Direction var3, BlockPos var4) {
      if (!var2) {
         return true;
      } else {
         BlockState var5 = var0.getBlockState(var4);
         return Block.shouldRenderFace(var1, var5, var3);
      }
   }

   public void tesselateWithAO(BlockAndTintGetter var1, List<BlockModelPart> var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, int var8) {
      AmbientOcclusionRenderStorage var9 = new AmbientOcclusionRenderStorage();
      int var10 = 0;
      int var11 = 0;

      for(BlockModelPart var13 : var2) {
         for(Direction var17 : DIRECTIONS) {
            int var18 = 1 << var17.ordinal();
            boolean var19 = (var10 & var18) == 1;
            boolean var20 = (var11 & var18) == 1;
            if (!var19 || var20) {
               List var21 = var13.getQuads(var17);
               if (!var21.isEmpty()) {
                  if (!var19) {
                     var20 = shouldRenderFace(var1, var3, var7, var17, var9.scratchPos.setWithOffset(var4, (Direction)var17));
                     var10 |= var18;
                     if (var20) {
                        var11 |= var18;
                     }
                  }

                  if (var20) {
                     this.renderModelFaceAO(var1, var3, var4, var5, var6, var21, var9, var8);
                  }
               }
            }
         }

         List var22 = var13.getQuads((Direction)null);
         if (!var22.isEmpty()) {
            this.renderModelFaceAO(var1, var3, var4, var5, var6, var22, var9, var8);
         }
      }

   }

   public void tesselateWithoutAO(BlockAndTintGetter var1, List<BlockModelPart> var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, int var8) {
      CommonRenderStorage var9 = new CommonRenderStorage();
      int var10 = 0;
      int var11 = 0;

      for(BlockModelPart var13 : var2) {
         for(Direction var17 : DIRECTIONS) {
            int var18 = 1 << var17.ordinal();
            boolean var19 = (var10 & var18) == 1;
            boolean var20 = (var11 & var18) == 1;
            if (!var19 || var20) {
               List var21 = var13.getQuads(var17);
               if (!var21.isEmpty()) {
                  BlockPos.MutableBlockPos var22 = var9.scratchPos.setWithOffset(var4, (Direction)var17);
                  if (!var19) {
                     var20 = shouldRenderFace(var1, var3, var7, var17, var22);
                     var10 |= var18;
                     if (var20) {
                        var11 |= var18;
                     }
                  }

                  if (var20) {
                     int var23 = var9.cache.getLightColor(var3, var1, var22);
                     this.renderModelFaceFlat(var1, var3, var4, var23, var8, false, var5, var6, var21, var9);
                  }
               }
            }
         }

         List var24 = var13.getQuads((Direction)null);
         if (!var24.isEmpty()) {
            this.renderModelFaceFlat(var1, var3, var4, -1, var8, true, var5, var6, var24, var9);
         }
      }

   }

   private void renderModelFaceAO(BlockAndTintGetter var1, BlockState var2, BlockPos var3, PoseStack var4, VertexConsumer var5, List<BakedQuad> var6, AmbientOcclusionRenderStorage var7, int var8) {
      for(BakedQuad var10 : var6) {
         calculateShape(var1, var2, var3, var10.vertices(), var10.direction(), var7);
         var7.calculate(var1, var2, var3, var10.direction(), var10.shade());
         this.putQuadData(var1, var2, var3, var5, var4.last(), var10, var7, var8);
      }

   }

   private void putQuadData(BlockAndTintGetter var1, BlockState var2, BlockPos var3, VertexConsumer var4, PoseStack.Pose var5, BakedQuad var6, CommonRenderStorage var7, int var8) {
      int var12 = var6.tintIndex();
      float var9;
      float var10;
      float var11;
      if (var12 != -1) {
         int var13;
         if (var7.tintCacheIndex == var12) {
            var13 = var7.tintCacheValue;
         } else {
            var13 = this.blockColors.getColor(var2, var1, var3, var12);
            var7.tintCacheIndex = var12;
            var7.tintCacheValue = var13;
         }

         var9 = ARGB.redFloat(var13);
         var10 = ARGB.greenFloat(var13);
         var11 = ARGB.blueFloat(var13);
      } else {
         var9 = 1.0F;
         var10 = 1.0F;
         var11 = 1.0F;
      }

      var4.putBulkData(var5, var6, var7.brightness, var9, var10, var11, 1.0F, var7.lightmap, var8, true);
   }

   private static void calculateShape(BlockAndTintGetter var0, BlockState var1, BlockPos var2, int[] var3, Direction var4, CommonRenderStorage var5) {
      float var6 = 32.0F;
      float var7 = 32.0F;
      float var8 = 32.0F;
      float var9 = -32.0F;
      float var10 = -32.0F;
      float var11 = -32.0F;

      for(int var12 = 0; var12 < 4; ++var12) {
         float var13 = Float.intBitsToFloat(var3[var12 * 8]);
         float var14 = Float.intBitsToFloat(var3[var12 * 8 + 1]);
         float var15 = Float.intBitsToFloat(var3[var12 * 8 + 2]);
         var6 = Math.min(var6, var13);
         var7 = Math.min(var7, var14);
         var8 = Math.min(var8, var15);
         var9 = Math.max(var9, var13);
         var10 = Math.max(var10, var14);
         var11 = Math.max(var11, var15);
      }

      if (var5 instanceof AmbientOcclusionRenderStorage var16) {
         var16.faceShape[ModelBlockRenderer.SizeInfo.WEST.index] = var6;
         var16.faceShape[ModelBlockRenderer.SizeInfo.EAST.index] = var9;
         var16.faceShape[ModelBlockRenderer.SizeInfo.DOWN.index] = var7;
         var16.faceShape[ModelBlockRenderer.SizeInfo.UP.index] = var10;
         var16.faceShape[ModelBlockRenderer.SizeInfo.NORTH.index] = var8;
         var16.faceShape[ModelBlockRenderer.SizeInfo.SOUTH.index] = var11;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_WEST.index] = 1.0F - var6;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_EAST.index] = 1.0F - var9;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_DOWN.index] = 1.0F - var7;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_UP.index] = 1.0F - var10;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_NORTH.index] = 1.0F - var8;
         var16.faceShape[ModelBlockRenderer.SizeInfo.FLIP_SOUTH.index] = 1.0F - var11;
      }

      float var17 = 1.0E-4F;
      float var18 = 0.9999F;
      boolean var10001;
      switch (var4) {
         case DOWN:
         case UP:
            var10001 = var6 >= 1.0E-4F || var8 >= 1.0E-4F || var9 <= 0.9999F || var11 <= 0.9999F;
            break;
         case NORTH:
         case SOUTH:
            var10001 = var6 >= 1.0E-4F || var7 >= 1.0E-4F || var9 <= 0.9999F || var10 <= 0.9999F;
            break;
         case WEST:
         case EAST:
            var10001 = var7 >= 1.0E-4F || var8 >= 1.0E-4F || var10 <= 0.9999F || var11 <= 0.9999F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      var5.facePartial = var10001;
      switch (var4) {
         case DOWN -> var10001 = var7 == var10 && (var7 < 1.0E-4F || var1.isCollisionShapeFullBlock(var0, var2));
         case UP -> var10001 = var7 == var10 && (var10 > 0.9999F || var1.isCollisionShapeFullBlock(var0, var2));
         case NORTH -> var10001 = var8 == var11 && (var8 < 1.0E-4F || var1.isCollisionShapeFullBlock(var0, var2));
         case SOUTH -> var10001 = var8 == var11 && (var11 > 0.9999F || var1.isCollisionShapeFullBlock(var0, var2));
         case WEST -> var10001 = var6 == var9 && (var6 < 1.0E-4F || var1.isCollisionShapeFullBlock(var0, var2));
         case EAST -> var10001 = var6 == var9 && (var9 > 0.9999F || var1.isCollisionShapeFullBlock(var0, var2));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var5.faceCubic = var10001;
   }

   private void renderModelFaceFlat(BlockAndTintGetter var1, BlockState var2, BlockPos var3, int var4, int var5, boolean var6, PoseStack var7, VertexConsumer var8, List<BakedQuad> var9, CommonRenderStorage var10) {
      for(BakedQuad var12 : var9) {
         if (var6) {
            calculateShape(var1, var2, var3, var12.vertices(), var12.direction(), var10);
            Object var13 = var10.faceCubic ? var10.scratchPos.setWithOffset(var3, (Direction)var12.direction()) : var3;
            var4 = var10.cache.getLightColor(var2, var1, (BlockPos)var13);
         }

         float var14 = var1.getShade(var12.direction(), var12.shade());
         var10.brightness[0] = var14;
         var10.brightness[1] = var14;
         var10.brightness[2] = var14;
         var10.brightness[3] = var14;
         var10.lightmap[0] = var4;
         var10.lightmap[1] = var4;
         var10.lightmap[2] = var4;
         var10.lightmap[3] = var4;
         this.putQuadData(var1, var2, var3, var8, var7.last(), var12, var10, var5);
      }

   }

   public static void renderModel(PoseStack.Pose var0, VertexConsumer var1, BlockStateModel var2, float var3, float var4, float var5, int var6, int var7) {
      for(BlockModelPart var9 : var2.collectParts(RandomSource.create(42L))) {
         for(Direction var13 : DIRECTIONS) {
            renderQuadList(var0, var1, var3, var4, var5, var9.getQuads(var13), var6, var7);
         }

         renderQuadList(var0, var1, var3, var4, var5, var9.getQuads((Direction)null), var6, var7);
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
      private final LevelRenderer.BrightnessGetter cachedBrightnessGetter = (var1, var2) -> {
         long var3 = var2.asLong();
         int var5 = this.colorCache.get(var3);
         if (var5 != 2147483647) {
            return var5;
         } else {
            int var6 = LevelRenderer.BrightnessGetter.DEFAULT.packedBrightness(var1, var2);
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(var3, var6);
            return var6;
         }
      };

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
         return LevelRenderer.getLightColor(this.enabled ? this.cachedBrightnessGetter : LevelRenderer.BrightnessGetter.DEFAULT, var2, var1, var3);
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

   static class CommonRenderStorage {
      public final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
      public boolean faceCubic;
      public boolean facePartial;
      public final float[] brightness = new float[4];
      public final int[] lightmap = new int[4];
      public int tintCacheIndex = -1;
      public int tintCacheValue;
      public final Cache cache;

      CommonRenderStorage() {
         super();
         this.cache = (Cache)ModelBlockRenderer.CACHE.get();
      }
   }

   static class AmbientOcclusionRenderStorage extends CommonRenderStorage {
      final float[] faceShape;

      public AmbientOcclusionRenderStorage() {
         super();
         this.faceShape = new float[ModelBlockRenderer.SizeInfo.COUNT];
      }

      public void calculate(BlockAndTintGetter var1, BlockState var2, BlockPos var3, Direction var4, boolean var5) {
         BlockPos var6 = this.faceCubic ? var3.relative(var4) : var3;
         AdjacencyInfo var7 = ModelBlockRenderer.AdjacencyInfo.fromFacing(var4);
         BlockPos.MutableBlockPos var8 = this.scratchPos;
         var8.setWithOffset(var6, (Direction)var7.corners[0]);
         BlockState var9 = var1.getBlockState(var8);
         int var10 = this.cache.getLightColor(var9, var1, var8);
         float var11 = this.cache.getShadeBrightness(var9, var1, var8);
         var8.setWithOffset(var6, (Direction)var7.corners[1]);
         BlockState var12 = var1.getBlockState(var8);
         int var13 = this.cache.getLightColor(var12, var1, var8);
         float var14 = this.cache.getShadeBrightness(var12, var1, var8);
         var8.setWithOffset(var6, (Direction)var7.corners[2]);
         BlockState var15 = var1.getBlockState(var8);
         int var16 = this.cache.getLightColor(var15, var1, var8);
         float var17 = this.cache.getShadeBrightness(var15, var1, var8);
         var8.setWithOffset(var6, (Direction)var7.corners[3]);
         BlockState var18 = var1.getBlockState(var8);
         int var19 = this.cache.getLightColor(var18, var1, var8);
         float var20 = this.cache.getShadeBrightness(var18, var1, var8);
         BlockState var21 = var1.getBlockState(var8.setWithOffset(var6, (Direction)var7.corners[0]).move(var4));
         boolean var22 = !var21.isViewBlocking(var1, var8) || var21.getLightBlock() == 0;
         BlockState var23 = var1.getBlockState(var8.setWithOffset(var6, (Direction)var7.corners[1]).move(var4));
         boolean var24 = !var23.isViewBlocking(var1, var8) || var23.getLightBlock() == 0;
         BlockState var25 = var1.getBlockState(var8.setWithOffset(var6, (Direction)var7.corners[2]).move(var4));
         boolean var26 = !var25.isViewBlocking(var1, var8) || var25.getLightBlock() == 0;
         BlockState var27 = var1.getBlockState(var8.setWithOffset(var6, (Direction)var7.corners[3]).move(var4));
         boolean var28 = !var27.isViewBlocking(var1, var8) || var27.getLightBlock() == 0;
         float var29;
         int var33;
         if (!var26 && !var22) {
            var29 = var11;
            var33 = var10;
         } else {
            var8.setWithOffset(var6, (Direction)var7.corners[0]).move(var7.corners[2]);
            BlockState var37 = var1.getBlockState(var8);
            var29 = this.cache.getShadeBrightness(var37, var1, var8);
            var33 = this.cache.getLightColor(var37, var1, var8);
         }

         float var30;
         int var34;
         if (!var28 && !var22) {
            var30 = var11;
            var34 = var10;
         } else {
            var8.setWithOffset(var6, (Direction)var7.corners[0]).move(var7.corners[3]);
            BlockState var65 = var1.getBlockState(var8);
            var30 = this.cache.getShadeBrightness(var65, var1, var8);
            var34 = this.cache.getLightColor(var65, var1, var8);
         }

         float var31;
         int var35;
         if (!var26 && !var24) {
            var31 = var11;
            var35 = var10;
         } else {
            var8.setWithOffset(var6, (Direction)var7.corners[1]).move(var7.corners[2]);
            BlockState var66 = var1.getBlockState(var8);
            var31 = this.cache.getShadeBrightness(var66, var1, var8);
            var35 = this.cache.getLightColor(var66, var1, var8);
         }

         float var32;
         int var36;
         if (!var28 && !var24) {
            var32 = var11;
            var36 = var10;
         } else {
            var8.setWithOffset(var6, (Direction)var7.corners[1]).move(var7.corners[3]);
            BlockState var67 = var1.getBlockState(var8);
            var32 = this.cache.getShadeBrightness(var67, var1, var8);
            var36 = this.cache.getLightColor(var67, var1, var8);
         }

         int var68 = this.cache.getLightColor(var2, var1, var3);
         var8.setWithOffset(var3, (Direction)var4);
         BlockState var38 = var1.getBlockState(var8);
         if (this.faceCubic || !var38.isSolidRender()) {
            var68 = this.cache.getLightColor(var38, var1, var8);
         }

         float var39 = this.faceCubic ? this.cache.getShadeBrightness(var1.getBlockState(var6), var1, var6) : this.cache.getShadeBrightness(var1.getBlockState(var3), var1, var3);
         AmbientVertexRemap var40 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(var4);
         if (this.facePartial && var7.doNonCubicWeight) {
            float var69 = (var20 + var11 + var30 + var39) * 0.25F;
            float var71 = (var17 + var11 + var29 + var39) * 0.25F;
            float var73 = (var17 + var14 + var31 + var39) * 0.25F;
            float var74 = (var20 + var14 + var32 + var39) * 0.25F;
            float var45 = this.faceShape[var7.vert0Weights[0].index] * this.faceShape[var7.vert0Weights[1].index];
            float var46 = this.faceShape[var7.vert0Weights[2].index] * this.faceShape[var7.vert0Weights[3].index];
            float var47 = this.faceShape[var7.vert0Weights[4].index] * this.faceShape[var7.vert0Weights[5].index];
            float var48 = this.faceShape[var7.vert0Weights[6].index] * this.faceShape[var7.vert0Weights[7].index];
            float var49 = this.faceShape[var7.vert1Weights[0].index] * this.faceShape[var7.vert1Weights[1].index];
            float var50 = this.faceShape[var7.vert1Weights[2].index] * this.faceShape[var7.vert1Weights[3].index];
            float var51 = this.faceShape[var7.vert1Weights[4].index] * this.faceShape[var7.vert1Weights[5].index];
            float var52 = this.faceShape[var7.vert1Weights[6].index] * this.faceShape[var7.vert1Weights[7].index];
            float var53 = this.faceShape[var7.vert2Weights[0].index] * this.faceShape[var7.vert2Weights[1].index];
            float var54 = this.faceShape[var7.vert2Weights[2].index] * this.faceShape[var7.vert2Weights[3].index];
            float var55 = this.faceShape[var7.vert2Weights[4].index] * this.faceShape[var7.vert2Weights[5].index];
            float var56 = this.faceShape[var7.vert2Weights[6].index] * this.faceShape[var7.vert2Weights[7].index];
            float var57 = this.faceShape[var7.vert3Weights[0].index] * this.faceShape[var7.vert3Weights[1].index];
            float var58 = this.faceShape[var7.vert3Weights[2].index] * this.faceShape[var7.vert3Weights[3].index];
            float var59 = this.faceShape[var7.vert3Weights[4].index] * this.faceShape[var7.vert3Weights[5].index];
            float var60 = this.faceShape[var7.vert3Weights[6].index] * this.faceShape[var7.vert3Weights[7].index];
            this.brightness[var40.vert0] = Math.clamp(var69 * var45 + var71 * var46 + var73 * var47 + var74 * var48, 0.0F, 1.0F);
            this.brightness[var40.vert1] = Math.clamp(var69 * var49 + var71 * var50 + var73 * var51 + var74 * var52, 0.0F, 1.0F);
            this.brightness[var40.vert2] = Math.clamp(var69 * var53 + var71 * var54 + var73 * var55 + var74 * var56, 0.0F, 1.0F);
            this.brightness[var40.vert3] = Math.clamp(var69 * var57 + var71 * var58 + var73 * var59 + var74 * var60, 0.0F, 1.0F);
            int var61 = blend(var19, var10, var34, var68);
            int var62 = blend(var16, var10, var33, var68);
            int var63 = blend(var16, var13, var35, var68);
            int var64 = blend(var19, var13, var36, var68);
            this.lightmap[var40.vert0] = blend(var61, var62, var63, var64, var45, var46, var47, var48);
            this.lightmap[var40.vert1] = blend(var61, var62, var63, var64, var49, var50, var51, var52);
            this.lightmap[var40.vert2] = blend(var61, var62, var63, var64, var53, var54, var55, var56);
            this.lightmap[var40.vert3] = blend(var61, var62, var63, var64, var57, var58, var59, var60);
         } else {
            float var41 = (var20 + var11 + var30 + var39) * 0.25F;
            float var42 = (var17 + var11 + var29 + var39) * 0.25F;
            float var43 = (var17 + var14 + var31 + var39) * 0.25F;
            float var44 = (var20 + var14 + var32 + var39) * 0.25F;
            this.lightmap[var40.vert0] = blend(var19, var10, var34, var68);
            this.lightmap[var40.vert1] = blend(var16, var10, var33, var68);
            this.lightmap[var40.vert2] = blend(var16, var13, var35, var68);
            this.lightmap[var40.vert3] = blend(var19, var13, var36, var68);
            this.brightness[var40.vert0] = var41;
            this.brightness[var40.vert1] = var42;
            this.brightness[var40.vert2] = var43;
            this.brightness[var40.vert3] = var44;
         }

         float var70 = var1.getShade(var4, var5);

         for(int var72 = 0; var72 < this.brightness.length; ++var72) {
            float[] var10000 = this.brightness;
            var10000[var72] *= var70;
         }

      }

      private static int blend(int var0, int var1, int var2, int var3) {
         if (var0 == 0) {
            var0 = var3;
         }

         if (var1 == 0) {
            var1 = var3;
         }

         if (var2 == 0) {
            var2 = var3;
         }

         return var0 + var1 + var2 + var3 >> 2 & 16711935;
      }

      private static int blend(int var0, int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
         int var8 = (int)((float)(var0 >> 16 & 255) * var4 + (float)(var1 >> 16 & 255) * var5 + (float)(var2 >> 16 & 255) * var6 + (float)(var3 >> 16 & 255) * var7) & 255;
         int var9 = (int)((float)(var0 & 255) * var4 + (float)(var1 & 255) * var5 + (float)(var2 & 255) * var6 + (float)(var3 & 255) * var7) & 255;
         return var8 << 16 | var9;
      }
   }

   protected static enum SizeInfo {
      DOWN(0),
      UP(1),
      NORTH(2),
      SOUTH(3),
      WEST(4),
      EAST(5),
      FLIP_DOWN(6),
      FLIP_UP(7),
      FLIP_NORTH(8),
      FLIP_SOUTH(9),
      FLIP_WEST(10),
      FLIP_EAST(11);

      public static final int COUNT = values().length;
      final int index;

      private SizeInfo(final int var3) {
         this.index = var3;
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
