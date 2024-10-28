package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundProjectilePowerPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundProjectilePowerPacket> STREAM_CODEC = Packet.codec(ClientboundProjectilePowerPacket::write, ClientboundProjectilePowerPacket::new);
   private final int id;
   private final double accelerationPower;

   public ClientboundProjectilePowerPacket(int var1, double var2) {
      super();
      this.id = var1;
      this.accelerationPower = var2;
   }

   private ClientboundProjectilePowerPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.accelerationPower = var1.readDouble();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeDouble(this.accelerationPower);
   }

   public PacketType<ClientboundProjectilePowerPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PROJECTILE_POWER;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleProjectilePowerPacket(this);
   }

   public int getId() {
      return this.id;
   }

   public double getAccelerationPower() {
      return this.accelerationPower;
   }
}
