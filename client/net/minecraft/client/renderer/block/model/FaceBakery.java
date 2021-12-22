package net.minecraft.client.renderer.block.model;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FaceBakery {
   public static final int VERTEX_INT_SIZE = 8;
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;
   public static final int VERTEX_COUNT = 4;
   private static final int COLOR_INDEX = 3;
   public static final int UV_INDEX = 4;

   public FaceBakery() {
      super();
   }

   public BakedQuad bakeQuad(Vector3f var1, Vector3f var2, BlockElementFace var3, TextureAtlasSprite var4, Direction var5, ModelState var6, @Nullable BlockElementRotation var7, boolean var8, ResourceLocation var9) {
      BlockFaceUV var10 = var3.field_245;
      if (var6.isUvLocked()) {
         var10 = recomputeUVs(var3.field_245, var5, var6.getRotation(), var9);
      }

      float[] var11 = new float[var10.uvs.length];
      System.arraycopy(var10.uvs, 0, var11, 0, var11.length);
      float var12 = var4.uvShrinkRatio();
      float var13 = (var10.uvs[0] + var10.uvs[0] + var10.uvs[2] + var10.uvs[2]) / 4.0F;
      float var14 = (var10.uvs[1] + var10.uvs[1] + var10.uvs[3] + var10.uvs[3]) / 4.0F;
      var10.uvs[0] = Mth.lerp(var12, var10.uvs[0], var13);
      var10.uvs[2] = Mth.lerp(var12, var10.uvs[2], var13);
      var10.uvs[1] = Mth.lerp(var12, var10.uvs[1], var14);
      var10.uvs[3] = Mth.lerp(var12, var10.uvs[3], var14);
      int[] var15 = this.makeVertices(var10, var4, var5, this.setupShape(var1, var2), var6.getRotation(), var7, var8);
      Direction var16 = calculateFacing(var15);
      System.arraycopy(var11, 0, var10.uvs, 0, var11.length);
      if (var7 == null) {
         this.recalculateWinding(var15, var16);
      }

      return new BakedQuad(var15, var3.tintIndex, var16, var4, var8);
   }

   public static BlockFaceUV recomputeUVs(BlockFaceUV var0, Direction var1, Transformation var2, ResourceLocation var3) {
      Matrix4f var4 = BlockMath.getUVLockTransform(var2, var1, () -> {
         return "Unable to resolve UVLock for model: " + var3;
      }).getMatrix();
      float var5 = var0.getU(var0.getReverseIndex(0));
      float var6 = var0.getV(var0.getReverseIndex(0));
      Vector4f var7 = new Vector4f(var5 / 16.0F, var6 / 16.0F, 0.0F, 1.0F);
      var7.transform(var4);
      float var8 = 16.0F * var7.method_66();
      float var9 = 16.0F * var7.method_67();
      float var10 = var0.getU(var0.getReverseIndex(2));
      float var11 = var0.getV(var0.getReverseIndex(2));
      Vector4f var12 = new Vector4f(var10 / 16.0F, var11 / 16.0F, 0.0F, 1.0F);
      var12.transform(var4);
      float var13 = 16.0F * var12.method_66();
      float var14 = 16.0F * var12.method_67();
      float var15;
      float var16;
      if (Math.signum(var10 - var5) == Math.signum(var13 - var8)) {
         var15 = var8;
         var16 = var13;
      } else {
         var15 = var13;
         var16 = var8;
      }

      float var17;
      float var18;
      if (Math.signum(var11 - var6) == Math.signum(var14 - var9)) {
         var17 = var9;
         var18 = var14;
      } else {
         var17 = var14;
         var18 = var9;
      }

      float var19 = (float)Math.toRadians((double)var0.rotation);
      Vector3f var20 = new Vector3f(Mth.cos(var19), Mth.sin(var19), 0.0F);
      Matrix3f var21 = new Matrix3f(var4);
      var20.transform(var21);
      int var22 = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)var20.method_83(), (double)var20.method_82())) / 90.0D)) * 90, 360);
      return new BlockFaceUV(new float[]{var15, var17, var16, var18}, var22);
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
      var3[FaceInfo.Constants.MIN_X] = var1.method_82() / 16.0F;
      var3[FaceInfo.Constants.MIN_Y] = var1.method_83() / 16.0F;
      var3[FaceInfo.Constants.MIN_Z] = var1.method_84() / 16.0F;
      var3[FaceInfo.Constants.MAX_X] = var2.method_82() / 16.0F;
      var3[FaceInfo.Constants.MAX_Y] = var2.method_83() / 16.0F;
      var3[FaceInfo.Constants.MAX_Z] = var2.method_84() / 16.0F;
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
      var1[var6] = Float.floatToRawIntBits(var3.method_82());
      var1[var6 + 1] = Float.floatToRawIntBits(var3.method_83());
      var1[var6 + 2] = Float.floatToRawIntBits(var3.method_84());
      var1[var6 + 3] = -1;
      var1[var6 + 4] = Float.floatToRawIntBits(var4.getU((double)var5.getU(var2)));
      var1[var6 + 4 + 1] = Float.floatToRawIntBits(var4.getV((double)var5.getV(var2)));
   }

   private void applyElementRotation(Vector3f var1, @Nullable BlockElementRotation var2) {
      if (var2 != null) {
         Vector3f var3;
         Vector3f var4;
         switch(var2.axis) {
         case field_500:
            var3 = Vector3f.field_290;
            var4 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case field_501:
            var3 = Vector3f.field_292;
            var4 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case field_502:
            var3 = Vector3f.field_294;
            var4 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion var5 = var3.rotationDegrees(var2.angle);
         if (var2.rescale) {
            if (Math.abs(var2.angle) == 22.5F) {
               var4.mul(RESCALE_22_5);
            } else {
               var4.mul(RESCALE_45);
            }

            var4.add(1.0F, 1.0F, 1.0F);
         } else {
            var4.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateVertexBy(var1, var2.origin.copy(), new Matrix4f(var5), var4);
      }
   }

   public void applyModelRotation(Vector3f var1, Transformation var2) {
      if (var2 != Transformation.identity()) {
         this.rotateVertexBy(var1, new Vector3f(0.5F, 0.5F, 0.5F), var2.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private void rotateVertexBy(Vector3f var1, Vector3f var2, Matrix4f var3, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.method_82() - var2.method_82(), var1.method_83() - var2.method_83(), var1.method_84() - var2.method_84(), 1.0F);
      var5.transform(var3);
      var5.mul(var4);
      var1.set(var5.method_66() + var2.method_82(), var5.method_67() + var2.method_83(), var5.method_68() + var2.method_84());
   }

   public static Direction calculateFacing(int[] var0) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(var0[8]), Float.intBitsToFloat(var0[9]), Float.intBitsToFloat(var0[10]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(var0[16]), Float.intBitsToFloat(var0[17]), Float.intBitsToFloat(var0[18]));
      Vector3f var4 = var1.copy();
      var4.sub(var2);
      Vector3f var5 = var3.copy();
      var5.sub(var2);
      Vector3f var6 = var5.copy();
      var6.cross(var4);
      var6.normalize();
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
         return Direction.field_526;
      } else {
         return var7;
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
