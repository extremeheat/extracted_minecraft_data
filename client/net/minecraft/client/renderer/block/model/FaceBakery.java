package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.math.MatrixUtil;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import java.util.Objects;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import org.joml.GeometryUtils;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class FaceBakery {
   private static final Vector3fc BLOCK_MIDDLE = new Vector3f(0.5F, 0.5F, 0.5F);

   public FaceBakery() {
      super();
   }

   @VisibleForTesting
   static BlockElementFace.UVs defaultFaceUV(Vector3fc var0, Vector3fc var1, Direction var2) {
      BlockElementFace.UVs var10000;
      switch (var2) {
         case DOWN -> var10000 = new BlockElementFace.UVs(var0.x(), 16.0F - var1.z(), var1.x(), 16.0F - var0.z());
         case UP -> var10000 = new BlockElementFace.UVs(var0.x(), var0.z(), var1.x(), var1.z());
         case NORTH -> var10000 = new BlockElementFace.UVs(16.0F - var1.x(), 16.0F - var1.y(), 16.0F - var0.x(), 16.0F - var0.y());
         case SOUTH -> var10000 = new BlockElementFace.UVs(var0.x(), 16.0F - var1.y(), var1.x(), 16.0F - var0.y());
         case WEST -> var10000 = new BlockElementFace.UVs(var0.z(), 16.0F - var1.y(), var1.z(), 16.0F - var0.y());
         case EAST -> var10000 = new BlockElementFace.UVs(16.0F - var1.z(), 16.0F - var1.y(), 16.0F - var0.z(), 16.0F - var0.y());
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static BakedQuad bakeQuad(ModelBaker.PartCache var0, Vector3fc var1, Vector3fc var2, BlockElementFace var3, TextureAtlasSprite var4, Direction var5, ModelState var6, @Nullable BlockElementRotation var7, boolean var8, int var9) {
      BlockElementFace.UVs var10 = var3.uvs();
      if (var10 == null) {
         var10 = defaultFaceUV(var1, var2, var5);
      }

      Matrix4fc var11 = var6.inverseFaceTransformation(var5);
      Vector3fc[] var12 = new Vector3fc[4];
      long[] var13 = new long[4];
      FaceInfo var14 = FaceInfo.fromFacing(var5);

      for(int var15 = 0; var15 < 4; ++var15) {
         bakeVertex(var15, var14, var10, var3.rotation(), var11, var1, var2, var4, var6.transformation(), var7, var12, var13, var0);
      }

      Direction var16 = calculateFacing(var12);
      if (var7 == null && var16 != null) {
         recalculateWinding(var12, var13, var16);
      }

      return new BakedQuad(var12[0], var12[1], var12[2], var12[3], var13[0], var13[1], var13[2], var13[3], var3.tintIndex(), (Direction)Objects.requireNonNullElse(var16, Direction.UP), var4, var8, var9);
   }

   private static void bakeVertex(int var0, FaceInfo var1, BlockElementFace.UVs var2, Quadrant var3, Matrix4fc var4, Vector3fc var5, Vector3fc var6, TextureAtlasSprite var7, Transformation var8, @Nullable BlockElementRotation var9, Vector3fc[] var10, long[] var11, ModelBaker.PartCache var12) {
      FaceInfo.VertexInfo var13 = var1.getVertexInfo(var0);
      Vector3f var14 = var13.select(var5, var6).div(16.0F);
      if (var9 != null) {
         rotateVertexBy(var14, var9.origin(), var9.transform());
      }

      if (var8 != Transformation.identity()) {
         rotateVertexBy(var14, BLOCK_MIDDLE, var8.getMatrix());
      }

      float var15 = BlockElementFace.getU(var2, var3, var0);
      float var16 = BlockElementFace.getV(var2, var3, var0);
      float var17;
      float var18;
      if (MatrixUtil.isIdentity(var4)) {
         var18 = var15;
         var17 = var16;
      } else {
         Vector3f var19 = var4.transformPosition(new Vector3f(cornerToCenter(var15), cornerToCenter(var16), 0.0F));
         var18 = centerToCorner(var19.x);
         var17 = centerToCorner(var19.y);
      }

      var10[var0] = var12.vector(var14);
      var11[var0] = UVPair.pack(var7.getU(var18), var7.getV(var17));
   }

   private static float cornerToCenter(float var0) {
      return var0 - 0.5F;
   }

   private static float centerToCorner(float var0) {
      return var0 + 0.5F;
   }

   private static void rotateVertexBy(Vector3f var0, Vector3fc var1, Matrix4fc var2) {
      var0.sub(var1);
      var2.transformPosition(var0);
      var0.add(var1);
   }

   private static @Nullable Direction calculateFacing(Vector3fc[] var0) {
      Vector3f var1 = new Vector3f();
      GeometryUtils.normal(var0[0], var0[1], var0[2], var1);
      return findClosestDirection(var1);
   }

   private static @Nullable Direction findClosestDirection(Vector3f var0) {
      if (!var0.isFinite()) {
         return null;
      } else {
         Direction var1 = null;
         float var2 = 0.0F;

         for(Direction var6 : Direction.values()) {
            float var7 = var0.dot(var6.getUnitVec3f());
            if (var7 >= 0.0F && var7 > var2) {
               var2 = var7;
               var1 = var6;
            }
         }

         return var1;
      }
   }

   private static void recalculateWinding(Vector3fc[] var0, long[] var1, Direction var2) {
      float var3 = 999.0F;
      float var4 = 999.0F;
      float var5 = 999.0F;
      float var6 = -999.0F;
      float var7 = -999.0F;
      float var8 = -999.0F;

      for(int var9 = 0; var9 < 4; ++var9) {
         Vector3fc var10 = var0[var9];
         float var11 = var10.x();
         float var12 = var10.y();
         float var13 = var10.z();
         if (var11 < var3) {
            var3 = var11;
         }

         if (var12 < var4) {
            var4 = var12;
         }

         if (var13 < var5) {
            var5 = var13;
         }

         if (var11 > var6) {
            var6 = var11;
         }

         if (var12 > var7) {
            var7 = var12;
         }

         if (var13 > var8) {
            var8 = var13;
         }
      }

      FaceInfo var16 = FaceInfo.fromFacing(var2);

      for(int var17 = 0; var17 < 4; ++var17) {
         FaceInfo.VertexInfo var18 = var16.getVertexInfo(var17);
         float var19 = var18.xFace().select(var3, var4, var5, var6, var7, var8);
         float var20 = var18.yFace().select(var3, var4, var5, var6, var7, var8);
         float var14 = var18.zFace().select(var3, var4, var5, var6, var7, var8);
         int var15 = findVertex(var0, var17, var19, var20, var14);
         if (var15 == -1) {
            throw new IllegalStateException("Can't find vertex to swap");
         }

         if (var15 != var17) {
            swap(var0, var15, var17);
            swap(var1, var15, var17);
         }
      }

   }

   private static int findVertex(Vector3fc[] var0, int var1, float var2, float var3, float var4) {
      for(int var5 = var1; var5 < 4; ++var5) {
         Vector3fc var6 = var0[var5];
         if (var2 == var6.x() && var3 == var6.y() && var4 == var6.z()) {
            return var5;
         }
      }

      return -1;
   }

   private static void swap(Vector3fc[] var0, int var1, int var2) {
      Vector3fc var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   private static void swap(long[] var0, int var1, int var2) {
      long var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }
}
