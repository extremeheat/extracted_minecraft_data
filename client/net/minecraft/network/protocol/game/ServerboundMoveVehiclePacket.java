package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ServerboundMoveVehiclePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundMoveVehiclePacket> STREAM_CODEC = Packet.codec(ServerboundMoveVehiclePacket::write, ServerboundMoveVehiclePacket::new);
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;

   public ServerboundMoveVehiclePacket(Entity var1) {
      super();
      this.x = var1.lerpTargetX();
      this.y = var1.lerpTargetY();
      this.z = var1.lerpTargetZ();
      this.yRot = var1.getYRot();
      this.xRot = var1.getXRot();
   }

   private ServerboundMoveVehiclePacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
   }

   public PacketType<ServerboundMoveVehiclePacket> type() {
      return GamePacketTypes.SERVERBOUND_MOVE_VEHICLE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}
