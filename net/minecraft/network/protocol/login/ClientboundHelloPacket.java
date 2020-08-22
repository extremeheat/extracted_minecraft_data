package net.minecraft.network.protocol.login;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;

public class ClientboundHelloPacket implements Packet {
   private String serverId;
   private PublicKey publicKey;
   private byte[] nonce;

   public ClientboundHelloPacket() {
   }

   public ClientboundHelloPacket(String var1, PublicKey var2, byte[] var3) {
      this.serverId = var1;
      this.publicKey = var2;
      this.nonce = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.serverId = var1.readUtf(20);
      this.publicKey = Crypt.byteToPublicKey(var1.readByteArray());
      this.nonce = var1.readByteArray();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.serverId);
      var1.writeByteArray(this.publicKey.getEncoded());
      var1.writeByteArray(this.nonce);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleHello(this);
   }

   public String getServerId() {
      return this.serverId;
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public byte[] getNonce() {
      return this.nonce;
   }
}
