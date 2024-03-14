package net.minecraft.network.protocol;

public enum PacketFlow {
   SERVERBOUND("serverbound"),
   CLIENTBOUND("clientbound");

   private final String id;

   private PacketFlow(String var3) {
      this.id = var3;
   }

   public PacketFlow getOpposite() {
      return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
   }

   public String id() {
      return this.id;
   }
}
