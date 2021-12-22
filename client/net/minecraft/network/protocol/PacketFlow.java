package net.minecraft.network.protocol;

public enum PacketFlow {
   SERVERBOUND,
   CLIENTBOUND;

   private PacketFlow() {
   }

   public PacketFlow getOpposite() {
      return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
   }

   // $FF: synthetic method
   private static PacketFlow[] $values() {
      return new PacketFlow[]{SERVERBOUND, CLIENTBOUND};
   }
}
