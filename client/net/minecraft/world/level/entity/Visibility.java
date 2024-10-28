package net.minecraft.world.level.entity;

import net.minecraft.server.level.FullChunkStatus;

public enum Visibility {
   HIDDEN(false, false),
   TRACKED(true, false),
   TICKING(true, true);

   private final boolean accessible;
   private final boolean ticking;

   private Visibility(boolean var3, boolean var4) {
      this.accessible = var3;
      this.ticking = var4;
   }

   public boolean isTicking() {
      return this.ticking;
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public static Visibility fromFullChunkStatus(FullChunkStatus var0) {
      if (var0.isOrAfter(FullChunkStatus.ENTITY_TICKING)) {
         return TICKING;
      } else {
         return var0.isOrAfter(FullChunkStatus.FULL) ? TRACKED : HIDDEN;
      }
   }

   // $FF: synthetic method
   private static Visibility[] $values() {
      return new Visibility[]{HIDDEN, TRACKED, TICKING};
   }
}
