package net.minecraft.server.level;

public enum FullChunkStatus {
   INACCESSIBLE,
   FULL,
   BLOCK_TICKING,
   ENTITY_TICKING;

   private FullChunkStatus() {
   }

   public boolean isOrAfter(FullChunkStatus var1) {
      return this.ordinal() >= var1.ordinal();
   }

   // $FF: synthetic method
   private static FullChunkStatus[] $values() {
      return new FullChunkStatus[]{INACCESSIBLE, FULL, BLOCK_TICKING, ENTITY_TICKING};
   }
}
