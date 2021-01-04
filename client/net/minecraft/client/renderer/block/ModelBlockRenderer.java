package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ModelBlockRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<ModelBlockRenderer.Cache> CACHE = ThreadLocal.withInitial(() -> {
      return new ModelBlockRenderer.Cache();
   });

   public ModelBlockRenderer(BlockColors var1) {
      super();
      this.blockColors = var1;
   }

   public boolean tesselateBlock(BlockAndBiomeGetter var1, BakedModel var2, BlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = Minecraft.useAmbientOcclusion() && var3.getLightEmission() == 0 && var2.useAmbientOcclusion();

      try {
         return var10 ? this.tesselateWithAO(var1, var2, var3, var4, var5, var6, var7, var8) : this.tesselateWithoutAO(var1, var2, var3, var4, var5, var6, var7, var8);
      } catch (Throwable var14) {
         CrashReport var12 = CrashReport.forThrowable(var14, "Tesselating block model");
         CrashReportCategory var13 = var12.addCategory("Block model being tesselated");
         CrashReportCategory.populateBlockDetails(var13, var4, var3);
         var13.setDetail("Using AO", (Object)var10);
         throw new ReportedException(var12);
      }
   }

   public boolean tesselateWithAO(BlockAndBiomeGetter var1, BakedModel var2, BlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = false;
      float[] var11 = new float[Direction.values().length * 2];
      BitSet var12 = new BitSet(3);
      ModelBlockRenderer.AmbientOcclusionFace var13 = new ModelBlockRenderer.AmbientOcclusionFace();
      Direction[] var14 = Direction.values();
      int var15 = var14.length;

      for(int var16 = 0; var16 < var15; ++var16) {
         Direction var17 = var14[var16];
         var7.setSeed(var8);
         List var18 = var2.getQuads(var3, var17, var7);
         if (!var18.isEmpty() && (!var6 || Block.shouldRenderFace(var3, var1, var4, var17))) {
            this.renderModelFaceAO(var1, var3, var4, var5, var18, var11, var12, var13);
            var10 = true;
         }
      }

      var7.setSeed(var8);
      List var19 = var2.getQuads(var3, (Direction)null, var7);
      if (!var19.isEmpty()) {
         this.renderModelFaceAO(var1, var3, var4, var5, var19, var11, var12, var13);
         var10 = true;
      }

      return var10;
   }

   public boolean tesselateWithoutAO(BlockAndBiomeGetter var1, BakedModel var2, BlockState var3, BlockPos var4, BufferBuilder var5, boolean var6, Random var7, long var8) {
      boolean var10 = false;
      BitSet var11 = new BitSet(3);
      Direction[] var12 = Direction.values();
      int var13 = var12.length;

      for(int var14 = 0; var14 < var13; ++var14) {
         Direction var15 = var12[var14];
         var7.setSeed(var8);
         List var16 = var2.getQuads(var3, var15, var7);
         if (!var16.isEmpty() && (!var6 || Block.shouldRenderFace(var3, var1, var4, var15))) {
            int var17 = var3.getLightColor(var1, var4.relative(var15));
            this.renderModelFaceFlat(var1, var3, var4, var17, false, var5, var16, var11);
            var10 = true;
         }
      }

      var7.setSeed(var8);
      List var18 = var2.getQuads(var3, (Direction)null, var7);
      if (!var18.isEmpty()) {
         this.renderModelFaceFlat(var1, var3, var4, -1, true, var5, var18, var11);
         var10 = true;
      }

      return var10;
   }

   private void renderModelFaceAO(BlockAndBiomeGetter var1, BlockState var2, BlockPos var3, BufferBuilder var4, List<BakedQuad> var5, float[] var6, BitSet var7, ModelBlockRenderer.AmbientOcclusionFace var8) {
      Vec3 var9 = var2.getOffset(var1, var3);
      double var10 = (double)var3.getX() + var9.x;
      double var12 = (double)var3.getY() + var9.y;
      double var14 = (double)var3.getZ() + var9.z;
      int var16 = 0;

      for(int var17 = var5.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)var5.get(var16);
         this.calculateShape(var1, var2, var3, var18.getVertices(), var18.getDirection(), var6, var7);
         var8.calculate(var1, var2, var3, var18.getDirection(), var6, var7);
         var4.putBulkData(var18.getVertices());
         var4.faceTex2(var8.lightmap[0], var8.lightmap[1], var8.lightmap[2], var8.lightmap[3]);
         if (var18.isTinted()) {
            int var19 = this.blockColors.getColor(var2, var1, var3, var18.getTintIndex());
            float var20 = (float)(var19 >> 16 & 255) / 255.0F;
            float var21 = (float)(var19 >> 8 & 255) / 255.0F;
            float var22 = (float)(var19 & 255) / 255.0F;
            var4.faceTint(var8.brightness[0] * var20, var8.brightness[0] * var21, var8.brightness[0] * var22, 4);
            var4.faceTint(var8.brightness[1] * var20, var8.brightness[1] * var21, var8.brightness[1] * var22, 3);
            var4.faceTint(var8.brightness[2] * var20, var8.brightness[2] * var21, var8.brightness[2] * var22, 2);
            var4.faceTint(var8.brightness[3] * var20, var8.brightness[3] * var21, var8.brightness[3] * var22, 1);
         } else {
            var4.faceTint(var8.brightness[0], var8.brightness[0], var8.brightness[0], 4);
            var4.faceTint(var8.brightness[1], var8.brightness[1], var8.brightness[1], 3);
            var4.faceTint(var8.brightness[2], var8.brightness[2], var8.brightness[2], 2);
            var4.faceTint(var8.brightness[3], var8.brightness[3], var8.brightness[3], 1);
         }

         var4.postProcessFacePosition(var10, var12, var14);
      }

   }

   private void calculateShape(BlockAndBiomeGetter var1, BlockState var2, BlockPos var3, int[] var4, Direction var5, @Nullable float[] var6, BitSet var7) {
      float var8 = 32.0F;
      float var9 = 32.0F;
      float var10 = 32.0F;
      float var11 = -32.0F;
      float var12 = -32.0F;
      float var13 = -32.0F;

      int var14;
      float var15;
      for(var14 = 0; var14 < 4; ++var14) {
         var15 = Float.intBitsToFloat(var4[var14 * 7]);
         float var16 = Float.intBitsToFloat(var4[var14 * 7 + 1]);
         float var17 = Float.intBitsToFloat(var4[var14 * 7 + 2]);
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
         var14 = Direction.values().length;
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

   private void renderModelFaceFlat(BlockAndBiomeGetter var1, BlockState var2, BlockPos var3, int var4, boolean var5, BufferBuilder var6, List<BakedQuad> var7, BitSet var8) {
      Vec3 var9 = var2.getOffset(var1, var3);
      double var10 = (double)var3.getX() + var9.x;
      double var12 = (double)var3.getY() + var9.y;
      double var14 = (double)var3.getZ() + var9.z;
      int var16 = 0;

      for(int var17 = var7.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)var7.get(var16);
         if (var5) {
            this.calculateShape(var1, var2, var3, var18.getVertices(), var18.getDirection(), (float[])null, var8);
            BlockPos var19 = var8.get(0) ? var3.relative(var18.getDirection()) : var3;
            var4 = var2.getLightColor(var1, var19);
         }

         var6.putBulkData(var18.getVertices());
         var6.faceTex2(var4, var4, var4, var4);
         if (var18.isTinted()) {
            int var23 = this.blockColors.getColor(var2, var1, var3, var18.getTintIndex());
            float var20 = (float)(var23 >> 16 & 255) / 255.0F;
            float var21 = (float)(var23 >> 8 & 255) / 255.0F;
            float var22 = (float)(var23 & 255) / 255.0F;
            var6.faceTint(var20, var21, var22, 4);
            var6.faceTint(var20, var21, var22, 3);
            var6.faceTint(var20, var21, var22, 2);
            var6.faceTint(var20, var21, var22, 1);
         }

         var6.postProcessFacePosition(var10, var12, var14);
      }

   }

   public void renderModel(BakedModel var1, float var2, float var3, float var4, float var5) {
      this.renderModel((BlockState)null, var1, var2, var3, var4, var5);
   }

   public void renderModel(@Nullable BlockState var1, BakedModel var2, float var3, float var4, float var5, float var6) {
      Random var7 = new Random();
      long var8 = 42L;
      Direction[] var10 = Direction.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction var13 = var10[var12];
         var7.setSeed(42L);
         this.renderQuadList(var3, var4, var5, var6, var2.getQuads(var1, var13, var7));
      }

      var7.setSeed(42L);
      this.renderQuadList(var3, var4, var5, var6, var2.getQuads(var1, (Direction)null, var7));
   }

   public void renderSingleBlock(BakedModel var1, BlockState var2, float var3, boolean var4) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int var5 = this.blockColors.getColor(var2, (BlockAndBiomeGetter)null, (BlockPos)null, 0);
      float var6 = (float)(var5 >> 16 & 255) / 255.0F;
      float var7 = (float)(var5 >> 8 & 255) / 255.0F;
      float var8 = (float)(var5 & 255) / 255.0F;
      if (!var4) {
         GlStateManager.color4f(var3, var3, var3, 1.0F);
      }

      this.renderModel(var2, var1, var3, var6, var7, var8);
   }

   private void renderQuadList(float var1, float var2, float var3, float var4, List<BakedQuad> var5) {
      Tesselator var6 = Tesselator.getInstance();
      BufferBuilder var7 = var6.getBuilder();
      int var8 = 0;

      for(int var9 = var5.size(); var8 < var9; ++var8) {
         BakedQuad var10 = (BakedQuad)var5.get(var8);
         var7.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
         var7.putBulkData(var10.getVertices());
         if (var10.isTinted()) {
            var7.fixupQuadColor(var2 * var1, var3 * var1, var4 * var1);
         } else {
            var7.fixupQuadColor(var1, var1, var1);
         }

         Vec3i var11 = var10.getDirection().getNormal();
         var7.postNormal((float)var11.getX(), (float)var11.getY(), (float)var11.getZ());
         var6.end();
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
         this.shape = var3.get3DDataValue() + (var4 ? Direction.values().length : 0);
      }
   }

   class AmbientOcclusionFace {
      private final float[] brightness = new float[4];
      private final int[] lightmap = new int[4];

      public AmbientOcclusionFace() {
         super();
      }

      public void calculate(BlockAndBiomeGetter var1, BlockState var2, BlockPos var3, Direction var4, float[] var5, BitSet var6) {
         BlockPos var7 = var6.get(0) ? var3.relative(var4) : var3;
         ModelBlockRenderer.AdjacencyInfo var8 = ModelBlockRenderer.AdjacencyInfo.fromFacing(var4);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         ModelBlockRenderer.Cache var10 = (ModelBlockRenderer.Cache)ModelBlockRenderer.CACHE.get();
         var9.set((Vec3i)var7).move(var8.corners[0]);
         BlockState var11 = var1.getBlockState(var9);
         int var12 = var10.getLightColor(var11, var1, var9);
         float var13 = var10.getShadeBrightness(var11, var1, var9);
         var9.set((Vec3i)var7).move(var8.corners[1]);
         BlockState var14 = var1.getBlockState(var9);
         int var15 = var10.getLightColor(var14, var1, var9);
         float var16 = var10.getShadeBrightness(var14, var1, var9);
         var9.set((Vec3i)var7).move(var8.corners[2]);
         BlockState var17 = var1.getBlockState(var9);
         int var18 = var10.getLightColor(var17, var1, var9);
         float var19 = var10.getShadeBrightness(var17, var1, var9);
         var9.set((Vec3i)var7).move(var8.corners[3]);
         BlockState var20 = var1.getBlockState(var9);
         int var21 = var10.getLightColor(var20, var1, var9);
         float var22 = var10.getShadeBrightness(var20, var1, var9);
         var9.set((Vec3i)var7).move(var8.corners[0]).move(var4);
         boolean var23 = var1.getBlockState(var9).getLightBlock(var1, var9) == 0;
         var9.set((Vec3i)var7).move(var8.corners[1]).move(var4);
         boolean var24 = var1.getBlockState(var9).getLightBlock(var1, var9) == 0;
         var9.set((Vec3i)var7).move(var8.corners[2]).move(var4);
         boolean var25 = var1.getBlockState(var9).getLightBlock(var1, var9) == 0;
         var9.set((Vec3i)var7).move(var8.corners[3]).move(var4);
         boolean var26 = var1.getBlockState(var9).getLightBlock(var1, var9) == 0;
         float var27;
         int var31;
         BlockState var35;
         if (!var25 && !var23) {
            var27 = var13;
            var31 = var12;
         } else {
            var9.set((Vec3i)var7).move(var8.corners[0]).move(var8.corners[2]);
            var35 = var1.getBlockState(var9);
            var27 = var10.getShadeBrightness(var35, var1, var9);
            var31 = var10.getLightColor(var35, var1, var9);
         }

         float var28;
         int var32;
         if (!var26 && !var23) {
            var28 = var13;
            var32 = var12;
         } else {
            var9.set((Vec3i)var7).move(var8.corners[0]).move(var8.corners[3]);
            var35 = var1.getBlockState(var9);
            var28 = var10.getShadeBrightness(var35, var1, var9);
            var32 = var10.getLightColor(var35, var1, var9);
         }

         float var29;
         int var33;
         if (!var25 && !var24) {
            var29 = var13;
            var33 = var12;
         } else {
            var9.set((Vec3i)var7).move(var8.corners[1]).move(var8.corners[2]);
            var35 = var1.getBlockState(var9);
            var29 = var10.getShadeBrightness(var35, var1, var9);
            var33 = var10.getLightColor(var35, var1, var9);
         }

         float var30;
         int var34;
         if (!var26 && !var24) {
            var30 = var13;
            var34 = var12;
         } else {
            var9.set((Vec3i)var7).move(var8.corners[1]).move(var8.corners[3]);
            var35 = var1.getBlockState(var9);
            var30 = var10.getShadeBrightness(var35, var1, var9);
            var34 = var10.getLightColor(var35, var1, var9);
         }

         int var63 = var10.getLightColor(var2, var1, var3);
         var9.set((Vec3i)var3).move(var4);
         BlockState var36 = var1.getBlockState(var9);
         if (var6.get(0) || !var36.isSolidRender(var1, var9)) {
            var63 = var10.getLightColor(var36, var1, var9);
         }

         float var37 = var6.get(0) ? var10.getShadeBrightness(var1.getBlockState(var7), var1, var7) : var10.getShadeBrightness(var1.getBlockState(var3), var1, var3);
         ModelBlockRenderer.AmbientVertexRemap var38 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(var4);
         float var39;
         float var40;
         float var41;
         float var42;
         if (var6.get(1) && var8.doNonCubicWeight) {
            var39 = (var22 + var13 + var28 + var37) * 0.25F;
            var40 = (var19 + var13 + var27 + var37) * 0.25F;
            var41 = (var19 + var16 + var29 + var37) * 0.25F;
            var42 = (var22 + var16 + var30 + var37) * 0.25F;
            float var43 = var5[var8.vert0Weights[0].shape] * var5[var8.vert0Weights[1].shape];
            float var44 = var5[var8.vert0Weights[2].shape] * var5[var8.vert0Weights[3].shape];
            float var45 = var5[var8.vert0Weights[4].shape] * var5[var8.vert0Weights[5].shape];
            float var46 = var5[var8.vert0Weights[6].shape] * var5[var8.vert0Weights[7].shape];
            float var47 = var5[var8.vert1Weights[0].shape] * var5[var8.vert1Weights[1].shape];
            float var48 = var5[var8.vert1Weights[2].shape] * var5[var8.vert1Weights[3].shape];
            float var49 = var5[var8.vert1Weights[4].shape] * var5[var8.vert1Weights[5].shape];
            float var50 = var5[var8.vert1Weights[6].shape] * var5[var8.vert1Weights[7].shape];
            float var51 = var5[var8.vert2Weights[0].shape] * var5[var8.vert2Weights[1].shape];
            float var52 = var5[var8.vert2Weights[2].shape] * var5[var8.vert2Weights[3].shape];
            float var53 = var5[var8.vert2Weights[4].shape] * var5[var8.vert2Weights[5].shape];
            float var54 = var5[var8.vert2Weights[6].shape] * var5[var8.vert2Weights[7].shape];
            float var55 = var5[var8.vert3Weights[0].shape] * var5[var8.vert3Weights[1].shape];
            float var56 = var5[var8.vert3Weights[2].shape] * var5[var8.vert3Weights[3].shape];
            float var57 = var5[var8.vert3Weights[4].shape] * var5[var8.vert3Weights[5].shape];
            float var58 = var5[var8.vert3Weights[6].shape] * var5[var8.vert3Weights[7].shape];
            this.brightness[var38.vert0] = var39 * var43 + var40 * var44 + var41 * var45 + var42 * var46;
            this.brightness[var38.vert1] = var39 * var47 + var40 * var48 + var41 * var49 + var42 * var50;
            this.brightness[var38.vert2] = var39 * var51 + var40 * var52 + var41 * var53 + var42 * var54;
            this.brightness[var38.vert3] = var39 * var55 + var40 * var56 + var41 * var57 + var42 * var58;
            int var59 = this.blend(var21, var12, var32, var63);
            int var60 = this.blend(var18, var12, var31, var63);
            int var61 = this.blend(var18, var15, var33, var63);
            int var62 = this.blend(var21, var15, var34, var63);
            this.lightmap[var38.vert0] = this.blend(var59, var60, var61, var62, var43, var44, var45, var46);
            this.lightmap[var38.vert1] = this.blend(var59, var60, var61, var62, var47, var48, var49, var50);
            this.lightmap[var38.vert2] = this.blend(var59, var60, var61, var62, var51, var52, var53, var54);
            this.lightmap[var38.vert3] = this.blend(var59, var60, var61, var62, var55, var56, var57, var58);
         } else {
            var39 = (var22 + var13 + var28 + var37) * 0.25F;
            var40 = (var19 + var13 + var27 + var37) * 0.25F;
            var41 = (var19 + var16 + var29 + var37) * 0.25F;
            var42 = (var22 + var16 + var30 + var37) * 0.25F;
            this.lightmap[var38.vert0] = this.blend(var21, var12, var32, var63);
            this.lightmap[var38.vert1] = this.blend(var18, var12, var31, var63);
            this.lightmap[var38.vert2] = this.blend(var18, var15, var33, var63);
            this.lightmap[var38.vert3] = this.blend(var21, var15, var34, var63);
            this.brightness[var38.vert0] = var39;
            this.brightness[var38.vert1] = var40;
            this.brightness[var38.vert2] = var41;
            this.brightness[var38.vert3] = var42;
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

      public int getLightColor(BlockState var1, BlockAndBiomeGetter var2, BlockPos var3) {
         long var4 = var3.asLong();
         int var6;
         if (this.enabled) {
            var6 = this.colorCache.get(var4);
            if (var6 != 2147483647) {
               return var6;
            }
         }

         var6 = var1.getLightColor(var2, var3);
         if (this.enabled) {
            if (this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(var4, var6);
         }

         return var6;
      }

      public float getShadeBrightness(BlockState var1, BlockAndBiomeGetter var2, BlockPos var3) {
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
