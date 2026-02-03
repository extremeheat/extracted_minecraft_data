package net.minecraft.network;

public enum ConnectionProtocol {
   HANDSHAKING("handshake"),
   PLAY("play"),
   STATUS("status"),
   LOGIN("login"),
   CONFIGURATION("configuration");

   private final String id;

   private ConnectionProtocol(final String id) {
      this.id = id;
   }

   public String id() {
      return this.id;
   }

   // $FF: synthetic method
   private static ConnectionProtocol[] $values() {
      return new ConnectionProtocol[]{HANDSHAKING, PLAY, STATUS, LOGIN, CONFIGURATION};
   }
}
