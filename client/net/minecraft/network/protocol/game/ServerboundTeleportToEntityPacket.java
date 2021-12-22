package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerboundTeleportToEntityPacket implements Packet<ServerGamePacketListener> {
   private final UUID uuid;

   public ServerboundTeleportToEntityPacket(UUID var1) {
      super();
      this.uuid = var1;
   }

   public ServerboundTeleportToEntityPacket(FriendlyByteBuf var1) {
      super();
      this.uuid = var1.readUUID();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.uuid);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleTeleportToEntityPacket(this);
   }

   @Nullable
   public Entity getEntity(ServerLevel var1) {
      return var1.getEntity(this.uuid);
   }
}
