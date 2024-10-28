package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddExperienceOrbPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundAddExperienceOrbPacket> STREAM_CODEC = Packet.codec(ClientboundAddExperienceOrbPacket::write, ClientboundAddExperienceOrbPacket::new);
   private final int id;
   private final double x;
   private final double y;
   private final double z;
   private final int value;

   public ClientboundAddExperienceOrbPacket(ExperienceOrb var1, ServerEntity var2) {
      super();
      this.id = var1.getId();
      Vec3 var3 = var2.getPositionBase();
      this.x = var3.x();
      this.y = var3.y();
      this.z = var3.z();
      this.value = var1.getValue();
   }

   private ClientboundAddExperienceOrbPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.value = var1.readShort();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeShort(this.value);
   }

   public PacketType<ClientboundAddExperienceOrbPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ADD_EXPERIENCE_ORB;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddExperienceOrb(this);
   }

   public int getId() {
      return this.id;
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

   public int getValue() {
      return this.value;
   }
}
