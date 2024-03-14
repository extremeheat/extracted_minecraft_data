package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundResourcePackPacket(UUID b, ServerboundResourcePackPacket.Action c) implements Packet<ServerCommonPacketListener> {
   private final UUID id;
   private final ServerboundResourcePackPacket.Action action;
   public static final StreamCodec<FriendlyByteBuf, ServerboundResourcePackPacket> STREAM_CODEC = Packet.codec(
      ServerboundResourcePackPacket::write, ServerboundResourcePackPacket::new
   );

   private ServerboundResourcePackPacket(FriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readEnum(ServerboundResourcePackPacket.Action.class));
   }

   public ServerboundResourcePackPacket(UUID var1, ServerboundResourcePackPacket.Action var2) {
      super();
      this.id = var1;
      this.action = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeEnum(this.action);
   }

   @Override
   public PacketType<ServerboundResourcePackPacket> type() {
      return CommonPacketTypes.SERVERBOUND_RESOURCE_PACK;
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
