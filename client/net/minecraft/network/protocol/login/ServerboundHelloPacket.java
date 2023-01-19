package net.minecraft.network.protocol.login;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundHelloPacket(String a, Optional<UUID> b) implements Packet<ServerLoginPacketListener> {
   private final String name;
   private final Optional<UUID> profileId;

   public ServerboundHelloPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readOptional(FriendlyByteBuf::readUUID));
   }

   public ServerboundHelloPacket(String var1, Optional<UUID> var2) {
      super();
      this.name = var1;
      this.profileId = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name, 16);
      var1.writeOptional(this.profileId, FriendlyByteBuf::writeUUID);
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleHello(this);
   }
}
