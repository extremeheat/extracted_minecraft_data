package net.minecraft.world.level.material;

public enum PushReaction {
   PUSH_PULL,
   PUSH,
   POPPED,
   IMMOVEABLE,
   IGNORE_ENTITY;

   private PushReaction() {
   }

   // $FF: synthetic method
   private static PushReaction[] $values() {
      return new PushReaction[]{PUSH_PULL, PUSH, POPPED, IMMOVEABLE, IGNORE_ENTITY};
   }
}
