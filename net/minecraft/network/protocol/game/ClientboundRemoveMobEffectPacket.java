package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRemoveMobEffectPacket implements Packet {
   private int entityId;
   private MobEffect effect;

   public ClientboundRemoveMobEffectPacket() {
   }

   public ClientboundRemoveMobEffectPacket(int var1, MobEffect var2) {
      this.entityId = var1;
      this.effect = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      this.effect = MobEffect.byId(var1.readUnsignedByte());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
      var1.writeByte(MobEffect.getId(this.effect));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRemoveMobEffect(this);
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   @Nullable
   public MobEffect getEffect() {
      return this.effect;
   }
}
