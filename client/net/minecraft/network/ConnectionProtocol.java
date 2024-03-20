package net.minecraft.network;

public enum ConnectionProtocol {
   HANDSHAKING("handshake"),
   PLAY("play"),
   STATUS("status"),
   LOGIN("login"),
   CONFIGURATION("configuration");

   private final String id;

   private ConnectionProtocol(String var3) {
      this.id = var3;
   }

   public String id() {
      return this.id;
   }
}