package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int b, float c) implements Packet<ClientGamePacketListener> {
   private final int id;
   private final float yaw;
   public static final StreamCodec<FriendlyByteBuf, ClientboundHurtAnimationPacket> STREAM_CODEC = Packet.codec(
      ClientboundHurtAnimationPacket::write, ClientboundHurtAnimationPacket::new
   );

   public ClientboundHurtAnimationPacket(LivingEntity var1) {
      this(var1.getId(), var1.getHurtDir());
   }

   private ClientboundHurtAnimationPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readFloat());
   }

   public ClientboundHurtAnimationPacket(int var1, float var2) {
      super();
      this.id = var1;
      this.yaw = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeFloat(this.yaw);
   }

   @Override
   public PacketType<ClientboundHurtAnimationPacket> type() {
      return GamePacketTypes.CLIENTBOUND_HURT_ANIMATION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleHurtAnimation(this);
   }
}
