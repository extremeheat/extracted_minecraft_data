package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundProjectilePowerPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundProjectilePowerPacket> STREAM_CODEC = Packet.codec(
      ClientboundProjectilePowerPacket::write, ClientboundProjectilePowerPacket::new
   );
   private final int id;
   private final double xPower;
   private final double yPower;
   private final double zPower;

   public ClientboundProjectilePowerPacket(int var1, double var2, double var4, double var6) {
      super();
      this.id = var1;
      this.xPower = var2;
      this.yPower = var4;
      this.zPower = var6;
   }

   private ClientboundProjectilePowerPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.xPower = var1.readDouble();
      this.yPower = var1.readDouble();
      this.zPower = var1.readDouble();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeDouble(this.xPower);
      var1.writeDouble(this.yPower);
      var1.writeDouble(this.zPower);
   }

   @Override
   public PacketType<ClientboundProjectilePowerPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PROJECTILE_POWER;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleProjectilePowerPacket(this);
   }

   public int getId() {
      return this.id;
   }

   public double getXPower() {
      return this.xPower;
   }

   public double getYPower() {
      return this.yPower;
   }

   public double getZPower() {
      return this.zPower;
   }
}
