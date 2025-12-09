package com.mojang.math;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public enum Quadrant {
   R0(0, OctahedralGroup.IDENTITY, OctahedralGroup.IDENTITY, OctahedralGroup.IDENTITY),
   R90(1, OctahedralGroup.BLOCK_ROT_X_90, OctahedralGroup.BLOCK_ROT_Y_90, OctahedralGroup.BLOCK_ROT_Z_90),
   R180(2, OctahedralGroup.BLOCK_ROT_X_180, OctahedralGroup.BLOCK_ROT_Y_180, OctahedralGroup.BLOCK_ROT_Z_180),
   R270(3, OctahedralGroup.BLOCK_ROT_X_270, OctahedralGroup.BLOCK_ROT_Y_270, OctahedralGroup.BLOCK_ROT_Z_270);

   public static final Codec<Quadrant> CODEC = Codec.INT.comapFlatMap((var0) -> {
      DataResult var10000;
      switch (Mth.positiveModulo(var0, 360)) {
         case 0 -> var10000 = DataResult.success(R0);
         case 90 -> var10000 = DataResult.success(R90);
         case 180 -> var10000 = DataResult.success(R180);
         case 270 -> var10000 = DataResult.success(R270);
         default -> var10000 = DataResult.error(() -> "Invalid rotation " + var0 + " found, only 0/90/180/270 allowed");
      }

      return var10000;
   }, (var0) -> {
      Integer var10000;
      switch (var0.ordinal()) {
         case 0 -> var10000 = 0;
         case 1 -> var10000 = 90;
         case 2 -> var10000 = 180;
         case 3 -> var10000 = 270;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   });
   public final int shift;
   public final OctahedralGroup rotationX;
   public final OctahedralGroup rotationY;
   public final OctahedralGroup rotationZ;

   private Quadrant(final int var3, final OctahedralGroup var4, final OctahedralGroup var5, final OctahedralGroup var6) {
      this.shift = var3;
      this.rotationX = var4;
      this.rotationY = var5;
      this.rotationZ = var6;
   }

   /** @deprecated */
   @Deprecated
   public static Quadrant parseJson(int var0) {
      Quadrant var10000;
      switch (Mth.positiveModulo(var0, 360)) {
         case 0 -> var10000 = R0;
         case 90 -> var10000 = R90;
         case 180 -> var10000 = R180;
         case 270 -> var10000 = R270;
         default -> throw new JsonParseException("Invalid rotation " + var0 + " found, only 0/90/180/270 allowed");
      }

      return var10000;
   }

   public static OctahedralGroup fromXYAngles(Quadrant var0, Quadrant var1) {
      return var1.rotationY.compose(var0.rotationX);
   }

   public static OctahedralGroup fromXYZAngles(Quadrant var0, Quadrant var1, Quadrant var2) {
      return var2.rotationZ.compose(var1.rotationY.compose(var0.rotationX));
   }

   public int rotateVertexIndex(int var1) {
      return (var1 + this.shift) % 4;
   }

   // $FF: synthetic method
   private static Quadrant[] $values() {
      return new Quadrant[]{R0, R90, R180, R270};
   }
}
