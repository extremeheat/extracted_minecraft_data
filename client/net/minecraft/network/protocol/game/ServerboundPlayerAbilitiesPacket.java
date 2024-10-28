package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerAbilitiesPacket> STREAM_CODEC = Packet.codec(ServerboundPlayerAbilitiesPacket::write, ServerboundPlayerAbilitiesPacket::new);
   private static final int FLAG_FLYING = 2;
   private final boolean isFlying;

   public ServerboundPlayerAbilitiesPacket(Abilities var1) {
      super();
      this.isFlying = var1.flying;
   }

   private ServerboundPlayerAbilitiesPacket(FriendlyByteBuf var1) {
      super();
      byte var2 = var1.readByte();
      this.isFlying = (var2 & 2) != 0;
   }

   private void write(FriendlyByteBuf var1) {
      byte var2 = 0;
      if (this.isFlying) {
         var2 = (byte)(var2 | 2);
      }

      var1.writeByte(var2);
   }

   public PacketType<ServerboundPlayerAbilitiesPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_ABILITIES;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerAbilities(this);
   }

   public boolean isFlying() {
      return this.isFlying;
   }
}
