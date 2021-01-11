package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FaceBakery {
   private static final float field_178418_a = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float field_178417_b = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;

   public FaceBakery() {
      super();
   }

   public BakedQuad func_178414_a(Vector3f var1, Vector3f var2, BlockPartFace var3, TextureAtlasSprite var4, EnumFacing var5, ModelRotation var6, BlockPartRotation var7, boolean var8, boolean var9) {
      int[] var10 = this.func_178405_a(var3, var4, var5, this.func_178403_a(var1, var2), var6, var7, var8, var9);
      EnumFacing var11 = func_178410_a(var10);
      if (var8) {
         this.func_178409_a(var10, var11, var3.field_178243_e, var4);
      }

      if (var7 == null) {
         this.func_178408_a(var10, var11);
      }

      return new BakedQuad(var10, var3.field_178245_c, var11);
   }

   private int[] func_178405_a(BlockPartFace var1, TextureAtlasSprite var2, EnumFacing var3, float[] var4, ModelRotation var5, BlockPartRotation var6, boolean var7, boolean var8) {
      int[] var9 = new int[28];

      for(int var10 = 0; var10 < 4; ++var10) {
         this.func_178402_a(var9, var10, var3, var1, var4, var2, var5, var6, var7, var8);
      }

      return var9;
   }

   private int func_178413_a(EnumFacing var1) {
      float var2 = this.func_178412_b(var1);
      int var3 = MathHelper.func_76125_a((int)(var2 * 255.0F), 0, 255);
      return -16777216 | var3 << 16 | var3 << 8 | var3;
   }

   private float func_178412_b(EnumFacing var1) {
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

   private float[] func_178403_a(Vector3f var1, Vector3f var2) {
      float[] var3 = new float[EnumFacing.values().length];
      var3[EnumFaceDirection.Constants.field_179176_f] = var1.x / 16.0F;
      var3[EnumFaceDirection.Constants.field_179178_e] = var1.y / 16.0F;
      var3[EnumFaceDirection.Constants.field_179177_d] = var1.z / 16.0F;
      var3[EnumFaceDirection.Constants.field_179180_c] = var2.x / 16.0F;
      var3[EnumFaceDirection.Constants.field_179179_b] = var2.y / 16.0F;
      var3[EnumFaceDirection.Constants.field_179181_a] = var2.z / 16.0F;
      return var3;
   }

   private void func_178402_a(int[] var1, int var2, EnumFacing var3, BlockPartFace var4, float[] var5, TextureAtlasSprite var6, ModelRotation var7, BlockPartRotation var8, boolean var9, boolean var10) {
      EnumFacing var11 = var7.func_177523_a(var3);
      int var12 = var10 ? this.func_178413_a(var11) : -1;
      EnumFaceDirection.VertexInformation var13 = EnumFaceDirection.func_179027_a(var3).func_179025_a(var2);
      Vector3f var14 = new Vector3f(var5[var13.field_179184_a], var5[var13.field_179182_b], var5[var13.field_179183_c]);
      this.func_178407_a(var14, var8);
      int var15 = this.func_178415_a(var14, var3, var2, var7, var9);
      this.func_178404_a(var1, var15, var2, var14, var12, var6, var4.field_178243_e);
   }

   private void func_178404_a(int[] var1, int var2, int var3, Vector3f var4, int var5, TextureAtlasSprite var6, BlockFaceUV var7) {
      int var8 = var2 * 7;
      var1[var8] = Float.floatToRawIntBits(var4.x);
      var1[var8 + 1] = Float.floatToRawIntBits(var4.y);
      var1[var8 + 2] = Float.floatToRawIntBits(var4.z);
      var1[var8 + 3] = var5;
      var1[var8 + 4] = Float.floatToRawIntBits(var6.func_94214_a((double)var7.func_178348_a(var3)));
      var1[var8 + 4 + 1] = Float.floatToRawIntBits(var6.func_94207_b((double)var7.func_178346_b(var3)));
   }

   private void func_178407_a(Vector3f var1, BlockPartRotation var2) {
      if (var2 != null) {
         Matrix4f var3 = this.func_178411_a();
         Vector3f var4 = new Vector3f(0.0F, 0.0F, 0.0F);
         switch(var2.field_178342_b) {
         case X:
            Matrix4f.rotate(var2.field_178343_c * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), var3, var3);
            var4.set(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            Matrix4f.rotate(var2.field_178343_c * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), var3, var3);
            var4.set(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            Matrix4f.rotate(var2.field_178343_c * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), var3, var3);
            var4.set(1.0F, 1.0F, 0.0F);
         }

         if (var2.field_178341_d) {
            if (Math.abs(var2.field_178343_c) == 22.5F) {
               var4.scale(field_178418_a);
            } else {
               var4.scale(field_178417_b);
            }

            Vector3f.add(var4, new Vector3f(1.0F, 1.0F, 1.0F), var4);
         } else {
            var4.set(1.0F, 1.0F, 1.0F);
         }

         this.func_178406_a(var1, new Vector3f(var2.field_178344_a), var3, var4);
      }
   }

   public int func_178415_a(Vector3f var1, EnumFacing var2, int var3, ModelRotation var4, boolean var5) {
      if (var4 == ModelRotation.X0_Y0) {
         return var3;
      } else {
         this.func_178406_a(var1, new Vector3f(0.5F, 0.5F, 0.5F), var4.func_177525_a(), new Vector3f(1.0F, 1.0F, 1.0F));
         return var4.func_177520_a(var2, var3);
      }
   }

   private void func_178406_a(Vector3f var1, Vector3f var2, Matrix4f var3, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.x - var2.x, var1.y - var2.y, var1.z - var2.z, 1.0F);
      Matrix4f.transform(var3, var5, var5);
      var5.x *= var4.x;
      var5.y *= var4.y;
      var5.z *= var4.z;
      var1.set(var5.x + var2.x, var5.y + var2.y, var5.z + var2.z);
   }

   private Matrix4f func_178411_a() {
      Matrix4f var1 = new Matrix4f();
      var1.setIdentity();
      return var1;
   }

   public static EnumFacing func_178410_a(int[] var0) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(var0[7]), Float.intBitsToFloat(var0[8]), Float.intBitsToFloat(var0[9]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(var0[14]), Float.intBitsToFloat(var0[15]), Float.intBitsToFloat(var0[16]));
      Vector3f var4 = new Vector3f();
      Vector3f var5 = new Vector3f();
      Vector3f var6 = new Vector3f();
      Vector3f.sub(var1, var2, var4);
      Vector3f.sub(var3, var2, var5);
      Vector3f.cross(var5, var4, var6);
      float var7 = (float)Math.sqrt((double)(var6.x * var6.x + var6.y * var6.y + var6.z * var6.z));
      var6.x /= var7;
      var6.y /= var7;
      var6.z /= var7;
      EnumFacing var8 = null;
      float var9 = 0.0F;
      EnumFacing[] var10 = EnumFacing.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         EnumFacing var13 = var10[var12];
         Vec3i var14 = var13.func_176730_m();
         Vector3f var15 = new Vector3f((float)var14.func_177958_n(), (float)var14.func_177956_o(), (float)var14.func_177952_p());
         float var16 = Vector3f.dot(var6, var15);
         if (var16 >= 0.0F && var16 > var9) {
            var9 = var16;
            var8 = var13;
         }
      }

      if (var8 == null) {
         return EnumFacing.UP;
      } else {
         return var8;
      }
   }

   public void func_178409_a(int[] var1, EnumFacing var2, BlockFaceUV var3, TextureAtlasSprite var4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         this.func_178401_a(var5, var1, var2, var3, var4);
      }

   }

   private void func_178408_a(int[] var1, EnumFacing var2) {
      int[] var3 = new int[var1.length];
      System.arraycopy(var1, 0, var3, 0, var1.length);
      float[] var4 = new float[EnumFacing.values().length];
      var4[EnumFaceDirection.Constants.field_179176_f] = 999.0F;
      var4[EnumFaceDirection.Constants.field_179178_e] = 999.0F;
      var4[EnumFaceDirection.Constants.field_179177_d] = 999.0F;
      var4[EnumFaceDirection.Constants.field_179180_c] = -999.0F;
      var4[EnumFaceDirection.Constants.field_179179_b] = -999.0F;
      var4[EnumFaceDirection.Constants.field_179181_a] = -999.0F;

      int var6;
      float var9;
      for(int var5 = 0; var5 < 4; ++var5) {
         var6 = 7 * var5;
         float var7 = Float.intBitsToFloat(var3[var6]);
         float var8 = Float.intBitsToFloat(var3[var6 + 1]);
         var9 = Float.intBitsToFloat(var3[var6 + 2]);
         if (var7 < var4[EnumFaceDirection.Constants.field_179176_f]) {
            var4[EnumFaceDirection.Constants.field_179176_f] = var7;
         }

         if (var8 < var4[EnumFaceDirection.Constants.field_179178_e]) {
            var4[EnumFaceDirection.Constants.field_179178_e] = var8;
         }

         if (var9 < var4[EnumFaceDirection.Constants.field_179177_d]) {
            var4[EnumFaceDirection.Constants.field_179177_d] = var9;
         }

         if (var7 > var4[EnumFaceDirection.Constants.field_179180_c]) {
            var4[EnumFaceDirection.Constants.field_179180_c] = var7;
         }

         if (var8 > var4[EnumFaceDirection.Constants.field_179179_b]) {
            var4[EnumFaceDirection.Constants.field_179179_b] = var8;
         }

         if (var9 > var4[EnumFaceDirection.Constants.field_179181_a]) {
            var4[EnumFaceDirection.Constants.field_179181_a] = var9;
         }
      }

      EnumFaceDirection var17 = EnumFaceDirection.func_179027_a(var2);

      for(var6 = 0; var6 < 4; ++var6) {
         int var18 = 7 * var6;
         EnumFaceDirection.VertexInformation var19 = var17.func_179025_a(var6);
         var9 = var4[var19.field_179184_a];
         float var10 = var4[var19.field_179182_b];
         float var11 = var4[var19.field_179183_c];
         var1[var18] = Float.floatToRawIntBits(var9);
         var1[var18 + 1] = Float.floatToRawIntBits(var10);
         var1[var18 + 2] = Float.floatToRawIntBits(var11);

         for(int var12 = 0; var12 < 4; ++var12) {
            int var13 = 7 * var12;
            float var14 = Float.intBitsToFloat(var3[var13]);
            float var15 = Float.intBitsToFloat(var3[var13 + 1]);
            float var16 = Float.intBitsToFloat(var3[var13 + 2]);
            if (MathHelper.func_180185_a(var9, var14) && MathHelper.func_180185_a(var10, var15) && MathHelper.func_180185_a(var11, var16)) {
               var1[var18 + 4] = var3[var13 + 4];
               var1[var18 + 4 + 1] = var3[var13 + 4 + 1];
            }
         }
      }

   }

   private void func_178401_a(int var1, int[] var2, EnumFacing var3, BlockFaceUV var4, TextureAtlasSprite var5) {
      int var6 = 7 * var1;
      float var7 = Float.intBitsToFloat(var2[var6]);
      float var8 = Float.intBitsToFloat(var2[var6 + 1]);
      float var9 = Float.intBitsToFloat(var2[var6 + 2]);
      if (var7 < -0.1F || var7 >= 1.1F) {
         var7 -= (float)MathHelper.func_76141_d(var7);
      }

      if (var8 < -0.1F || var8 >= 1.1F) {
         var8 -= (float)MathHelper.func_76141_d(var8);
      }

      if (var9 < -0.1F || var9 >= 1.1F) {
         var9 -= (float)MathHelper.func_76141_d(var9);
      }

      float var10 = 0.0F;
      float var11 = 0.0F;
      switch(var3) {
      case DOWN:
         var10 = var7 * 16.0F;
         var11 = (1.0F - var9) * 16.0F;
         break;
      case UP:
         var10 = var7 * 16.0F;
         var11 = var9 * 16.0F;
         break;
      case NORTH:
         var10 = (1.0F - var7) * 16.0F;
         var11 = (1.0F - var8) * 16.0F;
         break;
      case SOUTH:
         var10 = var7 * 16.0F;
         var11 = (1.0F - var8) * 16.0F;
         break;
      case WEST:
         var10 = var9 * 16.0F;
         var11 = (1.0F - var8) * 16.0F;
         break;
      case EAST:
         var10 = (1.0F - var9) * 16.0F;
         var11 = (1.0F - var8) * 16.0F;
      }

      int var12 = var4.func_178345_c(var1) * 7;
      var2[var12 + 4] = Float.floatToRawIntBits(var5.func_94214_a((double)var10));
      var2[var12 + 4 + 1] = Float.floatToRawIntBits(var5.func_94207_b((double)var11));
   }
}
