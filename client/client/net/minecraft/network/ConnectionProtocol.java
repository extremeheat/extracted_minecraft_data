package net.minecraft.network;

public enum ConnectionProtocol {
   HANDSHAKING("handshake"),
   PLAY("play"),
   STATUS("status"),
   LOGIN("login"),
   CONFIGURATION("configuration");

   private final String id;

   private ConnectionProtocol(final String param3) {
      this.id = nullxx;
   }

   public String id() {
      return this.id;
   }
}
