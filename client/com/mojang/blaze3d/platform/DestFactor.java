package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum DestFactor {
   CONSTANT_ALPHA,
   CONSTANT_COLOR,
   DST_ALPHA,
   DST_COLOR,
   ONE,
   ONE_MINUS_CONSTANT_ALPHA,
   ONE_MINUS_CONSTANT_COLOR,
   ONE_MINUS_DST_ALPHA,
   ONE_MINUS_DST_COLOR,
   ONE_MINUS_SRC_ALPHA,
   ONE_MINUS_SRC_COLOR,
   SRC_ALPHA,
   SRC_COLOR,
   ZERO;

   private DestFactor() {
   }

   // $FF: synthetic method
   private static DestFactor[] $values() {
      return new DestFactor[]{CONSTANT_ALPHA, CONSTANT_COLOR, DST_ALPHA, DST_COLOR, ONE, ONE_MINUS_CONSTANT_ALPHA, ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_DST_ALPHA, ONE_MINUS_DST_COLOR, ONE_MINUS_SRC_ALPHA, ONE_MINUS_SRC_COLOR, SRC_ALPHA, SRC_COLOR, ZERO};
   }
}
