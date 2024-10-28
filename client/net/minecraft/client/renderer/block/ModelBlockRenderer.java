package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.Iterator;
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
import net.minecraft.client.resources.model.BakedModel;
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

   public void tesselateBlock(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, RandomSource var8, long var9, int var11) {
      boolean var12 = Minecraft.useAmbientOcclusion() && var3.getLightEmission() == 0 && var2.useAmbientOcclusion();
      var5.translate(var3.getOffset(var4));

      try {
         if (var12) {
            this.tesselateWithAO(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11);
         } else {
            this.tesselateWithoutAO(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11);
         }

      } catch (Throwable var16) {
         CrashReport var14 = CrashReport.forThrowable(var16, "Tesselating block model");
         CrashReportCategory var15 = var14.addCategory("Block model being tesselated");
         CrashReportCategory.populateBlockDetails(var15, var1, var4, var3);
         var15.setDetail("Using AO", (Object)var12);
         throw new ReportedException(var14);
      }
   }

   public void tesselateWithAO(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, RandomSource var8, long var9, int var11) {
      float[] var12 = new float[DIRECTIONS.length * 2];
      BitSet var13 = new BitSet(3);
      AmbientOcclusionFace var14 = new AmbientOcclusionFace();
      BlockPos.MutableBlockPos var15 = var4.mutable();
      Direction[] var16 = DIRECTIONS;
      int var17 = var16.length;

      for(int var18 = 0; var18 < var17; ++var18) {
         Direction var19 = var16[var18];
         var8.setSeed(var9);
         List var20 = var2.getQuads(var3, var19, var8);
         if (!var20.isEmpty()) {
            var15.setWithOffset(var4, (Direction)var19);
            if (!var7 || Block.shouldRenderFace(var3, var1.getBlockState(var15), var19)) {
               this.renderModelFaceAO(var1, var3, var4, var5, var6, var20, var12, var13, var14, var11);
            }
         }
      }

      var8.setSeed(var9);
      List var21 = var2.getQuads(var3, (Direction)null, var8);
      if (!var21.isEmpty()) {
         this.renderModelFaceAO(var1, var3, var4, var5, var6, var21, var12, var13, var14, var11);
      }

   }

   public void tesselateWithoutAO(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, RandomSource var8, long var9, int var11) {
      BitSet var12 = new BitSet(3);
      BlockPos.MutableBlockPos var13 = var4.mutable();
      Direction[] var14 = DIRECTIONS;
      int var15 = var14.length;

      for(int var16 = 0; var16 < var15; ++var16) {
         Direction var17 = var14[var16];
         var8.setSeed(var9);
         List var18 = var2.getQuads(var3, var17, var8);
         if (!var18.isEmpty()) {
            var13.setWithOffset(var4, (Direction)var17);
            if (!var7 || Block.shouldRenderFace(var3, var1.getBlockState(var13), var17)) {
               int var19 = LevelRenderer.getLightColor(var1, var3, var13);
               this.renderModelFaceFlat(var1, var3, var4, var19, var11, false, var5, var6, var18, var12);
            }
         }
      }

      var8.setSeed(var9);
      List var20 = var2.getQuads(var3, (Direction)null, var8);
      if (!var20.isEmpty()) {
         this.renderModelFaceFlat(var1, var3, var4, -1, var11, true, var5, var6, var20, var12);
      }

   }

   private void renderModelFaceAO(BlockAndTintGetter var1, BlockState var2, BlockPos var3, PoseStack var4, VertexConsumer var5, List<BakedQuad> var6, float[] var7, BitSet var8, AmbientOcclusionFace var9, int var10) {
      Iterator var11 = var6.iterator();

      while(var11.hasNext()) {
         BakedQuad var12 = (BakedQuad)var11.next();
         this.calculateShape(var1, var2, var3, var12.getVertices(), var12.getDirection(), var7, var8);
         var9.calculate(var1, var2, var3, var12.getDirection(), var7, var8, var12.isShade());
         this.putQuadData(var1, var2, var3, var5, var4.last(), var12, var9.brightness[0], var9.brightness[1], var9.brightness[2], var9.brightness[3], var9.lightmap[0], var9.lightmap[1], var9.lightmap[2], var9.lightmap[3], var10);
      }

   }

   private void putQuadData(BlockAndTintGetter var1, BlockState var2, BlockPos var3, VertexConsumer var4, PoseStack.Pose var5, BakedQuad var6, float var7, float var8, float var9, float var10, int var11, int var12, int var13, int var14, int var15) {
      float var16;
      float var17;
      float var18;
      if (var6.isTinted()) {
         int var19 = this.blockColors.getColor(var2, var1, var3, var6.getTintIndex());
         var16 = (float)(var19 >> 16 & 255) / 255.0F;
         var17 = (float)(var19 >> 8 & 255) / 255.0F;
         var18 = (float)(var19 & 255) / 255.0F;
      } else {
         var16 = 1.0F;
         var17 = 1.0F;
         var18 = 1.0F;
      }

      var4.putBulkData(var5, var6, new float[]{var7, var8, var9, var10}, var16, var17, var18, 1.0F, new int[]{var11, var12, var13, var14}, var15, true);
   }

   private void calculateShape(BlockAndTintGetter var1, BlockState var2, BlockPos var3, int[] var4, Direction var5, @Nullable float[] var6, BitSet var7) {
      float var8 = 32.0F;
      float var9 = 32.0F;
      float var10 = 32.0F;
      float var11 = -32.0F;
      float var12 = -32.0F;
      float var13 = -32.0F;

      int var14;
      float var15;
      for(var14 = 0; var14 < 4; ++var14) {
         var15 = Float.intBitsToFloat(var4[var14 * 8]);
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
         var14 = DIRECTIONS.length;
         var6[Direction.WEST.get3DDataValue() + var14] = 1.0F - var8;
         var6[Direction.EAST.get3DDataValue() + var14] = 1.0F - var11;
         var6[Direction.DOWN.get3DDataValue() + var14] = 1.0F - var9;
         var6[Direction.UP.get3DDataValue() + var14] = 1.0F - var12;
         var6[Direction.NORTH.get3DDataValue() + var14] = 1.0F - var10;
         var6[Direction.SOUTH.get3DDataValue() + var14] = 1.0F - var13;
      }

      float var18 = 1.0E-4F;
      var15 = 0.9999F;
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
      Iterator var11 = var9.iterator();

      while(var11.hasNext()) {
         BakedQuad var12 = (BakedQuad)var11.next();
         if (var6) {
            this.calculateShape(var1, var2, var3, var12.getVertices(), var12.getDirection(), (float[])null, var10);
            BlockPos var13 = var10.get(0) ? var3.relative(var12.getDirection()) : var3;
            var4 = LevelRenderer.getLightColor(var1, var2, var13);
         }

         float var14 = var1.getShade(var12.getDirection(), var12.isShade());
         this.putQuadData(var1, var2, var3, var8, var7.last(), var12, var14, var14, var14, var14, var4, var4, var4, var4, var5);
      }

   }

   public void renderModel(PoseStack.Pose var1, VertexConsumer var2, @Nullable BlockState var3, BakedModel var4, float var5, float var6, float var7, int var8, int var9) {
      RandomSource var10 = RandomSource.create();
      long var11 = 42L;
      Direction[] var13 = DIRECTIONS;
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         Direction var16 = var13[var15];
         var10.setSeed(42L);
         renderQuadList(var1, var2, var5, var6, var7, var4.getQuads(var3, var16, var10), var8, var9);
      }

      var10.setSeed(42L);
      renderQuadList(var1, var2, var5, var6, var7, var4.getQuads(var3, (Direction)null, var10), var8, var9);
   }

   private static void renderQuadList(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, List<BakedQuad> var5, int var6, int var7) {
      BakedQuad var9;
      float var10;
      float var11;
      float var12;
      for(Iterator var8 = var5.iterator(); var8.hasNext(); var1.putBulkData(var0, var9, var10, var11, var12, 1.0F, var6, var7)) {
         var9 = (BakedQuad)var8.next();
         if (var9.isTinted()) {
            var10 = Mth.clamp(var2, 0.0F, 1.0F);
            var11 = Mth.clamp(var3, 0.0F, 1.0F);
            var12 = Mth.clamp(var4, 0.0F, 1.0F);
         } else {
            var10 = 1.0F;
            var11 = 1.0F;
            var12 = 1.0F;
         }
      }

   }

   public static void enableCaching() {
      ((Cache)CACHE.get()).enable();
   }

   public static void clearCache() {
      ((Cache)CACHE.get()).disable();
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
         BlockState var40;
         if (!var29 && !var25) {
            var32 = var14;
            var36 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[0]).move(var9.corners[2]);
            var40 = var1.getBlockState(var10);
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
            var40 = var1.getBlockState(var10);
            var33 = var11.getShadeBrightness(var40, var1, var10);
            var37 = var11.getLightColor(var40, var1, var10);
         }

         float var34;
         int var38;
         if (!var29 && !var27) {
            var34 = var14;
            var38 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[1]).move(var9.corners[2]);
            var40 = var1.getBlockState(var10);
            var34 = var11.getShadeBrightness(var40, var1, var10);
            var38 = var11.getLightColor(var40, var1, var10);
         }

         float var35;
         int var39;
         if (!var31 && !var27) {
            var35 = var14;
            var39 = var13;
         } else {
            var10.setWithOffset(var8, (Direction)var9.corners[1]).move(var9.corners[3]);
            var40 = var1.getBlockState(var10);
            var35 = var11.getShadeBrightness(var40, var1, var10);
            var39 = var11.getLightColor(var40, var1, var10);
         }

         int var68 = var11.getLightColor(var2, var1, var3);
         var10.setWithOffset(var3, (Direction)var4);
         BlockState var41 = var1.getBlockState(var10);
         if (var6.get(0) || !var41.isSolidRender()) {
            var68 = var11.getLightColor(var41, var1, var10);
         }

         float var42 = var6.get(0) ? var11.getShadeBrightness(var1.getBlockState(var8), var1, var8) : var11.getShadeBrightness(var1.getBlockState(var3), var1, var3);
         AmbientVertexRemap var43 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(var4);
         float var44;
         float var45;
         float var46;
         float var47;
         if (var6.get(1) && var9.doNonCubicWeight) {
            var44 = (var23 + var14 + var33 + var42) * 0.25F;
            var45 = (var20 + var14 + var32 + var42) * 0.25F;
            var46 = (var20 + var17 + var34 + var42) * 0.25F;
            var47 = (var23 + var17 + var35 + var42) * 0.25F;
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
            this.brightness[var43.vert0] = Math.clamp(var44 * var48 + var45 * var49 + var46 * var50 + var47 * var51, 0.0F, 1.0F);
            this.brightness[var43.vert1] = Math.clamp(var44 * var52 + var45 * var53 + var46 * var54 + var47 * var55, 0.0F, 1.0F);
            this.brightness[var43.vert2] = Math.clamp(var44 * var56 + var45 * var57 + var46 * var58 + var47 * var59, 0.0F, 1.0F);
            this.brightness[var43.vert3] = Math.clamp(var44 * var60 + var45 * var61 + var46 * var62 + var47 * var63, 0.0F, 1.0F);
            int var64 = this.blend(var22, var13, var37, var68);
            int var65 = this.blend(var19, var13, var36, var68);
            int var66 = this.blend(var19, var16, var38, var68);
            int var67 = this.blend(var22, var16, var39, var68);
            this.lightmap[var43.vert0] = this.blend(var64, var65, var66, var67, var48, var49, var50, var51);
            this.lightmap[var43.vert1] = this.blend(var64, var65, var66, var67, var52, var53, var54, var55);
            this.lightmap[var43.vert2] = this.blend(var64, var65, var66, var67, var56, var57, var58, var59);
            this.lightmap[var43.vert3] = this.blend(var64, var65, var66, var67, var60, var61, var62, var63);
         } else {
            var44 = (var23 + var14 + var33 + var42) * 0.25F;
            var45 = (var20 + var14 + var32 + var42) * 0.25F;
            var46 = (var20 + var17 + var34 + var42) * 0.25F;
            var47 = (var23 + var17 + var35 + var42) * 0.25F;
            this.lightmap[var43.vert0] = this.blend(var22, var13, var37, var68);
            this.lightmap[var43.vert1] = this.blend(var19, var13, var36, var68);
            this.lightmap[var43.vert2] = this.blend(var19, var16, var38, var68);
            this.lightmap[var43.vert3] = this.blend(var22, var16, var39, var68);
            this.brightness[var43.vert0] = var44;
            this.brightness[var43.vert1] = var45;
            this.brightness[var43.vert2] = var46;
            this.brightness[var43.vert3] = var47;
         }

         var44 = var1.getShade(var4, var7);

         for(int var69 = 0; var69 < this.brightness.length; ++var69) {
            float[] var10000 = this.brightness;
            var10000[var69] *= var44;
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

   private static class Cache {
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
         int var6;
         if (this.enabled) {
            var6 = this.colorCache.get(var4);
            if (var6 != 2147483647) {
               return var6;
            }
         }

         var6 = LevelRenderer.getLightColor(var2, var1, var3);
         if (this.enabled) {
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(var4, var6);
         }

         return var6;
      }

      public float getShadeBrightness(BlockState var1, BlockAndTintGetter var2, BlockPos var3) {
         long var4 = var3.asLong();
         float var6;
         if (this.enabled) {
            var6 = this.brightnessCache.get(var4);
            if (!Float.isNaN(var6)) {
               return var6;
            }
         }

         var6 = var1.getShadeBrightness(var2, var3);
         if (this.enabled) {
            if (this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(var4, var6);
         }

         return var6;
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

   private static enum AmbientVertexRemap {
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
}
