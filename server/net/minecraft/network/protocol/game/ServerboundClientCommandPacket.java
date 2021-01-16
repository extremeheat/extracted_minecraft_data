package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
   private ServerboundClientCommandPacket.Action action;

   public ServerboundClientCommandPacket() {
      super();
   }

   public ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action var1) {
      super();
      this.action = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.action = (ServerboundClientCommandPacket.Action)var1.readEnum(ServerboundClientCommandPacket.Action.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
