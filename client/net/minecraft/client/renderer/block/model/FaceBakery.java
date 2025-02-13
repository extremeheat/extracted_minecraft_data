package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.math.MatrixUtil;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class FaceBakery {
   public static final int VERTEX_INT_SIZE = 8;
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433) - 1.0F;
   public static final int VERTEX_COUNT = 4;
   private static final int COLOR_INDEX = 3;
   public static final int UV_INDEX = 4;

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

   public static BakedQuad bakeQuad(Vector3fc var0, Vector3fc var1, BlockElementFace var2, TextureAtlasSprite var3, Direction var4, ModelState var5, @Nullable BlockElementRotation var6, boolean var7, int var8) {
      BlockElementFace.UVs var9 = var2.uvs();
      if (var9 == null) {
         var9 = defaultFaceUV(var0, var1, var4);
      }

      var9 = shrinkUVs(var3, var9);
      Matrix4fc var10 = var5.inverseFaceTransformation(var4);
      int[] var11 = makeVertices(var9, var2.rotation(), var10, var3, var4, setupShape(var0, var1), var5.transformation(), var6);
      Direction var12 = calculateFacing(var11);
      if (var6 == null) {
         recalculateWinding(var11, var12);
      }

      return new BakedQuad(var11, var2.tintIndex(), var12, var3, var7, var8);
   }

   private static BlockElementFace.UVs shrinkUVs(TextureAtlasSprite var0, BlockElementFace.UVs var1) {
      float var2 = var1.minU();
      float var3 = var1.minV();
      float var4 = var1.maxU();
      float var5 = var1.maxV();
      float var6 = var0.uvShrinkRatio();
      float var7 = (var2 + var2 + var4 + var4) / 4.0F;
      float var8 = (var3 + var3 + var5 + var5) / 4.0F;
      return new BlockElementFace.UVs(Mth.lerp(var6, var2, var7), Mth.lerp(var6, var3, var8), Mth.lerp(var6, var4, var7), Mth.lerp(var6, var5, var8));
   }

   private static int[] makeVertices(BlockElementFace.UVs var0, Quadrant var1, Matrix4fc var2, TextureAtlasSprite var3, Direction var4, float[] var5, Transformation var6, @Nullable BlockElementRotation var7) {
      FaceInfo var8 = FaceInfo.fromFacing(var4);
      int[] var9 = new int[32];

      for(int var10 = 0; var10 < 4; ++var10) {
         bakeVertex(var9, var10, var8, var0, var1, var2, var5, var3, var6, var7);
      }

      return var9;
   }

   private static float[] setupShape(Vector3fc var0, Vector3fc var1) {
      float[] var2 = new float[Direction.values().length];
      var2[FaceInfo.Constants.MIN_X] = var0.x() / 16.0F;
      var2[FaceInfo.Constants.MIN_Y] = var0.y() / 16.0F;
      var2[FaceInfo.Constants.MIN_Z] = var0.z() / 16.0F;
      var2[FaceInfo.Constants.MAX_X] = var1.x() / 16.0F;
      var2[FaceInfo.Constants.MAX_Y] = var1.y() / 16.0F;
      var2[FaceInfo.Constants.MAX_Z] = var1.z() / 16.0F;
      return var2;
   }

   private static void bakeVertex(int[] var0, int var1, FaceInfo var2, BlockElementFace.UVs var3, Quadrant var4, Matrix4fc var5, float[] var6, TextureAtlasSprite var7, Transformation var8, @Nullable BlockElementRotation var9) {
      FaceInfo.VertexInfo var10 = var2.getVertexInfo(var1);
      Vector3f var11 = new Vector3f(var6[var10.xFace], var6[var10.yFace], var6[var10.zFace]);
      applyElementRotation(var11, var9);
      applyModelRotation(var11, var8);
      float var12 = BlockElementFace.getU(var3, var4, var1);
      float var13 = BlockElementFace.getV(var3, var4, var1);
      float var14;
      float var15;
      if (MatrixUtil.isIdentity(var5)) {
         var15 = var12;
         var14 = var13;
      } else {
         Vector3f var16 = var5.transformPosition(new Vector3f(cornerToCenter(var12), cornerToCenter(var13), 0.0F));
         var15 = centerToCorner(var16.x);
         var14 = centerToCorner(var16.y);
      }

      fillVertex(var0, var1, var11, var7, var15, var14);
   }

   private static float cornerToCenter(float var0) {
      return var0 - 0.5F;
   }

   private static float centerToCorner(float var0) {
      return var0 + 0.5F;
   }

   private static void fillVertex(int[] var0, int var1, Vector3f var2, TextureAtlasSprite var3, float var4, float var5) {
      int var6 = var1 * 8;
      var0[var6] = Float.floatToRawIntBits(var2.x());
      var0[var6 + 1] = Float.floatToRawIntBits(var2.y());
      var0[var6 + 2] = Float.floatToRawIntBits(var2.z());
      var0[var6 + 3] = -1;
      var0[var6 + 4] = Float.floatToRawIntBits(var3.getU(var4));
      var0[var6 + 4 + 1] = Float.floatToRawIntBits(var3.getV(var5));
   }

   private static void applyElementRotation(Vector3f var0, @Nullable BlockElementRotation var1) {
      if (var1 != null) {
         Vector3f var2;
         Vector3f var3;
         switch (var1.axis()) {
            case X:
               var2 = new Vector3f(1.0F, 0.0F, 0.0F);
               var3 = new Vector3f(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               var2 = new Vector3f(0.0F, 1.0F, 0.0F);
               var3 = new Vector3f(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               var2 = new Vector3f(0.0F, 0.0F, 1.0F);
               var3 = new Vector3f(1.0F, 1.0F, 0.0F);
               break;
            default:
               throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternionf var4 = (new Quaternionf()).rotationAxis(var1.angle() * 0.017453292F, var2);
         if (var1.rescale()) {
            if (Math.abs(var1.angle()) == 22.5F) {
               var3.mul(RESCALE_22_5);
            } else {
               var3.mul(RESCALE_45);
            }

            var3.add(1.0F, 1.0F, 1.0F);
         } else {
            var3.set(1.0F, 1.0F, 1.0F);
         }

         rotateVertexBy(var0, new Vector3f(var1.origin()), (new Matrix4f()).rotation(var4), var3);
      }
   }

   private static void applyModelRotation(Vector3f var0, Transformation var1) {
      if (var1 != Transformation.identity()) {
         rotateVertexBy(var0, new Vector3f(0.5F, 0.5F, 0.5F), var1.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private static void rotateVertexBy(Vector3f var0, Vector3fc var1, Matrix4fc var2, Vector3fc var3) {
      Vector4f var4 = var2.transform(new Vector4f(var0.x() - var1.x(), var0.y() - var1.y(), var0.z() - var1.z(), 1.0F));
      var4.mul(new Vector4f(var3, 1.0F));
      var0.set(var4.x() + var1.x(), var4.y() + var1.y(), var4.z() + var1.z());
   }

   private static Direction calculateFacing(int[] var0) {
      Vector3f var1 = vectorFromData(var0, 0);
      Vector3f var2 = vectorFromData(var0, 8);
      Vector3f var3 = vectorFromData(var0, 16);
      Vector3f var4 = (new Vector3f(var1)).sub(var2);
      Vector3f var5 = (new Vector3f(var3)).sub(var2);
      Vector3f var6 = (new Vector3f(var5)).cross(var4).normalize();
      if (!var6.isFinite()) {
         return Direction.UP;
      } else {
         Direction var7 = null;
         float var8 = 0.0F;

         for(Direction var12 : Direction.values()) {
            float var13 = var6.dot(var12.getUnitVec3f());
            if (var13 >= 0.0F && var13 > var8) {
               var8 = var13;
               var7 = var12;
            }
         }

         if (var7 == null) {
            return Direction.UP;
         } else {
            return var7;
         }
      }
   }

   private static float xFromData(int[] var0, int var1) {
      return Float.intBitsToFloat(var0[var1]);
   }

   private static float yFromData(int[] var0, int var1) {
      return Float.intBitsToFloat(var0[var1 + 1]);
   }

   private static float zFromData(int[] var0, int var1) {
      return Float.intBitsToFloat(var0[var1 + 2]);
   }

   private static Vector3f vectorFromData(int[] var0, int var1) {
      return new Vector3f(xFromData(var0, var1), yFromData(var0, var1), zFromData(var0, var1));
   }

   private static void recalculateWinding(int[] var0, Direction var1) {
      int[] var2 = new int[var0.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      float[] var3 = new float[Direction.values().length];
      var3[FaceInfo.Constants.MIN_X] = 999.0F;
      var3[FaceInfo.Constants.MIN_Y] = 999.0F;
      var3[FaceInfo.Constants.MIN_Z] = 999.0F;
      var3[FaceInfo.Constants.MAX_X] = -999.0F;
      var3[FaceInfo.Constants.MAX_Y] = -999.0F;
      var3[FaceInfo.Constants.MAX_Z] = -999.0F;

      for(int var4 = 0; var4 < 4; ++var4) {
         int var5 = 8 * var4;
         float var6 = xFromData(var2, var5);
         float var7 = yFromData(var2, var5);
         float var8 = zFromData(var2, var5);
         if (var6 < var3[FaceInfo.Constants.MIN_X]) {
            var3[FaceInfo.Constants.MIN_X] = var6;
         }

         if (var7 < var3[FaceInfo.Constants.MIN_Y]) {
            var3[FaceInfo.Constants.MIN_Y] = var7;
         }

         if (var8 < var3[FaceInfo.Constants.MIN_Z]) {
            var3[FaceInfo.Constants.MIN_Z] = var8;
         }

         if (var6 > var3[FaceInfo.Constants.MAX_X]) {
            var3[FaceInfo.Constants.MAX_X] = var6;
         }

         if (var7 > var3[FaceInfo.Constants.MAX_Y]) {
            var3[FaceInfo.Constants.MAX_Y] = var7;
         }

         if (var8 > var3[FaceInfo.Constants.MAX_Z]) {
            var3[FaceInfo.Constants.MAX_Z] = var8;
         }
      }

      FaceInfo var16 = FaceInfo.fromFacing(var1);

      for(int var17 = 0; var17 < 4; ++var17) {
         int var18 = 8 * var17;
         FaceInfo.VertexInfo var19 = var16.getVertexInfo(var17);
         float var20 = var3[var19.xFace];
         float var9 = var3[var19.yFace];
         float var10 = var3[var19.zFace];
         var0[var18] = Float.floatToRawIntBits(var20);
         var0[var18 + 1] = Float.floatToRawIntBits(var9);
         var0[var18 + 2] = Float.floatToRawIntBits(var10);

         for(int var11 = 0; var11 < 4; ++var11) {
            int var12 = 8 * var11;
            float var13 = xFromData(var2, var12);
            float var14 = yFromData(var2, var12);
            float var15 = zFromData(var2, var12);
            if (Mth.equal(var20, var13) && Mth.equal(var9, var14) && Mth.equal(var10, var15)) {
               var0[var18 + 4] = var2[var12 + 4];
               var0[var18 + 4 + 1] = var2[var12 + 4 + 1];
            }
         }
      }

   }

   public static void extractPositions(int[] var0, Consumer<Vector3f> var1) {
      for(int var2 = 0; var2 < 4; ++var2) {
         var1.accept(vectorFromData(var0, 8 * var2));
      }

   }
}
