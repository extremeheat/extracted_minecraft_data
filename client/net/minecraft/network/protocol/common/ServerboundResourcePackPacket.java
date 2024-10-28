package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundResourcePackPacket(UUID id, Action action) implements Packet<ServerCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundResourcePackPacket> STREAM_CODEC = Packet.codec(ServerboundResourcePackPacket::write, ServerboundResourcePackPacket::new);

   private ServerboundResourcePackPacket(FriendlyByteBuf var1) {
      this(var1.readUUID(), (Action)var1.readEnum(Action.class));
   }

   public ServerboundResourcePackPacket(UUID id, Action action) {
      super();
      this.id = id;
      this.action = action;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeEnum(this.action);
   }

   public PacketType<ServerboundResourcePackPacket> type() {
      return CommonPacketTypes.SERVERBOUND_RESOURCE_PACK;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleResourcePackResponse(this);
   }

   public UUID id() {
      return this.id;
   }

   public Action action() {
      return this.action;
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

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, DOWNLOADED, INVALID_URL, FAILED_RELOAD, DISCARDED};
      }
   }
}
