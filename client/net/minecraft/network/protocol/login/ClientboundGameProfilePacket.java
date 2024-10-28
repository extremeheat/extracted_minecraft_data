package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundGameProfilePacket(GameProfile gameProfile) implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundGameProfilePacket> STREAM_CODEC;

   public ClientboundGameProfilePacket(GameProfile var1) {
      super();
      this.gameProfile = var1;
   }

   public PacketType<ClientboundGameProfilePacket> type() {
      return LoginPacketTypes.CLIENTBOUND_GAME_PROFILE;
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleGameProfile(this);
   }

   public boolean isTerminal() {
      return true;
   }

   public GameProfile gameProfile() {
      return this.gameProfile;
   }

   static {
      STREAM_CODEC = ByteBufCodecs.GAME_PROFILE.map(ClientboundGameProfilePacket::new, ClientboundGameProfilePacket::gameProfile);
   }
}
