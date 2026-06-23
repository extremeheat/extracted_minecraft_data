package net.minecraft.world.entity;

public enum MoverType {
   SELF,
   PLAYER,
   PISTON,
   SHULKER_BOX,
   SHULKER;

   private MoverType() {
   }

   public boolean isServerAndClientSimulated() {
      return this != SELF;
   }

   // $FF: synthetic method
   private static MoverType[] $values() {
      return new MoverType[]{SELF, PLAYER, PISTON, SHULKER_BOX, SHULKER};
   }
}
