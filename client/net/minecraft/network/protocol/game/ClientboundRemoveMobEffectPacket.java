package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRemoveMobEffectPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final MobEffect effect;

   public ClientboundRemoveMobEffectPacket(int var1, MobEffect var2) {
      super();
      this.entityId = var1;
      this.effect = var2;
   }

   public ClientboundRemoveMobEffectPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.effect = var1.readById(Registry.MOB_EFFECT);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeId(Registry.MOB_EFFECT, this.effect);
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
