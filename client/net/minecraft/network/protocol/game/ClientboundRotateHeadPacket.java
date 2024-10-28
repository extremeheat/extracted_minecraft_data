package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundRotateHeadPacket> STREAM_CODEC = Packet.codec(ClientboundRotateHeadPacket::write, ClientboundRotateHeadPacket::new);
   private final int entityId;
   private final byte yHeadRot;

   public ClientboundRotateHeadPacket(Entity var1, byte var2) {
      super();
      this.entityId = var1.getId();
      this.yHeadRot = var2;
   }

   private ClientboundRotateHeadPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.yHeadRot = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeByte(this.yHeadRot);
   }

   public PacketType<ClientboundRotateHeadPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ROTATE_HEAD;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRotateMob(this);
   }

   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public byte getYHeadRot() {
      return this.yHeadRot;
   }
}
