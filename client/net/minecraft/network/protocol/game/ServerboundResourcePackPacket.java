package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundResourcePackPacket implements Packet<ServerGamePacketListener> {
   private final Action action;

   public ServerboundResourcePackPacket(Action var1) {
      super();
      this.action = var1;
   }

   public ServerboundResourcePackPacket(FriendlyByteBuf var1) {
      super();
      this.action = (Action)var1.readEnum(Action.class);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleResourcePackResponse(this);
   }

   public Action getAction() {
      return this.action;
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED};
      }
   }
}
