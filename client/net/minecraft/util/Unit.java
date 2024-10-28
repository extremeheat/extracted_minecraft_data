package net.minecraft.util;

import com.mojang.serialization.Codec;

public enum Unit {
   INSTANCE;

   public static final Codec<Unit> CODEC = Codec.unit(INSTANCE);

   private Unit() {
   }

   // $FF: synthetic method
   private static Unit[] $values() {
      return new Unit[]{INSTANCE};
   }
}
