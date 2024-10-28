package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateMobEffectPacket> STREAM_CODEC = Packet.codec(ClientboundUpdateMobEffectPacket::write, ClientboundUpdateMobEffectPacket::new);
   private static final int FLAG_AMBIENT = 1;
   private static final int FLAG_VISIBLE = 2;
   private static final int FLAG_SHOW_ICON = 4;
   private static final int FLAG_BLEND = 8;
   private final int entityId;
   private final Holder<MobEffect> effect;
   private final int effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;

   public ClientboundUpdateMobEffectPacket(int var1, MobEffectInstance var2, boolean var3) {
      super();
      this.entityId = var1;
      this.effect = var2.getEffect();
      this.effectAmplifier = var2.getAmplifier();
      this.effectDurationTicks = var2.getDuration();
      byte var4 = 0;
      if (var2.isAmbient()) {
         var4 = (byte)(var4 | 1);
      }

      if (var2.isVisible()) {
         var4 = (byte)(var4 | 2);
      }

      if (var2.showIcon()) {
         var4 = (byte)(var4 | 4);
      }

      if (var3) {
         var4 = (byte)(var4 | 8);
      }

      this.flags = var4;
   }

   private ClientboundUpdateMobEffectPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.effect = (Holder)MobEffect.STREAM_CODEC.decode(var1);
      this.effectAmplifier = var1.readVarInt();
      this.effectDurationTicks = var1.readVarInt();
      this.flags = var1.readByte();
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      MobEffect.STREAM_CODEC.encode(var1, this.effect);
      var1.writeVarInt(this.effectAmplifier);
      var1.writeVarInt(this.effectDurationTicks);
      var1.writeByte(this.flags);
   }

   public PacketType<ClientboundUpdateMobEffectPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_MOB_EFFECT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateMobEffect(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public Holder<MobEffect> getEffect() {
      return this.effect;
   }

   public int getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   public boolean isEffectVisible() {
      return (this.flags & 2) != 0;
   }

   public boolean isEffectAmbient() {
      return (this.flags & 1) != 0;
   }

   public boolean effectShowsIcon() {
      return (this.flags & 4) != 0;
   }

   public boolean shouldBlend() {
      return (this.flags & 8) != 0;
   }
}
