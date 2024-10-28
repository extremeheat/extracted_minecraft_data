package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetCameraPacket> STREAM_CODEC = Packet.codec(ClientboundSetCameraPacket::write, ClientboundSetCameraPacket::new);
   private final int cameraId;

   public ClientboundSetCameraPacket(Entity var1) {
      super();
      this.cameraId = var1.getId();
   }

   private ClientboundSetCameraPacket(FriendlyByteBuf var1) {
      super();
      this.cameraId = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.cameraId);
   }

   public PacketType<ClientboundSetCameraPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_CAMERA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetCamera(this);
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.cameraId);
   }
}
