package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   private static final short LONG_DURATION_THRESHOLD = 32767;
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
      this.effectAmplifier = (byte)(var2.getAmplifier() & 255);
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
      this.factorData = (MobEffectInstance.FactorData)var2.getFactorData().orElse((Object)null);
   }

   public ClientboundUpdateMobEffectPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.effect = (MobEffect)var1.readById(Registry.MOB_EFFECT);
      this.effectAmplifier = var1.readByte();
      this.effectDurationTicks = var1.readVarInt();
      this.flags = var1.readByte();
      this.factorData = (MobEffectInstance.FactorData)var1.readNullable((var0) -> {
         return (MobEffectInstance.FactorData)var0.readWithCodec(MobEffectInstance.FactorData.CODEC);
      });
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeId(Registry.MOB_EFFECT, this.effect);
      var1.writeByte(this.effectAmplifier);
      var1.writeVarInt(this.effectDurationTicks);
      var1.writeByte(this.flags);
      var1.writeNullable(this.factorData, (var0, var1x) -> {
         var0.writeWithCodec(MobEffectInstance.FactorData.CODEC, var1x);
      });
   }

   public boolean isSuperLongDuration() {
      return this.effectDurationTicks >= 32767;
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
