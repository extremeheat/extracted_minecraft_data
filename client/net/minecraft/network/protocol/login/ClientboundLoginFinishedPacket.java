package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundLoginFinishedPacket(GameProfile gameProfile) implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundLoginFinishedPacket> STREAM_CODEC;

   public ClientboundLoginFinishedPacket(GameProfile var1) {
      super();
      this.gameProfile = var1;
   }

   public PacketType<ClientboundLoginFinishedPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_LOGIN_FINISHED;
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleLoginFinished(this);
   }

   public boolean isTerminal() {
      return true;
   }

   public GameProfile gameProfile() {
      return this.gameProfile;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.GAME_PROFILE, ClientboundLoginFinishedPacket::gameProfile, ClientboundLoginFinishedPacket::new);
   }
}
