package net.minecraft.world.level.levelgen.structure;

public enum StructureCheckResult {
   START_PRESENT,
   START_NOT_PRESENT,
   CHUNK_LOAD_NEEDED;

   private StructureCheckResult() {
   }

   // $FF: synthetic method
   private static StructureCheckResult[] $values() {
      return new StructureCheckResult[]{START_PRESENT, START_NOT_PRESENT, CHUNK_LOAD_NEEDED};
   }
}
