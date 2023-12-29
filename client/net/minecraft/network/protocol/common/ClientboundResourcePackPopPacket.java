package net.minecraft.network.protocol.common;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundResourcePackPopPacket(Optional<UUID> a) implements Packet<ClientCommonPacketListener> {
   private final Optional<UUID> id;

   public ClientboundResourcePackPopPacket(FriendlyByteBuf var1) {
      this(var1.readOptional(FriendlyByteBuf::readUUID));
   }

   public ClientboundResourcePackPopPacket(Optional<UUID> var1) {
      super();
      this.id = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeOptional(this.id, FriendlyByteBuf::writeUUID);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleResourcePackPop(this);
   }
}
