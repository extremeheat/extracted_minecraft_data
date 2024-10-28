package net.minecraft.world.entity;

public enum MoverType {
   SELF,
   PLAYER,
   PISTON,
   SHULKER_BOX,
   SHULKER;

   private MoverType() {
   }

   // $FF: synthetic method
   private static MoverType[] $values() {
      return new MoverType[]{SELF, PLAYER, PISTON, SHULKER_BOX, SHULKER};
   }
}
