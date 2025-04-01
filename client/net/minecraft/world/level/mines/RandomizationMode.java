package net.minecraft.world.level.mines;

public enum RandomizationMode {
   NEVER,
   WHEN_UNLOCKABLE,
   WHEN_UNLOCKED;

   private RandomizationMode() {
   }

   // $FF: synthetic method
   private static RandomizationMode[] $values() {
      return new RandomizationMode[]{NEVER, WHEN_UNLOCKABLE, WHEN_UNLOCKED};
   }
}
