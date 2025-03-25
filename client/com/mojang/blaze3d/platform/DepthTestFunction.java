package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum DepthTestFunction {
   NO_DEPTH_TEST,
   EQUAL_DEPTH_TEST,
   LEQUAL_DEPTH_TEST,
   LESS_DEPTH_TEST,
   GREATER_DEPTH_TEST;

   private DepthTestFunction() {
   }

   // $FF: synthetic method
   private static DepthTestFunction[] $values() {
      return new DepthTestFunction[]{NO_DEPTH_TEST, EQUAL_DEPTH_TEST, LEQUAL_DEPTH_TEST, LESS_DEPTH_TEST, GREATER_DEPTH_TEST};
   }
}
