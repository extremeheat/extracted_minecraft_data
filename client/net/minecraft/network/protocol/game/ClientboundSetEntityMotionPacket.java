package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetEntityMotionPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundSetEntityMotionPacket>codec(ClientboundSetEntityMotionPacket::write, ClientboundSetEntityMotionPacket::new);
   private final int id;
   private final int xa;
   private final int ya;
   private final int za;

   public ClientboundSetEntityMotionPacket(Entity var1) {
      this(var1.getId(), var1.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int var1, Vec3 var2) {
      super();
      this.id = var1;
      double var3 = 3.9;
      double var5 = Mth.clamp(var2.x, -3.9, 3.9);
      double var7 = Mth.clamp(var2.y, -3.9, 3.9);
      double var9 = Mth.clamp(var2.z, -3.9, 3.9);
      this.xa = (int)(var5 * 8000.0);
      this.ya = (int)(var7 * 8000.0);
      this.za = (int)(var9 * 8000.0);
   }

   private ClientboundSetEntityMotionPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.xa = var1.readShort();
      this.ya = var1.readShort();
      this.za = var1.readShort();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeShort(this.xa);
      var1.writeShort(this.ya);
      var1.writeShort(this.za);
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

   public double getXa() {
      return (double)this.xa / 8000.0;
   }

   public double getYa() {
      return (double)this.ya / 8000.0;
   }

   public double getZa() {
      return (double)this.za / 8000.0;
   }
}
