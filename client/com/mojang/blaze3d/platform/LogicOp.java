package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public enum LogicOp {
   NONE,
   OR_REVERSE;

   private LogicOp() {
   }

   // $FF: synthetic method
   private static LogicOp[] $values() {
      return new LogicOp[]{NONE, OR_REVERSE};
   }
}
