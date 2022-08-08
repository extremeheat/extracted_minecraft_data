package net.minecraft.world.entity;

public enum Pose {
   STANDING,
   FALL_FLYING,
   SLEEPING,
   SWIMMING,
   SPIN_ATTACK,
   CROUCHING,
   LONG_JUMPING,
   DYING,
   CROAKING,
   USING_TONGUE,
   ROARING,
   SNIFFING,
   EMERGING,
   DIGGING;

   private Pose() {
   }

   // $FF: synthetic method
   private static Pose[] $values() {
      return new Pose[]{STANDING, FALL_FLYING, SLEEPING, SWIMMING, SPIN_ATTACK, CROUCHING, LONG_JUMPING, DYING, CROAKING, USING_TONGUE, ROARING, SNIFFING, EMERGING, DIGGING};
   }
}
