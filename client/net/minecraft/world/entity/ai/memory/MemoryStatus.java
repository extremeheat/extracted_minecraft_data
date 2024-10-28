package net.minecraft.world.entity.ai.memory;

public enum MemoryStatus {
   VALUE_PRESENT,
   VALUE_ABSENT,
   REGISTERED;

   private MemoryStatus() {
   }

   // $FF: synthetic method
   private static MemoryStatus[] $values() {
      return new MemoryStatus[]{VALUE_PRESENT, VALUE_ABSENT, REGISTERED};
   }
}
