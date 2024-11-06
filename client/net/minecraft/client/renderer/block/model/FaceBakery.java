package net.minecraft.client.renderer.block.model;

import com.mojang.math.Transformation;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

   public static BakedQuad bakeQuad(Vector3f var0, Vector3f var1, BlockElementFace var2, TextureAtlasSprite var3, Direction var4, ModelState var5, @Nullable BlockElementRotation var6, boolean var7, int var8) {
      BlockFaceUV var9 = var2.uv();
      if (var5.isUvLocked()) {
         var9 = recomputeUVs(var2.uv(), var4, var5.getRotation());
      }

      float[] var10 = new float[var9.uvs.length];
      System.arraycopy(var9.uvs, 0, var10, 0, var10.length);
      float var11 = var3.uvShrinkRatio();
      float var12 = (var9.uvs[0] + var9.uvs[0] + var9.uvs[2] + var9.uvs[2]) / 4.0F;
      float var13 = (var9.uvs[1] + var9.uvs[1] + var9.uvs[3] + var9.uvs[3]) / 4.0F;
      var9.uvs[0] = Mth.lerp(var11, var9.uvs[0], var12);
      var9.uvs[2] = Mth.lerp(var11, var9.uvs[2], var12);
      var9.uvs[1] = Mth.lerp(var11, var9.uvs[1], var13);
      var9.uvs[3] = Mth.lerp(var11, var9.uvs[3], var13);
      int[] var14 = makeVertices(var9, var3, var4, setupShape(var0, var1), var5.getRotation(), var6);
      Direction var15 = calculateFacing(var14);
      System.arraycopy(var10, 0, var9.uvs, 0, var10.length);
      if (var6 == null) {
         recalculateWinding(var14, var15);
      }

      return new BakedQuad(var14, var2.tintIndex(), var15, var3, var7, var8);
   }

   public static BlockFaceUV recomputeUVs(BlockFaceUV var0, Direction var1, Transformation var2) {
      Matrix4f var3 = BlockMath.getUVLockTransform(var2, var1).getMatrix();
      float var4 = var0.getU(var0.getReverseIndex(0));
      float var5 = var0.getV(var0.getReverseIndex(0));
      Vector4f var6 = var3.transform(new Vector4f(var4 / 16.0F, var5 / 16.0F, 0.0F, 1.0F));
      float var7 = 16.0F * var6.x();
      float var8 = 16.0F * var6.y();
      float var9 = var0.getU(var0.getReverseIndex(2));
      float var10 = var0.getV(var0.getReverseIndex(2));
      Vector4f var11 = var3.transform(new Vector4f(var9 / 16.0F, var10 / 16.0F, 0.0F, 1.0F));
      float var12 = 16.0F * var11.x();
      float var13 = 16.0F * var11.y();
      float var14;
      float var15;
      if (Math.signum(var9 - var4) == Math.signum(var12 - var7)) {
         var14 = var7;
         var15 = var12;
      } else {
         var14 = var12;
         var15 = var7;
      }

      float var16;
      float var17;
      if (Math.signum(var10 - var5) == Math.signum(var13 - var8)) {
         var16 = var8;
         var17 = var13;
      } else {
         var16 = var13;
         var17 = var8;
      }

      float var18 = (float)Math.toRadians((double)var0.rotation);
      Matrix3f var19 = new Matrix3f(var3);
      Vector3f var20 = var19.transform(new Vector3f(Mth.cos(var18), Mth.sin(var18), 0.0F));
      int var21 = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)var20.y(), (double)var20.x())) / 90.0)) * 90, 360);
      return new BlockFaceUV(new float[]{var14, var16, var15, var17}, var21);
   }

   private static int[] makeVertices(BlockFaceUV var0, TextureAtlasSprite var1, Direction var2, float[] var3, Transformation var4, @Nullable BlockElementRotation var5) {
      int[] var6 = new int[32];

      for(int var7 = 0; var7 < 4; ++var7) {
         bakeVertex(var6, var7, var2, var0, var3, var1, var4, var5);
      }

      return var6;
   }

   private static float[] setupShape(Vector3f var0, Vector3f var1) {
      float[] var2 = new float[Direction.values().length];
      var2[FaceInfo.Constants.MIN_X] = var0.x() / 16.0F;
      var2[FaceInfo.Constants.MIN_Y] = var0.y() / 16.0F;
      var2[FaceInfo.Constants.MIN_Z] = var0.z() / 16.0F;
      var2[FaceInfo.Constants.MAX_X] = var1.x() / 16.0F;
      var2[FaceInfo.Constants.MAX_Y] = var1.y() / 16.0F;
      var2[FaceInfo.Constants.MAX_Z] = var1.z() / 16.0F;
      return var2;
   }

   private static void bakeVertex(int[] var0, int var1, Direction var2, BlockFaceUV var3, float[] var4, TextureAtlasSprite var5, Transformation var6, @Nullable BlockElementRotation var7) {
      FaceInfo.VertexInfo var8 = FaceInfo.fromFacing(var2).getVertexInfo(var1);
      Vector3f var9 = new Vector3f(var4[var8.xFace], var4[var8.yFace], var4[var8.zFace]);
      applyElementRotation(var9, var7);
      applyModelRotation(var9, var6);
      fillVertex(var0, var1, var9, var5, var3);
   }

   private static void fillVertex(int[] var0, int var1, Vector3f var2, TextureAtlasSprite var3, BlockFaceUV var4) {
      int var5 = var1 * 8;
      var0[var5] = Float.floatToRawIntBits(var2.x());
      var0[var5 + 1] = Float.floatToRawIntBits(var2.y());
      var0[var5 + 2] = Float.floatToRawIntBits(var2.z());
      var0[var5 + 3] = -1;
      var0[var5 + 4] = Float.floatToRawIntBits(var3.getU(var4.getU(var1) / 16.0F));
      var0[var5 + 4 + 1] = Float.floatToRawIntBits(var3.getV(var4.getV(var1) / 16.0F));
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

   private static void rotateVertexBy(Vector3f var0, Vector3f var1, Matrix4f var2, Vector3f var3) {
      Vector4f var4 = var2.transform(new Vector4f(var0.x() - var1.x(), var0.y() - var1.y(), var0.z() - var1.z(), 1.0F));
      var4.mul(new Vector4f(var3, 1.0F));
      var0.set(var4.x() + var1.x(), var4.y() + var1.y(), var4.z() + var1.z());
   }

   private static Direction calculateFacing(int[] var0) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(var0[8]), Float.intBitsToFloat(var0[9]), Float.intBitsToFloat(var0[10]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(var0[16]), Float.intBitsToFloat(var0[17]), Float.intBitsToFloat(var0[18]));
      Vector3f var4 = (new Vector3f(var1)).sub(var2);
      Vector3f var5 = (new Vector3f(var3)).sub(var2);
      Vector3f var6 = (new Vector3f(var5)).cross(var4).normalize();
      if (!var6.isFinite()) {
         return Direction.UP;
      } else {
         Direction var7 = null;
         float var8 = 0.0F;
         Direction[] var9 = Direction.values();
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Direction var12 = var9[var11];
            Vec3i var13 = var12.getUnitVec3i();
            Vector3f var14 = new Vector3f((float)var13.getX(), (float)var13.getY(), (float)var13.getZ());
            float var15 = var6.dot(var14);
            if (var15 >= 0.0F && var15 > var8) {
               var8 = var15;
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

      int var5;
      float var8;
      for(int var4 = 0; var4 < 4; ++var4) {
         var5 = 8 * var4;
         float var6 = Float.intBitsToFloat(var2[var5]);
         float var7 = Float.intBitsToFloat(var2[var5 + 1]);
         var8 = Float.intBitsToFloat(var2[var5 + 2]);
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

      for(var5 = 0; var5 < 4; ++var5) {
         int var17 = 8 * var5;
         FaceInfo.VertexInfo var18 = var16.getVertexInfo(var5);
         var8 = var3[var18.xFace];
         float var9 = var3[var18.yFace];
         float var10 = var3[var18.zFace];
         var0[var17] = Float.floatToRawIntBits(var8);
         var0[var17 + 1] = Float.floatToRawIntBits(var9);
         var0[var17 + 2] = Float.floatToRawIntBits(var10);

         for(int var11 = 0; var11 < 4; ++var11) {
            int var12 = 8 * var11;
            float var13 = Float.intBitsToFloat(var2[var12]);
            float var14 = Float.intBitsToFloat(var2[var12 + 1]);
            float var15 = Float.intBitsToFloat(var2[var12 + 2]);
            if (Mth.equal(var8, var13) && Mth.equal(var9, var14) && Mth.equal(var10, var15)) {
               var0[var17 + 4] = var2[var12 + 4];
               var0[var17 + 4 + 1] = var2[var12 + 4 + 1];
            }
         }
      }

   }
}
