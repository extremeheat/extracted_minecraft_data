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

   public static final Codec<Quadrant> CODEC = Codec.INT.comapFlatMap((degrees) -> {
      DataResult var10000;
      switch (Mth.positiveModulo(degrees, 360)) {
         case 0 -> var10000 = DataResult.success(R0);
         case 90 -> var10000 = DataResult.success(R90);
         case 180 -> var10000 = DataResult.success(R180);
         case 270 -> var10000 = DataResult.success(R270);
         default -> var10000 = DataResult.error(() -> "Invalid rotation " + degrees + " found, only 0/90/180/270 allowed");
      }

      return var10000;
   }, (quadrant) -> {
      Integer var10000;
      switch (quadrant.ordinal()) {
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

   private Quadrant(final int shift, final OctahedralGroup rotationX, final OctahedralGroup rotationY, final OctahedralGroup rotationZ) {
      this.shift = shift;
      this.rotationX = rotationX;
      this.rotationY = rotationY;
      this.rotationZ = rotationZ;
   }

   /** @deprecated */
   @Deprecated
   public static Quadrant parseJson(final int degrees) {
      Quadrant var10000;
      switch (Mth.positiveModulo(degrees, 360)) {
         case 0 -> var10000 = R0;
         case 90 -> var10000 = R90;
         case 180 -> var10000 = R180;
         case 270 -> var10000 = R270;
         default -> throw new JsonParseException("Invalid rotation " + degrees + " found, only 0/90/180/270 allowed");
      }

      return var10000;
   }

   public static OctahedralGroup fromXYAngles(final Quadrant xRotation, final Quadrant yRotation) {
      return yRotation.rotationY.compose(xRotation.rotationX);
   }

   public static OctahedralGroup fromXYZAngles(final Quadrant xRotation, final Quadrant yRotation, final Quadrant zRotation) {
      return zRotation.rotationZ.compose(yRotation.rotationY.compose(xRotation.rotationX));
   }

   public int rotateVertexIndex(final int index) {
      return (index + this.shift) % 4;
   }

   // $FF: synthetic method
   private static Quadrant[] $values() {
      return new Quadrant[]{R0, R90, R180, R270};
   }
}
