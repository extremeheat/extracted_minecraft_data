package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int a, float b) implements Packet<ClientGamePacketListener> {
   private final int id;
   private final float yaw;

   public ClientboundHurtAnimationPacket(LivingEntity var1) {
      this(var1.getId(), var1.getHurtDir());
   }

   public ClientboundHurtAnimationPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readFloat());
   }

   public ClientboundHurtAnimationPacket(int var1, float var2) {
      super();
      this.id = var1;
      this.yaw = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeFloat(this.yaw);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleHurtAnimation(this);
   }
}
