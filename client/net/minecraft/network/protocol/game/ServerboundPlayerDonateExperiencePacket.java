package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPlayerDonateExperiencePacket() implements Packet<ServerGamePacketListener> {
   public static final ServerboundPlayerDonateExperiencePacket INSTANCE = new ServerboundPlayerDonateExperiencePacket();
   public static final StreamCodec<ByteBuf, ServerboundPlayerDonateExperiencePacket> STREAM_CODEC;

   public ServerboundPlayerDonateExperiencePacket() {
      super();
   }

   public PacketType<ServerboundPlayerDonateExperiencePacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_DONATE_EXPERIENCE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerDonateExperience(this);
   }

   static {
      STREAM_CODEC = StreamCodec.<ByteBuf, ServerboundPlayerDonateExperiencePacket>unit(INSTANCE);
   }
}
