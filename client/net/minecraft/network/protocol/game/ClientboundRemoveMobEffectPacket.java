package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record ClientboundRemoveMobEffectPacket(int entityId, Holder<MobEffect> effect) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRemoveMobEffectPacket> STREAM_CODEC;

   public ClientboundRemoveMobEffectPacket(int var1, Holder<MobEffect> var2) {
      super();
      this.entityId = var1;
      this.effect = var2;
   }

   public PacketType<ClientboundRemoveMobEffectPacket> type() {
      return GamePacketTypes.CLIENTBOUND_REMOVE_MOB_EFFECT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRemoveMobEffect(this);
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public int entityId() {
      return this.entityId;
   }

   public Holder<MobEffect> effect() {
      return this.effect;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, (var0) -> {
         return var0.entityId;
      }, ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT), ClientboundRemoveMobEffectPacket::effect, ClientboundRemoveMobEffectPacket::new);
   }
}
