package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundResourcePackPacket(UUID a, ServerboundResourcePackPacket.Action b) implements Packet<ServerCommonPacketListener> {
   private final UUID id;
   private final ServerboundResourcePackPacket.Action action;

   public ServerboundResourcePackPacket(FriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readEnum(ServerboundResourcePackPacket.Action.class));
   }

   public ServerboundResourcePackPacket(UUID var1, ServerboundResourcePackPacket.Action var2) {
      super();
      this.id = var1;
      this.action = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeEnum(this.action);
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleResourcePackResponse(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED,
      DOWNLOADED,
      INVALID_URL,
      FAILED_RELOAD,
      DISCARDED;

      private Action() {
      }

      public boolean isTerminal() {
         return this != ACCEPTED && this != DOWNLOADED;
      }
   }
}
