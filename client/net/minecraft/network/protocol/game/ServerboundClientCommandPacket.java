package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundClientCommandPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundClientCommandPacket>codec(ServerboundClientCommandPacket::write, ServerboundClientCommandPacket::new);
   private final Action action;

   public ServerboundClientCommandPacket(final Action action) {
      super();
      this.action = action;
   }

   private ServerboundClientCommandPacket(final FriendlyByteBuf input) {
      super();
      this.action = (Action)input.readEnum(Action.class);
   }

   private void write(final FriendlyByteBuf output) {
      output.writeEnum(this.action);
   }

   public PacketType<ServerboundClientCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_CLIENT_COMMAND;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleClientCommand(this);
   }

   public Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      REQUEST_GAMERULE_VALUES;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{PERFORM_RESPAWN, REQUEST_STATS, REQUEST_GAMERULE_VALUES};
      }
   }
}
