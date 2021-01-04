package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

public class FaceBakery {
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;
   private static final FaceBakery.Rotation[] BY_INDEX = new FaceBakery.Rotation[BlockModelRotation.values().length * Direction.values().length];
   private static final FaceBakery.Rotation ROT_0 = new FaceBakery.Rotation() {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var1, var2, var3, var4}, 0);
      }
   };
   private static final FaceBakery.Rotation ROT_90 = new FaceBakery.Rotation() {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var4, 16.0F - var1, var2, 16.0F - var3}, 270);
      }
   };
   private static final FaceBakery.Rotation ROT_180 = new FaceBakery.Rotation() {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var1, 16.0F - var2, 16.0F - var3, 16.0F - var4}, 0);
      }
   };
   private static final FaceBakery.Rotation ROT_270 = new FaceBakery.Rotation() {
      BlockFaceUV apply(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var2, var3, 16.0F - var4, var1}, 90);
      }
   };

   public FaceBakery() {
      super();
   }

   public BakedQuad bakeQuad(Vector3f var1, Vector3f var2, BlockElementFace var3, TextureAtlasSprite var4, Direction var5, ModelState var6, @Nullable BlockElementRotation var7, boolean var8) {
      BlockFaceUV var9 = var3.uv;
      if (var6.isUvLocked()) {
         var9 = this.recomputeUVs(var3.uv, var5, var6.getRotation());
      }

      float[] var10 = new float[var9.uvs.length];
      System.arraycopy(var9.uvs, 0, var10, 0, var10.length);
      float var11 = (float)var4.getWidth() / (var4.getU1() - var4.getU0());
      float var12 = (float)var4.getHeight() / (var4.getV1() - var4.getV0());
      float var13 = 4.0F / Math.max(var12, var11);
      float var14 = (var9.uvs[0] + var9.uvs[0] + var9.uvs[2] + var9.uvs[2]) / 4.0F;
      float var15 = (var9.uvs[1] + var9.uvs[1] + var9.uvs[3] + var9.uvs[3]) / 4.0F;
      var9.uvs[0] = Mth.lerp(var13, var9.uvs[0], var14);
      var9.uvs[2] = Mth.lerp(var13, var9.uvs[2], var14);
      var9.uvs[1] = Mth.lerp(var13, var9.uvs[1], var15);
      var9.uvs[3] = Mth.lerp(var13, var9.uvs[3], var15);
      int[] var16 = this.makeVertices(var9, var4, var5, this.setupShape(var1, var2), var6.getRotation(), var7, var8);
      Direction var17 = calculateFacing(var16);
      System.arraycopy(var10, 0, var9.uvs, 0, var10.length);
      if (var7 == null) {
         this.recalculateWinding(var16, var17);
      }

      return new BakedQuad(var16, var3.tintIndex, var17, var4);
   }

   private BlockFaceUV recomputeUVs(BlockFaceUV var1, Direction var2, BlockModelRotation var3) {
      return BY_INDEX[getIndex(var3, var2)].recompute(var1);
   }

   private int[] makeVertices(BlockFaceUV var1, TextureAtlasSprite var2, Direction var3, float[] var4, BlockModelRotation var5, @Nullable BlockElementRotation var6, boolean var7) {
      int[] var8 = new int[28];

      for(int var9 = 0; var9 < 4; ++var9) {
         this.bakeVertex(var8, var9, var3, var1, var4, var2, var5, var6, var7);
      }

      return var8;
   }

   private int getShadeValue(Direction var1) {
      float var2 = this.getShade(var1);
      int var3 = Mth.clamp((int)(var2 * 255.0F), 0, 255);
      return -16777216 | var3 << 16 | var3 << 8 | var3;
   }

   private float getShade(Direction var1) {
      switch(var1) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      case WEST:
      case EAST:
         return 0.6F;
      default:
         return 1.0F;
      }
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

   private void bakeVertex(int[] var1, int var2, Direction var3, BlockFaceUV var4, float[] var5, TextureAtlasSprite var6, BlockModelRotation var7, @Nullable BlockElementRotation var8, boolean var9) {
      Direction var10 = var7.rotate(var3);
      int var11 = var9 ? this.getShadeValue(var10) : -1;
      FaceInfo.VertexInfo var12 = FaceInfo.fromFacing(var3).getVertexInfo(var2);
      Vector3f var13 = new Vector3f(var5[var12.xFace], var5[var12.yFace], var5[var12.zFace]);
      this.applyElementRotation(var13, var8);
      int var14 = this.applyModelRotation(var13, var3, var2, var7);
      this.fillVertex(var1, var14, var2, var13, var11, var6, var4);
   }

   private void fillVertex(int[] var1, int var2, int var3, Vector3f var4, int var5, TextureAtlasSprite var6, BlockFaceUV var7) {
      int var8 = var2 * 7;
      var1[var8] = Float.floatToRawIntBits(var4.x());
      var1[var8 + 1] = Float.floatToRawIntBits(var4.y());
      var1[var8 + 2] = Float.floatToRawIntBits(var4.z());
      var1[var8 + 3] = var5;
      var1[var8 + 4] = Float.floatToRawIntBits(var6.getU((double)var7.getU(var3)));
      var1[var8 + 4 + 1] = Float.floatToRawIntBits(var6.getV((double)var7.getV(var3)));
   }

   private void applyElementRotation(Vector3f var1, @Nullable BlockElementRotation var2) {
      if (var2 != null) {
         Vector3f var3;
         Vector3f var4;
         switch(var2.axis) {
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

         Quaternion var5 = new Quaternion(var3, var2.angle, true);
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

         this.rotateVertexBy(var1, new Vector3f(var2.origin), var5, var4);
      }
   }

   public int applyModelRotation(Vector3f var1, Direction var2, int var3, BlockModelRotation var4) {
      if (var4 == BlockModelRotation.X0_Y0) {
         return var3;
      } else {
         this.rotateVertexBy(var1, new Vector3f(0.5F, 0.5F, 0.5F), var4.getRotationQuaternion(), new Vector3f(1.0F, 1.0F, 1.0F));
         return var4.rotateVertexIndex(var2, var3);
      }
   }

   private void rotateVertexBy(Vector3f var1, Vector3f var2, Quaternion var3, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.x() - var2.x(), var1.y() - var2.y(), var1.z() - var2.z(), 1.0F);
      var5.transform(var3);
      var5.mul(var4);
      var1.set(var5.x() + var2.x(), var5.y() + var2.y(), var5.z() + var2.z());
   }

   public static Direction calculateFacing(int[] var0) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(var0[7]), Float.intBitsToFloat(var0[8]), Float.intBitsToFloat(var0[9]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(var0[14]), Float.intBitsToFloat(var0[15]), Float.intBitsToFloat(var0[16]));
      Vector3f var4 = new Vector3f(var1);
      var4.sub(var2);
      Vector3f var5 = new Vector3f(var3);
      var5.sub(var2);
      Vector3f var6 = new Vector3f(var5);
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
         return Direction.UP;
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
         var6 = 7 * var5;
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
         int var18 = 7 * var6;
         FaceInfo.VertexInfo var19 = var17.getVertexInfo(var6);
         var9 = var4[var19.xFace];
         float var10 = var4[var19.yFace];
         float var11 = var4[var19.zFace];
         var1[var18] = Float.floatToRawIntBits(var9);
         var1[var18 + 1] = Float.floatToRawIntBits(var10);
         var1[var18 + 2] = Float.floatToRawIntBits(var11);

         for(int var12 = 0; var12 < 4; ++var12) {
            int var13 = 7 * var12;
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

   private static void register(BlockModelRotation var0, Direction var1, FaceBakery.Rotation var2) {
      BY_INDEX[getIndex(var0, var1)] = var2;
   }

   private static int getIndex(BlockModelRotation var0, Direction var1) {
      return BlockModelRotation.values().length * var1.ordinal() + var0.ordinal();
   }

   static {
      register(BlockModelRotation.X0_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X0_Y0, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y90, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y180, Direction.WEST, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.EAST, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.WEST, ROT_0);
      register(BlockModelRotation.X90_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X90_Y90, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y180, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X90_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X90_Y270, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X180_Y0, Direction.DOWN, ROT_0);
      register(BlockModelRotation.X180_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y0, Direction.SOUTH, ROT_0);
      register(BlockModelRotation.X270_Y0, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y90, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y180, Direction.NORTH, ROT_0);
      register(BlockModelRotation.X270_Y180, Direction.UP, ROT_0);
      register(BlockModelRotation.X270_Y270, Direction.UP, ROT_0);
      register(BlockModelRotation.X0_Y270, Direction.UP, ROT_90);
      register(BlockModelRotation.X0_Y90, Direction.DOWN, ROT_90);
      register(BlockModelRotation.X90_Y0, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y90, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y180, Direction.WEST, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.NORTH, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.SOUTH, ROT_90);
      register(BlockModelRotation.X90_Y270, Direction.WEST, ROT_90);
      register(BlockModelRotation.X180_Y90, Direction.UP, ROT_90);
      register(BlockModelRotation.X180_Y270, Direction.DOWN, ROT_90);
      register(BlockModelRotation.X270_Y0, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.NORTH, ROT_90);
      register(BlockModelRotation.X270_Y90, Direction.SOUTH, ROT_90);
      register(BlockModelRotation.X270_Y180, Direction.EAST, ROT_90);
      register(BlockModelRotation.X270_Y270, Direction.EAST, ROT_90);
      register(BlockModelRotation.X0_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X0_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X90_Y0, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y90, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X90_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X90_Y270, Direction.UP, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y0, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y90, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.UP, ROT_180);
      register(BlockModelRotation.X180_Y180, Direction.WEST, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.EAST, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X180_Y270, Direction.WEST, ROT_180);
      register(BlockModelRotation.X270_Y0, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y0, Direction.NORTH, ROT_180);
      register(BlockModelRotation.X270_Y90, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y180, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X270_Y180, Direction.SOUTH, ROT_180);
      register(BlockModelRotation.X270_Y270, Direction.DOWN, ROT_180);
      register(BlockModelRotation.X0_Y90, Direction.UP, ROT_270);
      register(BlockModelRotation.X0_Y270, Direction.DOWN, ROT_270);
      register(BlockModelRotation.X90_Y0, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.NORTH, ROT_270);
      register(BlockModelRotation.X90_Y90, Direction.SOUTH, ROT_270);
      register(BlockModelRotation.X90_Y180, Direction.EAST, ROT_270);
      register(BlockModelRotation.X90_Y270, Direction.EAST, ROT_270);
      register(BlockModelRotation.X270_Y0, Direction.WEST, ROT_270);
      register(BlockModelRotation.X180_Y90, Direction.DOWN, ROT_270);
      register(BlockModelRotation.X180_Y270, Direction.UP, ROT_270);
      register(BlockModelRotation.X270_Y90, Direction.WEST, ROT_270);
      register(BlockModelRotation.X270_Y180, Direction.WEST, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.NORTH, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.SOUTH, ROT_270);
      register(BlockModelRotation.X270_Y270, Direction.WEST, ROT_270);
   }

   abstract static class Rotation {
      private Rotation() {
         super();
      }

      public BlockFaceUV recompute(BlockFaceUV var1) {
         float var2 = var1.getU(var1.getReverseIndex(0));
         float var3 = var1.getV(var1.getReverseIndex(0));
         float var4 = var1.getU(var1.getReverseIndex(2));
         float var5 = var1.getV(var1.getReverseIndex(2));
         return this.apply(var2, var3, var4, var5);
      }

      abstract BlockFaceUV apply(float var1, float var2, float var3, float var4);

      // $FF: synthetic method
      Rotation(Object var1) {
         this();
      }
   }
}
