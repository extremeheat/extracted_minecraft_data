package net.minecraft.network.protocol.login;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket implements Packet<ClientLoginPacketListener> {
   private String serverId;
   private byte[] publicKey;
   private byte[] nonce;

   public ClientboundHelloPacket() {
      super();
   }

   public ClientboundHelloPacket(String var1, byte[] var2, byte[] var3) {
      super();
      this.serverId = var1;
      this.publicKey = var2;
      this.nonce = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.serverId = var1.readUtf(20);
      this.publicKey = var1.readByteArray();
      this.nonce = var1.readByteArray();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.serverId);
      var1.writeByteArray(this.publicKey);
      var1.writeByteArray(this.nonce);
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleHello(this);
   }

   public String getServerId() {
      return this.serverId;
   }

   public PublicKey getPublicKey() throws CryptException {
      return Crypt.byteToPublicKey(this.publicKey);
   }

   public byte[] getNonce() {
      return this.nonce;
   }
}
