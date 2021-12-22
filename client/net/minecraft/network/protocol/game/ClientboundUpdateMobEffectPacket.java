package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_AMBIENT = 1;
   private static final int FLAG_VISIBLE = 2;
   private static final int FLAG_SHOW_ICON = 4;
   private final int entityId;
   private final byte effectId;
   private final byte effectAmplifier;
   private final int effectDurationTicks;
   private final byte flags;

   public ClientboundUpdateMobEffectPacket(int var1, MobEffectInstance var2) {
      super();
      this.entityId = var1;
      this.effectId = (byte)(MobEffect.getId(var2.getEffect()) & 255);
      this.effectAmplifier = (byte)(var2.getAmplifier() & 255);
      if (var2.getDuration() > 32767) {
         this.effectDurationTicks = 32767;
      } else {
         this.effectDurationTicks = var2.getDuration();
      }

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
   }

   public ClientboundUpdateMobEffectPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.effectId = var1.readByte();
      this.effectAmplifier = var1.readByte();
      this.effectDurationTicks = var1.readVarInt();
      this.flags = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeByte(this.effectId);
      var1.writeByte(this.effectAmplifier);
      var1.writeVarInt(this.effectDurationTicks);
      var1.writeByte(this.flags);
   }

   public boolean isSuperLongDuration() {
      return this.effectDurationTicks == 32767;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateMobEffect(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public byte getEffectId() {
      return this.effectId;
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
}
