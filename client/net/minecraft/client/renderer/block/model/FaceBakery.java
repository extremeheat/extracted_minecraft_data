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

   public BakedQuad bakeQuad(Vector3f var1, Vector3f var2, BlockElementFace var3, TextureAtlasSprite var4, Direction var5, ModelState var6, @Nullable BlockElementRotation var7, boolean var8) {
      BlockFaceUV var9 = var3.uv();
      if (var6.isUvLocked()) {
         var9 = recomputeUVs(var3.uv(), var5, var6.getRotation());
      }

      float[] var10 = new float[var9.uvs.length];
      System.arraycopy(var9.uvs, 0, var10, 0, var10.length);
      float var11 = var4.uvShrinkRatio();
      float var12 = (var9.uvs[0] + var9.uvs[0] + var9.uvs[2] + var9.uvs[2]) / 4.0F;
      float var13 = (var9.uvs[1] + var9.uvs[1] + var9.uvs[3] + var9.uvs[3]) / 4.0F;
      var9.uvs[0] = Mth.lerp(var11, var9.uvs[0], var12);
      var9.uvs[2] = Mth.lerp(var11, var9.uvs[2], var12);
      var9.uvs[1] = Mth.lerp(var11, var9.uvs[1], var13);
      var9.uvs[3] = Mth.lerp(var11, var9.uvs[3], var13);
      int[] var14 = this.makeVertices(var9, var4, var5, this.setupShape(var1, var2), var6.getRotation(), var7, var8);
      Direction var15 = calculateFacing(var14);
      System.arraycopy(var10, 0, var9.uvs, 0, var10.length);
      if (var7 == null) {
         this.recalculateWinding(var14, var15);
      }

      return new BakedQuad(var14, var3.tintIndex(), var15, var4, var8);
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

   private int[] makeVertices(BlockFaceUV var1, TextureAtlasSprite var2, Direction var3, float[] var4, Transformation var5, @Nullable BlockElementRotation var6, boolean var7) {
      int[] var8 = new int[32];

      for(int var9 = 0; var9 < 4; ++var9) {
         this.bakeVertex(var8, var9, var3, var1, var4, var2, var5, var6, var7);
      }

      return var8;
   }

   private float[] setupShape(Vector3f var1, Vector3f var2) {
      float[] var3 = new float[Direction.values().length];
      var3[FaceInfo.Constants.MIN_X] = var1.x() / 16.0F;
      var3[FaceInfo.Constants.MIN_Y] = var1.y() / 16.0F;
      var3[FaceInfo.Constants.MIN_Z] = var1.z() / 16.0F;
      var3[FaceInfo.Constants.MAX_X] = var2.x() / 16.0F;
      var3[FaceInfo.Constants.MAX_Y] = var2.y() / 16.0F;
      var3[FaceInfo.Constants.MAX_Z] = var2.z() / 16.0F;
      return var3;
   }

   private void bakeVertex(int[] var1, int var2, Direction var3, BlockFaceUV var4, float[] var5, TextureAtlasSprite var6, Transformation var7, @Nullable BlockElementRotation var8, boolean var9) {
      FaceInfo.VertexInfo var10 = FaceInfo.fromFacing(var3).getVertexInfo(var2);
      Vector3f var11 = new Vector3f(var5[var10.xFace], var5[var10.yFace], var5[var10.zFace]);
      this.applyElementRotation(var11, var8);
      this.applyModelRotation(var11, var7);
      this.fillVertex(var1, var2, var11, var6, var4);
   }

   private void fillVertex(int[] var1, int var2, Vector3f var3, TextureAtlasSprite var4, BlockFaceUV var5) {
      int var6 = var2 * 8;
      var1[var6] = Float.floatToRawIntBits(var3.x());
      var1[var6 + 1] = Float.floatToRawIntBits(var3.y());
      var1[var6 + 2] = Float.floatToRawIntBits(var3.z());
      var1[var6 + 3] = -1;
      var1[var6 + 4] = Float.floatToRawIntBits(var4.getU(var5.getU(var2) / 16.0F));
      var1[var6 + 4 + 1] = Float.floatToRawIntBits(var4.getV(var5.getV(var2) / 16.0F));
   }

   private void applyElementRotation(Vector3f var1, @Nullable BlockElementRotation var2) {
      if (var2 != null) {
         Vector3f var3;
         Vector3f var4;
         switch (var2.axis()) {
            case X:
               var3 = new Vector3f(1.0F, 0.0F, 0.0F);
               var4 = new Vector3f(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               var3 = new Vector3f(0.0F, 1.0F, 0.0F);
               var4 = new Vector3f(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               var3 = new Vector3f(0.0F, 0.0F, 1.0F);
               var4 = new Vector3f(1.0F, 1.0F, 0.0F);
               break;
            default:
               throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternionf var5 = (new Quaternionf()).rotationAxis(var2.angle() * 0.017453292F, var3);
         if (var2.rescale()) {
            if (Math.abs(var2.angle()) == 22.5F) {
               var4.mul(RESCALE_22_5);
            } else {
               var4.mul(RESCALE_45);
            }

            var4.add(1.0F, 1.0F, 1.0F);
         } else {
            var4.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateVertexBy(var1, new Vector3f(var2.origin()), (new Matrix4f()).rotation(var5), var4);
      }
   }

   public void applyModelRotation(Vector3f var1, Transformation var2) {
      if (var2 != Transformation.identity()) {
         this.rotateVertexBy(var1, new Vector3f(0.5F, 0.5F, 0.5F), var2.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private void rotateVertexBy(Vector3f var1, Vector3f var2, Matrix4f var3, Vector3f var4) {
      Vector4f var5 = var3.transform(new Vector4f(var1.x() - var2.x(), var1.y() - var2.y(), var1.z() - var2.z(), 1.0F));
      var5.mul(new Vector4f(var4, 1.0F));
      var1.set(var5.x() + var2.x(), var5.y() + var2.y(), var5.z() + var2.z());
   }

   public static Direction calculateFacing(int[] var0) {
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
            Vec3i var13 = var12.getNormal();
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

   private void recalculateWinding(int[] var1, Direction var2) {
      int[] var3 = new int[var1.length];
      System.arraycopy(var1, 0, var3, 0, var1.length);
      float[] var4 = new float[Direction.values().length];
      var4[FaceInfo.Constants.MIN_X] = 999.0F;
      var4[FaceInfo.Constants.MIN_Y] = 999.0F;
      var4[FaceInfo.Constants.MIN_Z] = 999.0F;
      var4[FaceInfo.Constants.MAX_X] = -999.0F;
      var4[FaceInfo.Constants.MAX_Y] = -999.0F;
      var4[FaceInfo.Constants.MAX_Z] = -999.0F;

      int var6;
      float var9;
      for(int var5 = 0; var5 < 4; ++var5) {
         var6 = 8 * var5;
         float var7 = Float.intBitsToFloat(var3[var6]);
         float var8 = Float.intBitsToFloat(var3[var6 + 1]);
         var9 = Float.intBitsToFloat(var3[var6 + 2]);
         if (var7 < var4[FaceInfo.Constants.MIN_X]) {
            var4[FaceInfo.Constants.MIN_X] = var7;
         }

         if (var8 < var4[FaceInfo.Constants.MIN_Y]) {
            var4[FaceInfo.Constants.MIN_Y] = var8;
         }

         if (var9 < var4[FaceInfo.Constants.MIN_Z]) {
            var4[FaceInfo.Constants.MIN_Z] = var9;
         }

         if (var7 > var4[FaceInfo.Constants.MAX_X]) {
            var4[FaceInfo.Constants.MAX_X] = var7;
         }

         if (var8 > var4[FaceInfo.Constants.MAX_Y]) {
            var4[FaceInfo.Constants.MAX_Y] = var8;
         }

         if (var9 > var4[FaceInfo.Constants.MAX_Z]) {
            var4[FaceInfo.Constants.MAX_Z] = var9;
         }
      }

      FaceInfo var17 = FaceInfo.fromFacing(var2);

      for(var6 = 0; var6 < 4; ++var6) {
         int var18 = 8 * var6;
         FaceInfo.VertexInfo var19 = var17.getVertexInfo(var6);
         var9 = var4[var19.xFace];
         float var10 = var4[var19.yFace];
         float var11 = var4[var19.zFace];
         var1[var18] = Float.floatToRawIntBits(var9);
         var1[var18 + 1] = Float.floatToRawIntBits(var10);
         var1[var18 + 2] = Float.floatToRawIntBits(var11);

         for(int var12 = 0; var12 < 4; ++var12) {
            int var13 = 8 * var12;
            float var14 = Float.intBitsToFloat(var3[var13]);
            float var15 = Float.intBitsToFloat(var3[var13 + 1]);
            float var16 = Float.intBitsToFloat(var3[var13 + 2]);
            if (Mth.equal(var9, var14) && Mth.equal(var10, var15) && Mth.equal(var11, var16)) {
               var1[var18 + 4] = var3[var13 + 4];
               var1[var18 + 4 + 1] = var3[var13 + 4 + 1];
            }
         }
      }

   }
}
