package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundClientCommandPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundClientCommandPacket>codec(ServerboundClientCommandPacket::write, ServerboundClientCommandPacket::new);
   private final Action action;

   public ServerboundClientCommandPacket(Action var1) {
      super();
      this.action = var1;
   }

   private ServerboundClientCommandPacket(FriendlyByteBuf var1) {
      super();
      this.action = (Action)var1.readEnum(Action.class);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
   }

   public PacketType<ServerboundClientCommandPacket> type() {
      return GamePacketTypes.SERVERBOUND_CLIENT_COMMAND;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleClientCommand(this);
   }

   public Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{PERFORM_RESPAWN, REQUEST_STATS};
      }
   }
}
