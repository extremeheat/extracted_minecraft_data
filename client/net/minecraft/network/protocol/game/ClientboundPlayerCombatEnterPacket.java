package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPlayerCombatEnterPacket implements Packet<ClientGamePacketListener> {
   public static final ClientboundPlayerCombatEnterPacket INSTANCE = new ClientboundPlayerCombatEnterPacket();
   public static final StreamCodec<ByteBuf, ClientboundPlayerCombatEnterPacket> STREAM_CODEC;

   private ClientboundPlayerCombatEnterPacket() {
      super();
   }

   public PacketType<ClientboundPlayerCombatEnterPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_ENTER;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatEnter(this);
   }

   static {
      STREAM_CODEC = StreamCodec.unit(INSTANCE);
   }
}
