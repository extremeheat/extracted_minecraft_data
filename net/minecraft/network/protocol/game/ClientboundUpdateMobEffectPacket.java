package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class ClientboundUpdateMobEffectPacket implements Packet {
   private int entityId;
   private byte effectId;
   private byte effectAmplifier;
   private int effectDurationTicks;
   private byte flags;

   public ClientboundUpdateMobEffectPacket() {
   }

   public ClientboundUpdateMobEffectPacket(int var1, MobEffectInstance var2) {
      this.entityId = var1;
      this.effectId = (byte)(MobEffect.getId(var2.getEffect()) & 255);
      this.effectAmplifier = (byte)(var2.getAmplifier() & 255);
      if (var2.getDuration() > 32767) {
         this.effectDurationTicks = 32767;
      } else {
         this.effectDurationTicks = var2.getDuration();
      }

      this.flags = 0;
      if (var2.isAmbient()) {
         this.flags = (byte)(this.flags | 1);
      }

      if (var2.isVisible()) {
         this.flags = (byte)(this.flags | 2);
      }

      if (var2.showIcon()) {
         this.flags = (byte)(this.flags | 4);
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      this.effectId = var1.readByte();
      this.effectAmplifier = var1.readByte();
      this.effectDurationTicks = var1.readVarInt();
      this.flags = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
