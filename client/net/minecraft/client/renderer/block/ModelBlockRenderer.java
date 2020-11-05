package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ModelBlockRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockColors blockColors;
   private static final ThreadLocal<ModelBlockRenderer.Cache> CACHE = ThreadLocal.withInitial(() -> {
      return new ModelBlockRenderer.Cache();
   });

   public ModelBlockRenderer(BlockColors var1) {
      super();
      this.blockColors = var1;
   }

   public boolean tesselateBlock(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, Random var8, long var9, int var11) {
      boolean var12 = Minecraft.useAmbientOcclusion() && var3.getLightEmission() == 0 && var2.useAmbientOcclusion();
      Vec3 var13 = var3.getOffset(var1, var4);
      var5.translate(var13.x, var13.y, var13.z);

      try {
         return var12 ? this.tesselateWithAO(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11) : this.tesselateWithoutAO(var1, var2, var3, var4, var5, var6, var7, var8, var9, var11);
      } catch (Throwable var17) {
         CrashReport var15 = CrashReport.forThrowable(var17, "Tesselating block model");
         CrashReportCategory var16 = var15.addCategory("Block model being tesselated");
         CrashReportCategory.populateBlockDetails(var16, var1, var4, var3);
         var16.setDetail("Using AO", (Object)var12);
         throw new ReportedException(var15);
      }
   }

   public boolean tesselateWithAO(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, Random var8, long var9, int var11) {
      boolean var12 = false;
      float[] var13 = new float[DIRECTIONS.length * 2];
      BitSet var14 = new BitSet(3);
      ModelBlockRenderer.AmbientOcclusionFace var15 = new ModelBlockRenderer.AmbientOcclusionFace();
      BlockPos.MutableBlockPos var16 = var4.mutable();
      Direction[] var17 = DIRECTIONS;
      int var18 = var17.length;

      for(int var19 = 0; var19 < var18; ++var19) {
         Direction var20 = var17[var19];
         var8.setSeed(var9);
         List var21 = var2.getQuads(var3, var20, var8);
         if (!var21.isEmpty()) {
            var16.setWithOffset(var4, var20);
            if (!var7 || Block.shouldRenderFace(var3, var1, var4, var20, var16)) {
               this.renderModelFaceAO(var1, var3, var4, var5, var6, var21, var13, var14, var15, var11);
               var12 = true;
            }
         }
      }

      var8.setSeed(var9);
      List var22 = var2.getQuads(var3, (Direction)null, var8);
      if (!var22.isEmpty()) {
         this.renderModelFaceAO(var1, var3, var4, var5, var6, var22, var13, var14, var15, var11);
         var12 = true;
      }

      return var12;
   }

   public boolean tesselateWithoutAO(BlockAndTintGetter var1, BakedModel var2, BlockState var3, BlockPos var4, PoseStack var5, VertexConsumer var6, boolean var7, Random var8, long var9, int var11) {
      boolean var12 = false;
      BitSet var13 = new BitSet(3);
      BlockPos.MutableBlockPos var14 = var4.mutable();
      Direction[] var15 = DIRECTIONS;
      int var16 = var15.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         Direction var18 = var15[var17];
         var8.setSeed(var9);
         List var19 = var2.getQuads(var3, var18, var8);
         if (!var19.isEmpty()) {
            var14.setWithOffset(var4, var18);
            if (!var7 || Block.shouldRenderFace(var3, var1, var4, var18, var14)) {
               int var20 = LevelRenderer.getLightColor(var1, var3, var14);
               this.renderModelFaceFlat(var1, var3, var4, var20, var11, false, var5, var6, var19, var13);
               var12 = true;
            }
         }
      }

      var8.setSeed(var9);
      List var21 = var2.getQuads(var3, (Direction)null, var8);
      if (!var21.isEmpty()) {
         this.renderModelFaceFlat(var1, var3, var4, -1, var11, true, var5, var6, var21, var13);
         var12 = true;
      }

      return var12;
   }

   private void renderModelFaceAO(BlockAndTintGetter var1, BlockState var2, BlockPos var3, PoseStack var4, VertexConsumer var5, List<BakedQuad> var6, float[] var7, BitSet var8, ModelBlockRenderer.AmbientOcclusionFace var9, int var10) {
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

      var4.putBulkData(var5, var6, new float[]{var7, var8, var9, var10}, var16, var17, var18, new int[]{var11, var12, var13, var14}, var15, true);
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
      switch(var5) {
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
      Random var10 = new Random();
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
      for(Iterator var8 = var5.iterator(); var8.hasNext(); var1.putBulkData(var0, var9, var10, var11, var12, var6, var7)) {
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
      ((ModelBlockRenderer.Cache)CACHE.get()).enable();
   }

   public static void clearCache() {
      ((ModelBlockRenderer.Cache)CACHE.get()).disable();
   }

   public static enum AdjacencyInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final ModelBlockRenderer.SizeInfo[] vert0Weights;
      private final ModelBlockRenderer.SizeInfo[] vert1Weights;
      private final ModelBlockRenderer.SizeInfo[] vert2Weights;
      private final ModelBlockRenderer.SizeInfo[] vert3Weights;
      private static final ModelBlockRenderer.AdjacencyInfo[] BY_FACING = (ModelBlockRenderer.AdjacencyInfo[])Util.make(new ModelBlockRenderer.AdjacencyInfo[6], (var0) -> {
         var0[Direction.DOWN.get3DDataValue()] = DOWN;
         var0[Direction.UP.get3DDataValue()] = UP;
         var0[Direction.NORTH.get3DDataValue()] = NORTH;
         var0[Direction.SOUTH.get3DDataValue()] = SOUTH;
         var0[Direction.WEST.get3DDataValue()] = WEST;
         var0[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AdjacencyInfo(Direction[] var3, float var4, boolean var5, ModelBlockRenderer.SizeInfo[] var6, ModelBlockRenderer.SizeInfo[] var7, ModelBlockRenderer.SizeInfo[] var8, ModelBlockRenderer.SizeInfo[] var9) {
         this.corners = var3;
         this.doNonCubicWeight = var5;
         this.vert0Weights = var6;
         this.vert1Weights = var7;
         this.vert2Weights = var8;
         this.vert3Weights = var9;
      }

      public static ModelBlockRenderer.AdjacencyInfo fromFacing(Direction var0) {
         return BY_FACING[var0.get3DDataValue()];
      }
   }

   public static enum SizeInfo {
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

      private final int shape;

      private SizeInfo(Direction var3, boolean var4) {
         this.shape = var3.get3DDataValue() + (var4 ? ModelBlockRenderer.DIRECTIONS.length : 0);
      }
   }

   class AmbientOcclusionFace {
      private final float[] brightness = new float[4];
      private final int[] lightmap = new int[4];

      public AmbientOcclusionFace() {
         super();
      }

      public void calculate(BlockAndTintGetter var1, BlockState var2, BlockPos var3, Direction var4, float[] var5, BitSet var6, boolean var7) {
         BlockPos var8 = var6.get(0) ? var3.relative(var4) : var3;
         ModelBlockRenderer.AdjacencyInfo var9 = ModelBlockRenderer.AdjacencyInfo.fromFacing(var4);
         BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
         ModelBlockRenderer.Cache var11 = (ModelBlockRenderer.Cache)ModelBlockRenderer.CACHE.get();
         var10.setWithOffset(var8, var9.corners[0]);
         BlockState var12 = var1.getBlockState(var10);
         int var13 = var11.getLightColor(var12, var1, var10);
         float var14 = var11.getShadeBrightness(var12, var1, var10);
         var10.setWithOffset(var8, var9.corners[1]);
         BlockState var15 = var1.getBlockState(var10);
         int var16 = var11.getLightColor(var15, var1, var10);
         float var17 = var11.getShadeBrightness(var15, var1, var10);
         var10.setWithOffset(var8, var9.corners[2]);
         BlockState var18 = var1.getBlockState(var10);
         int var19 = var11.getLightColor(var18, var1, var10);
         float var20 = var11.getShadeBrightness(var18, var1, var10);
         var10.setWithOffset(var8, var9.corners[3]);
         BlockState var21 = var1.getBlockState(var10);
         int var22 = var11.getLightColor(var21, var1, var10);
         float var23 = var11.getShadeBrightness(var21, var1, var10);
         var10.setWithOffset(var8, var9.corners[0]).move(var4);
         boolean var24 = var1.getBlockState(var10).getLightBlock(var1, var10) == 0;
         var10.setWithOffset(var8, var9.corners[1]).move(var4);
         boolean var25 = var1.getBlockState(var10).getLightBlock(var1, var10) == 0;
         var10.setWithOffset(var8, var9.corners[2]).move(var4);
         boolean var26 = var1.getBlockState(var10).getLightBlock(var1, var10) == 0;
         var10.setWithOffset(var8, var9.corners[3]).move(var4);
         boolean var27 = var1.getBlockState(var10).getLightBlock(var1, var10) == 0;
         float var28;
         int var32;
         BlockState var36;
         if (!var26 && !var24) {
            var28 = var14;
            var32 = var13;
         } else {
            var10.setWithOffset(var8, var9.corners[0]).move(var9.corners[2]);
            var36 = var1.getBlockState(var10);
            var28 = var11.getShadeBrightness(var36, var1, var10);
            var32 = var11.getLightColor(var36, var1, var10);
         }

         float var29;
         int var33;
         if (!var27 && !var24) {
            var29 = var14;
            var33 = var13;
         } else {
            var10.setWithOffset(var8, var9.corners[0]).move(var9.corners[3]);
            var36 = var1.getBlockState(var10);
            var29 = var11.getShadeBrightness(var36, var1, var10);
            var33 = var11.getLightColor(var36, var1, var10);
         }

         float var30;
         int var34;
         if (!var26 && !var25) {
            var30 = var14;
            var34 = var13;
         } else {
            var10.setWithOffset(var8, var9.corners[1]).move(var9.corners[2]);
            var36 = var1.getBlockState(var10);
            var30 = var11.getShadeBrightness(var36, var1, var10);
            var34 = var11.getLightColor(var36, var1, var10);
         }

         float var31;
         int var35;
         if (!var27 && !var25) {
            var31 = var14;
            var35 = var13;
         } else {
            var10.setWithOffset(var8, var9.corners[1]).move(var9.corners[3]);
            var36 = var1.getBlockState(var10);
            var31 = var11.getShadeBrightness(var36, var1, var10);
            var35 = var11.getLightColor(var36, var1, var10);
         }

         int var64 = var11.getLightColor(var2, var1, var3);
         var10.setWithOffset(var3, var4);
         BlockState var37 = var1.getBlockState(var10);
         if (var6.get(0) || !var37.isSolidRender(var1, var10)) {
            var64 = var11.getLightColor(var37, var1, var10);
         }

         float var38 = var6.get(0) ? var11.getShadeBrightness(var1.getBlockState(var8), var1, var8) : var11.getShadeBrightness(var1.getBlockState(var3), var1, var3);
         ModelBlockRenderer.AmbientVertexRemap var39 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(var4);
         float var40;
         float var41;
         float var42;
         float var43;
         if (var6.get(1) && var9.doNonCubicWeight) {
            var40 = (var23 + var14 + var29 + var38) * 0.25F;
            var41 = (var20 + var14 + var28 + var38) * 0.25F;
            var42 = (var20 + var17 + var30 + var38) * 0.25F;
            var43 = (var23 + var17 + var31 + var38) * 0.25F;
            float var44 = var5[var9.vert0Weights[0].shape] * var5[var9.vert0Weights[1].shape];
            float var45 = var5[var9.vert0Weights[2].shape] * var5[var9.vert0Weights[3].shape];
            float var46 = var5[var9.vert0Weights[4].shape] * var5[var9.vert0Weights[5].shape];
            float var47 = var5[var9.vert0Weights[6].shape] * var5[var9.vert0Weights[7].shape];
            float var48 = var5[var9.vert1Weights[0].shape] * var5[var9.vert1Weights[1].shape];
            float var49 = var5[var9.vert1Weights[2].shape] * var5[var9.vert1Weights[3].shape];
            float var50 = var5[var9.vert1Weights[4].shape] * var5[var9.vert1Weights[5].shape];
            float var51 = var5[var9.vert1Weights[6].shape] * var5[var9.vert1Weights[7].shape];
            float var52 = var5[var9.vert2Weights[0].shape] * var5[var9.vert2Weights[1].shape];
            float var53 = var5[var9.vert2Weights[2].shape] * var5[var9.vert2Weights[3].shape];
            float var54 = var5[var9.vert2Weights[4].shape] * var5[var9.vert2Weights[5].shape];
            float var55 = var5[var9.vert2Weights[6].shape] * var5[var9.vert2Weights[7].shape];
            float var56 = var5[var9.vert3Weights[0].shape] * var5[var9.vert3Weights[1].shape];
            float var57 = var5[var9.vert3Weights[2].shape] * var5[var9.vert3Weights[3].shape];
            float var58 = var5[var9.vert3Weights[4].shape] * var5[var9.vert3Weights[5].shape];
            float var59 = var5[var9.vert3Weights[6].shape] * var5[var9.vert3Weights[7].shape];
            this.brightness[var39.vert0] = var40 * var44 + var41 * var45 + var42 * var46 + var43 * var47;
            this.brightness[var39.vert1] = var40 * var48 + var41 * var49 + var42 * var50 + var43 * var51;
            this.brightness[var39.vert2] = var40 * var52 + var41 * var53 + var42 * var54 + var43 * var55;
            this.brightness[var39.vert3] = var40 * var56 + var41 * var57 + var42 * var58 + var43 * var59;
            int var60 = this.blend(var22, var13, var33, var64);
            int var61 = this.blend(var19, var13, var32, var64);
            int var62 = this.blend(var19, var16, var34, var64);
            int var63 = this.blend(var22, var16, var35, var64);
            this.lightmap[var39.vert0] = this.blend(var60, var61, var62, var63, var44, var45, var46, var47);
            this.lightmap[var39.vert1] = this.blend(var60, var61, var62, var63, var48, var49, var50, var51);
            this.lightmap[var39.vert2] = this.blend(var60, var61, var62, var63, var52, var53, var54, var55);
            this.lightmap[var39.vert3] = this.blend(var60, var61, var62, var63, var56, var57, var58, var59);
         } else {
            var40 = (var23 + var14 + var29 + var38) * 0.25F;
            var41 = (var20 + var14 + var28 + var38) * 0.25F;
            var42 = (var20 + var17 + var30 + var38) * 0.25F;
            var43 = (var23 + var17 + var31 + var38) * 0.25F;
            this.lightmap[var39.vert0] = this.blend(var22, var13, var33, var64);
            this.lightmap[var39.vert1] = this.blend(var19, var13, var32, var64);
            this.lightmap[var39.vert2] = this.blend(var19, var16, var34, var64);
            this.lightmap[var39.vert3] = this.blend(var22, var16, var35, var64);
            this.brightness[var39.vert0] = var40;
            this.brightness[var39.vert1] = var41;
            this.brightness[var39.vert2] = var42;
            this.brightness[var39.vert3] = var43;
         }

         var40 = var1.getShade(var4, var7);

         for(int var65 = 0; var65 < this.brightness.length; ++var65) {
            float[] var10000 = this.brightness;
            var10000[var65] *= var40;
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

   static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap colorCache;
      private final Long2FloatLinkedOpenHashMap brightnessCache;

      private Cache() {
         super();
         this.colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
            Long2IntLinkedOpenHashMap var1 = new Long2IntLinkedOpenHashMap(100, 0.25F) {
               protected void rehash(int var1) {
               }
            };
            var1.defaultReturnValue(2147483647);
            return var1;
         });
         this.brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
            Long2FloatLinkedOpenHashMap var1 = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
               protected void rehash(int var1) {
               }
            };
            var1.defaultReturnValue(0.0F / 0.0);
            return var1;
         });
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

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }

   static enum AmbientVertexRemap {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final ModelBlockRenderer.AmbientVertexRemap[] BY_FACING = (ModelBlockRenderer.AmbientVertexRemap[])Util.make(new ModelBlockRenderer.AmbientVertexRemap[6], (var0) -> {
         var0[Direction.DOWN.get3DDataValue()] = DOWN;
         var0[Direction.UP.get3DDataValue()] = UP;
         var0[Direction.NORTH.get3DDataValue()] = NORTH;
         var0[Direction.SOUTH.get3DDataValue()] = SOUTH;
         var0[Direction.WEST.get3DDataValue()] = WEST;
         var0[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AmbientVertexRemap(int var3, int var4, int var5, int var6) {
         this.vert0 = var3;
         this.vert1 = var4;
         this.vert2 = var5;
         this.vert3 = var6;
      }

      public static ModelBlockRenderer.AmbientVertexRemap fromFacing(Direction var0) {
         return BY_FACING[var0.get3DDataValue()];
      }
   }
}
