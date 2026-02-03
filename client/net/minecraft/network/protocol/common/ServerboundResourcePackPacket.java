package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundResourcePackPacket(UUID id, Action action) implements Packet<ServerCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundResourcePackPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundResourcePackPacket>codec(ServerboundResourcePackPacket::write, ServerboundResourcePackPacket::new);

   private ServerboundResourcePackPacket(final FriendlyByteBuf input) {
      this(input.readUUID(), (Action)input.readEnum(Action.class));
   }

   public ServerboundResourcePackPacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUUID(this.id);
      output.writeEnum(this.action);
   }

   public PacketType<ServerboundResourcePackPacket> type() {
      return CommonPacketTypes.SERVERBOUND_RESOURCE_PACK;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleResourcePackResponse(this);
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
