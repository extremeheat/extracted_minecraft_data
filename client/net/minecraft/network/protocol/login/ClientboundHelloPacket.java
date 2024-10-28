package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundHelloPacket> STREAM_CODEC = Packet.codec(ClientboundHelloPacket::write, ClientboundHelloPacket::new);
   private final String serverId;
   private final byte[] publicKey;
   private final byte[] challenge;
   private final boolean shouldAuthenticate;

   public ClientboundHelloPacket(String var1, byte[] var2, byte[] var3, boolean var4) {
      super();
      this.serverId = var1;
      this.publicKey = var2;
      this.challenge = var3;
      this.shouldAuthenticate = var4;
   }

   private ClientboundHelloPacket(FriendlyByteBuf var1) {
      super();
      this.serverId = var1.readUtf(20);
      this.publicKey = var1.readByteArray();
      this.challenge = var1.readByteArray();
      this.shouldAuthenticate = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.serverId);
      var1.writeByteArray(this.publicKey);
      var1.writeByteArray(this.challenge);
      var1.writeBoolean(this.shouldAuthenticate);
   }

   public PacketType<ClientboundHelloPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_HELLO;
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

   public byte[] getChallenge() {
      return this.challenge;
   }

   public boolean shouldAuthenticate() {
      return this.shouldAuthenticate;
   }
}
