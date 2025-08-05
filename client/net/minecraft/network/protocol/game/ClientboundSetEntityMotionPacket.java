package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetEntityMotionPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundSetEntityMotionPacket>codec(ClientboundSetEntityMotionPacket::write, ClientboundSetEntityMotionPacket::new);
   private final int id;
   private final Vec3 movement;

   public ClientboundSetEntityMotionPacket(Entity var1) {
      this(var1.getId(), var1.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int var1, Vec3 var2) {
      super();
      this.id = var1;
      this.movement = var2;
   }

   private ClientboundSetEntityMotionPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.movement = var1.readLpVec3();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeLpVec3(this.movement);
   }

   public PacketType<ClientboundSetEntityMotionPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_ENTITY_MOTION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityMotion(this);
   }

   public int getId() {
      return this.id;
   }

   public Vec3 getMovement() {
      return this.movement;
   }
}
