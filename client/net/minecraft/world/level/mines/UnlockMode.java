package net.minecraft.world.level.mines;

public enum UnlockMode {
   NEVER_UNLOCKED,
   ALWAYS_UNLOCKED,
   UNLOCKED_BY_CONDITION,
   UNLOCKED_ON_WIN;

   private UnlockMode() {
   }

   // $FF: synthetic method
   private static UnlockMode[] $values() {
      return new UnlockMode[]{NEVER_UNLOCKED, ALWAYS_UNLOCKED, UNLOCKED_BY_CONDITION, UNLOCKED_ON_WIN};
   }
}
