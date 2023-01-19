package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
   private final ServerboundClientCommandPacket.Action action;

   public ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action var1) {
      super();
      this.action = var1;
   }

   public ServerboundClientCommandPacket(FriendlyByteBuf var1) {
      super();
      this.action = var1.readEnum(ServerboundClientCommandPacket.Action.class);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleClientCommand(this);
   }

   public ServerboundClientCommandPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      PERFORM_RESPAWN,
      REQUEST_STATS;

      private Action() {
      }
   }
}
