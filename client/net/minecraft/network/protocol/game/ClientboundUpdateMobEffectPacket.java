package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_AMBIENT = 1;
   private static final int FLAG_VISIBLE = 2;
   private static final int FLAG_SHOW_ICON = 4;
   private final int entityId;
   private final MobEffect effect;
   private final byte effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;
   @Nullable
   private final MobEffectInstance.FactorData factorData;

   public ClientboundUpdateMobEffectPacket(int var1, MobEffectInstance var2) {
      super();
      this.entityId = var1;
      this.effect = var2.getEffect();
      this.effectAmplifier = (byte)(var2.getAmplifier() & 0xFF);
      this.effectDurationTicks = var2.getDuration();
      byte var3 = 0;
      if (var2.isAmbient()) {
         var3 = (byte)(var3 | 1);
      }

      if (var2.isVisible()) {
         var3 = (byte)(var3 | 2);
      }

      if (var2.showIcon()) {
         var3 = (byte)(var3 | 4);
      }

      this.flags = var3;
      this.factorData = var2.getFactorData().orElse(null);
   }

   public ClientboundUpdateMobEffectPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.effect = var1.readById(BuiltInRegistries.MOB_EFFECT);
      this.effectAmplifier = var1.readByte();
      this.effectDurationTicks = var1.readVarInt();
      this.flags = var1.readByte();
      this.factorData = var1.readNullable(var0 -> var0.readWithCodec(NbtOps.INSTANCE, MobEffectInstance.FactorData.CODEC));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeId(BuiltInRegistries.MOB_EFFECT, this.effect);
      var1.writeByte(this.effectAmplifier);
      var1.writeVarInt(this.effectDurationTicks);
      var1.writeByte(this.flags);
      var1.writeNullable(this.factorData, (var0, var1x) -> var0.writeWithCodec(NbtOps.INSTANCE, MobEffectInstance.FactorData.CODEC, var1x));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateMobEffect(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public byte getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   public boolean isEffectVisible() {
      return (this.flags & 2) == 2;
   }

   public boolean isEffectAmbient() {
      return (this.flags & 1) == 1;
   }

   public boolean effectShowsIcon() {
      return (this.flags & 4) == 4;
   }

   @Nullable
   public MobEffectInstance.FactorData getFactorData() {
      return this.factorData;
   }
}
