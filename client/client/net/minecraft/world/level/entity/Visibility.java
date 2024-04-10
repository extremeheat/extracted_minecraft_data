package net.minecraft.world.level.entity;

import net.minecraft.server.level.FullChunkStatus;

public enum Visibility {
   HIDDEN(false, false),
   TRACKED(true, false),
   TICKING(true, true);

   private final boolean accessible;
   private final boolean ticking;

   private Visibility(final boolean param3, final boolean param4) {
      this.accessible = nullxx;
      this.ticking = nullxxx;
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
}
