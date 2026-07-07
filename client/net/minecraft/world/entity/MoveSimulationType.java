package net.minecraft.world.entity;

public enum MoveSimulationType {
   SERVER_AND_CLIENT,
   AUTHORITATIVE_SIDE,
   AUTHORITATIVE_SIDE_AND_SERVER;

   private MoveSimulationType() {
   }

   // $FF: synthetic method
   private static MoveSimulationType[] $values() {
      return new MoveSimulationType[]{SERVER_AND_CLIENT, AUTHORITATIVE_SIDE, AUTHORITATIVE_SIDE_AND_SERVER};
   }
}
