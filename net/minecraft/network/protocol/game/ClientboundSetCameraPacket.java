package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket implements Packet {
   public int cameraId;

   public ClientboundSetCameraPacket() {
   }

   public ClientboundSetCameraPacket(Entity var1) {
      this.cameraId = var1.getId();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.cameraId = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
