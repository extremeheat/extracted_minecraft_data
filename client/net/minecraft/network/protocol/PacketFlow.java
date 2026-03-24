package net.minecraft.network.protocol;

public enum PacketFlow {
   SERVERBOUND("serverbound"),
   CLIENTBOUND("clientbound");

   private final String id;

   private PacketFlow(final String id) {
      this.id = id;
   }

   public PacketFlow getOpposite() {
      return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
   }

   public String id() {
      return this.id;
   }

   // $FF: synthetic method
   private static PacketFlow[] $values() {
      return new PacketFlow[]{SERVERBOUND, CLIENTBOUND};
   }
}
