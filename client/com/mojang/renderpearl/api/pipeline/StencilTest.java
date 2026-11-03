package com.mojang.renderpearl.api.pipeline;

public record StencilTest(PerFace front, PerFace back) {
   public StencilTest {
      super();
   }

   public static record PerFace(Op fail, Op pass, Op depthFail, CompareOp compare, byte compareMask, byte writeMask, byte reference) {
      public PerFace {
         super();
      }
   }

   public static enum Op {
      KEEP,
      ZERO,
      REPLACE,
      INCREMENT_CLAMP,
      DECREEMENT_CLAMP,
      INVERT,
      INCREMENT_WRAP,
      DECREMENT_WRAP;

      private Op() {
      }

      // $FF: synthetic method
      private static Op[] $values() {
         return new Op[]{KEEP, ZERO, REPLACE, INCREMENT_CLAMP, DECREEMENT_CLAMP, INVERT, INCREMENT_WRAP, DECREMENT_WRAP};
      }
   }
}
