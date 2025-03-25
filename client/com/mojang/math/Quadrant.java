package com.mojang.math;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Mth;

public enum Quadrant {
   R0(0),
   R90(1),
   R180(2),
   R270(3);

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

   private Quadrant(final int var3) {
      this.shift = var3;
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

   public int rotateVertexIndex(int var1) {
      return (var1 + this.shift) % 4;
   }

   // $FF: synthetic method
   private static Quadrant[] $values() {
      return new Quadrant[]{R0, R90, R180, R270};
   }
}
