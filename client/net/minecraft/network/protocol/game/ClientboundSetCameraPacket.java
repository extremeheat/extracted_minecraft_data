package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket implements Packet<ClientGamePacketListener> {
   private final int cameraId;

   public ClientboundSetCameraPacket(Entity var1) {
      super();
      this.cameraId = var1.getId();
   }

   public ClientboundSetCameraPacket(FriendlyByteBuf var1) {
      super();
      this.cameraId = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.cameraId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetCamera(this);
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.cameraId);
   }
}
