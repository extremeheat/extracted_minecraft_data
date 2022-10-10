package net.minecraft.client.renderer.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class FaceBakery {
   private static final float field_178418_a = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float field_178417_b = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;
   private static final FaceBakery.Rotation[] field_188016_c = new FaceBakery.Rotation[ModelRotation.values().length * EnumFacing.values().length];
   private static final FaceBakery.Rotation field_188017_d = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var1, var2, var3, var4}, 0);
      }
   };
   private static final FaceBakery.Rotation field_188018_e = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{var4, 16.0F - var1, var2, 16.0F - var3}, 270);
      }
   };
   private static final FaceBakery.Rotation field_188019_f = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var1, 16.0F - var2, 16.0F - var3, 16.0F - var4}, 0);
      }
   };
   private static final FaceBakery.Rotation field_188020_g = new FaceBakery.Rotation() {
      BlockFaceUV func_188007_a(float var1, float var2, float var3, float var4) {
         return new BlockFaceUV(new float[]{16.0F - var2, var3, 16.0F - var4, var1}, 90);
      }
   };

   public FaceBakery() {
      super();
   }

   public BakedQuad func_199332_a(Vector3f var1, Vector3f var2, BlockPartFace var3, TextureAtlasSprite var4, EnumFacing var5, ModelRotation var6, @Nullable BlockPartRotation var7, boolean var8, boolean var9) {
      BlockFaceUV var10 = var3.field_178243_e;
      if (var8) {
         var10 = this.func_188010_a(var3.field_178243_e, var5, var6);
      }

      int[] var11 = this.func_188012_a(var10, var4, var5, this.func_199337_a(var1, var2), var6, var7, var9);
      EnumFacing var12 = func_178410_a(var11);
      if (var7 == null) {
         this.func_178408_a(var11, var12);
      }

      return new BakedQuad(var11, var3.field_178245_c, var12, var4);
   }

   private BlockFaceUV func_188010_a(BlockFaceUV var1, EnumFacing var2, ModelRotation var3) {
      return field_188016_c[func_188014_a(var3, var2)].func_188006_a(var1);
   }

   private int[] func_188012_a(BlockFaceUV var1, TextureAtlasSprite var2, EnumFacing var3, float[] var4, ModelRotation var5, @Nullable BlockPartRotation var6, boolean var7) {
      int[] var8 = new int[28];

      for(int var9 = 0; var9 < 4; ++var9) {
         this.func_188015_a(var8, var9, var3, var1, var4, var2, var5, var6, var7);
      }

      return var8;
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

   private float[] func_199337_a(Vector3f var1, Vector3f var2) {
      float[] var3 = new float[EnumFacing.values().length];
      var3[EnumFaceDirection.Constants.field_179176_f] = var1.func_195899_a() / 16.0F;
      var3[EnumFaceDirection.Constants.field_179178_e] = var1.func_195900_b() / 16.0F;
      var3[EnumFaceDirection.Constants.field_179177_d] = var1.func_195902_c() / 16.0F;
      var3[EnumFaceDirection.Constants.field_179180_c] = var2.func_195899_a() / 16.0F;
      var3[EnumFaceDirection.Constants.field_179179_b] = var2.func_195900_b() / 16.0F;
      var3[EnumFaceDirection.Constants.field_179181_a] = var2.func_195902_c() / 16.0F;
      return var3;
   }

   private void func_188015_a(int[] var1, int var2, EnumFacing var3, BlockFaceUV var4, float[] var5, TextureAtlasSprite var6, ModelRotation var7, @Nullable BlockPartRotation var8, boolean var9) {
      EnumFacing var10 = var7.func_177523_a(var3);
      int var11 = var9 ? this.func_178413_a(var10) : -1;
      EnumFaceDirection.VertexInformation var12 = EnumFaceDirection.func_179027_a(var3).func_179025_a(var2);
      Vector3f var13 = new Vector3f(var5[var12.field_179184_a], var5[var12.field_179182_b], var5[var12.field_179183_c]);
      this.func_199336_a(var13, var8);
      int var14 = this.func_199335_a(var13, var3, var2, var7);
      this.func_199333_a(var1, var14, var2, var13, var11, var6, var4);
   }

   private void func_199333_a(int[] var1, int var2, int var3, Vector3f var4, int var5, TextureAtlasSprite var6, BlockFaceUV var7) {
      int var8 = var2 * 7;
      var1[var8] = Float.floatToRawIntBits(var4.func_195899_a());
      var1[var8 + 1] = Float.floatToRawIntBits(var4.func_195900_b());
      var1[var8 + 2] = Float.floatToRawIntBits(var4.func_195902_c());
      var1[var8 + 3] = var5;
      var1[var8 + 4] = Float.floatToRawIntBits(var6.func_94214_a((double)var7.func_178348_a(var3)));
      var1[var8 + 4 + 1] = Float.floatToRawIntBits(var6.func_94207_b((double)var7.func_178346_b(var3)));
   }

   private void func_199336_a(Vector3f var1, @Nullable BlockPartRotation var2) {
      if (var2 != null) {
         Vector3f var3;
         Vector3f var4;
         switch(var2.field_178342_b) {
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

         Quaternion var5 = new Quaternion(var3, var2.field_178343_c, true);
         if (var2.field_178341_d) {
            if (Math.abs(var2.field_178343_c) == 22.5F) {
               var4.func_195898_a(field_178418_a);
            } else {
               var4.func_195898_a(field_178417_b);
            }

            var4.func_195904_b(1.0F, 1.0F, 1.0F);
         } else {
            var4.func_195905_a(1.0F, 1.0F, 1.0F);
         }

         this.func_199334_a(var1, new Vector3f(var2.field_178344_a), var5, var4);
      }
   }

   public int func_199335_a(Vector3f var1, EnumFacing var2, int var3, ModelRotation var4) {
      if (var4 == ModelRotation.X0_Y0) {
         return var3;
      } else {
         this.func_199334_a(var1, new Vector3f(0.5F, 0.5F, 0.5F), var4.func_195820_a(), new Vector3f(1.0F, 1.0F, 1.0F));
         return var4.func_177520_a(var2, var3);
      }
   }

   private void func_199334_a(Vector3f var1, Vector3f var2, Quaternion var3, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.func_195899_a() - var2.func_195899_a(), var1.func_195900_b() - var2.func_195900_b(), var1.func_195902_c() - var2.func_195902_c(), 1.0F);
      var5.func_195912_a(var3);
      var5.func_195909_a(var4);
      var1.func_195905_a(var5.func_195910_a() + var2.func_195899_a(), var5.func_195913_b() + var2.func_195900_b(), var5.func_195914_c() + var2.func_195902_c());
   }

   public static EnumFacing func_178410_a(int[] var0) {
      Vector3f var1 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var2 = new Vector3f(Float.intBitsToFloat(var0[7]), Float.intBitsToFloat(var0[8]), Float.intBitsToFloat(var0[9]));
      Vector3f var3 = new Vector3f(Float.intBitsToFloat(var0[14]), Float.intBitsToFloat(var0[15]), Float.intBitsToFloat(var0[16]));
      Vector3f var4 = new Vector3f(var1);
      var4.func_195897_a(var2);
      Vector3f var5 = new Vector3f(var3);
      var5.func_195897_a(var2);
      Vector3f var6 = new Vector3f(var5);
      var6.func_195896_c(var4);
      var6.func_195906_d();
      EnumFacing var7 = null;
      float var8 = 0.0F;
      EnumFacing[] var9 = EnumFacing.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         EnumFacing var12 = var9[var11];
         Vec3i var13 = var12.func_176730_m();
         Vector3f var14 = new Vector3f((float)var13.func_177958_n(), (float)var13.func_177956_o(), (float)var13.func_177952_p());
         float var15 = var6.func_195903_b(var14);
         if (var15 >= 0.0F && var15 > var8) {
            var8 = var15;
            var7 = var12;
         }
      }

      if (var7 == null) {
         return EnumFacing.UP;
      } else {
         return var7;
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

   private static void func_188013_a(ModelRotation var0, EnumFacing var1, FaceBakery.Rotation var2) {
      field_188016_c[func_188014_a(var0, var1)] = var2;
   }

   private static int func_188014_a(ModelRotation var0, EnumFacing var1) {
      return ModelRotation.values().length * var1.ordinal() + var0.ordinal();
   }

   static {
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X0_Y0, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.EAST, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.WEST, field_188017_d);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.DOWN, field_188017_d);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.SOUTH, field_188017_d);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.NORTH, field_188017_d);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.UP, field_188017_d);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.UP, field_188018_e);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.DOWN, field_188018_e);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.NORTH, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.SOUTH, field_188018_e);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.WEST, field_188018_e);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.UP, field_188018_e);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.DOWN, field_188018_e);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.NORTH, field_188018_e);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.SOUTH, field_188018_e);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.EAST, field_188018_e);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X0_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y0, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.UP, field_188019_f);
      func_188013_a(ModelRotation.X180_Y180, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.EAST, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.WEST, field_188019_f);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.NORTH, field_188019_f);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.SOUTH, field_188019_f);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.DOWN, field_188019_f);
      func_188013_a(ModelRotation.X0_Y90, EnumFacing.UP, field_188020_g);
      func_188013_a(ModelRotation.X0_Y270, EnumFacing.DOWN, field_188020_g);
      func_188013_a(ModelRotation.X90_Y0, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.NORTH, field_188020_g);
      func_188013_a(ModelRotation.X90_Y90, EnumFacing.SOUTH, field_188020_g);
      func_188013_a(ModelRotation.X90_Y180, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X90_Y270, EnumFacing.EAST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y0, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X180_Y90, EnumFacing.DOWN, field_188020_g);
      func_188013_a(ModelRotation.X180_Y270, EnumFacing.UP, field_188020_g);
      func_188013_a(ModelRotation.X270_Y90, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y180, EnumFacing.WEST, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.NORTH, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.SOUTH, field_188020_g);
      func_188013_a(ModelRotation.X270_Y270, EnumFacing.WEST, field_188020_g);
   }

   abstract static class Rotation {
      private Rotation() {
         super();
      }

      public BlockFaceUV func_188006_a(BlockFaceUV var1) {
         float var2 = var1.func_178348_a(var1.func_178345_c(0));
         float var3 = var1.func_178346_b(var1.func_178345_c(0));
         float var4 = var1.func_178348_a(var1.func_178345_c(2));
         float var5 = var1.func_178346_b(var1.func_178345_c(2));
         return this.func_188007_a(var2, var3, var4, var5);
      }

      abstract BlockFaceUV func_188007_a(float var1, float var2, float var3, float var4);

      // $FF: synthetic method
      Rotation(Object var1) {
         this();
      }
   }
}
