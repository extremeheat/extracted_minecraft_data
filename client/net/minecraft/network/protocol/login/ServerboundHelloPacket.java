package net.minecraft.network.protocol.login;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundHelloPacket(String a, UUID b) implements Packet<ServerLoginPacketListener> {
   private final String name;
   private final UUID profileId;

   public ServerboundHelloPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readUUID());
   }

   public ServerboundHelloPacket(String var1, UUID var2) {
      super();
      this.name = var1;
      this.profileId = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name, 16);
      var1.writeUUID(this.profileId);
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleHello(this);
   }
}
